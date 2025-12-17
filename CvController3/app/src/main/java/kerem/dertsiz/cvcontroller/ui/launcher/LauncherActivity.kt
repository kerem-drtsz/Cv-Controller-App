package kerem.dertsiz.cvcontroller.ui.launcher

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kerem.dertsiz.cvcontroller.data.local.AppPrefs
import kerem.dertsiz.cvcontroller.ui.auth.AuthActivity
import kerem.dertsiz.cvcontroller.ui.main.MainActivity
import kerem.dertsiz.cvcontroller.ui.onboarding.OnboardingActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = AppPrefs(this)

        lifecycleScope.launch {
            val seen = prefs.onboardingSeen.first()
            val loggedIn = prefs.isLoggedIn.first()

            val next = when {
                !seen -> Intent(this@LauncherActivity, OnboardingActivity::class.java)
                loggedIn -> Intent(this@LauncherActivity, MainActivity::class.java)
                else -> Intent(this@LauncherActivity, AuthActivity::class.java)
            }

            startActivity(next)
            finish()
        }
    }
}
