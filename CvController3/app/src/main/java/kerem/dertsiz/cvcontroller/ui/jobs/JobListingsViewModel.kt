package kerem.dertsiz.cvcontroller.ui.jobs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kerem.dertsiz.cvcontroller.data.model.JobListing
import kerem.dertsiz.cvcontroller.data.repository.CvAnalyzerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class JobListingsState {
    object Idle : JobListingsState()
    object Loading : JobListingsState()
    data class Success(val jobs: List<JobListing>) : JobListingsState()
    data class Error(val message: String) : JobListingsState()
}

class JobListingsViewModel(
    private val repository: CvAnalyzerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<JobListingsState>(JobListingsState.Idle)
    val uiState: StateFlow<JobListingsState> = _uiState.asStateFlow()

    fun loadJobListings() = viewModelScope.launch {
        _uiState.value = JobListingsState.Loading

        val result = repository.getJobListings()
        if (result.isSuccess) {
            val jobs = result.getOrNull() ?: emptyList()
            _uiState.value = JobListingsState.Success(jobs)
        } else {
            val exception = result.exceptionOrNull()
            _uiState.value = JobListingsState.Error(
                exception?.message ?: "İş ilanları yüklenemedi"
            )
        }
    }
}
