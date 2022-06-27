package com.ubs.backend.classes.database.dao.statistik;

import com.ubs.backend.classes.TempAmountWithDate;
import com.ubs.backend.classes.TempUnansweredQuestion;
import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.UserLogin;
import com.ubs.backend.classes.database.dao.DAO;
import com.ubs.backend.classes.database.dao.UserLoginDAO;
import com.ubs.backend.classes.database.dao.statistik.time.StatistikTimesDAO;
import com.ubs.backend.classes.database.statistik.UnansweredQuestionStatistik;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;
import com.ubs.backend.classes.enums.TimeSearchType;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * @author Tim Irmler
 * @since 17.07.2021
 */
public class UnansweredQuestionStatistikDAO extends DAO<UnansweredQuestionStatistik> {
    public UnansweredQuestionStatistikDAO() {
        super(UnansweredQuestionStatistik.class);
    }

    public List<UnansweredQuestionStatistik> selectOrderedByAskedAmount(int max) {
        EntityManager em = Connector.getInstance().open();
        List<UnansweredQuestionStatistik> unansweredQuestionStatistiks = null;

        try {
            em.getTransaction().begin();
            unansweredQuestionStatistiks = selectOrderedByAskedAmount(max, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return unansweredQuestionStatistiks;
    }

    public List<UnansweredQuestionStatistik> selectOrderedByAskedAmount(int max, EntityManager em) {
        if (max > 0) {
            return em.createQuery("select uQS from UnansweredQuestionStatistik uQS order by uQS.askedAmount desc", UnansweredQuestionStatistik.class).setMaxResults(max).getResultList();
        } else {
            return em.createQuery("select uQS from UnansweredQuestionStatistik uQS order by uQS.askedAmount desc", UnansweredQuestionStatistik.class).getResultList();
        }
    }

    public UnansweredQuestionStatistik selectByQuestion(String question, EntityManager em) {
        try {
            return em.createQuery("select uQS from UnansweredQuestionStatistik uQS where lower(uQS.unansweredQuestion.question) = lower(:question) order by uQS.questionStatistikID desc", UnansweredQuestionStatistik.class).setMaxResults(1).setParameter("question", question).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public UnansweredQuestionStatistik selectByQuestion(String question) {
        EntityManager em = Connector.getInstance().open();
        UnansweredQuestionStatistik unansweredQuestionStatistik = null;

        try {
            em.getTransaction().begin();
            unansweredQuestionStatistik = selectByQuestion(question, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return unansweredQuestionStatistik;
    }

    /**
     * @param userID
     * @param time
     * @param statistikTimes
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public List<TempUnansweredQuestion> selectByTimeGroupedByQuestion(long userID, TimeSearchType time, StatistikTimes statistikTimes) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<TempUnansweredQuestion> out = selectByTimeGroupedByQuestion(userID, time, statistikTimes, em);

        em.getTransaction().commit();
        em.close();

        return out;
    }

    /**
     * @param time
     * @param statistikTimes
     * @return
     * @author Tim Irmler
     */
    public List<TempUnansweredQuestion> selectByTimeGroupedByQuestion(TimeSearchType time, StatistikTimes statistikTimes) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<TempUnansweredQuestion> out = selectByTimeGroupedByQuestion(null, time, statistikTimes, em);

        em.getTransaction().commit();
        em.close();

        return out;
    }

    /**
     * @param userID
     * @param time
     * @param statistikTimes
     * @param em
     * @return
     * @author Tim Irmler
     */
    public List<TempUnansweredQuestion> selectByTimeGroupedByQuestion(Long userID, TimeSearchType time, StatistikTimes statistikTimes, EntityManager em) {
        if (statistikTimes == null) {
            StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
            statistikTimes = statistikTimesDAO.selectNow(true, em);
        }

        printDebug("timeRange in selectByTiemGroupedByQuestion", time);

        switch (time) {
            case HOUR:
                return selectByHourGroupedByQuestion(statistikTimes, em);
            case DAY:
                return selectByDayGroupedByQuestion(statistikTimes, em);
            case WEEK:
                return selectByWeekGroupedByQuestion(statistikTimes, em);
            case MONTH:
                return selectByMonthGroupedByQuestion(statistikTimes, em);
            case YEAR:
                return selectByYearGroupedByQuestion(statistikTimes, em);
            case ALL:
                return selectGroupedByQuestion(em);
            case SINCE_LAST_LOGIN:
                if (userID != null) {
                    return selectSinceLastLoginGroupedByQuestion(userID, em);
                }
            default:
                return null;
        }
    }

    public List<TempUnansweredQuestion> selectByHourGroupedByQuestion(StatistikTimes times, EntityManager em) {
        long hour = times.getHour().getMyDate();
        long day = times.getDay().getMyDate();
        long week = times.getWeek().getMyDate();
        long month = times.getMonth().getMyDate();
        long year = times.getYear().getMyDate();

        List<UnansweredQuestionStatistik> unansweredQuestionStatistiks = em.createQuery("select new UnansweredQuestionStatistik(uqs.unansweredQuestion, uqs.statistikTimes, sum(uqs.askedAmount)) " +
                                "from UnansweredQuestionStatistik uqs " +
                                "where uqs.statistikTimes.hour.myDate = :hour" +
                                " and uqs.statistikTimes.day.myDate = :day" +
                                " and uqs.statistikTimes.week.myDate = :week" +
                                " and uqs.statistikTimes.month.myDate = :month" +
                                " and uqs.statistikTimes.year.myDate = :year " +
                                " group by uqs.unansweredQuestion" +
                                " order by sum(uqs.askedAmount) desc",
                        UnansweredQuestionStatistik.class)
                .setParameter("hour", hour)
                .setParameter("day", day)
                .setParameter("week", week)
                .setParameter("month", month)
                .setParameter("year", year).getResultList();

        ArrayList<TempUnansweredQuestion> tempUnansweredQuestions = new ArrayList<>();
        for (UnansweredQuestionStatistik unansweredQuestionStatistik : unansweredQuestionStatistiks) {
            tempUnansweredQuestions.add(new TempUnansweredQuestion(unansweredQuestionStatistik.getUnansweredQuestion(), unansweredQuestionStatistik.getAskedAmount()));
        }
        return tempUnansweredQuestions;
    }

    public List<TempUnansweredQuestion> selectByDayGroupedByQuestion(StatistikTimes times, EntityManager em) {
        long day = times.getDay().getMyDate();
        long week = times.getWeek().getMyDate();
        long month = times.getMonth().getMyDate();
        long year = times.getYear().getMyDate();

        List<UnansweredQuestionStatistik> unansweredQuestionStatistiks = em.createQuery("select new UnansweredQuestionStatistik(uqs.unansweredQuestion, uqs.statistikTimes, sum(uqs.askedAmount)) " +
                                "from UnansweredQuestionStatistik uqs " +
                                "where uqs.statistikTimes.day.myDate = :day" +
                                " and uqs.statistikTimes.week.myDate = :week" +
                                " and uqs.statistikTimes.month.myDate = :month" +
                                " and uqs.statistikTimes.year.myDate = :year " +
                                " group by uqs.unansweredQuestion" +
                                " order by sum(uqs.askedAmount) desc",
                        UnansweredQuestionStatistik.class)
                .setParameter("day", day)
                .setParameter("week", week)
                .setParameter("month", month)
                .setParameter("year", year).getResultList();

        ArrayList<TempUnansweredQuestion> tempUnansweredQuestions = new ArrayList<>();
        for (UnansweredQuestionStatistik unansweredQuestionStatistik : unansweredQuestionStatistiks) {
            tempUnansweredQuestions.add(new TempUnansweredQuestion(unansweredQuestionStatistik.getUnansweredQuestion(), unansweredQuestionStatistik.getAskedAmount()));
        }
        return tempUnansweredQuestions;
    }

    public List<TempUnansweredQuestion> selectByWeekGroupedByQuestion(StatistikTimes times, EntityManager em) {
        long week = times.getWeek().getMyDate();
        long month = times.getMonth().getMyDate();
        long year = times.getYear().getMyDate();

        List<UnansweredQuestionStatistik> unansweredQuestionStatistiks = em.createQuery("select new UnansweredQuestionStatistik(uqs.unansweredQuestion, uqs.statistikTimes, sum(uqs.askedAmount)) " +
                                "from UnansweredQuestionStatistik uqs " +
                                "where uqs.statistikTimes.week.myDate = :week" +
                                " and uqs.statistikTimes.month.myDate = :month" +
                                " and uqs.statistikTimes.year.myDate = :year " +
                                " group by uqs.unansweredQuestion" +
                                " order by sum(uqs.askedAmount) desc",
                        UnansweredQuestionStatistik.class)
                .setParameter("week", week)
                .setParameter("month", month)
                .setParameter("year", year).getResultList();

        ArrayList<TempUnansweredQuestion> tempUnansweredQuestions = new ArrayList<>();
        for (UnansweredQuestionStatistik unansweredQuestionStatistik : unansweredQuestionStatistiks) {
            tempUnansweredQuestions.add(new TempUnansweredQuestion(unansweredQuestionStatistik.getUnansweredQuestion(), unansweredQuestionStatistik.getAskedAmount()));
        }
        return tempUnansweredQuestions;
    }

    public List<TempUnansweredQuestion> selectByMonthGroupedByQuestion(StatistikTimes times, EntityManager em) {
        long month = times.getMonth().getMyDate();
        long year = times.getYear().getMyDate();

        List<UnansweredQuestionStatistik> unansweredQuestionStatistiks = em.createQuery("select new UnansweredQuestionStatistik(uqs.unansweredQuestion, uqs.statistikTimes, sum(uqs.askedAmount)) " +
                                "from UnansweredQuestionStatistik uqs " +
                                "where uqs.statistikTimes.month.myDate = :month" +
                                " and uqs.statistikTimes.year.myDate = :year " +
                                " group by uqs.unansweredQuestion" +
                                " order by sum(uqs.askedAmount) desc",
                        UnansweredQuestionStatistik.class)
                .setParameter("month", month)
                .setParameter("year", year).getResultList();

        ArrayList<TempUnansweredQuestion> tempUnansweredQuestions = new ArrayList<>();
        for (UnansweredQuestionStatistik unansweredQuestionStatistik : unansweredQuestionStatistiks) {
            tempUnansweredQuestions.add(new TempUnansweredQuestion(unansweredQuestionStatistik.getUnansweredQuestion(), unansweredQuestionStatistik.getAskedAmount()));
        }
        return tempUnansweredQuestions;
    }

    public List<TempUnansweredQuestion> selectByYearGroupedByQuestion(StatistikTimes times, EntityManager em) {
        long year = times.getYear().getMyDate();

        List<UnansweredQuestionStatistik> unansweredQuestionStatistiks = em.createQuery("select new UnansweredQuestionStatistik(uqs.unansweredQuestion, uqs.statistikTimes, sum(uqs.askedAmount)) " +
                                "from UnansweredQuestionStatistik uqs " +
                                "where uqs.statistikTimes.year.myDate = :year " +
                                " group by uqs.unansweredQuestion" +
                                " order by sum(uqs.askedAmount) desc",
                        UnansweredQuestionStatistik.class)
                .setParameter("year", year).getResultList();

        ArrayList<TempUnansweredQuestion> tempUnansweredQuestions = new ArrayList<>();
        for (UnansweredQuestionStatistik unansweredQuestionStatistik : unansweredQuestionStatistiks) {
            tempUnansweredQuestions.add(new TempUnansweredQuestion(unansweredQuestionStatistik.getUnansweredQuestion(), unansweredQuestionStatistik.getAskedAmount()));
        }
        return tempUnansweredQuestions;
    }

    /**
     * @param userID
     * @param em
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public List<TempUnansweredQuestion> selectSinceLastLoginGroupedByQuestion(long userID, EntityManager em) {
        UserLoginDAO userLoginDAO = new UserLoginDAO();
        UserLogin userLogin = userLoginDAO.select(userID, em);

        if (userLogin != null) {
            StatistikTimes lastLogin = userLogin.getTempLastTimeLoggedIn();
            if (lastLogin != null) {
                long lastLoginNumber = lastLogin.getStatistikTimeNumber();

                List<UnansweredQuestionStatistik> unansweredQuestionStatistiks = em.createQuery("select new UnansweredQuestionStatistik (aqtr.unansweredQuestion, aqtr.statistikTimes, sum(aqtr.askedAmount)) from UnansweredQuestionStatistik aqtr " +
                        "where aqtr.statistikTimes.statistikTimeNumber >= :lastLogin" +
                        " group by aqtr.unansweredQuestion" +
                        " order by sum(aqtr.askedAmount) desc", UnansweredQuestionStatistik.class).setParameter("lastLogin", lastLoginNumber).getResultList();

                ArrayList<TempUnansweredQuestion> tempUnansweredQuestions = new ArrayList<>();
                for (UnansweredQuestionStatistik unansweredQuestionStatistik : unansweredQuestionStatistiks) {
                    tempUnansweredQuestions.add(new TempUnansweredQuestion(unansweredQuestionStatistik.getUnansweredQuestion(), unansweredQuestionStatistik.getAskedAmount()));
                }
                return tempUnansweredQuestions;
            } else {
                return selectGroupedByQuestion(em);
            }
        }

        return null;
    }

    public List<TempUnansweredQuestion> selectGroupedByQuestion(EntityManager em) {
        List<UnansweredQuestionStatistik> unansweredQuestionStatistiks = em.createQuery("select new UnansweredQuestionStatistik(uqs.unansweredQuestion, uqs.statistikTimes, sum(uqs.askedAmount)) " +
                                "from UnansweredQuestionStatistik uqs " +
                                " group by uqs.unansweredQuestion" +
                                " order by sum(uqs.askedAmount) desc",
                        UnansweredQuestionStatistik.class)
                .getResultList();

        ArrayList<TempUnansweredQuestion> tempUnansweredQuestions = new ArrayList<>();
        for (UnansweredQuestionStatistik unansweredQuestionStatistik : unansweredQuestionStatistiks) {
            tempUnansweredQuestions.add(new TempUnansweredQuestion(unansweredQuestionStatistik.getUnansweredQuestion(), unansweredQuestionStatistik.getAskedAmount()));
        }
        return tempUnansweredQuestions;
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
            return em.createQuery("select sum(aqs.askedAmount) from UnansweredQuestionStatistik aqs where aqs.statistikTimes.statistikID = :statistikID", Long.class).setParameter("statistikID", times.getStatistikID()).getSingleResult();
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

        List<TempAmountWithDate> tempAmountWithDates = new ArrayList<>();
        switch (time) {
            case DAY:
                tempAmountWithDates = countAskedAmountDay(statistikTimes, em);
                break;
            case WEEK:
                tempAmountWithDates = countAskedAmountWeek(statistikTimes, em);
                break;
            case MONTH:
                tempAmountWithDates = countAskedAmountMonth(statistikTimes, em);
                break;
            case YEAR:
                tempAmountWithDates = countAskedAmountYear(statistikTimes, em);
                break;
            case ALL:
                tempAmountWithDates = countAskedAmountGroupedByYear(em);
                break;
            case SINCE_LAST_LOGIN:
                if (userID != null) {
                    tempAmountWithDates = countAskedAmountSinceLastLogin(userID, em);
                    break;
                }
            default:
                return null;
        }

        for (TempAmountWithDate tempAmountWithDate : tempAmountWithDates) {
            tempAmountWithDate.setType(TempAmountWithDate.TempAmountWithDateType.UNANSWERED_QUESTION);
        }

        return tempAmountWithDates;
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
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from UnansweredQuestionStatistik aqs where" +
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
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from UnansweredQuestionStatistik aqs where" +
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
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from UnansweredQuestionStatistik aqs where" +
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
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from UnansweredQuestionStatistik aqs where" +
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
                    return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from UnansweredQuestionStatistik aqs" +
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
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from UnansweredQuestionStatistik aqs" +
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
            return em.createQuery("select new com.ubs.backend.classes.TempAmountWithDate(aqs.statistikTimes, sum(aqs.askedAmount)) from UnansweredQuestionStatistik aqs" +
                            " group by aqs.statistikTimes.year.myDate", TempAmountWithDate.class)
                    .getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean removeByQuestion(long id, EntityManager em) {
        try {
            em.createQuery("delete from UnansweredQuestionStatistik uQS where uQS.unansweredQuestion.unansweredQuestionID = :id").setParameter("id", id).executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
