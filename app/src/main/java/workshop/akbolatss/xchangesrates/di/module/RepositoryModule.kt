package workshop.akbolatss.xchangesrates.di.module

import org.koin.dsl.module
import workshop.akbolatss.xchangesrates.data.repository.ChartRepositoryImpl

val repositoryModule = module {

    single {
        ChartRepositoryImpl(get(), get())
    }
}
