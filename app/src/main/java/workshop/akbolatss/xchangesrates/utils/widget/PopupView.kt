package workshop.akbolatss.xchangesrates.utils.widget

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import workshop.akbolatss.xchangesrates.databinding.HolderPopupViewBinding

class PopupView(anchor: View, message: String) {

    private val popupWindow: PopupWindow

    companion object {
        fun show(anchor: View, message: String) {
            PopupView(anchor, message)
        }
    }

    init {
        val binding = HolderPopupViewBinding.inflate(LayoutInflater.from(anchor.context))
        binding.tvMessage.text = message
        popupWindow = PopupWindow(binding.root, WRAP_CONTENT, WRAP_CONTENT)
        popupWindow.isFocusable = true
        setDismissListener()
        popupWindow.showAsDropDown(anchor)
    }

    private fun setDismissListener() {
        popupWindow.setTouchInterceptor { _, event ->
            val dismissPopup = event.action == MotionEvent.ACTION_OUTSIDE
            if (dismissPopup)
                popupWindow.dismiss()
            dismissPopup
        }
    }
}
