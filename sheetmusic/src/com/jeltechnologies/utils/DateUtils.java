package com.jeltechnologies.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.jeltechnologies.utils.datatypes.WeekYear;

public class DateUtils {
    public static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public static final String DATE_PATTERN = "yyyy-MM-dd";

    public static final String TIME_PATTERN = "HH:mm";

    public static final String DATE_TIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;

    private static final ZoneId TIME_ZONE = ZoneId.systemDefault();

    private DateUtils() {
    }

    public static LocalDateTime now() {
	return LocalDateTime.now();
    }

    public static LocalDate today() {
	return LocalDate.now(ZoneId.systemDefault());
    }

    public static LocalDate toLocalDate(String date, String pattern) throws DateTimeParseException {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
	return LocalDate.parse(date, formatter);
    }

    public static LocalDate toLocalDate(Date utilDate) {
	return utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(String date, String pattern) throws DateTimeParseException {
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
	return LocalDateTime.parse(date, formatter);
    }

    public static Date toUtilDate(LocalDateTime ldt) {
	Date utilDate;
	if (ldt != null) {
	    utilDate = Date.from(ldt.atZone(TIME_ZONE).toInstant());
	} else {
	    utilDate = null;
	}
	return utilDate;
    }

    public static Date toUtilDate(LocalDate localDate) {
	return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime toLocalDateTime(Date utilDate) {
	if (utilDate == null) {
	    return null;
	} else {
	    return LocalDateTime.ofInstant(utilDate.toInstant(), TIME_ZONE);
	}
    }

    public static SimpleDateFormat getSimpleDateFormatISO8601() {
	return new SimpleDateFormat(ISO_8601);
    }

    public static String dateToISO8601(Date utilDate) {
	if (utilDate != null) {
	    return getSimpleDateFormatISO8601().format(utilDate);
	} else {
	    return null;
	}
    }

    /**
     * Convert a Date to a date and time, or only date
     * <p>
     * If the time is midnight (only zeroes) then only a date is returned, otherwise date and time. The returned time only contains hours and minutes, not seconds
     * 
     * @param date
     * @return
     */
    public static String toString(Date date) {
	String result = null;
	if (date != null) {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    int hour = cal.get(Calendar.HOUR_OF_DAY);
	    int minute = cal.get(Calendar.MINUTE);
	    int second = cal.get(Calendar.SECOND);
	    boolean hasTimeElement = (hour != 0) || (minute != 0) || (second != 0);
	    String pattern;
	    if (hasTimeElement) {
		pattern = DATE_TIME_PATTERN;
	    } else {
		pattern = DATE_PATTERN;
	    }
	    result = new SimpleDateFormat(pattern).format(date);
	}
	return result;
    }

    public static String toString(LocalTime localTime) {
	if (localTime == null) {
	    return null;
	} else {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtils.TIME_PATTERN).withZone(TIME_ZONE);
	    return formatter.format(localTime);
	}
    }

    public static String toString(LocalTime localTime, boolean skipMidnight) {
	String result;
	if (localTime == null) {
	    result = "";
	} else {
	    boolean skip = false;
	    if (skipMidnight) {
		if (localTime.getHour() == 0 && localTime.getMinute() == 0 && localTime.getSecond() == 0) {
		    skip = true;
		}
	    }
	    if (skip) {
		result = "";
	    } else {
		result = toString(localTime);
	    }
	}
	return result;
    }

    public static String toString(LocalDate localDate) {
	if (localDate == null) {
	    return "";
	} else {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtils.DATE_PATTERN).withZone(TIME_ZONE);
	    return formatter.format(localDate);
	}
    }

    public static String toString(LocalDateTime localDateTime) {
	if (localDateTime == null) {
	    return "";
	} else {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateUtils.DATE_TIME_PATTERN).withZone(TIME_ZONE);
	    return formatter.format(localDateTime);
	}
    }

    public static WeekYear getWeekAndYear(LocalDate localDate) {
	TemporalField woy = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
	int weekNumber = localDate.get(woy);
	int yearNumber = localDate.getYear();
	TemporalField fieldGerman = WeekFields.of(Locale.GERMAN).dayOfWeek();
	LocalDate lastDateOfWeek = localDate.with(fieldGerman, 7);
	WeekYear weekYear = new WeekYear(weekNumber, yearNumber, lastDateOfWeek);
	return weekYear;
    }
}
