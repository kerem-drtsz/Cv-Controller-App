package kerem.dertsiz.cvcontroller.ui.onboarding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kerem.dertsiz.cvcontroller.databinding.ItemOnboardingBinding

class OnboardingAdapter(
    private val items: List<OnboardingPage>
) : RecyclerView.Adapter<OnboardingAdapter.VH>() {

    inner class VH(val b: ItemOnboardingBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemOnboardingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.b.tvTitle.text = item.title
        holder.b.tvDesc.text = item.desc
    }

    override fun getItemCount() = items.size
}
