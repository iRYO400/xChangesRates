package workshop.akbolatss.xchangesrates.di.module

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import workshop.akbolatss.xchangesrates.presentation.root.RootActivity
import workshop.akbolatss.xchangesrates.presentation.root.RootViewModel
import workshop.akbolatss.xchangesrates.screens.snapshots.SnapshotsFragment
import workshop.akbolatss.xchangesrates.screens.snapshots.SnapshotsViewModel
import workshop.akbolatss.xchangesrates.screens.snapshots.dialog.options.SnapshotOptionsDialog
import workshop.akbolatss.xchangesrates.screens.snapshots.dialog.options.SnapshotOptionsViewModel

val viewModelModule: Module = module {
    scope(named<RootActivity>()) {
        viewModel { RootViewModel() }
    }

    scope(named<SnapshotsFragment>()) {
        viewModel { SnapshotsViewModel(get(), get(), get()) }
    }

    scope(named<SnapshotOptionsDialog>()) {
        viewModel { (itemId: Long?) ->
            SnapshotOptionsViewModel(get(), itemId)
        }
    }
}
