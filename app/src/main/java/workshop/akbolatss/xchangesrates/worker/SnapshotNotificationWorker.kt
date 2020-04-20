package workshop.akbolatss.xchangesrates.worker

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import workshop.akbolatss.xchangesrates.R
import workshop.akbolatss.xchangesrates.base.resource.flatMap
import workshop.akbolatss.xchangesrates.base.resource.map
import workshop.akbolatss.xchangesrates.domain.model.Snapshot
import workshop.akbolatss.xchangesrates.domain.usecase.FindSnapshotByIdUseCase
import workshop.akbolatss.xchangesrates.domain.usecase.UpdateSnapshotUseCase
import workshop.akbolatss.xchangesrates.utils.extension.defaultVal
import workshop.akbolatss.xchangesrates.utils.extension.generateNotificationTitle
import workshop.akbolatss.xchangesrates.utils.extension.generateSnapshotName

class SnapshotNotificationWorker(
    appContext: Context, workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams),
    KoinComponent {

    companion object {
        const val WORKER_SNAPSHOT_ID = "_workerSnapshotId"
        const val NOTIFICATION_GROUP_ID = "workshop.akbolatss.xchangesrates.group"
    }

    private val findSnapshotByIdUseCase: FindSnapshotByIdUseCase by inject()
    private val updateSnapshotUseCase: UpdateSnapshotUseCase by inject()

    private val notificationManager: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(appContext)
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val snapshotId = inputData.getLong(WORKER_SNAPSHOT_ID, defaultVal())
        if (snapshotId == defaultVal())
            return@withContext Result.failure()

        Timber.d("Beginning")

        updateSnapshotUseCase(this, UpdateSnapshotUseCase.Params(snapshotId))
            .flatMap {
                Timber.d("findSnapshotByIdUseCase called ")
                findSnapshotByIdUseCase(this, FindSnapshotByIdUseCase.Params(snapshotId))
            }.map { snapshot ->
                Timber.d("assembleNotification called ")
                pushNotification(snapshot)
            }

        Timber.d("return result")
        return@withContext Result.failure()
    }

    private fun pushNotification(snapshot: Snapshot) {
        val notification = assembleNotification(snapshot)
        notificationManager.notify(snapshot.id.toInt(), notification)
    }

    private fun assembleNotification(snapshot: Snapshot): Notification {
        val inboxStyle = assembleInboxStyle(snapshot)
        return NotificationCompat.Builder(applicationContext, snapshot.generateSnapshotName())
            .apply {
                setContentTitle(snapshot.generateNotificationTitle())
                setContentText(snapshot.rate.toPlainString())
                setAutoCancel(false)
                setStyle(inboxStyle)
                setSmallIcon(R.drawable.ic_xchange_ic)
                setGroup(NOTIFICATION_GROUP_ID)
                setOngoing(true)
                priority = if (snapshot.options.isStick)
                    NotificationCompat.PRIORITY_LOW
                else
                    NotificationCompat.PRIORITY_MIN
            }.build()
    }

    private fun assembleInboxStyle(snapshot: Snapshot): NotificationCompat.InboxStyle {
        val resources = applicationContext.resources
        val inboxStyle = NotificationCompat.InboxStyle()
        inboxStyle.addLine(
            "${resources.getString(R.string.notification_snapshot_high)} ${snapshot.high.toPlainString()} | " +
                    "${resources.getString(R.string.notification_snapshot_low)} ${snapshot.low.toPlainString()}"
        )
        inboxStyle.addLine(
            "${resources.getString(R.string.notification_snapshot_change)} ${snapshot.change.toPlainString()} | " +
                    "${resources.getString(R.string.notification_snapshot_change24)} ${snapshot.change24.toPlainString()}"
        )
//        inboxStyle.addLine()//TODO buy/sell
//        inboxStyle.addLine()//TODO volume
        return inboxStyle
    }

}
