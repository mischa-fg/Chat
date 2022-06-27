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
@Table(name = "statistikHour")
@JsonSerializableObject(listName = "statistikHours")
public class StatistikHour extends StatistikTimesAbstract {
    /**
     * no args cunstructor
     *
     * @author Tim Irmler
     * @see StatistikTimesAbstract for the rest of the cunstructor
     * @since 17.07.2021
     */
    public StatistikHour() {
        super();
    }

    /**
     * @param date
     * @author Tim Irmler
     * @see StatistikTimesAbstract for the rest of the cunstructor
     * @since 17.07.2021
     */
    public StatistikHour(Date date) {
        super(date);
    }

    /**
     * @param date
     * @author Tim Irmler
     * @see StatistikTimesAbstract for the rest of the cunstructor
     * @since 17.07.2021
     */
    public StatistikHour(long date) {
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

        return calendar.get(Calendar.HOUR_OF_DAY);
    }
}
