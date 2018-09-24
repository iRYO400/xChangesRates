package workshop.akbolatss.xchangesrates.utils

import android.util.Log
import workshop.akbolatss.xchangesrates.BuildConfig

/**
 * Logcat helper. Used only in debug mode
 */
class Logger {
    companion object {
        fun i(name: String, msg: String) {
            if (BuildConfig.DEBUG) {
                Log.d(name, msg)
            }
        }

        fun i(s: String) {
            if (BuildConfig.DEBUG) {
                val maxLogSize = 1000
                for (i in 0..s.length / maxLogSize) {
                    val start = i * maxLogSize
                    var end = (i + 1) * maxLogSize
                    end = if (end > s.length) s.length else end
                    Log.d("XChangesRate, Debug", s.substring(start, end))
                }
            }
        }

        fun e(s: String) {
            if (BuildConfig.DEBUG) {
                val maxLogSize = 1000
                for (i in 0..s.length / maxLogSize) {
                    val start = i * maxLogSize
                    var end = (i + 1) * maxLogSize
                    end = if (end > s.length) s.length else end
                    Log.e("XChangesRate, Error", s.substring(start, end))
                }
            }
        }
    }
}