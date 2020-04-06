package workshop.akbolatss.xchangesrates.screens.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import org.koin.androidx.scope.currentScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.resource.LoadingState
import workshop.akbolatss.xchangesrates.presentation.root.RootActivity

class SplashActivity : AppCompatActivity() {

    private val viewModel by currentScope.viewModel<SplashViewModel>(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.success.observe(this, Observer {
            if (it) {
                startActivity(Intent(this@SplashActivity, RootActivity::class.java))
                finish()
            }
        })
        viewModel.error.observe(this, Observer {
            if (it)
                finish()
        })
        viewModel.loadingState.observe(this, Observer {
            it?.let {
                when (it) {
                    is LoadingState.Loading -> {
                        // TODO loading
                    }
                    is LoadingState.Ready -> {
                        // TODO Idle?
                    }
                }
            }
        })
    }

}
