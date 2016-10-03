package utils;

import android.content.Context;
import android.content.Intent;
import android.provider.CalendarContract;

import com.srh.birthdayassistant.App;
import com.srh.birthdayassistant.Constants;

import java.util.Calendar;
import java.util.Locale;

public class CalendarUtils {
    public static Intent getEditCalendarIntent(Calendar eventStart){
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, eventStart.getTimeInMillis());
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
        intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, eventStart.getTimeInMillis() + Constants.HOUR_IN_MILLIS);
        return intent.putExtra("title", "A Test Event from android app");
    }

    public static String getFormatedBirthdate(int year, int month, int day){
        return String.format(Locale.getDefault(), "%d-%02d-%02d", year, month+1, day);
    }
}
