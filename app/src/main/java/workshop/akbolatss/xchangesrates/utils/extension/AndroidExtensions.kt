package workshop.akbolatss.xchangesrates.utils.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
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
