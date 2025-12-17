package kerem.dertsiz.cvcontroller.data.model

data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
