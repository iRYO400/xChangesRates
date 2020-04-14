package workshop.akbolatss.xchangesrates.utils.binding

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.utils.extension.hideKeyboard
import java.util.*

@BindingAdapter("selectedEntryAttrChanged")
fun AutoCompleteTextView.setListener(listener: InverseBindingListener?) {
    listener?.let {
        this.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            setTag(R.id.selected_item_position, position)
            listener.onChange()
        }
    }
}

@get:InverseBindingAdapter(attribute = "selectedEntry")
@set:BindingAdapter("selectedEntry")
var AutoCompleteTextView.selectedEntry: Any?
    get() = getTag(R.id.selected_item_position)?.let { position ->
        val positionInt = position as Int
        if (adapter.count > positionInt)
            adapter.getItem(positionInt)
        else
            null
    }
    set(value) {
        value?.let {
            setText(value.toString().toUpperCase(Locale.getDefault()), false)
        }
        clearFocus()
        hideKeyboard()
    }

@BindingAdapter("entries")
fun AutoCompleteTextView.bindAdapter(
    entries: List<Any>?
) {
    val safeEntries = entries ?: emptyList()
    val adapter = ArrayAdapter(context, R.layout.custom_spinner_dropdown_item, safeEntries)
    setAdapter(adapter)
    adapter.notifyDataSetChanged()
}

@BindingAdapter("showDropDownOnClick")
fun AutoCompleteTextView.showDropDownOnClick(show: Boolean) {
    if (show) {
        setOnClickListener {
            this.showDropDown()
        }
    }
}
