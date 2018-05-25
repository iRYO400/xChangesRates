package workshop.akbolatss.xchangesrates.utils

import android.util.Log
import workshop.akbolatss.xchangesrates.BuildConfig

/**
 * Logcat helper. Used only in debug mode
 */
class Logger {
    companion object {

        private const val TAG = "XChangesRate"

        fun l(msg: String) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, msg)
            }
        }

        fun l(name: String, msg: String) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "$name: $msg")
            }
        }

        fun i(name: String, msg: String) {
            if (BuildConfig.DEBUG) {
                Log.d(name, msg)
            }
        }

        fun i(s: String) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, s)
            }
        }

        fun e(name: String, msg: String) {
            if (BuildConfig.DEBUG) {
                Log.e(name, msg)
            }
        }

        fun e(s: String) {
            if (BuildConfig.DEBUG) {
                Log.e(TAG, s)
            }
        }
    }
}