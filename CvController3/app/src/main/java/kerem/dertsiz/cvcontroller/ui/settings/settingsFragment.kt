package kerem.dertsiz.cvcontroller.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kerem.dertsiz.cvcontroller.R
import kerem.dertsiz.cvcontroller.data.local.AppPrefs
import kerem.dertsiz.cvcontroller.databinding.FragmentSettingsBinding
import kerem.dertsiz.cvcontroller.ui.auth.AuthActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private var _b: FragmentSettingsBinding? = null
    private val b get() = _b!!
    private lateinit var prefs: AppPrefs

    private var selectedPhotoUri: Uri? = null

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                selectedPhotoUri = uri
                b.ivProfile.setImageURI(uri)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentSettingsBinding.bind(view)
        prefs = AppPrefs(requireContext())

        // Mevcut profil bilgilerini yükle
        lifecycleScope.launch {
            b.etFirstName.setText(prefs.firstName.first())
            b.etLastName.setText(prefs.lastName.first())

            val photo = prefs.photoUri.first()
            if (photo.isNotEmpty()) {
                b.ivProfile.setImageURI(Uri.parse(photo))
            }

            // Theme ayarını yükle
            val themeMode = prefs.getThemeMode()
            b.switchTheme.isChecked = themeMode == "dark"
            
            // Dil ayarını yükle
            val currentLanguage = prefs.getLanguage()
            updateLanguageButtons(currentLanguage)
        }

        b.ivProfile.setOnClickListener {
            pickImage.launch(arrayOf("image/*"))
        }

        b.btnSave.setOnClickListener {
            lifecycleScope.launch {
                prefs.saveProfile(
                    first = b.etFirstName.text.toString().trim(),
                    last = b.etLastName.text.toString().trim(),
                    photoUri = selectedPhotoUri?.toString()
                )
                findNavController().popBackStack()
            }
        }

        b.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                val currentTheme = prefs.getThemeMode()
                val newThemeMode = if (isChecked) "dark" else "light"
                
                // Sadece tema gerçekten değiştiyse güncelle
                if (currentTheme != newThemeMode) {
                    prefs.saveThemeMode(newThemeMode)
                    
                    // Tema değişikliğini uygula
                    if (isChecked) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                    
                    // Activity'yi yeniden başlat ki tüm UI güncellensin
                    requireActivity().recreate()
                }
            }
        }

        b.btnLanguageTurkish.setOnClickListener {
            lifecycleScope.launch {
                val currentLanguage = prefs.getLanguage()
                if (currentLanguage != "tr") {
                    prefs.saveLanguage("tr")
                    updateLanguageButtons("tr")
                    // Dil değişikliğini uygula - sadece bir kez recreate çağır
                    setAppLanguage("tr")
                }
            }
        }

        b.btnLanguageEnglish.setOnClickListener {
            lifecycleScope.launch {
                val currentLanguage = prefs.getLanguage()
                if (currentLanguage != "en") {
                    prefs.saveLanguage("en")
                    updateLanguageButtons("en")
                    // Dil değişikliğini uygula - sadece bir kez recreate çağır
                    setAppLanguage("en")
                }
            }
        }

        b.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                prefs.logout()
                startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
            }
        }
    }
    
    private fun updateLanguageButtons(selectedLanguage: String) {
        val primaryColor = ContextCompat.getColor(requireContext(), R.color.light_primary)
        val onPrimaryColor = ContextCompat.getColor(requireContext(), R.color.light_on_primary)
        
        // Tüm butonları normal duruma getir (outlined button)
        b.btnLanguageTurkish.backgroundTintList = null
        b.btnLanguageEnglish.backgroundTintList = null
        b.btnLanguageTurkish.setTextColor(primaryColor)
        b.btnLanguageEnglish.setTextColor(primaryColor)
        
        // Seçili dili vurgula
        when (selectedLanguage) {
            "tr" -> {
                b.btnLanguageTurkish.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.light_primary)
                b.btnLanguageTurkish.setTextColor(onPrimaryColor)
            }
            "en" -> {
                b.btnLanguageEnglish.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.light_primary)
                b.btnLanguageEnglish.setTextColor(onPrimaryColor)
            }
        }
    }
    
    private fun setAppLanguage(language: String) {
        // Sadece activity'yi yeniden başlat
        // Locale MainActivity.attachBaseContext içinde ayarlanacak
        requireActivity().recreate()
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
