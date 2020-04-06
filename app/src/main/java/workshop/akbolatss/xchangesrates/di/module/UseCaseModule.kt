package workshop.akbolatss.xchangesrates.di.module

import org.koin.dsl.module
import workshop.akbolatss.xchangesrates.domain.usecase.FindAllSnapshotsUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.UpdateDataUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.UpdateSingleSnapshotUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.UpdateSnapshotListUseCase

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
        UpdateDataUseCase(get())
    }
}
