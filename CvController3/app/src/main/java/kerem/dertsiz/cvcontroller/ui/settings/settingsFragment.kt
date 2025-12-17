package kerem.dertsiz.cvcontroller.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
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

        b.btnLogout.setOnClickListener {
            lifecycleScope.launch {
                prefs.logout()
                startActivity(Intent(requireContext(), AuthActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
