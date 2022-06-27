package com.ubs.backend.classes.database.statistik.times;

import com.ubs.backend.annotations.json.JsonSerializableObject;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Tim Irmler
 * @since 17.07.2021
 */
@Entity
@Table(name = "StatistikWeek")
@JsonSerializableObject(listName = "statistikWeeks")
public class StatistikWeek extends StatistikTimesAbstract {
    /**
     * default constructor
     *
     * @author Tim Irmler
     * @see StatistikTimesAbstract for the rest of the cunstructor
     * @since 17.07.2021
     */
    public StatistikWeek() {
        super();
    }

    /**
     * @param date
     * @author Tim Irmler
     * @see StatistikTimesAbstract for the rest of the cunstructor
     * @since 17.07.2021
     */
    public StatistikWeek(Date date) {
        super(date);
    }

    /**
     * @param date
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @Override
    public long getSimpleDateFormat(Date date) {
        return getSimpleDateFormatStatic(date);
    }

    @Override
    public String getSimpleDateFormatString() {
        return String.format("%02d", this.getMyDate());
    }

    /**
     * @param date
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public static long getSimpleDateFormatStatic(Date date) {
        Calendar calendar = StatistikTimesAbstract.createMyCalendar(date);

        return calendar.get(Calendar.WEEK_OF_YEAR);
    }
}
