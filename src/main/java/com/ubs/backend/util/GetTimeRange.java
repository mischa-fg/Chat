package com.ubs.backend.util;

import com.ubs.backend.classes.enums.TimeSearchType;

import java.util.Locale;

/**
 * @author Tim Irmler
 * @since 17.08.2021
 */
public class GetTimeRange {
    private static final TimeSearchType defaultDefault = TimeSearchType.DAY;

    /**
     * @param timeRange
     * @return
     * @author Tim Irmler
     * @since 25.08.2021
     */
    public static TimeSearchType getTimeRangeByString(String timeRange) {
        return getTimeRangeByString(timeRange, defaultDefault);
    }

    /**
     * @param timeRange
     * @param ignores
     * @return
     * @author Tim Irmler
     * @since 25.08.2021
     */
    public static TimeSearchType getTimeRangeByString(String timeRange, TimeSearchType... ignores) {
        return getTimeRangeByString(timeRange, defaultDefault, ignores);
    }

    /**
     * @param timeRange
     * @param defaultTimeSearchType
     * @return
     * @author Tim Irmler
     * @since 25.08.2021
     */
    public static TimeSearchType getTimeRangeByString(String timeRange, TimeSearchType defaultTimeSearchType) {
        return getTimeRangeByString(timeRange, defaultTimeSearchType, new TimeSearchType[]{});
    }

    /**
     * @param timeRange
     * @param defaultTimeSearchType
     * @param ignores
     * @return
     * @author Tim Irmler
     * @since 17.08.2021
     */
    public static TimeSearchType getTimeRangeByString(String timeRange, TimeSearchType defaultTimeSearchType, TimeSearchType... ignores) {
        TimeSearchType time;
        timeRange = timeRange.toLowerCase(Locale.ROOT);
        switch (timeRange) {
            case "hour":
            case "now":
                time = TimeSearchType.HOUR;
                break;

            case "day":
            case "today":
                time = TimeSearchType.DAY;
                break;
            case "week":
                time = TimeSearchType.WEEK;
                break;
            case "month":
                time = TimeSearchType.MONTH;
                break;
            case "year":
                time = TimeSearchType.YEAR;
                break;
            case "all":
                time = TimeSearchType.ALL;
                break;
            case "last":
            case "lastLogin":
                time = TimeSearchType.SINCE_LAST_LOGIN;
                break;
            default:
                time = defaultTimeSearchType;
                break;
        }
        for (TimeSearchType timeSearchType : ignores) {
            if (time == timeSearchType) {
                time = defaultTimeSearchType;
                break;
            }
        }

        return time;
    }
}
