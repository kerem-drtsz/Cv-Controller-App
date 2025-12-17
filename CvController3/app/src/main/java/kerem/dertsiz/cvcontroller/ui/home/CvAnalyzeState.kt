package kerem.dertsiz.cvcontroller.ui.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.UUID

sealed class CvAnalyzeState {
    object Idle : CvAnalyzeState()
    object Loading : CvAnalyzeState()
    data class Success(val historyId: String) : CvAnalyzeState()
    data class Error(val message: String) : CvAnalyzeState()
}

class CvAnalyzeViewModel : ViewModel() {

    private val _uiState = MutableLiveData<CvAnalyzeState>(CvAnalyzeState.Idle)
    val uiState: LiveData<CvAnalyzeState> = _uiState

    fun analyzeCv(uri: Uri) = viewModelScope.launch {
        _uiState.value = CvAnalyzeState.Loading

        try {
            // TODO: dosyayı oku / görüntüye çevir / OpenAI'ye gönder
            delay(2500)

            val id = UUID.randomUUID().toString()
            // TODO: Room'a kaydet (fileName, uri, result)
            // historyDao.upsert(...)

            _uiState.value = CvAnalyzeState.Success(id)
        } catch (e: Exception) {
            _uiState.value = CvAnalyzeState.Error(e.message ?: "Analiz başarısız")
        }
    }

    fun reset() { _uiState.value = CvAnalyzeState.Idle }
}
