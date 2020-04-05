package workshop.akbolatss.xchangesrates.base.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import workshop.akbolatss.xchangesrates.R

abstract class BaseDialogFragment<DB : ViewDataBinding>(
        private val layoutId: Int,
        private val windowWidth: Int? = WindowManager.LayoutParams.WRAP_CONTENT,
        private val windowHeight: Int? = WindowManager.LayoutParams.WRAP_CONTENT
) : AppCompatDialogFragment() {

    protected open lateinit var binding: DB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_RoundedDialog)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setWindowOptions()
    }

    protected open fun setWindowOptions() {
        val window = dialog!!.window ?: return
        if (windowWidth == null && windowHeight == null)
            return
        val params = WindowManager.LayoutParams()
        params.copyFrom(window.attributes)
        if (windowWidth != null)
            params.width = windowWidth
        if (windowHeight != null)
            params.height = windowHeight
        window.attributes = params
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        setDialogProperties(dialog)
        return dialog
    }

    protected open fun setDialogProperties(dialog: Dialog) {
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window
        window?.let { setDialogWindowProperties(it) }
    }

    open fun setDialogWindowProperties(window: Window) {
        window.requestFeature(Window.FEATURE_NO_TITLE)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val viewRoot =
                inflater.inflate(R.layout.dialog_holder, container, false) as ViewGroup
        inflateContainer(inflater, viewRoot, savedInstanceState)
        return viewRoot
    }

    private fun inflateContainer(
            inflater: LayoutInflater,
            newContainer: ViewGroup,
            savedInstanceState: Bundle?
    ) {
        val contentView = inflateView(inflater, newContainer, savedInstanceState)
        if (newContainer.indexOfChild(contentView) == -1) newContainer.addView(contentView)
    }

    private fun inflateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
                inflater,
                layoutId,
                container,
                false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        init(savedInstanceState)
        return binding.root
    }

    open fun init(savedInstanceState: Bundle?) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserversListeners()
    }

    abstract fun setObserversListeners()
}
