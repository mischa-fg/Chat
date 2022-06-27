package com.ubs.backend.services;

import com.ubs.backend.classes.TempAmountWithDate;
import com.ubs.backend.classes.database.UserLogin;
import com.ubs.backend.classes.database.dao.UserLoginDAO;
import com.ubs.backend.classes.database.dao.statistik.AnswerStatistikDAO;
import com.ubs.backend.classes.database.dao.statistik.AnsweredQuestionStatistikDAO;
import com.ubs.backend.classes.database.dao.statistik.UnansweredQuestionStatistikDAO;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;
import com.ubs.backend.classes.enums.TimeSearchType;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.ubs.backend.util.GetTimeRange.getTimeRangeByString;
import static com.ubs.backend.util.GetTimeStringFromTime.getTimeStringFromTime;

/**
 * @author Tim Irmler
 * @since 25.08.2021
 */
@Path("getStatistics")
public class GetStatistics {
    /**
     * @param timeRangeString the time range which the statistics will be returned for
     * @return a JSON Array with the Statistics
     * @author Tim Irmler
     * @since 25.08.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/sentAnswersPerTime")
    public String allSentAnswersPerTime(@QueryParam("timeRange") String timeRangeString, @Context HttpServletRequest request) {
        long userID = (long) request.getSession().getAttribute("user");
        UserLoginDAO userLoginDAO = new UserLoginDAO();
        UserLogin user = userLoginDAO.select(userID);

        TimeSearchType time;

        if (user != null) {
            time = getTimeRangeByString(timeRangeString);
        } else {
            time = getTimeRangeByString(timeRangeString, new TimeSearchType[]{TimeSearchType.SINCE_LAST_LOGIN});
        }

        AnswerStatistikDAO answerStatistikDAO = new AnswerStatistikDAO();
        List<TempAmountWithDate> amounts = answerStatistikDAO.countAskedAmountByTimeGroupedByAnswer(userID, time, null);

        StringBuilder json = new StringBuilder("[");

        Iterator<TempAmountWithDate> tempAmountWithDateIterator = amounts.iterator();
        while (tempAmountWithDateIterator.hasNext()) {
            TempAmountWithDate tempAmountWithDate = tempAmountWithDateIterator.next();

            String amountString = "{";

            String timeString = getTimeStringFromTime(tempAmountWithDate.getTimes(), time);

            amountString += "\"amount\":" + tempAmountWithDate.getAmount() + ",";
            amountString += "\"date\":\"" + timeString + "\"";

            amountString += "}";

            if (tempAmountWithDateIterator.hasNext()) amountString += ",";

            json.append(amountString);
        }

        json.append("]");

        return json.toString();
    }

    /**
     * @param timeRangeString the time range which the statistics will be returned for
     * @return a JSON Array with the Statistics
     * @author Tim Irmler
     * @since 30.08.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/answeredVsUnansweredPerTime")
    public String answeredVsUnansweredPerTime(@QueryParam("timeRange") String timeRangeString, @Context HttpServletRequest request) {
        long userID = (long) request.getSession().getAttribute("user");
        UserLoginDAO userLoginDAO = new UserLoginDAO();
        UserLogin user = userLoginDAO.select(userID);

        TimeSearchType time;

        if (user != null) {
            time = getTimeRangeByString(timeRangeString);
        } else {
            time = getTimeRangeByString(timeRangeString, new TimeSearchType[]{TimeSearchType.SINCE_LAST_LOGIN});
        }

        AnsweredQuestionStatistikDAO answeredQuestionStatistikDAO = new AnsweredQuestionStatistikDAO();
        List<TempAmountWithDate> amountsAnswered = answeredQuestionStatistikDAO.countAskedAmountByTime(userID, time, null);

        

        UnansweredQuestionStatistikDAO unansweredQuestionStatistikDAO = new UnansweredQuestionStatistikDAO();
        List<TempAmountWithDate> amountsUnanswered = unansweredQuestionStatistikDAO.countAskedAmountByTime(userID, time, null);

        

        ArrayList<StatistikTimes> unfilteredDates = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            boolean answered = (i < 1);
            List<TempAmountWithDate> currentList = (answered) ? amountsAnswered : amountsUnanswered;
            for (TempAmountWithDate tempAmountWithDate : currentList) {
                unfilteredDates.add(tempAmountWithDate.getTimes());
            }
        }

        
        for (StatistikTimes statistikTimes : unfilteredDates) {
            System.out.print(statistikTimes.getFormatted(true) + ",");
        }
        
        

        for (int i = 0; i < unfilteredDates.size(); i++) {
            for (int j = 1; j < (unfilteredDates.size() - i); j++) {
                long num1 = unfilteredDates.get(j - 1).getStatistikTimeNumber();
                long num2 = unfilteredDates.get(j).getStatistikTimeNumber();
                if (num1 > num2) {
                    //swap elements
                    StatistikTimes temp = unfilteredDates.get(j - 1);
                    unfilteredDates.set(j - 1, unfilteredDates.get(j));
                    unfilteredDates.set(j, temp);
                }
            }
        }

        
        for (StatistikTimes statistikTimes : unfilteredDates) {
            System.out.print(statistikTimes.getFormatted(true) + ",");
        }
        
        
        ArrayList<StatistikTimes> datesFiltered = new ArrayList<>();
        for (StatistikTimes statistikTimes : unfilteredDates) {
            if (!datesFiltered.contains(statistikTimes)) {
                boolean notFound = true;
                for (StatistikTimes statistikTimes1 : datesFiltered) {
                    if (statistikTimes.isSamePerTimeSpecial(time, statistikTimes1)) {
                        
                        notFound = false;
                        break;
                    }
                }
                if (notFound) {
                    datesFiltered.add(statistikTimes);
                }
            }
        }
        
        for (StatistikTimes statistikTimes : datesFiltered) {
            System.out.print(statistikTimes.getFormatted(true) + ",");
        }
        
        

        ArrayList<TempAmountWithDate> afterCheckAmountsUnanswered = new ArrayList<>();
        ArrayList<TempAmountWithDate> afterCheckAmountsAnswered = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            boolean answered = (i < 1);
            List<TempAmountWithDate> currentListToFill = (answered) ? amountsAnswered : amountsUnanswered;
            List<TempAmountWithDate> currentListToCheckWith = (answered) ? amountsUnanswered : amountsAnswered;
            ArrayList<TempAmountWithDate> targetArrayFilled = (answered) ? afterCheckAmountsAnswered : afterCheckAmountsUnanswered;

            String listName = (answered) ? "answered" : "unanswered";

            
            for (TempAmountWithDate tempAmountWithDate : currentListToFill) {
                System.out.print("{" + tempAmountWithDate.getAmount() + "," + tempAmountWithDate.getTimes().getFormatted(true) + "}" + ",");
            }
            
            

            // handle fill before and between
            int counter = 0;
            for (int ii = 0; ii < currentListToFill.size(); ii++) {
                TempAmountWithDate currentToFill = currentListToFill.get(ii);
                for (int j = counter; j < currentListToCheckWith.size(); j++) {
                    TempAmountWithDate currentToCheck = currentListToCheckWith.get(j);
                    boolean increaseCounter = false;
                    if (currentToCheck.getTimes().isDifferentPerTimeSpecial(time, currentToFill.getTimes(), true)) {
                        increaseCounter = true;
                        targetArrayFilled.add(new TempAmountWithDate(currentToCheck.getTimes(), 0L));
                    } else if (currentToCheck.getTimes().isSamePerTimeSpecial(time, currentToFill.getTimes())) {
                        increaseCounter = true;
                    }

                    if (increaseCounter) {
                        counter++;
                    }
                }
                targetArrayFilled.add(currentToFill);
            }

            
            for (TempAmountWithDate tempAmountWithDate : targetArrayFilled) {
                System.out.print("{" + tempAmountWithDate.getAmount() + "," + tempAmountWithDate.getTimes().getFormatted(true) + "}" + ",");
            }
            
            
        }

        if (afterCheckAmountsAnswered.size() != afterCheckAmountsUnanswered.size()) {
            boolean answeredBoolean = (afterCheckAmountsUnanswered.size() < afterCheckAmountsAnswered.size());
            ArrayList<TempAmountWithDate> longerArray = (answeredBoolean) ? afterCheckAmountsAnswered : afterCheckAmountsUnanswered;
            ArrayList<TempAmountWithDate> shorterArray = (answeredBoolean) ? afterCheckAmountsUnanswered : afterCheckAmountsAnswered;

            for (int i = shorterArray.size(); i < longerArray.size(); i++) {
                shorterArray.add(new TempAmountWithDate(longerArray.get(i).getTimes(), 0L));
            }

            
            for (TempAmountWithDate tempAmountWithDate : shorterArray) {
                System.out.print("{" + tempAmountWithDate.getAmount() + "," + tempAmountWithDate.getTimes().getFormatted(true) + "}" + ",");
            }
            
            
        }

        StringBuilder json = new StringBuilder("{");

        StringBuilder answered = new StringBuilder("\"answered\":[");
        StringBuilder unanswered = new StringBuilder("\"unanswered\":[");
        for (int i = 0; i < 2; i++) {
            boolean answeredIs = (i < 1);
            Iterator<TempAmountWithDate> amountsIterator = (answeredIs) ? afterCheckAmountsAnswered.iterator() : afterCheckAmountsUnanswered.iterator();
            while (amountsIterator.hasNext()) {
                long amount = amountsIterator.next().getAmount();

                String amountString = "{";

                amountString += "\"amount\":" + amount;

                amountString += "}";

                if (amountsIterator.hasNext()) amountString += ",";

                if (answeredIs) {
                    answered.append(amountString);
                } else {
                    unanswered.append(amountString);
                }
            }
        }

        answered.append("],");
        unanswered.append("],");

        StringBuilder dates = new StringBuilder("\"dates\":[");
        Iterator<StatistikTimes> statistikTimesIterator = datesFiltered.iterator();
        while (statistikTimesIterator.hasNext()) {
            StatistikTimes statistikTimes = statistikTimesIterator.next();
            

            String dateString = "{";
            String timeString = getTimeStringFromTime(statistikTimes, time);

            dateString += "\"date\":\"" + timeString + "\"";

            dateString += "}";

            if (statistikTimesIterator.hasNext()) dateString += ",";

            dates.append(dateString);
        }

        dates.append("]");

        json.append(answered).append(unanswered).append(dates).append("}");

        

        return json.toString();
    }

    /**
     * @param timeRangeString the time range which the statistics will be returned for
     * @return a JSON Array with the Statistics
     * @author Tim Irmler
     * @since 30.08.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/answeredQuestionsPerTime")
    public String amountAnsweredQuestionsPerTime(@QueryParam("timeRange") String timeRangeString, @Context HttpServletRequest request) {
        long userID = (long) request.getSession().getAttribute("user");
        UserLoginDAO userLoginDAO = new UserLoginDAO();
        UserLogin user = userLoginDAO.select(userID);

        TimeSearchType time;

        if (user != null) {
            time = getTimeRangeByString(timeRangeString);
        } else {
            time = getTimeRangeByString(timeRangeString, new TimeSearchType[]{TimeSearchType.SINCE_LAST_LOGIN});
        }

        AnsweredQuestionStatistikDAO answeredQuestionStatistikDAO = new AnsweredQuestionStatistikDAO();
        List<TempAmountWithDate> amounts = answeredQuestionStatistikDAO.countAskedAmountByTime(userID, time, null);

        StringBuilder json = new StringBuilder("[");

        Iterator<TempAmountWithDate> tempAmountWithDateIterator = amounts.iterator();
        while (tempAmountWithDateIterator.hasNext()) {
            TempAmountWithDate tempAmountWithDate = tempAmountWithDateIterator.next();

            String amountString = "{";

            String timeString = getTimeStringFromTime(tempAmountWithDate.getTimes(), time);

            amountString += "\"amount\":" + tempAmountWithDate.getAmount() + ",";
            amountString += "\"date\":\"" + timeString + "\"";

            amountString += "}";

            if (tempAmountWithDateIterator.hasNext()) amountString += ",";

            json.append(amountString);
        }

        json.append("]");

        return json.toString();
    }

    /**
     * @param timeRangeString the time range which the statistics will be returned for
     * @return a JSON Array with the Statistics
     * @author Tim Irmler
     * @since 30.08.2021
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/unansweredQuestionsPerTime")
    public String amountUnansweredQuestionsPerTime(@QueryParam("timeRange") String timeRangeString, @Context HttpServletRequest request) {
        long userID = (long) request.getSession().getAttribute("user");
        UserLoginDAO userLoginDAO = new UserLoginDAO();
        UserLogin user = userLoginDAO.select(userID);

        TimeSearchType time;

        if (user != null) {
            time = getTimeRangeByString(timeRangeString);
        } else {
            time = getTimeRangeByString(timeRangeString, new TimeSearchType[]{TimeSearchType.SINCE_LAST_LOGIN});
        }

        UnansweredQuestionStatistikDAO answeredQuestionStatistikDAO = new UnansweredQuestionStatistikDAO();
        List<TempAmountWithDate> amounts = answeredQuestionStatistikDAO.countAskedAmountByTime(userID, time, null);

        StringBuilder json = new StringBuilder("[");

        Iterator<TempAmountWithDate> tempAmountWithDateIterator = amounts.iterator();
        while (tempAmountWithDateIterator.hasNext()) {
            TempAmountWithDate tempAmountWithDate = tempAmountWithDateIterator.next();

            String amountString = "{";

            String timeString = getTimeStringFromTime(tempAmountWithDate.getTimes(), time);

            amountString += "\"amount\":" + tempAmountWithDate.getAmount() + ",";
            amountString += "\"date\":\"" + timeString + "\"";

            amountString += "}";

            if (tempAmountWithDateIterator.hasNext()) amountString += ",";

            json.append(amountString);
        }

        json.append("]");

        return json.toString();
    }
}
