package kerem.dertsiz.cvcontroller.data.remote

import android.net.Uri
import android.content.Context
import kerem.dertsiz.cvcontroller.data.model.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class CvAnalyzerDataSource(
    private val apiService: CvAnalyzerApiService
) {
    suspend fun uploadCv(userUuid: String, uri: Uri, context: Context): Result<UploadCvResponse> {
        return try {
            // URI'den dosya adını ve tipini al
            val fileName = getFileNameFromUri(uri, context)
            val mimeType = context.contentResolver.getType(uri) ?: "application/pdf"
            
            // URI'den dosyayı geçici bir File'a kopyala
            val file = createTempFileFromUri(uri, context, fileName)
            
            // MIME type'a göre RequestBody oluştur
            val mediaType = when {
                mimeType.contains("pdf") -> "application/pdf"
                mimeType.contains("word") || mimeType.contains("msword") -> "application/msword"
                mimeType.contains("officedocument.wordprocessingml") -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                else -> "application/pdf"
            }.toMediaTypeOrNull()
            
            val requestFile = file.asRequestBody(mediaType)
            // Dosya adını doğru şekilde gönder (API'nin beklediği format: "file")
            val body = MultipartBody.Part.createFormData("file", fileName, requestFile)

            val response = apiService.uploadCv(userUuid, body)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Upload failed: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.code()} - ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun getFileNameFromUri(uri: Uri, context: Context): String {
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

    suspend fun getAnalysis(userUuid: String, cvId: Int): Result<AnalysisResponse> {
        return try {
            val response = apiService.getAnalysis(userUuid, cvId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Analysis not found: ${response.message()}"))
            }
        } catch (e: HttpException) {
            // 404 ise analiz yok demektir, bu normal
            if (e.code() == 404) {
                Result.failure(Exception("Analysis not found"))
            } else {
                Result.failure(Exception("HTTP error: ${e.code()} - ${e.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun analyzeCv(userUuid: String, cvId: Int): Result<AnalysisResponse> {
        return try {
            val request = AnalyzeCvRequest(cvId)
            val response = apiService.analyzeCv(userUuid, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Analysis failed: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.code()} - ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getJobListings(): Result<JobListingsResponse> {
        return try {
            val response = apiService.getJobListings()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to fetch job listings: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.code()} - ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateFromJob(userUuid: String, cvId: Int, jobId: Int): Result<GenerateFromJobResponse> {
        return try {
            val request = GenerateFromJobRequest(cvId, jobId)
            val response = apiService.generateFromJob(userUuid, request)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Generation failed: ${response.message()}"))
            }
        } catch (e: HttpException) {
            Result.failure(Exception("HTTP error: ${e.code()} - ${e.message()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun createTempFileFromUri(uri: Uri, context: Context, originalFileName: String): File {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        
        // Orijinal dosya uzantısını koru
        val extension = originalFileName.substringAfterLast(".", "pdf")
        val tempFile = File.createTempFile("cv_upload_", ".$extension", context.cacheDir)
        
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }
        
        return tempFile
    }
}
