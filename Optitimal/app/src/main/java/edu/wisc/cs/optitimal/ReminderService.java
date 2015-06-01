package edu.wisc.cs.optitimal;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import edu.cs.wisc.optitimal.R;

public class ReminderService extends WakeReminderIntentService {

    public ReminderService() {
        super("ReminderService");
    }

    @Override
    void doReminderWork(Intent intent) {
        Log.d("ReminderService", "Doing work.");
        Long rowId = intent.getExtras().getLong(ReminderProvider.COLUMN_ROWID);

        NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(this, ReminderEditActivity.class);
        notificationIntent.putExtra(ReminderProvider.COLUMN_ROWID, rowId);

        PendingIntent pi = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        Notification note = new Notification(
                android.R.drawable.stat_sys_warning,
                getString(R.string.notify_new_task_message),
                System.currentTimeMillis());
        note.setLatestEventInfo(this,
                getString(R.string.notify_new_task_title),
                getString(R.string.notify_new_task_message), pi);
        note.defaults |= Notification.DEFAULT_SOUND;
        note.flags |= Notification.FLAG_AUTO_CANCEL;

        // An issue could occur if user ever enters over 2,147,483,647 tasks.
        // (Max int value).
        // I highly doubt this will ever happen. But is good to note.
        int id = (int) ((long) rowId);
        mgr.notify(id, note);

    }
}
