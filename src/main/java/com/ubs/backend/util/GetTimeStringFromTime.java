package com.ubs.backend.util;

import com.ubs.backend.classes.database.statistik.times.StatistikTimes;
import com.ubs.backend.classes.enums.TimeSearchType;

/**
 * @author Tim Irmler
 * @since 30.08.2021
 */
public class GetTimeStringFromTime {

    /**
     * Converts {@link StatistikTimes} to a String
     *
     * @param times the Time which will be Converted
     * @param time  the Unit the Time will be formatted to
     * @return the Converted Time String
     */
    public static String getTimeStringFromTime(StatistikTimes times, TimeSearchType time) {
        String timeString;

        switch (time) {
            case DAY:
                timeString = times.getHour().getSimpleDateFormatString() + ":00";
                break;
            case WEEK:
                timeString = times.getDayOfWeekFormatted();
                break;
            case MONTH:
                timeString = times.getFirstAndLastDayOfWeek(times);
                break;
            case YEAR:
                timeString = times.getMonthOfYearFormatted();
                break;
            case ALL:
                timeString = times.getYear().getSimpleDateFormatString();
                break;
            case SINCE_LAST_LOGIN:
                timeString = times.getFormatted(false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + time);
        }

        return timeString;
    }
}
