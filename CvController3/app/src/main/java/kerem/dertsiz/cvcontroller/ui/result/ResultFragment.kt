package kerem.dertsiz.cvcontroller.ui.result

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kerem.dertsiz.cvcontroller.R
import kerem.dertsiz.cvcontroller.data.database.AppDatabase
import kerem.dertsiz.cvcontroller.databinding.FragmentResultBinding
import kotlinx.coroutines.launch

class ResultFragment : Fragment(R.layout.fragment_result) {

    private var _b: FragmentResultBinding? = null
    private val b get() = _b!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _b = FragmentResultBinding.bind(view)

        val historyId = requireArguments().getString(ARG_HISTORY_ID).orEmpty()

        b.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        if (historyId.isBlank()) {
            b.tvTitle.text = "Sonuç bulunamadı"
            b.tvDetails.text = "Geçersiz kayıt."
            return
        }

        // Room'dan sonucu çek
        val dao = AppDatabase.get(requireContext()).cvHistoryDao()
        lifecycleScope.launch {
            val item = dao.getById(historyId)
            if (item == null) {
                b.tvTitle.text = "Sonuç bulunamadı"
                b.tvDetails.text = "Bu kayda ait analiz bulunamadı."
            } else {
                b.tvTitle.text = item.resultSummary.ifBlank { 
                    if (item.type == kerem.dertsiz.cvcontroller.data.database.CvHistoryType.DOWNLOADED_CV.name) {
                        "İndirilen CV"
                    } else {
                        "CV Analizi"
                    }
                }
                
                // Eğer indirilen CV ise, detayları göster
                if (item.type == kerem.dertsiz.cvcontroller.data.database.CvHistoryType.DOWNLOADED_CV.name) {
                    b.tvDetails.text = item.resultDetails.ifBlank { "Detay bulunamadı." }
                } else {
                    b.tvDetails.text = item.resultDetails.ifBlank { "Detay bulunamadı." }
                }
            }
        }
    }

    override fun onDestroyView() {
        _b = null
        super.onDestroyView()
    }

    companion object {
        const val ARG_HISTORY_ID = "historyId"
    }
}
