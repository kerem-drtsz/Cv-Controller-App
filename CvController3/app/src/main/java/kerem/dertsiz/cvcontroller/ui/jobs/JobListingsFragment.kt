package kerem.dertsiz.cvcontroller.ui.jobs

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kerem.dertsiz.cvcontroller.R
import kerem.dertsiz.cvcontroller.databinding.FragmentJobListingsBinding
import kotlinx.coroutines.launch

class JobListingsFragment : Fragment(R.layout.fragment_job_listings) {

    private var _b: FragmentJobListingsBinding? = null
    private val b get() = _b!!
    private val vm: JobListingsViewModel by viewModels {
        JobListingsViewModelFactory(requireContext())
    }

    private lateinit var adapter: JobListingsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _b = FragmentJobListingsBinding.bind(view)

        setupRecyclerView()
        setupToolbar()
        observeViewModel()

        vm.loadJobListings()
    }

    private fun setupRecyclerView() {
        adapter = JobListingsAdapter { job ->
            val bundle = Bundle().apply {
                putInt("jobId", job.id)
            }
            findNavController().navigate(R.id.jobOptimizeFragment, bundle)
        }
        b.rvJobListings.layoutManager = LinearLayoutManager(requireContext())
        b.rvJobListings.adapter = adapter
    }

    private fun setupToolbar() {
        b.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            vm.uiState.collect { state ->
                when (state) {
                    is JobListingsState.Idle -> {
                        b.progressBar.visibility = View.GONE
                        b.tvError.visibility = View.GONE
                    }
                    is JobListingsState.Loading -> {
                        b.progressBar.visibility = View.VISIBLE
                        b.tvError.visibility = View.GONE
                        b.rvJobListings.visibility = View.GONE
                    }
                    is JobListingsState.Success -> {
                        b.progressBar.visibility = View.GONE
                        b.tvError.visibility = View.GONE
                        b.rvJobListings.visibility = View.VISIBLE
                        adapter.submitList(state.jobs)
                    }
                    is JobListingsState.Error -> {
                        b.progressBar.visibility = View.GONE
                        b.rvJobListings.visibility = View.GONE
                        b.tvError.visibility = View.VISIBLE
                        b.tvError.text = state.message
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
