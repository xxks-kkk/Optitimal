package edu.wisc.cs.optitimal;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class OnBootReceiver extends BroadcastReceiver {

    private static final String TAG = OnBootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        ReminderManager reminderMgr = new ReminderManager(context);

        Cursor cursor = context.getContentResolver().query(
                ReminderProvider.CONTENT_URI, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            int rowIdColumnIndex = cursor
                    .getColumnIndex(ReminderProvider.COLUMN_ROWID);
            
            //TODO: has modified
//            int dateTimeColumnIndex = cursor
//                    .getColumnIndex(ReminderProvider.COLUMN_DATE_TIME);

              int startDateTimeColumnIndex = cursor.getColumnIndex(ReminderProvider.COLUMN_START_DATE_TIME);
              
//              Log.d("test",String.valueOf(startDateTimeColumnIndex));
              
              int endDateTimeColumnIndex = cursor.getColumnIndex(ReminderProvider.COLUMN_END_DATE_TIME);
              
            while (cursor.isAfterLast() == false) {

                long rowId = cursor.getLong(rowIdColumnIndex);
                
                //TODO: has modified
                //long dateTime = cursor.getLong(dateTimeColumnIndex);
                
                long startDateTime = cursor.getLong(startDateTimeColumnIndex);
                long endDateTime = cursor.getLong(endDateTimeColumnIndex);

                Log.d(TAG, "Adding alarm from boot.");
                Log.d(TAG, "Row Id - " + rowId);
                Log.d(TAG, "Start Date Time - " + startDateTime);
                Log.d(TAG, "End Date Time - " + endDateTime);

                Calendar cal = Calendar.getInstance();
                
                //TODO: has modified
                //cal.setTime(new java.util.Date(dateTime));
                //cal.setTime(new java.util.Date(startDateTime));
                cal.setTime(new java.util.Date(endDateTime));
                
                reminderMgr.setReminder(rowId, cal);

                cursor.moveToNext();
            }
            cursor.close();
        }
    }
}
