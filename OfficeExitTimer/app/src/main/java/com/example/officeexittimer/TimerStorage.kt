package com.example.officeexittimer

import android.content.Context

object TimerStorage {

    private const val PREFS = "office_timer"
    private const val START_AT = "start_at"
    private const val LEAVE_AT = "leave_at"

    fun save(context: Context, startAt: Long, leaveAt: Long) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putLong(START_AT, startAt)
            .putLong(LEAVE_AT, leaveAt)
            .apply()
    }

    fun getLeaveAt(context: Context): Long? {
        val value = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getLong(LEAVE_AT, 0L)

        return if (value > 0L) value else null
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .apply()
    }
}
