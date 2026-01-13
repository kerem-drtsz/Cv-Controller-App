package kerem.dertsiz.cvcontroller.data.repository

import android.content.Context
import android.net.Uri
import kerem.dertsiz.cvcontroller.data.database.AppDatabase
import kerem.dertsiz.cvcontroller.data.database.CvHistoryEntity
import kerem.dertsiz.cvcontroller.data.local.AppPrefs
import kerem.dertsiz.cvcontroller.data.model.*
import kerem.dertsiz.cvcontroller.data.remote.CvAnalyzerDataSource
import java.util.UUID

class CvAnalyzerRepository(
    private val dataSource: CvAnalyzerDataSource,
    private val appPrefs: AppPrefs,
    private val context: Context
) {
    private val historyDao = AppDatabase.get(context).cvHistoryDao()

    suspend fun getOrCreateUserUuid(): String {
        return appPrefs.getOrCreateUserUuid()
    }

    suspend fun uploadCv(uri: Uri): Result<Int> {
        val userUuid = getOrCreateUserUuid()
        val result = dataSource.uploadCv(userUuid, uri, context)
        return if (result.isSuccess) {
            val response = result.getOrNull()
            if (response != null) {
                val cvId = response.cvId
                // CV ID'yi kaydet
                appPrefs.saveCvId(cvId)
                // Kaydedildiğini doğrula
                val savedCvId = appPrefs.getCvId()
                if (savedCvId == cvId) {
                    Result.success(cvId)
                } else {
                    Result.failure(Exception("CV ID kaydedilemedi. Lütfen tekrar deneyin."))
                }
            } else {
                Result.failure(Exception("Invalid response"))
            }
        } else {
            // Yükleme başarısız oldu, eski CV ID'yi temizle
            appPrefs.clearCvId()
            Result.failure(result.exceptionOrNull() ?: Exception("Upload failed"))
        }
    }

    suspend fun analyzeCv(forceNew: Boolean = false): Result<CvAnalysis> {
        val userUuid = getOrCreateUserUuid()
        val cvId = appPrefs.getCvId()
        
        if (cvId == null) {
            return Result.failure(Exception("CV yüklenemedi. Lütfen tekrar deneyin."))
        }
        
        // Önce GET ile mevcut analizi kontrol et (eğer forceNew false ise)
        if (!forceNew) {
            val getResult = dataSource.getAnalysis(userUuid, cvId)
            if (getResult.isSuccess) {
                val response = getResult.getOrNull()
                if (response != null) {
                    return Result.success(response.analysis)
                }
            }
            // GET başarısız oldu (404 gibi), POST ile yeni analiz yap
        }
        
        // POST ile yeni analiz yap
        val result = dataSource.analyzeCv(userUuid, cvId)
        return if (result.isSuccess) {
            val response = result.getOrNull()
            if (response != null) {
                Result.success(response.analysis)
            } else {
                Result.failure(Exception("Invalid response"))
            }
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Analysis failed"))
        }
    }

    suspend fun getJobListings(): Result<List<JobListing>> {
        val result = dataSource.getJobListings()
        return if (result.isSuccess) {
            val response = result.getOrNull()
            if (response != null) {
                Result.success(response.jobs)
            } else {
                Result.failure(Exception("Invalid response"))
            }
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Failed to fetch job listings"))
        }
    }

    suspend fun generateFromJob(jobId: Int): Result<List<GeneratedCvVersion>> {
        val userUuid = getOrCreateUserUuid()
        val cvId = appPrefs.getCvId()
        
        if (cvId == null) {
            return Result.failure(Exception("Lütfen önce bir CV yükleyin. Ana sayfadan CV'nizi yükleyip analiz ettikten sonra bu özelliği kullanabilirsiniz."))
        }
        
        val result = dataSource.generateFromJob(userUuid, cvId, jobId)
        return if (result.isSuccess) {
            val response = result.getOrNull()
            if (response != null) {
                Result.success(response.results)
            } else {
                Result.failure(Exception("Invalid response"))
            }
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Generation failed"))
        }
    }

    suspend fun saveCvHistory(
        fileName: String,
        fileUri: String,
        analysis: CvAnalysis
    ): String {
        val historyId = UUID.randomUUID().toString()
        val entity = CvHistoryEntity(
            id = historyId,
            fileName = fileName,
            fileUri = fileUri,
            createdAt = System.currentTimeMillis(),
            resultSummary = "Puan: ${analysis.score}/100",
            resultDetails = buildString {
                append("Özet: ${analysis.summary}\n\n")
                append("Güçlü Yönler:\n")
                analysis.strengths.forEach { append("• $it\n") }
                append("\nZayıf Yönler:\n")
                analysis.weaknesses.forEach { append("• $it\n") }
                append("\nÖneriler:\n")
                analysis.recommendations.forEach { append("• $it\n") }
            }
        )
        historyDao.upsert(entity)
        return historyId
    }

    fun observeCvHistory() = historyDao.observeAll()
    
    suspend fun getCvHistoryById(id: String) = historyDao.getById(id)
    
    suspend fun deleteCvHistory(id: String) = historyDao.deleteById(id)

    fun getFileNameFromUri(uri: Uri): String {
        var result: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    result = it.getString(nameIndex)
                }
            }
        }
        return result ?: uri.lastPathSegment ?: "CV.pdf"
    }
}
