package kerem.dertsiz.cvcontroller.ui.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kerem.dertsiz.cvcontroller.data.remote.FirebaseAuthDataSource
import kerem.dertsiz.cvcontroller.data.remote.FirestoreUserDataSource
import kerem.dertsiz.cvcontroller.data.repository.AuthRepository
import kerem.dertsiz.cvcontroller.util.UiState
import kotlinx.coroutines.launch

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AuthRepository(
        authDs = FirebaseAuthDataSource(),
        userDs = FirestoreUserDataSource()
    )

    private val _state = MutableLiveData<UiState<String>>(UiState.Idle)
    val state: LiveData<UiState<String>> = _state

    fun register(fullName: String, email: String, password: String) = viewModelScope.launch {
        _state.value = UiState.Loading
        try {
            val uid = repo.register(fullName.trim(), email.trim(), password)
            _state.value = UiState.Success(uid)
        } catch (e: Exception) {
            _state.value = UiState.Error(e.message ?: "Kayıt başarısız")
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _state.value = UiState.Loading
        try {
            val uid = repo.login(email.trim(), password)
            _state.value = UiState.Success(uid)
        } catch (e: Exception) {
            _state.value = UiState.Error(e.message ?: "Giriş başarısız")
        }
    }
}
