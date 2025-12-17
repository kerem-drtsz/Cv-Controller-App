package kerem.dertsiz.cvcontroller.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kerem.dertsiz.cvcontroller.data.local.AppPrefs
import kerem.dertsiz.cvcontroller.databinding.ActivityOnboardingBinding
import kerem.dertsiz.cvcontroller.ui.auth.AuthActivity
import kerem.dertsiz.cvcontroller.ui.main.MainActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var prefs: AppPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = AppPrefs(this)

        val pages = listOf(
            OnboardingPage("CV’ni Yönet", "CV bilgilerini tek yerden düzenle ve sakla."),
            OnboardingPage("Şablonlar", "Profesyonel şablonlarla hızlı CV oluştur."),
            OnboardingPage("Bulut + Offline", "Firebase ile senkron, local ile offline.")
        )

        binding.viewPager.adapter = OnboardingAdapter(pages)

        // Eğer dots kullanıyorsan kalsın; yoksa bu satırı kaldır
        binding.dotsIndicator.attachTo(binding.viewPager)

        fun finishOnboarding() {
            lifecycleScope.launch {
                prefs.setOnboardingSeen(true)

                val loggedIn = prefs.isLoggedIn.first()
                val next = if (loggedIn) {
                    Intent(this@OnboardingActivity, MainActivity::class.java)
                } else {
                    Intent(this@OnboardingActivity, AuthActivity::class.java)
                }

                startActivity(next)
                finish()
            }
        }

        binding.btnNext.setOnClickListener {
            val lastIndex = pages.lastIndex
            val current = binding.viewPager.currentItem
            if (current < lastIndex) {
                binding.viewPager.currentItem = current + 1
            } else {
                finishOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }
    }
}
