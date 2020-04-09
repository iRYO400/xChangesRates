package workshop.akbolatss.xchangesrates.di.module

import org.koin.dsl.module
import workshop.akbolatss.xchangesrates.data.repository.ChartRepositoryImpl
import workshop.akbolatss.xchangesrates.data.repository.ExchangeRepositoryImpl
import workshop.akbolatss.xchangesrates.data.repository.SnapshotRepositoryImpl
import workshop.akbolatss.xchangesrates.domain.repository.ChartRepository
import workshop.akbolatss.xchangesrates.domain.repository.ExchangeRepository
import workshop.akbolatss.xchangesrates.domain.repository.SnapshotRepository

val repositoryModule = module {

    single {
        ChartRepositoryImpl(get(), get()) as ChartRepository
    }

    single {
        ExchangeRepositoryImpl(get(), get()) as ExchangeRepository
    }

    single {
        SnapshotRepositoryImpl(get()) as SnapshotRepository
    }
}
