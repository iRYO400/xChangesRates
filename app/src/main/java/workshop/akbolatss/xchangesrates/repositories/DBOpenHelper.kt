package workshop.akbolatss.xchangesrates.repositories

import android.content.Context

import org.greenrobot.greendao.database.Database

import workshop.akbolatss.xchangesrates.model.dao.DaoMaster

/**
 * Author: Akbolat Sadvakassov
 * Date: 25.01.2018
 */

class DBOpenHelper(context: Context, name: String) : DaoMaster.OpenHelper(context, name) {

    override fun onUpgrade(db: Database?, oldVersion: Int, newVersion: Int) {
        super.onUpgrade(db, oldVersion, newVersion)

        when (oldVersion) {
            1//version one
            -> {
            }
            2//version two
            -> {
            }
        }
    }
}
