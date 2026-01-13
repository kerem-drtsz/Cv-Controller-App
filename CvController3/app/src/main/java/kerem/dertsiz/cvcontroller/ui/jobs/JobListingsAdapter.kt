package kerem.dertsiz.cvcontroller.ui.jobs

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kerem.dertsiz.cvcontroller.data.model.JobListing
import kerem.dertsiz.cvcontroller.databinding.ItemJobListingBinding

class JobListingsAdapter(
    private val onJobClick: (JobListing) -> Unit
) : ListAdapter<JobListing, JobListingsAdapter.JobViewHolder>(JobDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = ItemJobListingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return JobViewHolder(binding, onJobClick)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class JobViewHolder(
        private val binding: ItemJobListingBinding,
        private val onJobClick: (JobListing) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(job: JobListing) {
            binding.tvJobTitle.text = job.title
            binding.tvCompany.text = job.company
            binding.tvLocation.text = job.location
            binding.tvSummary.text = job.summary

            binding.root.setOnClickListener {
                onJobClick(job)
            }
        }
    }

    class JobDiffCallback : DiffUtil.ItemCallback<JobListing>() {
        override fun areItemsTheSame(oldItem: JobListing, newItem: JobListing): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: JobListing, newItem: JobListing): Boolean {
            return oldItem == newItem
        }
    }
}
