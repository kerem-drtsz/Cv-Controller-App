package kerem.dertsiz.cvcontroller.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kerem.dertsiz.cvcontroller.R
import kerem.dertsiz.cvcontroller.databinding.FragmentHomeBinding
import kerem.dertsiz.cvcontroller.ui.common.LoadingDialog
import java.util.Locale

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _b: FragmentHomeBinding? = null
    private val b get() = _b!!
    private val vm: CvAnalyzeViewModel by viewModels {
        CvAnalyzeViewModelFactory(requireContext())
    }

    private val pickDoc = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            requireContext().contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            vm.analyzeCv(uri)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentHomeBinding.bind(view)

        b.tvDate.text = java.time.LocalDate.now()
            .format(java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("tr")))

        b.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.settingsFragment)
        }

        b.btnJobListings.setOnClickListener {
            findNavController().navigate(R.id.jobListingsFragment)
        }

        fun startPick() {
            pickDoc.launch(arrayOf(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
            ))
        }

        b.uploadCard.setOnClickListener { startPick() }

        // BottomNav ortadaki "+" basılınca gelen event
        parentFragmentManager.setFragmentResultListener("UPLOAD_REQUEST", viewLifecycleOwner) { _, _ ->
            startPick()
        }

        vm.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is CvAnalyzeState.Idle -> Unit
                is CvAnalyzeState.Loading -> LoadingDialog.show(childFragmentManager)
                is CvAnalyzeState.Success -> {
                    LoadingDialog.hide(childFragmentManager)
                    val bundle = Bundle().apply {
                        putString(kerem.dertsiz.cvcontroller.ui.result.ResultFragment.ARG_HISTORY_ID, state.historyId)
                    }
                    findNavController().navigate(R.id.resultFragment, bundle)

                    vm.reset()
                }
                is CvAnalyzeState.Error -> {
                    LoadingDialog.hide(childFragmentManager)
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    vm.reset()
                }
            }
        }
    }

    override fun onDestroyView() { _b = null; super.onDestroyView() }
}
