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
@Table(name = "StatistikMonth")
@JsonSerializableObject(listName = "statistikMonths")
public class StatistikMonth extends StatistikTimesAbstract {
    /**
     * default no args constructor
     *
     * @author Tim Irmler
     * @see StatistikTimesAbstract for the rest of the cunstructor
     * @since 17.07.2021
     */
    public StatistikMonth() {
        super();
    }

    /**
     * @param date
     * @author Tim Irmler
     * @see StatistikTimesAbstract for the rest of the cunstructor
     * @since 17.07.2021
     */
    public StatistikMonth(Date date) {
        super(date);
    }

    /**
     * @param date
     * @author Tim Irmler
     * @see StatistikTimesAbstract for the rest of the cunstructor
     * @since 17.07.2021
     */
    public StatistikMonth(int date) {
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
        Calendar calendar = createMyCalendar(date);

        return calendar.get(Calendar.MONTH) + 1;
    }
}
