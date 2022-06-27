package com.ubs.backend.classes.database.dao.statistik;

import com.ubs.backend.classes.TempAmountWithDate;
import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.UserLogin;
import com.ubs.backend.classes.database.dao.DAO;
import com.ubs.backend.classes.database.dao.UserLoginDAO;
import com.ubs.backend.classes.database.dao.statistik.time.StatistikTimesDAO;
import com.ubs.backend.classes.database.statistik.AnswerStatistik;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;
import com.ubs.backend.classes.enums.TimeSearchType;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

import static com.ubs.backend.util.PrintDebug.printDebug;

public class AnswerStatistikDAO extends DAO<AnswerStatistik> {
    public AnswerStatistikDAO() {
        super(AnswerStatistik.class);
    }

    /**
     * @param time
     * @param statistikTimes
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public List<TempAmountWithDate> countAskedAmountByTimeGroupedByAnswer(long userID, TimeSearchType time, StatistikTimes statistikTimes) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<TempAmountWithDate> out = countAskedAmountByTimeGroupedByAnswer(userID, time, statistikTimes, em);

        em.getTransaction().commit();
        em.close();

        return out;
    }

    /**
     * @param time
     * @param statistikTimes
     * @return
     * @author Tim Irmler
     * @since 25.08.2021
     */
    public List<TempAmountWithDate> countAskedAmountByTimeGroupedByAnswer(TimeSearchType time, StatistikTimes statistikTimes) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<TempAmountWithDate> out = countAskedAmountByTimeGroupedByAnswer(null, time, statistikTimes, em);

        em.getTransaction().commit();
        em.close();

        return out;
    }

    /**
     * @param times
     * @param em
     * @return
     * @author Tim Irmler
     * @since 02.09.2021
     */
    public Long countAskedAmountSingleHour(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select sum(aqs.askedAmount) from AnswerStatistik aqs where" +
                            " aqs.statistikTimes.hour.myDate = :hour" +
                            " and aqs.statistikTimes.day.myDate = :day" +
                            " and aqs.statistikTimes.week.myDate = :week" +
                            " and aqs.statistikTimes.month.myDate = :month" +
                            " and aqs.statistikTimes.year.myDate = :year", Long.class)
                    .setParameter("hour", times.getHour().getMyDate())
                    .setParameter("day", times.getDay().getMyDate())
                    .setParameter("week", times.getWeek().getMyDate())
                    .setParameter("month", times.getMonth().getMyDate())
                    .setParameter("year", times.getYear().getMyDate())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param times
     * @param em
     * @return
     * @author Tim Irmler
     * @since 02.09.2021
     */
    public Long countAskedAmountSingleDay(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select sum(aqs.askedAmount) from AnswerStatistik aqs where" +
                            " aqs.statistikTimes.day.myDate = :day" +
                            " and aqs.statistikTimes.week.myDate = :week" +
                            " and aqs.statistikTimes.month.myDate = :month" +
                            " and aqs.statistikTimes.year.myDate = :year", Long.class)
                    .setParameter("day", times.getDay().getMyDate())
                    .setParameter("week", times.getWeek().getMyDate())
                    .setParameter("month", times.getMonth().getMyDate())
                    .setParameter("year", times.getYear().getMyDate())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param times
     * @param em
     * @return
     * @author Tim Irmler
     * @since 02.09.2021
     */
    public Long countAskedAmountSingleWeek(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select sum(aqs.askedAmount) from AnswerStatistik aqs where" +
                            " aqs.statistikTimes.week.myDate = :week" +
                            " and aqs.statistikTimes.month.myDate = :month" +
                            " and aqs.statistikTimes.year.myDate = :year", Long.class)
                    .setParameter("week", times.getWeek().getMyDate())
                    .setParameter("month", times.getMonth().getMyDate())
                    .setParameter("year", times.getYear().getMyDate())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param times
     * @param em
     * @return
     * @author Tim Irmler
     * @since 02.09.2021
     */
    public Long countAskedAmountSingleMonth(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select sum(aqs.askedAmount) from AnswerStatistik aqs where" +
                            " aqs.statistikTimes.month.myDate = :month" +
                            " and aqs.statistikTimes.year.myDate = :year", Long.class)
                    .setParameter("month", times.getMonth().getMyDate())
                    .setParameter("year", times.getYear().getMyDate())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param times
     * @param em
     * @return
     * @author Tim Irmler
     * @since 02.09.2021
     */
    public Long countAskedAmountSingleYear(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select sum(aqs.askedAmount) from AnswerStatistik aqs where" +
                            " aqs.statistikTimes.year.myDate = :year", Long.class)
                    .setParameter("year", times.getYear().getMyDate())
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param time
     * @param statistikTimes
     * @param em
     * @return
     * @author Tim Irmler
     * @since 25.08.2021
     */
    public List<TempAmountWithDate> countAskedAmountByTimeGroupedByAnswer(Long userID, TimeSearchType time, StatistikTimes statistikTimes, EntityManager em) {
        if (statistikTimes == null) {
            StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
            statistikTimes = statistikTimesDAO.selectNow(true, em);
        }

        printDebug("timeRange in selectByTimeGroupedByAnswer", time);

        switch (time) {
            case DAY:
                return countAskedAmountDayGroupedByAnswer(statistikTimes, em);
            case WEEK:
                return countAskedAmountWeekGroupedByAnswer(statistikTimes, em);
            case MONTH:
                return countAskedAmountMonthGroupedByAnswer(statistikTimes, em);
            case YEAR:
                return countAskedAmountYearGroupedByAnswer(statistikTimes, em);
            case ALL:
                return countAskedAmountGroupedByYearGroupedByAnswer(em);
            case SINCE_LAST_LOGIN:
                if (userID != null) {
                    return countAskedAmountSinceLastLoginGroupedByAnswer(userID, em);
                }
            default:
                return null;
        }
    }

    /**
     * @param times
     * @param em
     * @return
     * @author Tim Irmler
     * @since 25.08.2021
     */
    public List<TempAmountWithDate> countAskedAmountDayGroupedByAnswer(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnswerStatistik aqs where" +
                            " aqs.statistikTimes.day.myDate = :day" +
                            " and aqs.statistikTimes.week.myDate = :week" +
                            " and aqs.statistikTimes.month.myDate = :month" +
                            " and aqs.statistikTimes.year.myDate = :year" +
                            " group by aqs.statistikTimes.hour.myDate", TempAmountWithDate.class)
                    .setParameter("day", times.getDay().getMyDate())
                    .setParameter("week", times.getWeek().getMyDate())
                    .setParameter("month", times.getMonth().getMyDate())
                    .setParameter("year", times.getYear().getMyDate())
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param times
     * @param em
     * @return
     * @author Tim Irmler
     * @since 25.08.2021
     */
    public List<TempAmountWithDate> countAskedAmountWeekGroupedByAnswer(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnswerStatistik aqs where" +
                            " aqs.statistikTimes.week.myDate = :week" +
                            " and aqs.statistikTimes.month.myDate = :month" +
                            " and aqs.statistikTimes.year.myDate = :year" +
                            " group by aqs.statistikTimes.day.myDate", TempAmountWithDate.class)
                    .setParameter("week", times.getWeek().getMyDate())
                    .setParameter("month", times.getMonth().getMyDate())
                    .setParameter("year", times.getYear().getMyDate())
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param times
     * @param em
     * @return
     * @author Tim Irmler
     * @since 25.08.2021
     */
    public List<TempAmountWithDate> countAskedAmountMonthGroupedByAnswer(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnswerStatistik aqs where" +
                            " aqs.statistikTimes.month.myDate = :month" +
                            " and aqs.statistikTimes.year.myDate = :year" +
                            " group by aqs.statistikTimes.week.myDate", TempAmountWithDate.class)
                    .setParameter("month", times.getMonth().getMyDate())
                    .setParameter("year", times.getYear().getMyDate())
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param times
     * @param em
     * @return
     * @author Tim Irmler
     * @since 25.08.2021
     */
    public List<TempAmountWithDate> countAskedAmountYearGroupedByAnswer(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnswerStatistik aqs where" +
                            " aqs.statistikTimes.year.myDate = :year" +
                            " group by aqs.statistikTimes.month.myDate", TempAmountWithDate.class)
                    .setParameter("year", times.getYear().getMyDate())
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param userID
     * @param em
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public List<TempAmountWithDate> countAskedAmountSinceLastLoginGroupedByAnswer(long userID, EntityManager em) {
        UserLoginDAO userLoginDAO = new UserLoginDAO();
        UserLogin userLogin = userLoginDAO.select(userID, em);

        if (userLogin != null) {
            StatistikTimes lastLogin = userLogin.getTempLastTimeLoggedIn();
            if (lastLogin != null) {
                long lastLoginNumber = lastLogin.getStatistikTimeNumber();

                try {
                    return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnswerStatistik aqs" +
                                    " where aqs.statistikTimes.statistikTimeNumber >= :lastLogin" +
                                    " group by aqs.statistikTimes.day.myDate", TempAmountWithDate.class)
                            .setParameter("lastLogin", lastLoginNumber)
                            .getResultList();
                } catch (NoResultException e) {
                    return null;
                }
            } else {
                return countAskedAmountGroupedByDay(em);
            }
        }
        return null;
    }

    /**
     * @param em
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public List<TempAmountWithDate> countAskedAmountGroupedByDay(EntityManager em) {
        try {
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnswerStatistik aqs" +
                            " group by aqs.statistikTimes.day.myDate", TempAmountWithDate.class)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * @param em
     * @return
     * @author Tim Irmler
     * @since 25.08.2021
     */
    public List<TempAmountWithDate> countAskedAmountGroupedByYearGroupedByAnswer(EntityManager em) {
        try {
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnswerStatistik aqs" +
                            " group by aqs.statistikTimes.year.myDate", TempAmountWithDate.class)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }
}
