package notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.SystemClock;

import com.srh.birthdayassistant.App;
import com.srh.birthdayassistant.R;

import java.util.Calendar;

public class NotificationManager {

    public static void scheduleBirthdayNotification(Notification notification, int month, int day){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int thisYearCurrentMonth = calendar.get(Calendar.MONTH);
        if(thisYearCurrentMonth > month) {
            //set to next year
            year++;
        } else if(thisYearCurrentMonth == month) {
            int thisMonthCurrentDay = calendar.get(Calendar.DAY_OF_MONTH);
            if(thisMonthCurrentDay > day) {
                //set to next year
                year++;
            }
        }

        int hour = 10;
        int minute = 0;

        calendar.set(year, month, day, hour, minute);
        NotificationManager.scheduleNotification(notification, calendar);
    }

    public static void scheduleNotification(Notification notification, Calendar dateAndTime) {
        AlarmManager alarmManager = (AlarmManager) App.get().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, dateAndTime.getTimeInMillis(), getNotificationIntent(notification));
    }

    public static void scheduleNotification(Notification notification, int delayMillis) {
        long futureInMillis = SystemClock.elapsedRealtime() + delayMillis;
        AlarmManager alarmManager = (AlarmManager) App.get().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, getNotificationIntent(notification));
    }

    public static Notification createNotification(String title, String msg, Bitmap largeBitmap) {
        Notification.Builder builder = new Notification.Builder(App.get());
        builder.setContentTitle(title);
        builder.setContentText(msg);
        builder.setLargeIcon(largeBitmap);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        return builder.build();
    }

    private static PendingIntent getNotificationIntent(Notification notification){
        Intent notificationIntent = new Intent(App.get(), NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.EXTRA_NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.EXTRA_NOTIFICATION, notification);
        return PendingIntent.getBroadcast(App.get(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
