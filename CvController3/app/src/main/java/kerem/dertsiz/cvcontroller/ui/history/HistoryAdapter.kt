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
            b.tvSummary.text = item.resultSummary

            val df = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr"))
            b.tvDate.text = df.format(Date(item.createdAt))

            b.root.setOnClickListener { onClick(item) }
            b.btnDelete.setOnClickListener { onDelete(item) }
        }
    }
}
