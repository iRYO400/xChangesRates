package workshop.akbolatss.xchangesrates.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import me.yokeyword.fragmentation.SupportFragment
import org.koin.core.KoinComponent

abstract class BaseFragment<DB : ViewDataBinding> : SupportFragment(), KoinComponent {

    protected open lateinit var binding: DB

    protected abstract val layoutId: Int

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)
        init(savedInstanceState)
        return binding.root
    }

    fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
    }

    protected open fun init(savedInstanceState: Bundle?) = Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObserversListeners()
    }

    abstract fun setObserversListeners()
}
