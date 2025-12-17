package kerem.dertsiz.cvcontroller.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import kerem.dertsiz.cvcontroller.R
import kerem.dertsiz.cvcontroller.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
