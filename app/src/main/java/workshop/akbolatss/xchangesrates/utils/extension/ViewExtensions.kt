package workshop.akbolatss.xchangesrates.utils.extension

import android.content.Context
import android.graphics.Color
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.snackbar.Snackbar

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun CoordinatorLayout.showSnackBar(messageRes: Int) {
    val snackbar = Snackbar.make(
        this, messageRes,
        Snackbar.LENGTH_LONG
    )
    snackbar.show()
}

fun CoordinatorLayout.showSnackBar(message: String?) {
    val safeMessage = message ?: return
    val snackbar = Snackbar.make(
        this, safeMessage,
        Snackbar.LENGTH_LONG
    )
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

fun View.visible() = apply {
    this.visibility = View.VISIBLE
}

fun View.gone() = apply {
    this.visibility = View.GONE
}

fun View.invisible() = apply {
    this.visibility = View.INVISIBLE
}

fun Context.dpToPx(dp: Float): Float =
    dp * resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT

fun Context.pxToDp(px: Float): Float =
    px / (resources.displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
