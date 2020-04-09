package workshop.akbolatss.xchangesrates.di.module

import org.koin.dsl.module
import workshop.akbolatss.xchangesrates.domain.usecase.*

val useCaseModule = module {
    factory {
        FindAllSnapshotsUseCase(get())
    }
    factory {
        UpdateSingleSnapshotUseCase(get())
    }
    factory {
        UpdateSnapshotListUseCase(get(), get())
    }

    factory {
        DownloadExchangesUseCase(get())
    }

    factory {
        LoadExchangesUseCase(get())
    }
    factory {
        LoadChartUseCase(get())
    }
}
