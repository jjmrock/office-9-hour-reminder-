package com.example.officeexittimer

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.format.DateFormat
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import java.util.Date

class MainActivity : android.app.Activity() {

    private lateinit var statusText: TextView
    private lateinit var timeText: TextView
    private lateinit var startButton: Button
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        buildUi()
        requestNotificationPermissionIfNeeded()
        refreshUi()
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_HORIZONTAL
            setPadding(48, 70, 48, 48)
        }

        val title = TextView(this).apply {
            text = "OFFICE EXIT TIMER"
            textSize = 25f
            gravity = Gravity.CENTER
        }

        statusText = TextView(this).apply {
            textSize = 18f
            gravity = Gravity.CENTER
            setPadding(0, 50, 0, 12)
        }

        timeText = TextView(this).apply {
            textSize = 17f
            gravity = Gravity.CENTER
            setPadding(0, 0, 0, 40)
        }

        startButton = Button(this).apply {
            text = "START 9-HOUR TIMER"
            textSize = 17f
            setOnClickListener { startTimer() }
        }

        cancelButton = Button(this).apply {
            text = "CANCEL TIMER"
            textSize = 17f
            setOnClickListener { cancelTimer() }
        }

        root.addView(title)
        root.addView(statusText)
        root.addView(timeText)
        root.addView(startButton)
        root.addView(cancelButton)

        setContentView(root)
    }

    private fun startTimer() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Android 12+ exact alarms require special access.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms()
        ) {
            Toast.makeText(
                this,
                "Allow 'Alarms & reminders', then press Start again.",
                Toast.LENGTH_LONG
            ).show()

            startActivity(
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    .setData(android.net.Uri.parse("package:$packageName"))
            )
            return
        }

        requestNotificationPermissionIfNeeded()

        val start = System.currentTimeMillis()
        val leaveAt = start + NINE_HOURS_MS

        TimerStorage.save(this, start, leaveAt)
        AlarmScheduler.schedule(this, leaveAt)

        Toast.makeText(
            this,
            "Reminder scheduled for ${formatTime(leaveAt)}",
            Toast.LENGTH_LONG
        ).show()

        refreshUi()
    }

    private fun cancelTimer() {
        AlarmScheduler.cancel(this)
        TimerStorage.clear(this)
        Toast.makeText(this, "Timer cancelled", Toast.LENGTH_SHORT).show()
        refreshUi()
    }

    private fun refreshUi() {
        val leaveAt = TimerStorage.getLeaveAt(this)

        if (leaveAt != null && leaveAt > System.currentTimeMillis()) {
            statusText.text = "Timer is running"
            timeText.text = "Leave office at: ${formatDateTime(leaveAt)}"
            startButton.text = "RESET — START 9-HOUR TIMER"
        } else {
            if (leaveAt != null) {
                TimerStorage.clear(this)
            }
            statusText.text = "No timer is running"
            timeText.text = "Tap Start when you arrive at work."
            startButton.text = "START 9-HOUR TIMER"
        }
    }

    private fun formatTime(millis: Long): String {
        return DateFormat.getTimeFormat(this).format(Date(millis))
    }

    private fun formatDateTime(millis: Long): String {
        return DateFormat.getDateFormat(this).format(Date(millis)) +
                "  " + DateFormat.getTimeFormat(this).format(Date(millis))
    }

    private fun createNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java)

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Office Exit Reminder",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminder to leave the office after 9 hours"
            enableVibration(true)
        }

        manager.createNotificationChannel(channel)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATION_PERMISSION_REQUEST
            )
        }
    }

    companion object {
        const val CHANNEL_ID = "office_exit_reminder"
        const val NOTIFICATION_ID = 9001
        const val ALARM_REQUEST_CODE = 9002
        const val NINE_HOURS_MS = 9L * 60L * 60L * 1000L
        const val NOTIFICATION_PERMISSION_REQUEST = 7001
    }
}
