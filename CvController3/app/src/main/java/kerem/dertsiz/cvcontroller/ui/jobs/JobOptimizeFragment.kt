package kerem.dertsiz.cvcontroller.ui.jobs

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kerem.dertsiz.cvcontroller.R
import kerem.dertsiz.cvcontroller.databinding.FragmentJobOptimizeBinding
import kotlinx.coroutines.launch

class JobOptimizeFragment : Fragment(R.layout.fragment_job_optimize) {

    private var _b: FragmentJobOptimizeBinding? = null
    private val b get() = _b!!
    private val vm: JobOptimizeViewModel by viewModels {
        JobOptimizeViewModelFactory(requireContext(), arguments?.getInt("jobId") ?: 0)
    }

    private lateinit var adapter: CvVersionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentJobOptimizeBinding.bind(view)

        val jobId = arguments?.getInt("jobId") ?: run {
            Toast.makeText(requireContext(), "Geçersiz iş ilanı", Toast.LENGTH_SHORT).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        setupRecyclerView()
        setupToolbar()
        observeViewModel()

        vm.loadJobInfo(jobId)
        vm.generateOptimizedCv(jobId)
    }

    private fun setupRecyclerView() {
        adapter = CvVersionAdapter { version ->
            vm.downloadPdf(version.downloadUrl)
        }
        b.rvCvVersions.layoutManager = LinearLayoutManager(requireContext())
        b.rvCvVersions.adapter = adapter
    }

    private fun setupToolbar() {
        b.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            vm.uiState.collect { state ->
                when (state) {
                    is JobOptimizeState.Idle -> {
                        b.progressBar.visibility = View.GONE
                        b.tvError.visibility = View.GONE
                    }
                    is JobOptimizeState.Loading -> {
                        b.progressBar.visibility = View.VISIBLE
                        b.tvError.visibility = View.GONE
                        b.rvCvVersions.visibility = View.GONE
                    }
                    is JobOptimizeState.Success -> {
                        b.progressBar.visibility = View.GONE
                        b.tvError.visibility = View.GONE
                        b.rvCvVersions.visibility = View.VISIBLE
                        adapter.submitList(state.versions)
                    }
                    is JobOptimizeState.Error -> {
                        b.progressBar.visibility = View.GONE
                        b.rvCvVersions.visibility = View.GONE
                        b.tvError.visibility = View.VISIBLE
                        b.tvError.text = state.message
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        lifecycleScope.launch {
            vm.jobInfo.collect { job ->
                job?.let {
                    b.tvJobTitle.text = it.title
                    b.tvCompany.text = it.company
                }
            }
        }
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
