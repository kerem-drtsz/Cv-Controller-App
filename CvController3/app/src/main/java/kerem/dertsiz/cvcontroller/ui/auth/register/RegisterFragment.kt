package kerem.dertsiz.cvcontroller.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kerem.dertsiz.cvcontroller.R
import kerem.dertsiz.cvcontroller.data.local.AppPrefs
import kerem.dertsiz.cvcontroller.databinding.FragmentRegisterBinding
import kerem.dertsiz.cvcontroller.ui.main.MainActivity
import kotlinx.coroutines.launch

class RegisterFragment : Fragment(R.layout.fragment_register) {

    private var _b: FragmentRegisterBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentRegisterBinding.bind(view)
        val prefs = AppPrefs(requireContext())

        b.btnRegister.setOnClickListener {
            val fullName = b.etFullName.text.toString().trim()
            val email = b.etEmail.text.toString().trim()
            val password = b.etPassword.text.toString()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty()) {
                b.tvError.text = "Tüm alanlar zorunlu"
                b.tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (password.length < 6) {
                b.tvError.text = "Şifre en az 6 karakter"
                b.tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            b.progress.visibility = View.VISIBLE

            lifecycleScope.launch {
                prefs.saveLogin(fullName, email)
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        }

        b.tvGoLogin.setOnClickListener {
            findNavController().navigate(R.id.action_register_to_login)
        }
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
