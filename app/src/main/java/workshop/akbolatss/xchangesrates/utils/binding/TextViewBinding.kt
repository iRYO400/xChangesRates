package workshop.akbolatss.xchangesrates.utils.binding

import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter

@BindingAdapter("android:text")
fun TextView.setTextRes(@StringRes text: Int) {
    this.text = context.getString(text)
}
