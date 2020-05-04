package workshop.akbolatss.xchangesrates.presentation.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.databinding.ActivitySplashBinding
import workshop.akbolatss.xchangesrates.presentation.root.RootActivity
import workshop.akbolatss.xchangesrates.utils.extension.gone
import workshop.akbolatss.xchangesrates.utils.extension.showSnackBar

class SplashActivity : AppCompatActivity() {

    private val viewModel by currentScope.viewModel<SplashViewModel>(this)

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.exchangersUpdated.observe(this, Observer {
            it?.let {
                startActivity(Intent(this@SplashActivity, RootActivity::class.java))
                finish()
            }
        })
        viewModel.exchangersUpdatedError.observe(this, Observer {
            it?.let {
                binding.tvStatus.gone()
                binding.progressBar.gone()
                binding.coordinator.showSnackBar(getString(R.string.splash_updating_database_error))
            }
        })
        viewModel.exitApp.observe(this, Observer {
            finish()
        })
    }

}
