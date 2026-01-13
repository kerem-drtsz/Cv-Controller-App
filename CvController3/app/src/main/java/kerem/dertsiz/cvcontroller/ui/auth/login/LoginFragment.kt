package kerem.dertsiz.cvcontroller.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kerem.dertsiz.cvcontroller.R
import kerem.dertsiz.cvcontroller.data.local.AppPrefs
import kerem.dertsiz.cvcontroller.databinding.FragmentLoginBinding
import kerem.dertsiz.cvcontroller.ui.auth.AuthViewModel
import kerem.dertsiz.cvcontroller.ui.main.MainActivity
import kerem.dertsiz.cvcontroller.util.UiState
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login) {

    private var _b: FragmentLoginBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentLoginBinding.bind(view)
        val prefs = AppPrefs(requireContext())

        b.btnLogin.setOnClickListener {
            val email = b.tilEmail.editText?.text?.toString()?.trim() ?: ""
            val password = b.tilPassword.editText?.text?.toString() ?: ""

            if (email.isEmpty() || password.isEmpty()) {
                b.tvError.text = "Email ve şifre gir"
                b.tvError.visibility = View.VISIBLE
                return@setOnClickListener
            }

            b.progress.visibility = View.VISIBLE

            lifecycleScope.launch {
                // fake login → email’i isim gibi kaydediyoruz
                prefs.saveLogin(fullName = email, email = email)

                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        }

        b.tvGoRegister.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_register)
        }
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
