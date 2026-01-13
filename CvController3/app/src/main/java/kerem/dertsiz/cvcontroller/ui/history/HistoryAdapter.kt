package kerem.dertsiz.cvcontroller.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kerem.dertsiz.cvcontroller.data.database.CvHistoryEntity
import kerem.dertsiz.cvcontroller.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private val onClick: (CvHistoryEntity) -> Unit,
    private val onDelete: (CvHistoryEntity) -> Unit
) : ListAdapter<CvHistoryEntity, HistoryAdapter.VH>(Diff) {

    object Diff : DiffUtil.ItemCallback<CvHistoryEntity>() {
        override fun areItemsTheSame(old: CvHistoryEntity, new: CvHistoryEntity) = old.id == new.id
        override fun areContentsTheSame(old: CvHistoryEntity, new: CvHistoryEntity) = old == new
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VH(private val b: ItemHistoryBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(item: CvHistoryEntity) {
            b.tvFileName.text = item.fileName
            
            // Tip'e göre özet göster
            val summaryText = when (item.type ?: kerem.dertsiz.cvcontroller.data.database.CvHistoryType.ANALYSIS.name) {
                kerem.dertsiz.cvcontroller.data.database.CvHistoryType.DOWNLOADED_CV.name -> {
                    val levelText = when (item.cvLevel) {
                        "simple" -> "Sade"
                        "aggressive" -> "Gelişmiş"
                        else -> item.cvLevel ?: ""
                    }
                    val langText = when (item.cvLanguage) {
                        "tr" -> "Türkçe"
                        "en" -> "English"
                        else -> item.cvLanguage ?: ""
                    }
                    "📄 Optimize CV - $levelText ($langText)"
                }
                else -> item.resultSummary
            }
            b.tvSummary.text = summaryText

            val df = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr"))
            b.tvDate.text = df.format(Date(item.createdAt))

            b.root.setOnClickListener { onClick(item) }
            b.btnDelete.setOnClickListener { onDelete(item) }
        }
    }
}
