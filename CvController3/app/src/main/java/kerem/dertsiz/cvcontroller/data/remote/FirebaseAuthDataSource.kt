package kerem.dertsiz.cvcontroller.data.remote

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await

class FirebaseAuthDataSource(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {

    suspend fun register(email: String, password: String): String {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.uid ?: throw IllegalStateException("User is null")
        } catch (e: FirebaseAuthException) {
            Log.e("FirebaseAuth", "Register failed: ${e.errorCode}", e)
            throw e
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Register failed", e)
            throw e
        }
    }

    suspend fun login(email: String, password: String): String {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.uid ?: throw IllegalStateException("User is null")
        } catch (e: FirebaseAuthException) {
            Log.e("FirebaseAuth", "Login failed: ${e.errorCode}", e)
            throw e
        } catch (e: Exception) {
            Log.e("FirebaseAuth", "Login failed", e)
            throw e
        }
    }

    fun currentUserId(): String? = auth.currentUser?.uid
    fun logout() = auth.signOut()
}
