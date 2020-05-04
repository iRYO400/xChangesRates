package workshop.akbolatss.xchangesrates.utils.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import timber.log.Timber.DebugTree
import workshop.akbolatss.xchangesrates.BuildConfig

object TimberLogImplementation {
    fun init() {
        if (BuildConfig.DEBUG) Timber.plant(DevelopmentTree()) else Timber.plant(
            ProductionTree()
        )
    }
}

private class DevelopmentTree : DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return String.format(
            "xChangesRates:C:%s:%s",
            super.createStackElementTag(element),
            element.lineNumber
        )
    }

    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        super.log(priority, tag, message, t)
        if (t != null && (priority == Log.ERROR || priority == Log.WARN))
            FirebaseCrashlytics.getInstance().recordException(t)
        else FirebaseCrashlytics.getInstance()
            .log("$tag: Exception with `null` Throwable. Message $message")
    }
}

private class ProductionTree : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?
    ) {
        if (t != null && (priority == Log.ERROR || priority == Log.WARN))
            FirebaseCrashlytics.getInstance().recordException(t)
        else FirebaseCrashlytics.getInstance()
            .log("$tag: Exception with `null` Throwable. Message $message")
    }
}
