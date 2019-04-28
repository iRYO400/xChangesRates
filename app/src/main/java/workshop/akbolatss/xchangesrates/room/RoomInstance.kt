package workshop.akbolatss.xchangesrates.room

import android.arch.persistence.room.Room
import android.content.Context
import workshop.akbolatss.xchangesrates.utils.Constants

class RoomInstance {

    companion object {
        private var appDatabase: AppDataBase? = null

        fun get(context: Context): AppDataBase {
            if (appDatabase == null)
                appDatabase = newInstance(context)
            return appDatabase as AppDataBase
        }

        private fun newInstance(context: Context): AppDataBase {
            return Room.databaseBuilder(context, AppDataBase::class.java, Constants.DB_SNAPS_NAME_NEW)
                    .build()
        }
    }
}