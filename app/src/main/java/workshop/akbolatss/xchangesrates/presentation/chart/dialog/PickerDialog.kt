package workshop.akbolatss.xchangesrates.presentation.chart.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.dialog.BaseDialogFragment
import workshop.akbolatss.xchangesrates.databinding.DialogItemPickerBinding


enum class PICKER_TYPE {
    EXCHANGER,
    COIN,
    CURRENCY
}

class PickerDialog : BaseDialogFragment<DialogItemPickerBinding>(
    layoutId = R.layout.dialog_item_picker
) {

    companion object {

        private const val BUNDLE_ENUM = "BundleEnum"
        private const val BUNDLE_LIST = "BundleList"
        private const val BUNDLE_POS = "BundlePosition"

        fun newInstance(
            pickType: PICKER_TYPE,
            ids: ArrayList<String>,
            position: Int
        ): PickerDialog {
            val fragment =
                PickerDialog()
            val arg = Bundle()
            arg.putSerializable(BUNDLE_ENUM, pickType)
            arg.putStringArrayList(BUNDLE_LIST, ids)
            arg.putInt(BUNDLE_POS, position)
            fragment.arguments = arg
            return fragment
        }
    }

    private val viewModel by currentScope.viewModel<ItemPickerViewModel>(this)

    private lateinit var callback: OnItemPickerCallback
    private lateinit var mList: ArrayList<String>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.dialog_item_picker, null)

        val alertDialogBuilder = AlertDialog.Builder(activity!!, R.style.CustomDialog)
        alertDialogBuilder.setView(view)

        mList = arguments!!.getStringArrayList(BUNDLE_LIST) as ArrayList<String>

        val numberPicker = view.findViewById<NumberPicker>(R.id.spinPicker)
        numberPicker.minValue = 0
        numberPicker.maxValue = mList.size - 1
        numberPicker.wrapSelectorWheel = true
        numberPicker.displayedValues = mList.toTypedArray()
//        numberPicker.setFormatter {
//            return@setFormatter mList[it]
//        }

        val pos = arguments!!.getInt(BUNDLE_POS)
        numberPicker.value = pos

        alertDialogBuilder.setPositiveButton(R.string.alert_save) { dialog, which ->
            val mListener = targetFragment as OnItemPickerCallback
            val type = arguments!!.get(BUNDLE_ENUM) as PICKER_TYPE
            mListener.itemSelected(numberPicker.value, mList[numberPicker.value], type)
        }

        val dialog = alertDialogBuilder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    interface OnItemPickerCallback {

        fun itemSelected(intValue: Int, stringValue: String, pickType: PICKER_TYPE)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is OnItemPickerCallback)
            callback = parentFragment as OnItemPickerCallback
        else
            throw RuntimeException("Must implement OnItemPickerCallback")
    }

    override fun init(savedInstanceState: Bundle?) {
        super.init(savedInstanceState)

    }

    override fun setObserversListeners() {

    }
}
