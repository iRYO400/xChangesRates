package workshop.akbolatss.xchangesrates.screens.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.presentation.base.Error
import workshop.akbolatss.xchangesrates.presentation.base.Loading
import workshop.akbolatss.xchangesrates.presentation.base.Success
import workshop.akbolatss.xchangesrates.presentation.root.RootActivity

class SplashActivity : AppCompatActivity() {

    private val viewModel by currentScope.viewModel<SplashViewModel>(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(this, Observer {
            it?.let {
                when (it) {
                    is Loading -> {
                        Timber.d("Loading")
                    }
                    is Success<*> -> {
                        startActivity(Intent(this@SplashActivity, RootActivity::class.java))
                        finish()
                    }
                    is Error -> {
                        finish()
                    }
                }
            }
        })
    }

}
