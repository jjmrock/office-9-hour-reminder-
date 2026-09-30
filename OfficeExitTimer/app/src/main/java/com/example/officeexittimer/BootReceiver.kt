package com.example.officeexittimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.AlarmManager
import android.os.Build

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED &&
            intent?.action != Intent.ACTION_LOCKED_BOOT_COMPLETED
        ) {
            return
        }

        val leaveAt = TimerStorage.getLeaveAt(context) ?: return

        // If the 9-hour point has already passed while the phone was off,
        // deliver the reminder as soon as possible after boot.
        val trigger = maxOf(leaveAt, System.currentTimeMillis() + 1000L)

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
            alarmManager.canScheduleExactAlarms()
        ) {
            AlarmScheduler.schedule(context, trigger)
        }
    }
}
