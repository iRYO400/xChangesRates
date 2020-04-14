package workshop.akbolatss.xchangesrates.utils.binding

import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.utils.widget.spinner.MaterialSpinnerAdapter

@BindingAdapter("entriesSpinner")
fun AutoCompleteTextView.bindSpinnerAdapter(
    entries: List<Any>?
) {
    val safeEntries = entries ?: emptyList()
    val adapter =
        MaterialSpinnerAdapter(context, R.layout.custom_spinner_dropdown_item, safeEntries)
    setAdapter(adapter)
}


@get:InverseBindingAdapter(attribute = "selectedSpinnerEntry")
@set:BindingAdapter("selectedSpinnerEntry")
var AutoCompleteTextView.selectedSpinnerEntry: Any?
    get() = getTag(R.id.selected_item_position)?.let { position ->
        val positionInt = position as Int
        if (adapter.count > positionInt)
            adapter.getItem(positionInt)
        else
            null
    }
    set(value) {
        value?.let {
            setText(value.toString(), true)
        }
    }

@BindingAdapter("selectedSpinnerEntryAttrChanged")
fun AutoCompleteTextView.setSpinnerListener(listener: InverseBindingListener?) {
    listener?.let {
        this.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            setTag(R.id.selected_item_position, position)
            listener.onChange()
        }
    }
}
