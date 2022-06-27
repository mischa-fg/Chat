package com.ubs.backend.classes.database.dao.questions;

import com.ubs.backend.classes.TempAnsweredQuestionTimesResult;
import com.ubs.backend.classes.database.*;
import com.ubs.backend.classes.database.dao.DAO;
import com.ubs.backend.classes.database.dao.ResultDAO;
import com.ubs.backend.classes.database.dao.UserLoginDAO;
import com.ubs.backend.classes.database.dao.statistik.time.StatistikTimesDAO;
import com.ubs.backend.classes.database.questions.AnsweredQuestion;
import com.ubs.backend.classes.database.questions.AnsweredQuestionResult;
import com.ubs.backend.classes.database.questions.AnsweredQuestionTimesResult;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;
import com.ubs.backend.classes.enums.TimeSearchType;
import org.jetbrains.annotations.NotNull;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

import static com.ubs.backend.util.PrintDebug.printDebug;

public class AnsweredQuestionTimesResultDAO extends DAO<AnsweredQuestionTimesResult> {
    public AnsweredQuestionTimesResultDAO() {
        super(AnsweredQuestionTimesResult.class);
    }

    /**
     * @param times
     * @param max
     * @param em
     * @return
     * @author Tim Irmler
     * @since 03.08.2021
     */
    public List<TempAnsweredQuestionTimesResult> selectMonthlyOrderedByUpvotes(StatistikTimes times, int max, EntityManager em) {
        try {
            long month = times.getMonth().getMyDate();
            long year = times.getYear().getMyDate();

            List<AnsweredQuestionTimesResult> answeredQuestionTimesResults = em.createQuery("select new AnsweredQuestionTimesResult(aqtr.answeredQuestionStatistik, aqtr.answeredQuestionResult, sum(aqtr.upvote), sum(aqtr.downvote)) from AnsweredQuestionTimesResult aqtr " +
                                    "where aqtr.answeredQuestionStatistik.statistikTimes.month.myDate = :month" +
                                    " and aqtr.answeredQuestionStatistik.statistikTimes.year.myDate = :year " +
                                    " and aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                                    " group by aqtr.answeredQuestionStatistik.answeredQuestion" +
                                    " order by sum(aqtr.upvote) desc",
                            AnsweredQuestionTimesResult.class)
                    .setParameter("month", month)
                    .setParameter("year", year).setMaxResults(max).getResultList();

            return getTempAnsweredQuestionTimesResultsMonthlyViews(em, month, year, answeredQuestionTimesResults);
        } catch (NoResultException e) {
            return null;
        }
    }

    @NotNull
    private List<TempAnsweredQuestionTimesResult> getTempAnsweredQuestionTimesResultsMonthlyViews(EntityManager em, long month, long year, List<AnsweredQuestionTimesResult> answeredQuestionTimesResults) {
        ArrayList<TempAnsweredQuestionTimesResult> tempAnsweredQuestionTimesResults = new ArrayList<>();
        for (AnsweredQuestionTimesResult answeredQuestionTimesResult : answeredQuestionTimesResults) {
            Long count = em.createQuery("select sum(aqtr.answeredQuestionStatistik.askedAmount) from AnsweredQuestionTimesResult aqtr " +
                            "where aqtr.answeredQuestionStatistik.statistikTimes.month.myDate = :month" +
                            " and aqtr.answeredQuestionStatistik.statistikTimes.year.myDate = :year " +
                            " and aqtr.answeredQuestionResult.answeredQuestion.answeredQuestionID = :answeredQuestionId" +
                            " and aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                            " and aqtr.answeredQuestionResult.resultID = :id" +
                            " group by aqtr.answeredQuestionStatistik.answeredQuestion", Long.class)
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("answeredQuestionId", answeredQuestionTimesResult.getAnsweredQuestionResult().getAnsweredQuestion().getAnsweredQuestionID())
                    .setParameter("id", answeredQuestionTimesResult.getAnsweredQuestionResult().getResultID())
                    .getSingleResult();

            tempAnsweredQuestionTimesResults.add(new TempAnsweredQuestionTimesResult(answeredQuestionTimesResult, count));
        }
        return tempAnsweredQuestionTimesResults;
    }

    /**
     * @param times
     * @param max
     * @return
     * @author Tim Irmler
     * @since 03.08.2021
     */
    public List<TempAnsweredQuestionTimesResult> selectMonthlyOrderedByUpvotes(StatistikTimes times, int max) {
        EntityManager em = Connector.getInstance().open();
        List<TempAnsweredQuestionTimesResult> answeredQuestions = null;

        try {
            em.getTransaction().begin();
            answeredQuestions = selectMonthlyOrderedByUpvotes(times, max, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return answeredQuestions;
    }

    public List<AnsweredQuestionTimesResult> selectByResultQuestionAndTime(ResultParent resultParent, String question, StatistikTimes times, EntityManager em) {
        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
        AnsweredQuestion answeredQuestion = answeredQuestionDAO.selectByQuestion(question, em);

        if (answeredQuestion != null) {
            if (resultParent instanceof Result) {
                return em.createQuery("select aqtr from AnsweredQuestionTimesResult aqtr " +
                                "where aqtr.answeredQuestionResult.result.id = :resultID " +
                                "and aqtr.answeredQuestionResult.answeredQuestion.answeredQuestionID = :answeredQuestionID " +
                                "and aqtr.answeredQuestionStatistik.statistikTimes.statistikID = :timesID", AnsweredQuestionTimesResult.class)
                        .setParameter("resultID", resultParent.getId())
                        .setParameter("timesID", times.getStatistikID())
                        .setParameter("answeredQuestionID", answeredQuestion.getAnsweredQuestionID()).getResultList();
            } else if (resultParent instanceof TypeTag) {
                return em.createQuery("select aqtr from AnsweredQuestionTimesResult aqtr " +
                                "where aqtr.answeredQuestionResult.typeTag.id = :resultID " +
                                "and aqtr.answeredQuestionResult.answeredQuestion.answeredQuestionID = :answeredQuestionID " +
                                "and aqtr.answeredQuestionStatistik.statistikTimes.statistikID = :timesID", AnsweredQuestionTimesResult.class)
                        .setParameter("resultID", resultParent.getId())
                        .setParameter("timesID", times.getStatistikID())
                        .setParameter("answeredQuestionID", answeredQuestion.getAnsweredQuestionID()).getResultList();
            }
        }
        return null;
    }

    /**
     * @param matchID
     * @param answerID
     * @param isUpvote
     * @param revert
     * @param question
     * @param em
     * @author Tim Irmler
     * @since 21.08.2021
     */
    public void voteWithMatch(long matchID, long answerID, boolean isUpvote, boolean revert, String question, EntityManager em) {
        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
        AnsweredQuestionResult answeredQuestionResult = answeredQuestionDAO.selectResultByQuestionAndMatchAndAnswer(matchID, answerID, question, em);

        if (answeredQuestionResult != null) {
            ResultParent resultParent = answeredQuestionResult.getResult();
            if (resultParent == null) {
                resultParent = answeredQuestionResult.getTypeTag();
            }

            vote(resultParent, isUpvote, revert, question, em);
        } else {
            printDebug("voteWithMatch", "answeredQuestionResult = null");
        }
    }

    public void vote(ResultParent resultParent, boolean isUpvote, boolean revert, String question, EntityManager em) {
        StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
        StatistikTimes statistikTime = statistikTimesDAO.selectNow(false, em);
        if (statistikTime != null) {
            List<AnsweredQuestionTimesResult> answeredQuestionTimesResults = selectByResultQuestionAndTime(resultParent, question, statistikTime, em);

            printDebug("vote in answeredQuestionTimesResultDAO", "going through all " + answeredQuestionTimesResults.size() + " answeredQuestionTimesResults...");
            for (AnsweredQuestionTimesResult r : answeredQuestionTimesResults) {
                printDebug("current result", r);

                if (isUpvote) {
                    r.setUpvote(r.getUpvote() + 1);
                    if (revert) {
                        r.setDownvote(r.getDownvote() - 1);
                    }
                } else {
                    r.setDownvote(r.getDownvote() + 1);
                    if (revert) {
                        r.setUpvote(r.getUpvote() - 1);
                    }
                }
            }
        } else {
            printDebug("vote in answeredquestionTiemsResultDAO", "statistiktimes = null");
        }
    }

    /**
     * @param resultID
     * @param em
     * @author Tim Irmler
     * @since 04.08.2021
     */
    public void removeByResult(long resultID, EntityManager em) {
        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
        List<AnsweredQuestionResult> answeredQuestionResults = answeredQuestionDAO.selectResultByResult(resultID, em);
        for (AnsweredQuestionResult answeredQuestionResult : answeredQuestionResults) {
            em.createQuery("delete from AnsweredQuestionTimesResult aqtr where aqtr.answeredQuestionResult.resultID = :resultID").setParameter("resultID", answeredQuestionResult.getResultID()).executeUpdate();
        }
    }

    public void removeByAnsweredQuestion(long id, EntityManager em) {
        AnsweredQuestionDAO answeredQuestionDAO = new AnsweredQuestionDAO();
        List<AnsweredQuestionResult> answeredQuestionResults = answeredQuestionDAO.selectResultByAnsweredQuestionId(id, em);
        for (AnsweredQuestionResult answeredQuestionResult : answeredQuestionResults) {
            em.createQuery("delete from AnsweredQuestionTimesResult aqtr where aqtr.answeredQuestionResult.resultID = :id").setParameter("id", answeredQuestionResult.getResultID()).executeUpdate();
        }
    }

    public long countMonthly(StatistikTimes times, EntityManager em) {
        try {
            long month = times.getMonth().getMyDate();
            long year = times.getYear().getMyDate();

            return em.createQuery("select count(aqtr) from AnsweredQuestionTimesResult aqtr " +
                            "where aqtr.answeredQuestionStatistik.statistikTimes.month.myDate = :month" +
                            " and aqtr.answeredQuestionStatistik.statistikTimes.year.myDate = :year " +
                            " and aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false", Long.class)
                    .setParameter("month", month)
                    .setParameter("year", year).getSingleResult();
        } catch (NoResultException e) {
            return 0;
        }
    }

    public long countMonthly(StatistikTimes times) {
        EntityManager em = Connector.getInstance().open();
        long count = 0;

        try {
            em.getTransaction().begin();
            count = countMonthly(times, em);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return count;
    }

    /**
     * @param userID
     * @param time
     * @param statistikTimes
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public List<TempAnsweredQuestionTimesResult> selectByTimeGroupedByQuestion(long userID, TimeSearchType time, StatistikTimes statistikTimes) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<TempAnsweredQuestionTimesResult> out = selectByTimeGroupedByQuestion(userID, time, statistikTimes, em);

        em.getTransaction().commit();
        em.close();

        return out;
    }

    /**
     * @param time
     * @param statistikTimes
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public List<TempAnsweredQuestionTimesResult> selectByTimeGroupedByQuestion(TimeSearchType time, StatistikTimes statistikTimes) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<TempAnsweredQuestionTimesResult> out = selectByTimeGroupedByQuestion(null, time, statistikTimes, em);

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
    public List<TempAnsweredQuestionTimesResult> selectByTimeGroupedByQuestion(Long userID, TimeSearchType time, StatistikTimes statistikTimes, EntityManager em) {
        if (statistikTimes == null) {
            StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
            statistikTimes = statistikTimesDAO.selectNow(true, em);
        }

        printDebug("timeRange in selectByTimeGroupedByQuestion", time);

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

    public List<TempAnsweredQuestionTimesResult> selectByHourGroupedByQuestion(StatistikTimes times, EntityManager em) {
        long hour = times.getHour().getMyDate();
        long day = times.getDay().getMyDate();
        long week = times.getWeek().getMyDate();
        long month = times.getMonth().getMyDate();
        long year = times.getYear().getMyDate();

        List<AnsweredQuestionTimesResult> answeredQuestionTimesResults = em.createQuery("select new AnsweredQuestionTimesResult(aqtr.answeredQuestionStatistik, aqtr.answeredQuestionResult, sum(aqtr.upvote), sum(aqtr.downvote)) from AnsweredQuestionTimesResult aqtr " +
                                "where aqtr.answeredQuestionStatistik.statistikTimes.hour.myDate = :hour" +
                                " and aqtr.answeredQuestionStatistik.statistikTimes.day.myDate = :day" +
                                " and aqtr.answeredQuestionStatistik.statistikTimes.week.myDate = :week" +
                                " and aqtr.answeredQuestionStatistik.statistikTimes.month.myDate = :month" +
                                " and aqtr.answeredQuestionStatistik.statistikTimes.year.myDate = :year " +
                                " and aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                                " group by aqtr.answeredQuestionStatistik.answeredQuestion" +
                                " order by sum(aqtr.upvote) desc",
                        AnsweredQuestionTimesResult.class)
                .setParameter("hour", hour)
                .setParameter("day", day)
                .setParameter("week", week)
                .setParameter("month", month)
                .setParameter("year", year).getResultList();

        ArrayList<TempAnsweredQuestionTimesResult> tempAnsweredQuestionTimesResults = new ArrayList<>();
        for (AnsweredQuestionTimesResult answeredQuestionTimesResult : answeredQuestionTimesResults) {
            Long count = em.createQuery("select sum(aqtr.answeredQuestionStatistik.askedAmount) from AnsweredQuestionTimesResult aqtr " +
                            "where aqtr.answeredQuestionStatistik.statistikTimes.hour.myDate = :hour" +
                            " and aqtr.answeredQuestionStatistik.statistikTimes.day.myDate = :day" +
                            " and aqtr.answeredQuestionStatistik.statistikTimes.week.myDate = :week" +
                            " and aqtr.answeredQuestionStatistik.statistikTimes.month.myDate = :month" +
                            " and aqtr.answeredQuestionStatistik.statistikTimes.year.myDate = :year " +
                            " and aqtr.answeredQuestionResult.answeredQuestion.answeredQuestionID = :answeredQuestionId" +
                            " and aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                            " and aqtr.answeredQuestionResult.resultID = :id" +
                            " group by aqtr.answeredQuestionStatistik.answeredQuestion", Long.class)
                    .setParameter("hour", hour)
                    .setParameter("day", day)
                    .setParameter("week", week)
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("answeredQuestionId", answeredQuestionTimesResult.getAnsweredQuestionResult().getAnsweredQuestion().getAnsweredQuestionID())
                    .setParameter("id", answeredQuestionTimesResult.getAnsweredQuestionResult().getResultID())
                    .getSingleResult();

            printDebug("stunde count", count);

            tempAnsweredQuestionTimesResults.add(new TempAnsweredQuestionTimesResult(answeredQuestionTimesResult, count));
        }
        return tempAnsweredQuestionTimesResults;
    }

    public List<TempAnsweredQuestionTimesResult> selectByDayGroupedByQuestion(StatistikTimes times, EntityManager em) {
        long day = times.getDay().getMyDate();
        long week = times.getWeek().getMyDate();
        long month = times.getMonth().getMyDate();
        long year = times.getYear().getMyDate();

        List<AnsweredQuestionTimesResult> answeredQuestionTimesResults = em.createQuery("select new AnsweredQuestionTimesResult(aqtr.answeredQuestionStatistik, aqtr.answeredQuestionResult, sum(aqtr.upvote), sum(aqtr.downvote)) from AnsweredQuestionTimesResult aqtr " +
                                "where aqtr.answeredQuestionStatistik.statistikTimes.day.myDate = :day" +
                                " and aqtr.answeredQuestionStatistik.statistikTimes.week.myDate = :week" +
                                " and aqtr.answeredQuestionStatistik.statistikTimes.month.myDate = :month" +
                                " and aqtr.answeredQuestionStatistik.statistikTimes.year.myDate = :year" +
                                " and aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                                " group by aqtr.answeredQuestionStatistik.answeredQuestion" +
                                " order by sum(aqtr.upvote) desc",
                        AnsweredQuestionTimesResult.class)
                .setParameter("day", day)
                .setParameter("week", week)
                .setParameter("month", month)
                .setParameter("year", year).getResultList();

        ArrayList<TempAnsweredQuestionTimesResult> tempAnsweredQuestionTimesResults = new ArrayList<>();
        for (AnsweredQuestionTimesResult answeredQuestionTimesResult : answeredQuestionTimesResults) {
            Long count = em.createQuery("select sum(aqtr.answeredQuestionStatistik.askedAmount) from AnsweredQuestionTimesResult aqtr " +
                            "where aqtr.answeredQuestionStatistik.statistikTimes.day.myDate = :day" +
                            " and aqtr.answeredQuestionStatistik.statistikTimes.week.myDate = :week" +
                            " and aqtr.answeredQuestionStatistik.statistikTimes.month.myDate = :month" +
                            " and aqtr.answeredQuestionStatistik.statistikTimes.year.myDate = :year " +
                            " and aqtr.answeredQuestionResult.answeredQuestion.answeredQuestionID = :answeredQuestionId" +
                            " and aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                            " and aqtr.answeredQuestionResult.resultID = :id" +
                            " group by aqtr.answeredQuestionStatistik.answeredQuestion", Long.class)
                    .setParameter("day", day)
                    .setParameter("week", week)
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("answeredQuestionId", answeredQuestionTimesResult.getAnsweredQuestionResult().getAnsweredQuestion().getAnsweredQuestionID())
                    .setParameter("id", answeredQuestionTimesResult.getAnsweredQuestionResult().getResultID())
                    .getSingleResult();

            tempAnsweredQuestionTimesResults.add(new TempAnsweredQuestionTimesResult(answeredQuestionTimesResult, count));
        }
        return tempAnsweredQuestionTimesResults;
    }

    public List<TempAnsweredQuestionTimesResult> selectByWeekGroupedByQuestion(StatistikTimes times, EntityManager em) {
        long week = times.getWeek().getMyDate();
        long month = times.getMonth().getMyDate();
        long year = times.getYear().getMyDate();

        List<AnsweredQuestionTimesResult> answeredQuestionTimesResults = em.createQuery("select new AnsweredQuestionTimesResult(aqtr.answeredQuestionStatistik, aqtr.answeredQuestionResult, sum(aqtr.upvote), sum(aqtr.downvote)) from AnsweredQuestionTimesResult aqtr " +
                                "where aqtr.answeredQuestionStatistik.statistikTimes.week.myDate = :week" +
                                " and aqtr.answeredQuestionStatistik.statistikTimes.month.myDate = :month" +
                                " and aqtr.answeredQuestionStatistik.statistikTimes.year.myDate = :year " +
                                " and aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                                " group by aqtr.answeredQuestionStatistik.answeredQuestion" +
                                " order by sum(aqtr.upvote) desc",
                        AnsweredQuestionTimesResult.class)
                .setParameter("week", week)
                .setParameter("month", month)
                .setParameter("year", year).getResultList();

        ArrayList<TempAnsweredQuestionTimesResult> tempAnsweredQuestionTimesResults = new ArrayList<>();
        for (AnsweredQuestionTimesResult answeredQuestionTimesResult : answeredQuestionTimesResults) {
            Long count = em.createQuery("select sum(aqtr.answeredQuestionStatistik.askedAmount) from AnsweredQuestionTimesResult aqtr " +
                            "where aqtr.answeredQuestionStatistik.statistikTimes.week.myDate = :week" +
                            " and aqtr.answeredQuestionStatistik.statistikTimes.month.myDate = :month" +
                            " and aqtr.answeredQuestionStatistik.statistikTimes.year.myDate = :year" +
                            " and aqtr.answeredQuestionResult.answeredQuestion.answeredQuestionID = :answeredQuestionId" +
                            " and aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                            " and aqtr.answeredQuestionResult.resultID = :id" +
                            " group by aqtr.answeredQuestionResult.answeredQuestion", Long.class)
                    .setParameter("week", week)
                    .setParameter("month", month)
                    .setParameter("year", year)
                    .setParameter("answeredQuestionId", answeredQuestionTimesResult.getAnsweredQuestionResult().getAnsweredQuestion().getAnsweredQuestionID())
                    .setParameter("id", answeredQuestionTimesResult.getAnsweredQuestionResult().getResultID())
                    .getSingleResult();

            printDebug("counts", count);
            tempAnsweredQuestionTimesResults.add(new TempAnsweredQuestionTimesResult(answeredQuestionTimesResult, count));
        }
        return tempAnsweredQuestionTimesResults;
    }

    public List<TempAnsweredQuestionTimesResult> selectByMonthGroupedByQuestion(StatistikTimes times, EntityManager em) {
        long month = times.getMonth().getMyDate();
        long year = times.getYear().getMyDate();

        List<AnsweredQuestionTimesResult> answeredQuestionTimesResults = em.createQuery("select new AnsweredQuestionTimesResult(aqtr.answeredQuestionStatistik, aqtr.answeredQuestionResult, sum(aqtr.upvote), sum(aqtr.downvote)) from AnsweredQuestionTimesResult aqtr " +
                                "where aqtr.answeredQuestionStatistik.statistikTimes.month.myDate = :month" +
                                " and aqtr.answeredQuestionStatistik.statistikTimes.year.myDate = :year " +
                                " and aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                                " group by aqtr.answeredQuestionStatistik.answeredQuestion" +
                                " order by sum(aqtr.upvote) desc",
                        AnsweredQuestionTimesResult.class)
                .setParameter("month", month)
                .setParameter("year", year).getResultList();

        return getTempAnsweredQuestionTimesResultsMonthlyViews(em, month, year, answeredQuestionTimesResults);
    }

    public List<TempAnsweredQuestionTimesResult> selectByYearGroupedByQuestion(StatistikTimes times, EntityManager em) {
        long year = times.getYear().getMyDate();

        List<AnsweredQuestionTimesResult> answeredQuestionTimesResults = em.createQuery("select new AnsweredQuestionTimesResult(aqtr.answeredQuestionStatistik, aqtr.answeredQuestionResult, sum(aqtr.upvote), sum(aqtr.downvote)) from AnsweredQuestionTimesResult aqtr " +
                                "where aqtr.answeredQuestionStatistik.statistikTimes.year.myDate = :year " +
                                " and aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                                " group by aqtr.answeredQuestionStatistik.answeredQuestion" +
                                " order by sum(aqtr.upvote) desc",
                        AnsweredQuestionTimesResult.class)
                .setParameter("year", year).getResultList();

        ArrayList<TempAnsweredQuestionTimesResult> tempAnsweredQuestionTimesResults = new ArrayList<>();
        for (AnsweredQuestionTimesResult answeredQuestionTimesResult : answeredQuestionTimesResults) {
            Long count = em.createQuery("select sum(aqtr.answeredQuestionStatistik.askedAmount) from AnsweredQuestionTimesResult aqtr " +
                            "where aqtr.answeredQuestionStatistik.statistikTimes.year.myDate = :year " +
                            " and aqtr.answeredQuestionResult.answeredQuestion.answeredQuestionID = :answeredQuestionId" +
                            " and aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                            " and aqtr.answeredQuestionResult.resultID = :id" +
                            " group by aqtr.answeredQuestionStatistik.answeredQuestion", Long.class)
                    .setParameter("year", year)
                    .setParameter("answeredQuestionId", answeredQuestionTimesResult.getAnsweredQuestionResult().getAnsweredQuestion().getAnsweredQuestionID())
                    .setParameter("id", answeredQuestionTimesResult.getAnsweredQuestionResult().getResultID())
                    .getSingleResult();

            tempAnsweredQuestionTimesResults.add(new TempAnsweredQuestionTimesResult(answeredQuestionTimesResult, count));
        }
        return tempAnsweredQuestionTimesResults;
    }

    /**
     * @param userID
     * @param em
     * @return
     * @author Tim Irmler
     * @since 29.08.2021
     */
    public List<TempAnsweredQuestionTimesResult> selectSinceLastLoginGroupedByQuestion(long userID, EntityManager em) {
        UserLoginDAO userLoginDAO = new UserLoginDAO();
        UserLogin userLogin = userLoginDAO.select(userID, em);

        if (userLogin != null) {
            StatistikTimes lastLogin = userLogin.getTempLastTimeLoggedIn();
            if (lastLogin != null) {
                long lastLoginNumber = lastLogin.getStatistikTimeNumber();

                List<AnsweredQuestionTimesResult> answeredQuestionTimesResults = em.createQuery("select new AnsweredQuestionTimesResult(aqtr.answeredQuestionStatistik, aqtr.answeredQuestionResult, sum(aqtr.upvote), sum(aqtr.downvote)) from AnsweredQuestionTimesResult aqtr " +
                        "where aqtr.answeredQuestionStatistik.statistikTimes.statistikTimeNumber >= :lastLogin" +
                        " group by aqtr.answeredQuestionStatistik.answeredQuestion" +
                        " order by sum(aqtr.upvote) desc", AnsweredQuestionTimesResult.class).setParameter("lastLogin", lastLoginNumber).getResultList();

                
                
                for (AnsweredQuestionTimesResult answeredQuestionTimesResult : answeredQuestionTimesResults) {
                    long number = answeredQuestionTimesResult.getAnsweredQuestionStatistik().getStatistikTimes().getStatistikTimeNumber();
                    
                }

                ArrayList<TempAnsweredQuestionTimesResult> tempAnsweredQuestionTimesResults = new ArrayList<>();
                for (AnsweredQuestionTimesResult answeredQuestionTimesResult : answeredQuestionTimesResults) {
                    Long count = em.createQuery("select sum(aqtr.answeredQuestionStatistik.askedAmount) from AnsweredQuestionTimesResult aqtr " +
                                    "where aqtr.answeredQuestionStatistik.statistikTimes.statistikTimeNumber >= :lastLogin " +
                                    " and aqtr.answeredQuestionResult.answeredQuestion.answeredQuestionID = :answeredQuestionId" +
                                    " and aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                                    " and aqtr.answeredQuestionResult.resultID = :id" +
                                    " group by aqtr.answeredQuestionStatistik.answeredQuestion", Long.class)
                            .setParameter("lastLogin", lastLoginNumber)
                            .setParameter("answeredQuestionId", answeredQuestionTimesResult.getAnsweredQuestionResult().getAnsweredQuestion().getAnsweredQuestionID())
                            .setParameter("id", answeredQuestionTimesResult.getAnsweredQuestionResult().getResultID())
                            .getSingleResult();

                    tempAnsweredQuestionTimesResults.add(new TempAnsweredQuestionTimesResult(answeredQuestionTimesResult, count));
                }
                return tempAnsweredQuestionTimesResults;
            } else {
                return selectGroupedByQuestion(em);
            }
        }

        return null;
    }

    public List<TempAnsweredQuestionTimesResult> selectGroupedByQuestion(EntityManager em) {
        List<AnsweredQuestionTimesResult> answeredQuestionTimesResults = em.createQuery("select new AnsweredQuestionTimesResult(aqtr.answeredQuestionStatistik, aqtr.answeredQuestionResult, sum(aqtr.upvote), sum(aqtr.downvote)) from AnsweredQuestionTimesResult aqtr " +
                                "where aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                                " group by aqtr.answeredQuestionStatistik.answeredQuestion" +
                                " order by sum(aqtr.upvote) desc",
                        AnsweredQuestionTimesResult.class)
                .getResultList();

        ArrayList<TempAnsweredQuestionTimesResult> tempAnsweredQuestionTimesResults = new ArrayList<>();
        for (AnsweredQuestionTimesResult answeredQuestionTimesResult : answeredQuestionTimesResults) {
            Long count = getTotalCount(answeredQuestionTimesResult, em);

            tempAnsweredQuestionTimesResults.add(new TempAnsweredQuestionTimesResult(answeredQuestionTimesResult, count));
        }
        return tempAnsweredQuestionTimesResults;
    }

    public List<AnsweredQuestionTimesResult> selectByQuestion(long questionId, EntityManager em) {
        return em.createQuery("select aqtr from AnsweredQuestionTimesResult aqtr where aqtr.answeredQuestionResult.answeredQuestion.answeredQuestionID = :questionID", AnsweredQuestionTimesResult.class).setParameter("questionID", questionId).getResultList();
    }

    public List<TempAnsweredQuestionTimesResult> selectByAnswer(long answerId) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<TempAnsweredQuestionTimesResult> out = selectByAnswer(answerId, em);

        em.getTransaction().commit();
        em.close();

        return out;
    }

    public List<TempAnsweredQuestionTimesResult> selectByAnswer(long answerId, EntityManager em) {
        ResultDAO resultDAO = new ResultDAO();
        List<Result> results = resultDAO.selectByAnswer(answerId, em);

        ArrayList<TempAnsweredQuestionTimesResult> tempAnsweredQuestionTimesResults = new ArrayList<>();
        if (results != null && results.size() > 0) {
            for (Result result : results) {
                List<AnsweredQuestionTimesResult> answeredQuestionTimesResults = selectByResult(result.getId(), em);

                if (answeredQuestionTimesResults != null && answeredQuestionTimesResults.size() > 0) {
                    for (AnsweredQuestionTimesResult answeredQuestionTimesResult : answeredQuestionTimesResults) {
                        Long count = getTotalCount(answeredQuestionTimesResult, em);
                        tempAnsweredQuestionTimesResults.add(new TempAnsweredQuestionTimesResult(answeredQuestionTimesResult, count));
                    }
                }
            }
        }

        return tempAnsweredQuestionTimesResults;
    }

    public List<AnsweredQuestionTimesResult> selectByResult(long resultId, EntityManager em) {
        try {
            return em.createQuery("select new AnsweredQuestionTimesResult(aqtr.answeredQuestionStatistik, aqtr.answeredQuestionResult, sum(aqtr.upvote), sum(aqtr.downvote)) from AnsweredQuestionTimesResult aqtr where aqtr.answeredQuestionResult.result.id = :id group by aqtr.answeredQuestionResult.answeredQuestion.answeredQuestionID", AnsweredQuestionTimesResult.class).setParameter("id", resultId).getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long getTotalCount(AnsweredQuestionTimesResult answeredQuestionTimesResult, EntityManager em) {
        try {
            return em.createQuery("select sum(aqtr.answeredQuestionStatistik.askedAmount) from AnsweredQuestionTimesResult aqtr " +
                            "where aqtr.answeredQuestionStatistik.answeredQuestion.isHidden = false" +
                            " and aqtr.answeredQuestionResult.answeredQuestion.answeredQuestionID = :answeredQuestionId" +
                            " and aqtr.answeredQuestionResult.resultID = :id" +
                            " group by aqtr.answeredQuestionStatistik.answeredQuestion.answeredQuestionID", Long.class)
                    .setParameter("answeredQuestionId", answeredQuestionTimesResult.getAnsweredQuestionResult().getAnsweredQuestion().getAnsweredQuestionID())
                    .setParameter("id", answeredQuestionTimesResult.getAnsweredQuestionResult().getResultID())
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0L;
        }
    }
}
