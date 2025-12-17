package kerem.dertsiz.cvcontroller.data.repository

import kerem.dertsiz.cvcontroller.data.model.UserProfile
import kerem.dertsiz.cvcontroller.data.remote.FirebaseAuthDataSource
import kerem.dertsiz.cvcontroller.data.remote.FirestoreUserDataSource

class AuthRepository(
    private val authDs: FirebaseAuthDataSource,
    private val userDs: FirestoreUserDataSource
) {
    suspend fun register(fullName: String, email: String, password: String): String {
        val uid = authDs.register(email, password)
        userDs.createUser(UserProfile(uid = uid, fullName = fullName, email = email))
        return uid
    }

    suspend fun login(email: String, password: String): String =
        authDs.login(email, password)
}
