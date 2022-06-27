package com.ubs.backend.classes.database.dao.statistik;

import com.ubs.backend.classes.TempAmountWithDate;
import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.UserLogin;
import com.ubs.backend.classes.database.dao.DAO;
import com.ubs.backend.classes.database.dao.UserLoginDAO;
import com.ubs.backend.classes.database.dao.statistik.time.StatistikTimesDAO;
import com.ubs.backend.classes.database.statistik.AnsweredQuestionStatistik;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;
import com.ubs.backend.classes.enums.TimeSearchType;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.List;

import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * @author Tim Irmler
 * @since 17.07.2021
 */
public class AnsweredQuestionStatistikDAO extends DAO<AnsweredQuestionStatistik> {
    public AnsweredQuestionStatistikDAO() {
        super(AnsweredQuestionStatistik.class);
    }

    public List<AnsweredQuestionStatistik> selectOrderedByAskedAmount(int max) {
        EntityManager em = Connector.getInstance().open();
        List<AnsweredQuestionStatistik> answeredQuestionStatistiks = null;

        try {
            em.getTransaction().begin();
            answeredQuestionStatistiks = selectOrderedByAskedAmount(max, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return answeredQuestionStatistiks;
    }

    public List<AnsweredQuestionStatistik> selectOrderedByAskedAmount(int max, EntityManager em) {
        if (max > 0) {
            return em.createQuery("select aQS from AnsweredQuestionStatistik aQS order by aQS.askedAmount desc", AnsweredQuestionStatistik.class).setMaxResults(max).getResultList();
        } else {
            return em.createQuery("select aQS from AnsweredQuestionStatistik aQS order by aQS.askedAmount desc", AnsweredQuestionStatistik.class).getResultList();
        }
    }

    public AnsweredQuestionStatistik selectByQuestion(String question, EntityManager em) {
        try {
            return em.createQuery("select aQS from AnsweredQuestionStatistik aQS where lower(aQS.answeredQuestion.question) = lower(:question) order by aQS.questionStatistikID desc", AnsweredQuestionStatistik.class).setMaxResults(1).setParameter("question", question).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AnsweredQuestionStatistik selectByQuestion(String question) {
        EntityManager em = Connector.getInstance().open();
        AnsweredQuestionStatistik answeredQuestionStatistik = null;

        try {
            em.getTransaction().begin();
            answeredQuestionStatistik = selectByQuestion(question, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return answeredQuestionStatistik;
    }

    /**
     * @param time
     * @param statistikTimes
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public List<TempAmountWithDate> countAskedAmountByTime(long userID, TimeSearchType time, StatistikTimes statistikTimes) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<TempAmountWithDate> out = countAskedAmountByTime(userID, time, statistikTimes, em);

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
    public List<TempAmountWithDate> countAskedAmountByTime(TimeSearchType time, StatistikTimes statistikTimes) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<TempAmountWithDate> out = countAskedAmountByTime(null, time, statistikTimes, em);

        em.getTransaction().commit();
        em.close();

        return out;
    }

    /**
     * @param times
     * @param em
     * @return
     * @author Tim Irmler
     * @since 25.08.2021
     */
    public Long countAskedAmountSingleHour(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select sum(aqs.askedAmount) from AnsweredQuestionStatistik aqs where aqs.statistikTimes.statistikID = :statistikID", Long.class).setParameter("statistikID", times.getStatistikID()).getSingleResult();
        } catch (NoResultException e) {
            return 0L;
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
    public List<TempAmountWithDate> countAskedAmountByTime(Long userID, TimeSearchType time, StatistikTimes statistikTimes, EntityManager em) {
        if (statistikTimes == null) {
            StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
            statistikTimes = statistikTimesDAO.selectNow(true, em);
        }

        printDebug("timeRange in selectByTimeGroupedByQuestion", time);

        switch (time) {
            case DAY:
                return countAskedAmountDay(statistikTimes, em);
            case WEEK:
                return countAskedAmountWeek(statistikTimes, em);
            case MONTH:
                return countAskedAmountMonth(statistikTimes, em);
            case YEAR:
                return countAskedAmountYear(statistikTimes, em);
            case ALL:
                return countAskedAmountGroupedByYear(em);
            case SINCE_LAST_LOGIN:
                if (userID != null) {
                    return countAskedAmountSinceLastLogin(userID, em);
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
    public List<TempAmountWithDate> countAskedAmountDay(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnsweredQuestionStatistik aqs where" +
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
    public List<TempAmountWithDate> countAskedAmountWeek(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnsweredQuestionStatistik aqs where" +
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
    public List<TempAmountWithDate> countAskedAmountMonth(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnsweredQuestionStatistik aqs where" +
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
    public List<TempAmountWithDate> countAskedAmountYear(StatistikTimes times, EntityManager em) {
        try {
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnsweredQuestionStatistik aqs where" +
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
    public List<TempAmountWithDate> countAskedAmountSinceLastLogin(long userID, EntityManager em) {
        UserLoginDAO userLoginDAO = new UserLoginDAO();
        UserLogin userLogin = userLoginDAO.select(userID, em);

        if (userLogin != null) {
            StatistikTimes lastLogin = userLogin.getTempLastTimeLoggedIn();
            if (lastLogin != null) {
                long lastLoginNumber = lastLogin.getStatistikTimeNumber();

                try {
                    return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnsweredQuestionStatistik aqs" +
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
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnsweredQuestionStatistik aqs" +
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
    public List<TempAmountWithDate> countAskedAmountGroupedByYear(EntityManager em) {
        try {
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from AnsweredQuestionStatistik aqs" +
                            " group by aqs.statistikTimes.year.myDate", TempAmountWithDate.class)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AnsweredQuestionStatistik selectToday(long date, EntityManager em) {
        try {
            return em.createQuery("select aQS from AnsweredQuestionStatistik aQS where aQS.statistikTimes.day.myDate = :date order by aQS.statistikTimes.statistikID DESC", AnsweredQuestionStatistik.class).setMaxResults(1).setParameter("date", date).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AnsweredQuestionStatistik selectToday(long date) {
        EntityManager em = Connector.getInstance().open();
        AnsweredQuestionStatistik answeredQuestionStatistik = null;

        try {
            em.getTransaction().begin();
            answeredQuestionStatistik = selectToday(date, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return answeredQuestionStatistik;
    }

    public AnsweredQuestionStatistik selectLatest(EntityManager em) {
        try {
            return em.createQuery("select aQS from AnsweredQuestionStatistik aQS order by aQS.statistikTimes.statistikID desc", AnsweredQuestionStatistik.class).setMaxResults(1).getSingleResult(); // aQS.statistikTimes.hour.myDate, aQS.statistikTimes.day.myDate, aQS.statistikTimes.week.myDate, aQS.statistikTimes.month.myDate, aQS.statistikTimes.year.myDate, <- not working
        } catch (NoResultException e) {
            return null;
        }
    }

    public AnsweredQuestionStatistik selectLatest() {
        EntityManager em = Connector.getInstance().open();
        AnsweredQuestionStatistik answeredQuestionStatistik = null;

        try {
            em.getTransaction().begin();
            answeredQuestionStatistik = selectLatest(em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return answeredQuestionStatistik;
    }

    public boolean removeByQuestion(long id, EntityManager em) {
        try {
            em.createQuery("delete from AnsweredQuestionStatistik aQS where aQS.answeredQuestion.answeredQuestionID = :id").setParameter("id", id).executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}