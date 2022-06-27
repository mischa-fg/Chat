package com.ubs.backend.classes.database.statistik.times;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;
import io.smallrye.common.constraint.NotNull;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * a template for the different times (day, month, etc)
 *
 * @author Tim Irmler
 * @since 17.07.2021
 */
@MappedSuperclass
@JsonSerializableObject(listName = "statistikTimesAbstracts")
public abstract class StatistikTimesAbstract {
    /**
     * the id of this class
     *
     * @since 17.07.2021
     */
    @Id
    @NotNull
    @JsonField(type = JSONType.INTEGER)
    private long myDate;

    /**
     * default constructor
     *
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public StatistikTimesAbstract() {
    }

    /**
     * @param date
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public static Calendar createMyCalendar(Date date) {
        TimeZone myTimeZone = TimeZone.getTimeZone("Europe/Zurich");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(myTimeZone);
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        return calendar;
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 26.08.2021
     */
    public static Calendar createMyCalendar() {
        TimeZone myTimeZone = TimeZone.getTimeZone("Europe/Zurich");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(myTimeZone);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        
        return calendar;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +
                "myDate=" + myDate +
                '}';
    }

    /**
     * @param date
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public abstract long getSimpleDateFormat(Date date);

    /**
     * @return
     * @author Tim Irmler
     * @since 07.08.2021
     */
    public abstract String getSimpleDateFormatString();

    /**
     * @param date
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public StatistikTimesAbstract(Date date) {
        this.myDate = getSimpleDateFormat(date);
    }

    /**
     * @param date
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public StatistikTimesAbstract(long date) {
        this.myDate = date;
    }

    /**
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public long getMyDate() {
        return myDate;
    }

    /**
     * @param myDate
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setMyDate(long myDate) {
        this.myDate = myDate;
    }

    /**
     * @param date
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public void setMyDate(Date date) {
        this.myDate = getSimpleDateFormat(date);
    }
}
