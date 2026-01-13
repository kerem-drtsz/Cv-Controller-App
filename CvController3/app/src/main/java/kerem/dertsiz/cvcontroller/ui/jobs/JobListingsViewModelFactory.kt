package kerem.dertsiz.cvcontroller.ui.jobs

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kerem.dertsiz.cvcontroller.data.local.AppPrefs
import kerem.dertsiz.cvcontroller.data.remote.ApiClient
import kerem.dertsiz.cvcontroller.data.remote.CvAnalyzerDataSource
import kerem.dertsiz.cvcontroller.data.repository.CvAnalyzerRepository

class JobListingsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JobListingsViewModel::class.java)) {
            val dataSource = CvAnalyzerDataSource(ApiClient.apiService)
            val appPrefs = AppPrefs(context)
            val repository = CvAnalyzerRepository(dataSource, appPrefs, context)
            @Suppress("UNCHECKED_CAST")
            return JobListingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
