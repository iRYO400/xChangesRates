package workshop.akbolatss.xchangesrates.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import workshop.akbolatss.xchangesrates.presentation.root.RootActivity
import workshop.akbolatss.xchangesrates.presentation.root.RootViewModel

val viewModelModule: Module = module {
    scope(named<RootActivity>()) {
        viewModel { RootViewModel() }
    }
}
