/*
 * TimeUtil.java
 *
 * Created on 4. April 2007, 17:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package de.wehner.mediamagpie.common.util;

import java.io.File;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;

/**
 * Simple immutable helper class to work with time duration. The class provides functionality as follows:
 * <ul>
 * <li>Create timestamps</li>
 * <li>Parse a String that represent a time.</li>
 * </ul>
 */
public class TimeUtil {

    private Date _date;
    private final static SimpleDateFormat TIMESTAMPFORMATTER = new SimpleDateFormat("DDDHHmmssSSS");
    private final static SimpleDateFormat FILE_TIMESTAMPFORMATTER = new SimpleDateFormat("yyMMdd_HHmmss");
    private final static SimpleDateFormat GERMAN_SECOND_TIMESTAMPFORMATTER = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final static SimpleDateFormat GERMAN_DAY_TIMESTAMPFORMATTER = new SimpleDateFormat("dd.MM.yyyy");

    /** Creates a new instance of TimeUtil */
    public TimeUtil() {
        _date = new Date();
    }

    /**
     * Provides a String of date this object represents.<br/>
     * The format is:<br/>
     * 04.04.2007
     * 
     * @return a formatted String of date, this object represents
     */
    public String getDateString() {
        Calendar calendar = new GregorianCalendar();

        calendar.setTime(_date);
        SimpleDateFormat osdf = new SimpleDateFormat("dd.MM.yyyy");

        return osdf.format(calendar.getTime());
    }

    /**
     * Provides a String of time this object represents.<br/>
     * The format is:<br/>
     * 04.04.2007 17:21:31
     * 
     * @return a formatted String of time, this object represents
     */
    public String getDateAndTimeString() {
        Calendar calendar = new GregorianCalendar();

        calendar.setTime(_date);
        SimpleDateFormat osdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        return osdf.format(calendar.getTime());
    }

    /**
     * Provides a String of time this object represents.<br/>
     * The format is:<br/>
     * Mi, 4 Apr 2007 16:52:24
     * 
     * @return a formatted String of time, this object represents
     */
    public String getDateAndTimeString2() {
        Calendar calendar = new GregorianCalendar();

        calendar.setTime(_date);
        SimpleDateFormat osdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");

        return osdf.format(calendar.getTime());
    }

    /**
     * Provides a string that represents the actual timestamp with millisecnds resolution.<br/>
     * The format is:
     * 
     * <pre>
     * 'DDDHHmmssSSS', were DDD is the day in year and SSS is milliseconds.
     * E.g.: 090105645643
     * </pre>
     * 
     * @return A timestamp which contains of 9 decimal digits.
     */
    public static String createTimeStamp() {
        Calendar calendar = new GregorianCalendar();
        return createTimeStamp(calendar.getTime(), TIMESTAMPFORMATTER);
    }

    /**
     * Formats a time given in milliseconds to a common german string. The used format is <code>dd.MM.yyyy HH:mm:ss</code>.
     * 
     * @param milliseconds
     * @return The common german time with second precision.
     */
    public static String createCommonTimeStamp(long milliseconds) {
        return createTimeStamp(new Date(milliseconds), GERMAN_SECOND_TIMESTAMPFORMATTER);
    }

    protected static String createTimeStamp(Date date, Format formatter) {
        String timestampStr = formatter.format(date);
        return timestampStr;
    }

    public static File buildFileNameWithTimeStamp(File xmlFile, Date date, Format formatter) {
        File path = xmlFile.getParentFile();
        String baseName = FilenameUtils.getBaseName(xmlFile.getName());
        if (formatter == null) {
            formatter = FILE_TIMESTAMPFORMATTER;
        }
        String newFileName = String.format("%s%s.%s", baseName, createTimeStamp(date, formatter), FilenameUtils.getExtension(xmlFile.getName()));
        return new File(path, newFileName);
    }

    /**
     * Parses a given string. The string needs to have the format ' <code>&lt;value&gt;&lt;time unit&gt;</code>', were 'value' must be a
     * whole number and 'time unit' can be:
     * 
     * <pre>
     * 'ms' -&gt; milliseconds, ie: '3000ms' 
     * 's' -&gt; seconds, ie: '20s' 
     * 'm' -&gt; minutes, ie: '1m' 
     * 'h' -&gt; hours, ie: '5h' 
     * 'd' -&gt; days, ie: '1d'
     * </pre>
     * 
     * @param durationStr
     *            The duration this object should represent.
     * @return the duration in milliseconds.
     */
    public static long convertToMilliseconds(String durationStr) {
        if (durationStr == null) {
            throw new IllegalArgumentException("Argument 'durationStr' should not be null.");
        }
        durationStr = durationStr.trim().toLowerCase();

        if (StringUtil.isEmpty(durationStr)) {
            return 0;
        }

        if (durationStr.length() == 1) {
            // we need at least one digit for value and one character for time unit
            throw new IllegalArgumentException("Argument durationStr '' contains only one character.");
        }

        TimeUnit timeUnit;
        long duration;
        if (durationStr.endsWith("ms")) {
            timeUnit = TimeUnit.MILLISECONDS;
            duration = parse(durationStr, 2);
        } else if (durationStr.endsWith("s")) {
            timeUnit = TimeUnit.SECONDS;
            duration = parse(durationStr, 1);
        } else if (durationStr.endsWith("m")) {
            timeUnit = TimeUnit.MINUTES;
            duration = parse(durationStr, 1);
        } else if (durationStr.endsWith("h")) {
            timeUnit = TimeUnit.HOURS;
            duration = parse(durationStr, 1);
        } else if (durationStr.endsWith("d")) {
            timeUnit = TimeUnit.DAYS;
            duration = parse(durationStr, 1);
        } else {
            throw new IllegalArgumentException("No valid input format for argument durationStr '" + durationStr + "'.");
        }

        return timeUnit.toMillis(duration);
    }

    private static long parse(String durationStr, int unitLength) {
        if (durationStr.length() <= unitLength) {
            throw new IllegalArgumentException("The argument durationStr '" + durationStr + "' does not contains a value for the duration.");
        }
        return Long.parseLong(durationStr.substring(0, durationStr.length() - unitLength));
    }

    public static String duration2HumanReadableString(TimeUnit timeUnit, Long duration) {
        if (duration == null) {
            return "null";
        }

        long millis = timeUnit.toMillis(duration);

        if (millis < TimeUnit.SECONDS.toMillis(1)) {
            return millis + " ms";
        } else if (millis < TimeUnit.MINUTES.toMillis(1)) {
            return TimeUnit.MILLISECONDS.toSeconds(millis) + " second(s)";
        } else if (millis < TimeUnit.HOURS.toMillis(1)) {
            return TimeUnit.MILLISECONDS.toMinutes(millis) + " minute(s)";
        } else if (millis < TimeUnit.DAYS.toMillis(1)) {
            return TimeUnit.MILLISECONDS.toHours(millis) + " hour(s)";
        } else {
            return TimeUnit.MILLISECONDS.toDays(millis) + " day(s)";
        }
    }

    /**
     * Creates a <code>Date</code> object based on <b>yyyyMMdd</b> time formatting pattern. The date precision is day.
     * 
     * @param dateString
     *            The date to parse
     * @return A date with day precision.
     * @throws ParseException
     */
    public static Date parseDate(String dateString) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyyMMdd");
        Date date = df.parse(dateString);
        return date;
    }

    /**
     * Parses a date from string using the pattern <code>dd.MM.yyyy</code>
     * 
     * @param dateString
     * @return The parsed Date
     * @throws ParseException
     *             When given string can't be parsed with given pattern.
     */
    public static Date parseGermanDate(String dateString) throws ParseException {
        return GERMAN_DAY_TIMESTAMPFORMATTER.parse(dateString);
    }

    /**
     * Parses a date and time with second resolution from string using the pattern <code>dd.MM.yyyy HH:mm:ss</code>
     * 
     * @param dateString
     * @return The parsed Date
     * @throws ParseException
     *             When given string can't be parsed with given pattern.
     */
    public static Date parseGermanDateAndTime(String dateString) throws ParseException {
        return GERMAN_SECOND_TIMESTAMPFORMATTER.parse(dateString);
    }

    /**
     * Provides only the year of a <code>Date</code> object.
     * 
     * @param date
     * @return
     */
    public static int getYearFromDate(Date date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
}
