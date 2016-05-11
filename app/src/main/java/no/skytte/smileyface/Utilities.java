package no.skytte.smileyface;

import android.content.Context;
import android.text.format.DateUtils;
import android.widget.ImageView;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

public class Utilities {

    private static DateTimeFormatter sDateFormatter = DateTimeFormat.forPattern("ddMMyyyy");
    private static String sShortDateNoYearFormat;

    public static String formatDate(String date){
        if(date == null || date.length() != 8){
            return "";
        }

        if(sShortDateNoYearFormat == null){
            createShortDateFormat();
        }

        DateTime d = sDateFormatter.parseDateTime(date);
        return DateTimeFormat.shortDate().print(d);
    }

    public static String formatDateToShortDate(String date){
        if(date == null || date.length() != 8){
            return "";
        }

        if(sShortDateNoYearFormat == null){
            createShortDateFormat();
        }

        DateTime d = sDateFormatter.parseDateTime(date);
        String dateWithYear = d.toString(DateTimeFormat.shortDate());
        return dateWithYear.substring(0, (dateWithYear.length() - 5));
        //return d.toString(sShortDateNoYearFormat);
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

    private static void createShortDateFormat() {

        //Get local date format
        String format = DateTimeFormat.patternForStyle("S-", Locale.getDefault());

        //Remove year part from beginning/end
        format = format.replaceAll("y", "");

        //sShortDateNoYearFormat = format.replaceAll("yy/", "");
        sShortDateNoYearFormat = format.replaceAll("y", "");
    }

    public static void setSmileyImage(ImageView mIconView, int grade) {
        if(grade == 0 || grade == 1){
            mIconView.setImageResource(R.drawable.ic_mood_happy);
        }
        else if(grade == 2){
            mIconView.setImageResource(R.drawable.ic_mood_neutral);
        }
        else if(grade == 3){
            mIconView.setImageResource(R.drawable.ic_mood_sad);
        }
        else {
            mIconView.setImageResource(R.drawable.ic_mood_none);
        }
    }
}
