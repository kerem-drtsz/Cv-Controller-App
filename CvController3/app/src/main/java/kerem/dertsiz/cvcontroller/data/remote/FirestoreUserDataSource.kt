package kerem.dertsiz.cvcontroller.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kerem.dertsiz.cvcontroller.data.model.UserProfile
import kotlinx.coroutines.tasks.await

class FirestoreUserDataSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val users = db.collection("users")

    suspend fun createUser(user: UserProfile) {
        users.document(user.uid).set(user).await()
    }

    suspend fun getUser(uid: String): UserProfile? {
        val snap = users.document(uid).get().await()
        return snap.toObject(UserProfile::class.java)
    }
}
