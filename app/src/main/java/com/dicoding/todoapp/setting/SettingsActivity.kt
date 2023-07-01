package com.dicoding.todoapp.setting

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.work.*
import com.dicoding.todoapp.R
import com.dicoding.todoapp.notification.NotificationWorker
import com.dicoding.todoapp.utils.NOTIFICATION_CHANNEL_ID
import java.util.concurrent.TimeUnit

class SettingsActivity : AppCompatActivity() {

    private lateinit var workManager: WorkManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        workManager = WorkManager.getInstance(this)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment(workManager))
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment(private val workManager: WorkManager): PreferenceFragmentCompat() {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            val prefNotification = findPreference<SwitchPreference>(getString(R.string.pref_key_notify))
            prefNotification?.setOnPreferenceChangeListener { preference, newValue ->
                val channelName = getString(R.string.notify_channel_name)
                // XTODO 13 : Schedule and cancel daily reminder using WorkManager with data channelName
                if (newValue as Boolean) {
                    scheduleDailyReminder(channelName)
                } else {
                    cancelDailyReminder()
                }
                true
            }
        }

        private fun scheduleDailyReminder(channelName: String) {

            val inputData = workDataOf(NOTIFICATION_CHANNEL_ID to channelName)

            val dailyReminderRequest = PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.DAYS)
                .setInputData(inputData)
                .addTag(REMINDER_WORK_TAG)
                .build()

            workManager.enqueueUniquePeriodicWork(
                REMINDER_WORK_TAG,
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyReminderRequest
            )
        }

        private fun cancelDailyReminder() {
            workManager.cancelAllWorkByTag(REMINDER_WORK_TAG)
        }

        private fun updateTheme(mode: Int): Boolean {
            AppCompatDelegate.setDefaultNightMode(mode)
            requireActivity().recreate()
            return true
        }

        companion object {
            private const val REMINDER_WORK_TAG = "daily_reminder"
        }

    }
}