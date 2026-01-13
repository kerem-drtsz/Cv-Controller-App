package kerem.dertsiz.cvcontroller.ui.jobs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kerem.dertsiz.cvcontroller.data.model.GeneratedCvVersion
import kerem.dertsiz.cvcontroller.databinding.ItemCvVersionBinding

class CvVersionAdapter(
    private val onDownloadClick: (GeneratedCvVersion) -> Unit
) : ListAdapter<GeneratedCvVersion, CvVersionAdapter.VersionViewHolder>(VersionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VersionViewHolder {
        val binding = ItemCvVersionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VersionViewHolder(binding, onDownloadClick)
    }

    override fun onBindViewHolder(holder: VersionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class VersionViewHolder(
        private val binding: ItemCvVersionBinding,
        private val onDownloadClick: (GeneratedCvVersion) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(version: GeneratedCvVersion) {
            val levelText = when (version.level) {
                "simple" -> "Sade"
                "aggressive" -> "Gelişmiş"
                else -> version.level.replaceFirstChar { it.uppercaseChar() }
            }
            val languageText = when (version.language) {
                "tr" -> "Türkçe"
                "en" -> "English"
                else -> version.language.uppercase()
            }

            binding.tvLevel.text = levelText
            binding.tvLanguage.text = languageText
            binding.tvScore.text = "${version.score}/100"

            binding.btnDownload.setOnClickListener {
                onDownloadClick(version)
            }
        }
    }

    class VersionDiffCallback : DiffUtil.ItemCallback<GeneratedCvVersion>() {
        override fun areItemsTheSame(
            oldItem: GeneratedCvVersion,
            newItem: GeneratedCvVersion
        ): Boolean {
            return oldItem.downloadUrl == newItem.downloadUrl
        }

        override fun areContentsTheSame(
            oldItem: GeneratedCvVersion,
            newItem: GeneratedCvVersion
        ): Boolean {
            return oldItem == newItem
        }
    }
}
