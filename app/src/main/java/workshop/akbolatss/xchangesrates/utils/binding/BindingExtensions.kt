package workshop.akbolatss.xchangesrates.utils.binding

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("isSelected")
fun View.isSelected(isSelected: Boolean) {
    this.isSelected = isSelected
}
