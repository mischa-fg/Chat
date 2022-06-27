package com.ubs.backend.classes.database.statistik.times;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;
import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.dao.statistik.time.StatistikTimesDAO;
import com.ubs.backend.classes.enums.TimeSearchType;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * class where we combine all different statistik time elements (day, week, etc)
 *
 * @author Tim Irmler
 * @since 17.07.2021
 */
@Entity
@Table(name = "StatistikTimes")
@JsonSerializableObject(listName = "statistikTimes")
public class StatistikTimes {
    /**
     * the id of this class
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonField(type = JSONType.INTEGER)
    private long statistikID;

    /**
     * @see StatistikHour
     * @since 17.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "hourID")
    @JsonField(type = JSONType.LIST)
    private StatistikHour hour;

    /**
     * @see StatistikDay
     * @since 17.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "dayID")
    @JsonField(type = JSONType.LIST)
    private StatistikDay day;

    /**
     * @see StatistikWeek
     * @since 17.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "weekID")
    @JsonField(type = JSONType.LIST)
    private StatistikWeek week;

    /**
     * @see StatistikMonth
     * @since 17.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "monthID")
    @JsonField(type = JSONType.LIST)
    private StatistikMonth month;

    /**
     * @see StatistikYear
     * @since 17.07.2021
     */
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "yearID")
    @JsonField(type = JSONType.LIST)
    private StatistikYear year;

    /**
     * @since 08.08.2021
     */
    @JsonField(type = JSONType.INTEGER)
    private long statistikTimeNumber = 0;

    /**
     * default no args constructor
     *
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public StatistikTimes() {
    }

    /**
     * @param hour
     * @param day
     * @param week
     * @param month
     * @param year
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public StatistikTimes(StatistikHour hour, StatistikDay day, StatistikWeek week, StatistikMonth month, StatistikYear year) {
        this.hour = hour;
        this.day = day;
        this.week = week;
        this.month = month;
        this.year = year;
        this.statistikTimeNumber = calculateTimeNumber();
    }

    /**
     * @param date
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public StatistikTimes(Date date) {
        this.setDates(date);
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 26.08.2021
     */
    public Calendar createMyCalendarWithMyDates() throws ParseException {
        Calendar calendar = StatistikTimesAbstract.createMyCalendar();

        String date = getFormatted(false, ':') + " " + this.getHour().getMyDate() + ":00";
        SimpleDateFormat sdf = new SimpleDateFormat("dd:MM:yyyy HH:mm", Locale.ROOT);
        calendar.setTime(sdf.parse(date));

        return calendar;
    }

    public int getDayOfWeek() {
        Calendar calendar = null;
        try {
            calendar = createMyCalendarWithMyDates();

            return calendar.get(Calendar.DAY_OF_WEEK);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 26.08.2021
     */
    public String getDayOfWeekFormatted() {
        switch (getDayOfWeek()) {
            case 1:
                return "Sonntag";
            case 2:
                return "Montag";
            case 3:
                return "Dienstag";
            case 4:
                return "Mittwoch";
            case 5:
                return "Donnerstag";
            case 6:
                return "Freitag";
            case 7:
                return "Samstag";
            default:
                return "Undefined";
        }
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 26.08.2021
     */
    public int getWeekOfMonth() {
        Calendar calendar = null;
        try {
            calendar = createMyCalendarWithMyDates();
            return calendar.get(Calendar.WEEK_OF_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 26.08.2021
     */
    public String getWeekOfMonthFormatted() {
        return "Woche " + getWeekOfMonth();
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public String getFirstDayOfWeek(StatistikTimes times) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
        if (times == null) {
            times = statistikTimesDAO.selectNow(true, em);
        }
        StatistikTimes statistikTimes = statistikTimesDAO.selectFirstDayOfWeek(times, em);

        em.getTransaction().commit();
        em.close();

        return statistikTimes.getFormatted(false);
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public String getLastDayOfWeek(StatistikTimes times) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
        if (times == null) {
            times = statistikTimesDAO.selectNow(true, em);
        }
        StatistikTimes statistikTimes = statistikTimesDAO.selectLastDayOfWeek(times, em);

        em.getTransaction().commit();
        em.close();

        return statistikTimes.getFormatted(false);
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public String getFirstAndLastDayOfWeek(StatistikTimes times) {
        String first = getFirstDayOfWeek(times);
        String last = getLastDayOfWeek(times);

        return first + "-" + last;
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 26.08.2021
     */
    public String getMonthOfYearFormatted() {
        switch (Math.toIntExact(this.month.getMyDate())) {
            case 1:
                return "Januar";
            case 2:
                return "Februar";
            case 3:
                return "März";
            case 4:
                return "April";
            case 5:
                return "Mai";
            case 6:
                return "Juni";
            case 7:
                return "Juli";
            case 8:
                return "August";
            case 9:
                return "September";
            case 10:
                return "Oktober";
            case 11:
                return "November";
            case 12:
                return "Dezember";
            default:
                return "Undefined";
        }
    }

    /**
     * @param withHour
     * @param dateSeparator
     * @return
     * @author Tim Irmler
     * @since 26.08.2021
     */
    public String getFormatted(boolean withHour, char dateSeparator) {
        return getFormatted(withHour, dateSeparator, ',');
    }

    /**
     * @param withHour
     * @param dateSeparator
     * @param hourSeperator
     * @return
     * @author Tim Irmler
     * @since 17.08.2021
     */
    public String getFormatted(boolean withHour, char dateSeparator, char hourSeperator) {
        String formattedNoHour = this.day.getSimpleDateFormatString() + dateSeparator + this.month.getSimpleDateFormatString() + dateSeparator + this.year.getSimpleDateFormatString();
        if (withHour) {
            return formattedNoHour + hourSeperator + this.hour.getSimpleDateFormatString() + ":00";
        }
        return formattedNoHour;
    }

    /**
     * get the time formatted, displaying day, month and year and if wanted hours
     *
     * @return
     * @author Tim Irmler
     */
    public String getFormatted(boolean withHour) {
        return getFormatted(withHour, '.');
    }

    private long calculateTimeNumber() {
        if (this.year != null && this.month != null && this.week != null && this.day != null && this.hour != null) {
            String timeNumberString = this.year.getSimpleDateFormatString() + "" + this.month.getSimpleDateFormatString() + "" + this.week.getSimpleDateFormatString() + "" + this.day.getSimpleDateFormatString() + "" + this.hour.getSimpleDateFormatString();

            return Long.parseLong(timeNumberString);
        } else {
            return 0;
        }
    }

    /**
     * @param timeSearchType
     * @param times
     * @return
     * @author Tim Irmler
     * @since 01.09.2021
     */
    public boolean isSamePerTimeSpecial(TimeSearchType timeSearchType, StatistikTimes times) {
        if (this.getStatistikTimeNumber() == times.getStatistikTimeNumber()) {
            return true;
        }

        switch (timeSearchType) {
            case DAY:
                return (this.getHour().getMyDate() == times.getHour().getMyDate());
            case SINCE_LAST_LOGIN:
            case WEEK:
                return (this.getDay().getMyDate() == times.getDay().getMyDate());
            case MONTH:
                return (this.getWeek().getMyDate() == times.getWeek().getMyDate());
            case YEAR:
                return (this.getMonth().getMyDate() == times.getMonth().getMyDate());
            case ALL:
                return (this.getYear().getMyDate() == times.getYear().getMyDate());
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * @param timeSearchType
     * @param times
     * @param checkSmaller
     * @return
     * @author Tim Irmler
     * @since 01.09.2021
     */
    public boolean isDifferentPerTimeSpecial(TimeSearchType timeSearchType, StatistikTimes times, boolean checkSmaller) {
        if(this.isSamePerTimeSpecial(timeSearchType, times)) {
            return false;
        }

        switch (timeSearchType) {
            case DAY:
                return (this.getHour().getMyDate() < times.getHour().getMyDate() && checkSmaller);
            case SINCE_LAST_LOGIN:
            case WEEK:
                return (this.getDay().getMyDate() < times.getDay().getMyDate() && checkSmaller);
            case MONTH:
                return (this.getWeek().getMyDate() < times.getWeek().getMyDate() && checkSmaller);
            case YEAR:
                return (this.getMonth().getMyDate() < times.getMonth().getMyDate() && checkSmaller);
            case ALL:
                return (this.getYear().getMyDate() < times.getYear().getMyDate() && checkSmaller);
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * @param newTimes
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setDates(StatistikTimes newTimes) {
        this.hour = newTimes.getHour();
        this.day = newTimes.getDay();
        this.week = newTimes.getWeek();
        this.month = newTimes.getMonth();
        this.year = newTimes.getYear();
        this.statistikTimeNumber = calculateTimeNumber();
    }

    /**
     * @param date
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setDates(Date date) {
        this.setHour(date);
        this.setDay(date);
        this.setWeek(date);
        this.setMonth(date);
        this.setYear(date);
        this.statistikTimeNumber = calculateTimeNumber();
    }

    /**
     * @param toCompare
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public boolean isSameDate(StatistikTimes toCompare) {
        boolean isHourSame = hour.getMyDate() == toCompare.getHour().getMyDate();
        boolean isDaySame = day.getMyDate() == toCompare.getDay().getMyDate();
        boolean isWeekSame = week.getMyDate() == toCompare.getWeek().getMyDate();
        boolean isMonthSame = month.getMyDate() == toCompare.getMonth().getMyDate();
        boolean isYearSame = year.getMyDate() == toCompare.getYear().getMyDate();
        return (isHourSame && isDaySame && isWeekSame && isMonthSame && isYearSame); // returns only true if all booleans are true
    }

    @Override
    public String toString() {
        return "StatistikTimes{" +
                "statistikID=" + statistikID +
                ", hour=" + hour +
                ", day=" + day +
                ", week=" + week +
                ", month=" + month +
                ", year=" + year +
                ", statistikTimeNumber=" + statistikTimeNumber +
                '}';
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public long getStatistikID() {
        return statistikID;
    }

    /**
     * @param statistikTimesID
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setStatistikID(long statistikTimesID) {
        this.statistikID = statistikTimesID;
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public StatistikDay getDay() {
        return day;
    }

    /**
     * @param day
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setDay(StatistikDay day) {
        this.day = day;
        this.statistikTimeNumber = calculateTimeNumber();
    }

    /**
     * @param date
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setDay(Date date) {
        this.day = new StatistikDay(date);
        this.statistikTimeNumber = calculateTimeNumber();
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public StatistikWeek getWeek() {
        return week;
    }

    /**
     * @param week
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setWeek(StatistikWeek week) {
        this.week = week;
        this.statistikTimeNumber = calculateTimeNumber();
    }

    /**
     * @param date
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setWeek(Date date) {
        this.week = new StatistikWeek(date);
        this.statistikTimeNumber = calculateTimeNumber();
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public StatistikMonth getMonth() {
        return month;
    }

    /**
     * @param month
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setMonth(StatistikMonth month) {
        this.month = month;
        this.statistikTimeNumber = calculateTimeNumber();
    }

    /**
     * @param date
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setMonth(Date date) {
        this.month = new StatistikMonth(date);
        this.statistikTimeNumber = calculateTimeNumber();
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public StatistikYear getYear() {
        return year;
    }

    /**
     * @param year
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setYear(StatistikYear year) {
        this.year = year;
        this.statistikTimeNumber = calculateTimeNumber();
    }

    /**
     * @param date
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setYear(Date date) {
        this.year = new StatistikYear(date);
        this.statistikTimeNumber = calculateTimeNumber();
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public StatistikHour getHour() {
        return hour;
    }

    /**
     * @param hour
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setHour(StatistikHour hour) {
        this.hour = hour;
        this.statistikTimeNumber = calculateTimeNumber();
    }

    /**
     * @param date
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setHour(Date date) {
        this.hour = new StatistikHour(date);
        this.statistikTimeNumber = calculateTimeNumber();
    }

    public long getStatistikTimeNumber() {
        if (statistikTimeNumber == 0) {
            this.statistikTimeNumber = calculateTimeNumber();
        }
        return statistikTimeNumber;
    }

    public void setStatistikTimeNumber(long statistikTimeNumber) {
        this.statistikTimeNumber = statistikTimeNumber;
    }
}
