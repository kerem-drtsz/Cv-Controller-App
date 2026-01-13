package kerem.dertsiz.cvcontroller.ui.home

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kerem.dertsiz.cvcontroller.data.repository.CvAnalyzerRepository
import kotlinx.coroutines.launch

sealed class CvAnalyzeState {
    object Idle : CvAnalyzeState()
    object Loading : CvAnalyzeState()
    data class Success(val historyId: String) : CvAnalyzeState()
    data class Error(val message: String) : CvAnalyzeState()
}

class CvAnalyzeViewModel(
    private val repository: CvAnalyzerRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<CvAnalyzeState>(CvAnalyzeState.Idle)
    val uiState: LiveData<CvAnalyzeState> = _uiState

    fun analyzeCv(uri: Uri) = viewModelScope.launch {
        _uiState.value = CvAnalyzeState.Loading

        try {
            // 1. CV'yi yükle
            val uploadResult = repository.uploadCv(uri)
            if (uploadResult.isFailure) {
                _uiState.value = CvAnalyzeState.Error(
                    uploadResult.exceptionOrNull()?.message ?: "CV yükleme başarısız"
                )
                return@launch
            }

            // 2. CV'yi analiz et
            val analysisResult = repository.analyzeCv()
            if (analysisResult.isFailure) {
                _uiState.value = CvAnalyzeState.Error(
                    analysisResult.exceptionOrNull()?.message ?: "CV analizi başarısız"
                )
                return@launch
            }

            val analysis = analysisResult.getOrNull() ?: run {
                _uiState.value = CvAnalyzeState.Error("Analiz sonucu alınamadı")
                return@launch
            }

            // 3. Sonucu Room'a kaydet
            val fileName = repository.getFileNameFromUri(uri)
            val historyId = repository.saveCvHistory(
                fileName = fileName,
                fileUri = uri.toString(),
                analysis = analysis
            )

            _uiState.value = CvAnalyzeState.Success(historyId)
        } catch (e: Exception) {
            _uiState.value = CvAnalyzeState.Error(e.message ?: "Analiz başarısız")
        }
    }

    fun reset() { _uiState.value = CvAnalyzeState.Idle }
}
