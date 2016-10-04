package utils;

import android.app.Activity;
import android.content.Intent;
import android.provider.CalendarContract;

import com.srh.birthdayassistant.Constants;

import java.sql.Date;
import java.util.Calendar;
import java.util.Locale;

public class CalendarUtils {
    public static Intent getEditCalendarIntent(String title, Calendar eventStart){
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventStart.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventStart.getTimeInMillis() + Constants.HOUR_IN_MILLIS);
        return intent.putExtra("title", title);
    }

    public static String getFormatedBirthdate(int month, int day){
        return String.format(Locale.getDefault(), "%d/%d", day, month+1);
    }

    public static String getFormatedBirthdate(int year, int month, int day){
        return String.format(Locale.getDefault(), "%d-%02d-%02d", year, month+1, day);
    }

    public static Calendar getCalendarFromDate(String date){
        Calendar c = Calendar.getInstance();
        c.setTime(Date.valueOf(date));
        return c;
    }

    public static void startCalendar(Activity activity, String title, Calendar eventStart){
        activity.startActivity(getEditCalendarIntent(title, eventStart));
    }
}
