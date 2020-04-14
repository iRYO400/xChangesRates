package workshop.akbolatss.xchangesrates.utils.extension

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun CoordinatorLayout.showSnackBar(messageRes: Int) {
    val snackbar = Snackbar.make(
        this, messageRes,
        Snackbar.LENGTH_LONG
    )
//    snackbar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
//        .textAlignment = View.TEXT_ALIGNMENT_CENTER
    snackbar.show()
}


@ColorInt
fun Context.getThemeColorV2(@AttrRes attribute: Int): Int {
    return TypedValue().let {
        theme.resolveAttribute(attribute, it, true)
        it.data
    }
}

@ColorInt
fun Context.getThemeColor(@AttrRes attribute: Int): Int {
    val typedArray = obtainStyledAttributes(intArrayOf(attribute))
    val color = typedArray.getColor(0, Color.MAGENTA)
    typedArray.recycle()
    return color
}
