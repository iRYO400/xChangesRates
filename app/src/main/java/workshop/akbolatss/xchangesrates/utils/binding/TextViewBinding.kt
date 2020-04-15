package workshop.akbolatss.xchangesrates.utils.binding

import android.widget.TextView
import androidx.annotation.StringRes
import androidx.databinding.BindingAdapter
import workshop.akbolatss.xchangesrates.utils.extension.getRelative
import java.util.*

@BindingAdapter("android:text")
fun TextView.setTextRes(@StringRes text: Int) {
    this.text = context.getString(text)
}

@BindingAdapter("dateRelative")
fun TextView.setRelativeDate(date: Date) {
    this.text = date.getRelative()
}
