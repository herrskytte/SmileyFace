package no.skytte.smileyface;

import android.content.Context;
import android.text.format.DateUtils;
import android.widget.ImageView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Utilities {

    private static DateTimeFormatter sDateFormatter = DateTimeFormat.forPattern("ddMMyyyy");

    public static long dateStringToMillis(String dateString){
        if(dateString == null || dateString.length() != 8){
            return -1;
        }
        return sDateFormatter.parseDateTime(dateString).getMillis();
    }

    public static String formatDate(long date){
        if(date <= 0){
            return "";
        }

        DateTime d = new DateTime(date);
        return DateTimeFormat.shortDate().print(d);
    }

    public static String formatDateToShortDate(long date){
        String dateWithYear = formatDate(date);
        if(dateWithYear.equals("")){
            return dateWithYear;
        }

        if(dateWithYear.length() == 10){
            return dateWithYear.substring(0, (dateWithYear.length() - 5));
        }
        else if(dateWithYear.length() == 8){
            return dateWithYear.substring(0, (dateWithYear.length() - 3));
        }
        else {
            return dateWithYear;
        }
    }

    public static String formatDateToShortDate(String date, Context context) {
        if(date == null || date.length() != 8){
            return "";
        }
        DateTime d = sDateFormatter.parseDateTime(date);
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NO_YEAR;
        String monthAndDayText = DateUtils.formatDateTime(context, d.getMillis(), flags);
        return monthAndDayText;
    }

    public static void setSmileyImage(ImageView mIconView, int grade) {
        mIconView.setImageResource(getSmileyResource(grade));
    }

    public static int getSmileyResource(int grade){
        if(grade == 0 || grade == 1){
            return R.drawable.ic_mood_happy;
        }
        else if(grade == 2){
            return R.drawable.ic_mood_neutral;
        }
        else if(grade == 3){
            return R.drawable.ic_mood_sad;
        }
        else {
            return R.drawable.ic_mood_none;
        }
    }
}
