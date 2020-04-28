package workshop.akbolatss.xchangesrates.utils.logging;

import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;
import workshop.akbolatss.xchangesrates.BuildConfig;

public class TimberLogImplementation {

    public static void init() {
        if (BuildConfig.DEBUG)
            Timber.plant(new UmagPosDebugTree());
        else
            Timber.plant(new ProductionTree());
    }

    public static class UmagPosDebugTree extends Timber.DebugTree {
        @Override
        protected @Nullable String createStackElementTag(@NotNull StackTraceElement element) {
            return String.format("xChangesRates:C:%s:%s",
                    super.createStackElementTag(element),
                    element.getLineNumber());
        }

        @Override
        protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
            super.log(priority, tag, message, t);

            if (priority == Log.ERROR || priority == Log.WARN)
                if (t != null)
                    FirebaseCrashlytics.getInstance().recordException(t);
                else
                    FirebaseCrashlytics.getInstance().log(tag + ": Exception with `null` Throwable. Message " + message);
        }
    }

    public static class ProductionTree extends Timber.Tree {

        @Override
        protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
            if (priority == Log.ERROR || priority == Log.WARN)
                if (t != null)
                    FirebaseCrashlytics.getInstance().recordException(t);
                else
                    FirebaseCrashlytics.getInstance().log(tag + ": Exception with `null` Throwable. Message " + message);
        }
    }
}
