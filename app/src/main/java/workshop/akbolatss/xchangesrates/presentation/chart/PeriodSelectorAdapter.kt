package workshop.akbolatss.xchangesrates.presentation.chart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.BaseRVA
import workshop.akbolatss.xchangesrates.base.DataBoundViewHolder
import workshop.akbolatss.xchangesrates.databinding.RvBtnBinding
import workshop.akbolatss.xchangesrates.presentation.model.ChartPeriod

class PeriodSelectorAdapter(
    private val itemClickListener: (ChartPeriod, Int) -> Unit
) : BaseRVA<ChartPeriod>(DIFF_CALLBACK) {

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rv_btn,
            parent,
            false
        )
    }

    override fun bind(holder: DataBoundViewHolder, item: ChartPeriod) {
        when (holder.binding) {
            is RvBtnBinding -> with(holder.binding) {
                model = item

                btnHistory.setOnClickListener {
                    itemClickListener(item, holder.layoutPosition)
                }

                btnHistory.isSelected = item.isSelected
            }
        }
    }

}

private val DIFF_CALLBACK: DiffUtil.ItemCallback<ChartPeriod> =
    object : DiffUtil.ItemCallback<ChartPeriod>() {

        override fun areItemsTheSame(
            oldItem: ChartPeriod,
            newItem: ChartPeriod
        ): Boolean =
            oldItem.code == newItem.code


        override fun areContentsTheSame(
            oldItem: ChartPeriod,
            newItem: ChartPeriod
        ): Boolean =
            oldItem.code == newItem.code

    }
