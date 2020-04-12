package workshop.akbolatss.xchangesrates.di.module

import android.app.Application
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import workshop.akbolatss.xchangesrates.data.persistent.AppDataBase
import workshop.akbolatss.xchangesrates.utils.Constants

val daoModule = module {
    factory { get<AppDataBase>().exchangeDao() }
    factory { get<AppDataBase>().snapshotDao() }
}
val persistentModule = module {
    single {
        createRoom(androidApplication())
    }
}

fun createRoom(androidApplication: Application): AppDataBase {
    return Room.databaseBuilder(
        androidApplication,
        AppDataBase::class.java,
        Constants.DB_SNAPS_NAME_NEW
    )
        .build()
}
