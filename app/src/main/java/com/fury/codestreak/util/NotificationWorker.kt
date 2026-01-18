package com.fury.codestreak.presentation.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.fury.codestreak.MainActivity
import com.fury.codestreak.R

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.d("CodeStreak", "Worker Started: Attempting to send notification...")
        sendSassyNotification()
        return Result.success()
    }

    private fun sendSassyNotification() {
        val context = applicationContext
        val channelId = "codestreak_sassy_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Daily Reminders"
            val descriptionText = "Sassy coding reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val messages = listOf(
            "Your streak is dying. Do something! 😱",
            "Coding > Scrolling. You know it's true. 👨‍💻",
            "I saw you online... but not coding. 👀",
            "Don't make me reset your streak to 0. 🔥",
            "Future You is disappointed you haven't coded yet. 📉",
            "Knock knock. Who's there? A broken streak. 🚪"
        )
        val message = messages.random()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("CodeStreak Alert 🚨")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                NotificationManagerCompat.from(context).notify(1001, builder.build())
                Log.d("CodeStreak", "Notification SENT SUCCESS! ✅")
            } else {
                Log.e("CodeStreak", "Notification FAILED: Permission denied ❌")
            }
        } catch (e: Exception) {
            Log.e("CodeStreak", "Notification FAILED: Exception ${e.message}")
            e.printStackTrace()
        }
    }
}