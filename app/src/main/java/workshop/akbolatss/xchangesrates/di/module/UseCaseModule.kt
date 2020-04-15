package workshop.akbolatss.xchangesrates.di.module

import org.koin.dsl.module
import workshop.akbolatss.xchangesrates.domain.usecase.*

val useCaseModule = module {
    factory {
        FindAllSnapshotsUseCase(get())
    }
    factory {
        DownloadExchangesUseCase(get())
    }

    factory {
        LoadExchangesUseCase(get())
    }
    factory {
        DownloadChartUseCase(get())
    }

    factory {
        CreateSnapshotUseCase(get())
    }
    factory {
        CreateOrUpdateSnapshotUseCase(get(), get())
    }
    factory {
        UpdateSnapshotUseCase(get(), get())
    }
    factory {
        ToggleNotificationUseCase(get())
    }
}
