package kerem.dertsiz.cvcontroller.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kerem.dertsiz.cvcontroller.R
import kerem.dertsiz.cvcontroller.data.database.AppDatabase
import kerem.dertsiz.cvcontroller.databinding.FragmentHistoryBinding
import kerem.dertsiz.cvcontroller.ui.result.ResultFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryFragment : Fragment(R.layout.fragment_history) {

    private var _b: FragmentHistoryBinding? = null
    private val b get() = _b!!

    private lateinit var adapter: HistoryAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentHistoryBinding.bind(view)

        val dao = AppDatabase.get(requireContext()).cvHistoryDao()

        adapter = HistoryAdapter(
            onClick = { item ->
                val bundle = Bundle().apply {
                    putString(ResultFragment.ARG_HISTORY_ID, item.id)
                }
                findNavController().navigate(R.id.resultFragment, bundle)
            },
            onDelete = { item ->
                viewLifecycleOwner.lifecycleScope.launch {
                    dao.deleteById(item.id)
                }
            }
        )

        b.recyclerHistory.adapter = adapter

        // Listeyi sürekli güncelle (Flow)
        viewLifecycleOwner.lifecycleScope.launch {
            dao.observeAll().collectLatest { list ->
                b.tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                adapter.submitList(list)
            }
        }
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }
}
