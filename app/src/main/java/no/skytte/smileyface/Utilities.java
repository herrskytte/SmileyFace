package no.skytte.smileyface;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

public class Utilities {

//    private static DateTimeFormatter sDateFormatter = DateTimeFormat.forPattern("ddMM");
    private static DateTimeFormatter sDateFormatter = DateTimeFormat.forPattern("ddMMyyyy");
    private static String sShortDateNoYearFormat;

    public static String formatDateToShortDate(String date){
        if(date == null || date.length() != 8){
            return "";
        }

        if(sShortDateNoYearFormat == null){
            createShortDateFormat();
        }

        DateTime d = sDateFormatter.parseDateTime(date);
        return d.toString(sShortDateNoYearFormat);
    }

    private static void createShortDateFormat() {
        //Get local date format
        String format = DateTimeFormat.patternForStyle("S-", Locale.getDefault());

        //Remove year part from beginning/end
        format = format.replaceAll("/yy", "");
        sShortDateNoYearFormat = format.replaceAll("yy/", "");
    }
}
