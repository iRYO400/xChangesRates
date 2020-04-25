package workshop.akbolatss.xchangesrates.di.module

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import workshop.akbolatss.xchangesrates.presentation.chart.ChartFragment
import workshop.akbolatss.xchangesrates.presentation.chart.ChartViewModel
import workshop.akbolatss.xchangesrates.presentation.root.RootActivity
import workshop.akbolatss.xchangesrates.presentation.root.RootViewModel
import workshop.akbolatss.xchangesrates.presentation.snapshots.SnapshotsFragment
import workshop.akbolatss.xchangesrates.presentation.snapshots.SnapshotsViewModel
import workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.details.SnapshotDetailsBottomDialog
import workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.details.SnapshotDetailsViewModel
import workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.options.SnapshotOptionsDialog
import workshop.akbolatss.xchangesrates.presentation.snapshots.dialog.options.SnapshotOptionsViewModel
import workshop.akbolatss.xchangesrates.presentation.splash.SplashActivity
import workshop.akbolatss.xchangesrates.presentation.splash.SplashViewModel

val viewModelModule: Module = module {

    scope(named<SplashActivity>()) {
        viewModel {
            SplashViewModel(get())
        }
    }

    scope(named<RootActivity>()) {
        viewModel { RootViewModel() }
    }

    scope(named<SnapshotsFragment>()) {
        viewModel { SnapshotsViewModel(get(), get(), get()) }
    }

    scope(named<SnapshotOptionsDialog>()) {
        viewModel { (itemId: Long) ->
            SnapshotOptionsViewModel(get(), get(), itemId)
        }
    }

    scope(named<ChartFragment>()) {
        viewModel {
            ChartViewModel(get(), get(), get())
        }
    }

    scope(named<SnapshotDetailsBottomDialog>()) {
        viewModel { (itemId: Long) ->
            SnapshotDetailsViewModel(get(), get(), itemId)
        }
    }
}
