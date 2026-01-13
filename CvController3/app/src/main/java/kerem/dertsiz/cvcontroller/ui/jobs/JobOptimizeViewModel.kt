package kerem.dertsiz.cvcontroller.ui.jobs

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kerem.dertsiz.cvcontroller.data.model.GeneratedCvVersion
import kerem.dertsiz.cvcontroller.data.model.JobListing
import kerem.dertsiz.cvcontroller.data.repository.CvAnalyzerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class JobOptimizeState {
    object Idle : JobOptimizeState()
    object Loading : JobOptimizeState()
    data class Success(val versions: List<GeneratedCvVersion>) : JobOptimizeState()
    data class Error(val message: String) : JobOptimizeState()
}

class JobOptimizeViewModel(
    private val repository: CvAnalyzerRepository,
    private val context: Context,
    private val jobId: Int
) : ViewModel() {

    private var currentJobTitle: String? = null
    private var currentJobCompany: String? = null

    private val _uiState = MutableStateFlow<JobOptimizeState>(JobOptimizeState.Idle)
    val uiState: StateFlow<JobOptimizeState> = _uiState.asStateFlow()

    private val _jobInfo = MutableStateFlow<JobListing?>(null)
    val jobInfo: StateFlow<JobListing?> = _jobInfo.asStateFlow()

    fun loadJobInfo(jobId: Int) = viewModelScope.launch {
        val result = repository.getJobListings()
        if (result.isSuccess) {
            val jobs = result.getOrNull() ?: emptyList()
            val job = jobs.find { it.id == jobId }
            _jobInfo.value = job
            currentJobTitle = job?.title
            currentJobCompany = job?.company
        }
    }

    fun generateOptimizedCv(jobId: Int) = viewModelScope.launch {
        _uiState.value = JobOptimizeState.Loading

        val result = repository.generateFromJob(jobId)
        if (result.isSuccess) {
            val versions = result.getOrNull() ?: emptyList()
            _uiState.value = JobOptimizeState.Success(versions)
        } else {
            val exception = result.exceptionOrNull()
            _uiState.value = JobOptimizeState.Error(
                exception?.message ?: "CV optimize edilemedi"
            )
        }
    }

    fun downloadPdf(version: kerem.dertsiz.cvcontroller.data.model.GeneratedCvVersion) = viewModelScope.launch {
        try {
            // URL relative path ise (örn: /static/...), base URL ile birleştir
            val fullUrl = if (version.downloadUrl.startsWith("http://") || version.downloadUrl.startsWith("https://")) {
                version.downloadUrl
            } else {
                // Relative path ise domain URL ile birleştir
                val domainUrl = kerem.dertsiz.cvcontroller.data.remote.ApiClient.DOMAIN_URL
                if (version.downloadUrl.startsWith("/")) {
                    "$domainUrl${version.downloadUrl}"
                } else {
                    "$domainUrl/${version.downloadUrl}"
                }
            }
            
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(fullUrl))
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                "CV_Optimized_${System.currentTimeMillis()}.pdf"
            )
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            downloadManager.enqueue(request)
            
            // İndirilen CV'yi geçmişe kaydet
            repository.saveDownloadedCv(
                downloadUrl = fullUrl,
                jobTitle = currentJobTitle,
                jobCompany = currentJobCompany,
                level = version.level,
                language = version.language,
                score = version.score
            )
        } catch (e: Exception) {
            _uiState.value = JobOptimizeState.Error("PDF indirilemedi: ${e.message}")
        }
    }
}
