package com.ubs.backend.classes.database.statistik.times;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;
import io.smallrye.common.constraint.NotNull;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Tim Irmler
 * @since 17.07.2021
 */
@Entity
@Table(name = "StatistikDay")
@JsonSerializableObject(listName = "statistikDays")
public class StatistikDay extends StatistikTimesAbstract {
    /**
     * no args constructor
     *
     * @see StatistikTimesAbstract for default constructor
     * @since 17.07.2021
     */
    public StatistikDay() {
        super();
    }

    /**
     * @param date
     * @author Tim Irmler
     * @see StatistikTimesAbstract for the rest of the cunstructor
     * @since 17.07.2021
     */
    public StatistikDay(Date date) {
        super(date);
        Calendar calendar = createMyCalendar(date);
    }

    /**
     * @param date
     * @author Tim Irmler
     * @see StatistikTimesAbstract for the rest of the cunstructor
     * @since 17.07.2021
     */
    public StatistikDay(int date) {
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
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
}
