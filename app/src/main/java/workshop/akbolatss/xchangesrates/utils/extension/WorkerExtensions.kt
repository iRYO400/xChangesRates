package workshop.akbolatss.xchangesrates.utils.extension

import android.content.Context
import androidx.work.*
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.screens.notifications.SnapshotNotificationWorker
import java.util.concurrent.TimeUnit

fun Context?.launchWorker(snapshot: Snapshot) {
    this?.let {
        val constraints = Constraints.Builder().apply {
            setRequiredNetworkType(NetworkType.CONNECTED)
            setRequiresCharging(false)
        }.build()

        val inputData = Data.Builder().apply {
            putLong(SnapshotNotificationWorker.WORKER_SNAPSHOT_ID, snapshot.id)
        }.build()

        val notificationWorker =
            PeriodicWorkRequestBuilder<SnapshotNotificationWorker>(
                snapshot.options.updateInterval.seconds,
                TimeUnit.SECONDS
            ).apply {
                setConstraints(constraints)
                setInputData(inputData)
                setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)
            }.build()

        WorkManager.getInstance(it).enqueueUniquePeriodicWork(
            snapshot.generateSnapshotName(),
            ExistingPeriodicWorkPolicy.REPLACE,
            notificationWorker
        )
    }
}

fun Context?.cancelWorker(snapshot: Snapshot) {
    this?.let {
        WorkManager.getInstance(it).cancelUniqueWork(snapshot.generateSnapshotName())
    }
}
