package utils;

import java.util.Calendar;
import java.util.Date;

public class BirthdayUtils {
    public static Calendar getCorrectYearForFutureBirthday(Calendar origin){
        return getCorrectYearForFutureBirthday(origin.get(Calendar.MONTH), origin.get(Calendar.DAY_OF_MONTH));
    }

    public static Calendar getCorrectYearForFutureBirthday(int month, int day){
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int thisYearCurrentMonth = c.get(Calendar.MONTH);
        if(thisYearCurrentMonth > month) {
            //set to next year
            year++;
        } else if(thisYearCurrentMonth == month) {
            int thisMonthCurrentDay = c.get(Calendar.DAY_OF_MONTH);
            if(thisMonthCurrentDay > day) {
                //set to next year
                year++;
            }
        }

        c.set(year, month, day);
        return c;
    }

    public static int getAge(Date dateOfBirth){
        Calendar birth = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        birth.setTime(dateOfBirth);
        int diff = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (birth.get(Calendar.MONTH) > now.get(Calendar.MONTH) ||
                (birth.get(Calendar.MONTH) == now.get(Calendar.MONTH) && birth.get(Calendar.DATE) > now.get(Calendar.DATE))) {
            diff--;
        }
        return diff;
    }
}
