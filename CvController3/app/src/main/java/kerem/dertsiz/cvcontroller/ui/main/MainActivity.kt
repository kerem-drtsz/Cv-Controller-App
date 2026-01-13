package kerem.dertsiz.cvcontroller.ui.main

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import kerem.dertsiz.cvcontroller.R
import kerem.dertsiz.cvcontroller.data.local.AppPrefs
import kerem.dertsiz.cvcontroller.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: AppPrefs

    override fun attachBaseContext(newBase: Context?) {
        if (newBase == null) {
            super.attachBaseContext(newBase)
            return
        }
        
        // DataStore kullanarak dil ayarını oku (attachBaseContext'te runBlocking güvenli)
        val prefs = AppPrefs(newBase)
        val language = try {
            kotlinx.coroutines.runBlocking { prefs.getLanguage() }
        } catch (e: Exception) {
            "system"
        }
        
        val context = if (language != "system") {
            val locale = java.util.Locale(language)
            val config = newBase.resources.configuration
            config.setLocale(locale)
            newBase.createConfigurationContext(config)
        } else {
            newBase
        }
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = AppPrefs(this)
        
        // Theme ayarını yükle (synchronous olarak) - ÖNCE tema ayarlanmalı
        val themeMode = kotlinx.coroutines.runBlocking { prefs.getThemeMode() }
        when (themeMode) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Status bar ve navigation bar için padding ayarla (top 16dp + status bar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainNavHost) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val topPadding = systemBars.top + resources.getDimensionPixelSize(R.dimen.top_padding)
            v.setPadding(v.paddingLeft, topPadding, v.paddingRight, v.paddingBottom)
            insets
        }

        val navHost = supportFragmentManager.findFragmentById(R.id.mainNavHost) as NavHostFragment
        val navController = navHost.navController

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> { navController.navigate(R.id.homeFragment); true }
                R.id.historyFragment -> { navController.navigate(R.id.historyFragment); true }
                R.id.uploadAction -> {
                    // Home'a git (eğer zaten home'da değilsek)
                    navController.navigate(R.id.homeFragment)

                    // HomeFragment'e "upload başlat" event'i gönder
                    supportFragmentManager.setFragmentResult("UPLOAD_REQUEST", Bundle())

                    // Bu item seçili kalmasın diye false
                    false
                }
                else -> false
            }
        }
    }
}
