package kerem.dertsiz.cvcontroller.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "cv_controller_prefs")

class AppPrefs(private val context: Context) {

    private val KEY_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val KEY_FULL_NAME = stringPreferencesKey("full_name")
    private val KEY_EMAIL = stringPreferencesKey("email")
    private val KEY_ONBOARDING_SEEN = booleanPreferencesKey("onboarding_seen")
    private val KEY_FIRST_NAME = stringPreferencesKey("first_name")
    private val KEY_LAST_NAME  = stringPreferencesKey("last_name")
    private val KEY_PHOTO_URI  = stringPreferencesKey("photo_uri")
    private val KEY_USER_UUID = stringPreferencesKey("user_uuid")
    private val KEY_CV_ID = stringPreferencesKey("cv_id")

    val firstName = context.dataStore.data.map { it[KEY_FIRST_NAME] ?: "" }
    val lastName  = context.dataStore.data.map { it[KEY_LAST_NAME] ?: "" }
    val photoUri  = context.dataStore.data.map { it[KEY_PHOTO_URI] ?: "" }
    val userUuid: Flow<String> = context.dataStore.data.map { it[KEY_USER_UUID] ?: "" }

    val onboardingSeen: Flow<Boolean> =
        context.dataStore.data.map { it[KEY_ONBOARDING_SEEN] ?: false }

    val isLoggedIn: Flow<Boolean> =
        context.dataStore.data.map { it[KEY_LOGGED_IN] ?: false }

    suspend fun saveLogin(fullName: String, email: String) {
        context.dataStore.edit {
            it[KEY_LOGGED_IN] = true
            it[KEY_FULL_NAME] = fullName
            it[KEY_EMAIL] = email
        }
    }

    suspend fun logout() {
        context.dataStore.edit {
            it[KEY_LOGGED_IN] = false
            it.remove(KEY_FULL_NAME)
            it.remove(KEY_EMAIL)
        }
    }

    suspend fun saveProfile(first: String, last: String, photoUri: String?) {
        context.dataStore.edit {
            it[KEY_FIRST_NAME] = first
            it[KEY_LAST_NAME]  = last
            if (photoUri != null) it[KEY_PHOTO_URI] = photoUri else it.remove(KEY_PHOTO_URI)
        }
    }

    suspend fun setOnboardingSeen(value: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_SEEN] = value }
    }

    suspend fun saveUserUuid(uuid: String) {
        context.dataStore.edit {
            it[KEY_USER_UUID] = uuid
        }
    }

    suspend fun getOrCreateUserUuid(): String {
        val existingUuid = context.dataStore.data.map { it[KEY_USER_UUID] ?: "" }.first()
        return if (existingUuid.isNotEmpty()) {
            existingUuid
        } else {
            val newUuid = java.util.UUID.randomUUID().toString()
            saveUserUuid(newUuid)
            newUuid
        }
    }

    suspend fun saveCvId(cvId: Int) {
        context.dataStore.edit {
            it[KEY_CV_ID] = cvId.toString()
        }
    }

    suspend fun getCvId(): Int? {
        val cvIdString = context.dataStore.data.map { it[KEY_CV_ID] ?: "" }.first()
        return cvIdString.toIntOrNull()
    }

    suspend fun clearCvId() {
        context.dataStore.edit {
            it.remove(KEY_CV_ID)
        }
    }
}
