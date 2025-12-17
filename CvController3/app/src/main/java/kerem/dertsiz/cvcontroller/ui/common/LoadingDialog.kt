package kerem.dertsiz.cvcontroller.ui.common

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kerem.dertsiz.cvcontroller.R

class LoadingDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?) =
        MaterialAlertDialogBuilder(requireContext())
            .setView(layoutInflater.inflate(R.layout.dialog_loading, null))
            .setCancelable(false)
            .create()

    companion object {
        fun show(fm: FragmentManager) {
            if (fm.findFragmentByTag("loading") == null) LoadingDialog().show(fm, "loading")
        }
        fun hide(fm: FragmentManager) {
            (fm.findFragmentByTag("loading") as? DialogFragment)?.dismissAllowingStateLoss()
        }
    }
}
