package com.ubs.backend.classes.database.dao.statistik.time;

import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.dao.DAO;
import com.ubs.backend.classes.database.statistik.times.*;
import com.ubs.backend.classes.enums.TimeSearchType;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

/**
 * @author Tim Irmler
 * @since 17.07.2021
 */
public class StatistikTimesDAO extends DAO<StatistikTimes> {
    public StatistikTimesDAO() {
        super(StatistikTimes.class);
    }

    public StatistikTimes selectNow(boolean createIfNotExists) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        StatistikTimes out = selectNow(createIfNotExists, em);

        em.getTransaction().commit();
        em.close();

        return out;
    }

    /**
     * selects the statistiktimes now, if now does not exist create new statistikTimes
     *
     * @param createIfNotExists should automatically a new statistik times be created when now doesnt exist?
     * @param em                the entity manager
     * @return statistikTimes from db of now, new statistiktimes if none exists but should be created, or null if none exists
     */
    public StatistikTimes selectNow(boolean createIfNotExists, EntityManager em) {
        Date date = new Date();
        List<StatistikTimes> statistikTimes = selectByDate(date, em);

        if ((statistikTimes == null || statistikTimes.size() == 0) && createIfNotExists) {
            StatistikHourDAO statistikHourDAO = new StatistikHourDAO();
            StatistikHour statistikHour = statistikHourDAO.selectByDate(date, em);
            if (statistikHour == null) {
                statistikHour = new StatistikHour(date);
            }

            StatistikDayDAO statistikDayDAO = new StatistikDayDAO();
            StatistikDay statistikDay = statistikDayDAO.selectByDate(date, em);
            if (statistikDay == null) {
                statistikDay = new StatistikDay(date);
            }

            StatistikWeekDAO statistikWeekDAO = new StatistikWeekDAO();
            StatistikWeek statistikWeek = statistikWeekDAO.selectByDate(date, em);
            if (statistikWeek == null) {
                statistikWeek = new StatistikWeek(date);
            }

            StatistikMonthDAO statistikMonthDAO = new StatistikMonthDAO();
            StatistikMonth statistikMonth = statistikMonthDAO.selectByDate(date, em);
            if (statistikMonth == null) {
                statistikMonth = new StatistikMonth(date);
            }

            StatistikYearDAO statistikYearDAO = new StatistikYearDAO();
            StatistikYear statistikYear = statistikYearDAO.selectByDate(date, em);
            if (statistikYear == null) {
                statistikYear = new StatistikYear(date);
            }

            return new StatistikTimes(statistikHour, statistikDay, statistikWeek, statistikMonth, statistikYear);
        } else if (statistikTimes != null && statistikTimes.size() > 0) {
            return statistikTimes.get(0);
        }
        return null;
    }

    /**
     * @param statistikTimes
     * @param em
     * @param timeSearchType
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public List<StatistikTimes> selectByDate(StatistikTimes statistikTimes, EntityManager em, TimeSearchType timeSearchType) {
        try {
            if (timeSearchType == TimeSearchType.HOUR) {
                return em.createQuery("select sT from StatistikTimes sT " +
                                "where " +
                                "sT.hour.myDate = :hourSQL " +
                                "and sT.day.myDate = :daySQL " +
                                "and sT.week.myDate = :weekSQL " +
                                "and sT.month.myDate = :monthSQL " +
                                "and sT.year.myDate = :yearSQL", StatistikTimes.class)
                        .setParameter("hourSQL", statistikTimes.getHour().getMyDate())
                        .setParameter("daySQL", statistikTimes.getDay().getMyDate())
                        .setParameter("weekSQL", statistikTimes.getWeek().getMyDate())
                        .setParameter("monthSQL", statistikTimes.getMonth().getMyDate())
                        .setParameter("yearSQL", statistikTimes.getYear().getMyDate())
                        .getResultList();
            } else {
                switch (timeSearchType) {
                    case DAY:
                        return selectByDay(statistikTimes, em);
                    case WEEK:
                        return selectByWeek(statistikTimes, em);
                    case MONTH:
                        return selectByMonth(statistikTimes, em);
                    case YEAR:
                        return selectByYear(statistikTimes, em);
                    default:
                        return null;
                }
            }
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param date
     * @param em
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public List<StatistikTimes> selectByDate(Date date, EntityManager em) {
        StatistikTimes statistikTimes = new StatistikTimes(date);
        return selectByDate(statistikTimes, em, TimeSearchType.HOUR);
    }

    /**
     * @param date
     * @return
     * @author Tim Irmler
     * @since 17.07.2021
     */
    public List<StatistikTimes> selectByDate(Date date) {
        EntityManager em = Connector.getInstance().open();
        List<StatistikTimes> out = null;

        try {
            em.getTransaction().begin();

            out = selectByDate(date, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }

        return out;
    }

    private List<StatistikTimes> selectByDay(StatistikTimes statistikTimes, EntityManager em) {
        return em.createQuery("select sT from StatistikTimes sT " +
                        "where " +
                        "sT.day.myDate = :daySQL " +
                        "and sT.week.myDate = :weekSQL " +
                        "and sT.month.myDate = :monthSQL " +
                        "and sT.year.myDate = :yearSQL", StatistikTimes.class)
                .setParameter("daySQL", statistikTimes.getDay().getMyDate())
                .setParameter("weekSQL", statistikTimes.getWeek().getMyDate())
                .setParameter("monthSQL", statistikTimes.getMonth().getMyDate())
                .setParameter("yearSQL", statistikTimes.getYear().getMyDate())
                .getResultList();
    }

    private List<StatistikTimes> selectByWeek(StatistikTimes statistikTimes, EntityManager em) {
        return em.createQuery("select sT from StatistikTimes sT " +
                        "where " +
                        "sT.week.myDate = :weekSQL " +
                        "and sT.month.myDate = :monthSQL " +
                        "and sT.year.myDate = :yearSQL", StatistikTimes.class)
                .setParameter("weekSQL", statistikTimes.getWeek().getMyDate())
                .setParameter("monthSQL", statistikTimes.getMonth().getMyDate())
                .setParameter("yearSQL", statistikTimes.getYear().getMyDate())
                .getResultList();
    }

    private List<StatistikTimes> selectByMonth(StatistikTimes statistikTimes, EntityManager em) {
        return em.createQuery("select sT from StatistikTimes sT " +
                        "where " +
                        "sT.month.myDate = :monthSQL " +
                        "and sT.year.myDate = :yearSQL", StatistikTimes.class)
                .setParameter("monthSQL", statistikTimes.getMonth().getMyDate())
                .setParameter("yearSQL", statistikTimes.getYear().getMyDate())
                .getResultList();
    }

    private List<StatistikTimes> selectByYear(StatistikTimes statistikTimes, EntityManager em) {
        return em.createQuery("select sT from StatistikTimes sT " +
                        "where " +
                        "sT.year.myDate = :yearSQL", StatistikTimes.class)
                .setParameter("yearSQL", statistikTimes.getYear().getMyDate())
                .getResultList();
    }

    /**
     * @param statistikTimes
     * @param em
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public StatistikTimes selectFirstDayOfWeek(StatistikTimes statistikTimes, EntityManager em) {
        return em.createQuery("select st from StatistikTimes st where" +
                        " st.week.myDate = :week and" +
                        " st.month.myDate = :month and" +
                        " st.year.myDate = :year" +
                        " order by st.day.myDate asc", StatistikTimes.class)
                .setParameter("week", statistikTimes.getWeek().getMyDate())
                .setParameter("month", statistikTimes.getMonth().getMyDate())
                .setParameter("year", statistikTimes.getYear().getMyDate())
                .setMaxResults(1).getSingleResult();
    }

    /**
     * @param statistikTimes
     * @param em
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public StatistikTimes selectLastDayOfWeek(StatistikTimes statistikTimes, EntityManager em) {
        return em.createQuery("select st from StatistikTimes st where" +
                        " st.week.myDate = :week and" +
                        " st.month.myDate = :month and" +
                        " st.year.myDate = :year" +
                        " order by st.day.myDate desc", StatistikTimes.class)
                .setParameter("week", statistikTimes.getWeek().getMyDate())
                .setParameter("month", statistikTimes.getMonth().getMyDate())
                .setParameter("year", statistikTimes.getYear().getMyDate())
                .setMaxResults(1).getSingleResult();
    }
}