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

    var selectedPeriodPosition: Int = -1

    enum class PeriodSelectorPayload { ITEM_SELECTED, ITEM_UNSELECTED }

    override fun createBinding(parent: ViewGroup, viewType: Int): ViewDataBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.rv_btn,
            parent,
            false
        )
    }

    override fun onBindViewHolder(
        holder: DataBoundViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty())
            super.onBindViewHolder(holder, position, payloads)
        else
            bindPayloads(holder, payloads)
    }

    override fun bind(holder: DataBoundViewHolder, item: ChartPeriod) {
        when (holder.binding) {
            is RvBtnBinding -> with(holder.binding) {
                model = item

                btnHistory.setOnClickListener {
                    itemClickListener(model, holder.layoutPosition)
                }

                btnHistory.isSelected = selectedPeriodPosition == holder.layoutPosition
            }
        }
    }

    private fun bindPayloads(holder: DataBoundViewHolder, payloads: MutableList<Any>) {
        when (holder.binding) {
            is RvBtnBinding -> with(holder.binding) {
                if (payloads.contains(PeriodSelectorPayload.ITEM_SELECTED))
                    btnHistory.isSelected = true
                else if (payloads.contains(PeriodSelectorPayload.ITEM_UNSELECTED))
                    btnHistory.isSelected = false
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
