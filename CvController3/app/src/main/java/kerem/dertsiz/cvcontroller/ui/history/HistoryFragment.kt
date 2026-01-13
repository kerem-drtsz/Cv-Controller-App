package kerem.dertsiz.cvcontroller.ui.history

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
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

        b.recyclerHistory.layoutManager = LinearLayoutManager(requireContext())
        b.recyclerHistory.adapter = adapter

        // Listeyi sürekli güncelle (Flow)
        viewLifecycleOwner.lifecycleScope.launch {
            dao.observeAll().collectLatest { list ->
                android.util.Log.d("HistoryFragment", "History list size: ${list.size}")
                list.forEachIndexed { index, item ->
                    android.util.Log.d("HistoryFragment", "Item $index: ${item.fileName}, type: ${item.type}")
                }
                
                if (list.isEmpty()) {
                    b.tvEmpty.visibility = View.VISIBLE
                    b.recyclerHistory.visibility = View.GONE
                } else {
                    b.tvEmpty.visibility = View.GONE
                    b.recyclerHistory.visibility = View.VISIBLE
                    adapter.submitList(list) {
                        android.util.Log.d("HistoryFragment", "Adapter item count: ${adapter.itemCount}")
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
