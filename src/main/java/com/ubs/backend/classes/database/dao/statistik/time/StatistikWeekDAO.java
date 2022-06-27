package com.ubs.backend.classes.database.dao.statistik.time;

import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.dao.DAO;
import com.ubs.backend.classes.database.statistik.times.StatistikWeek;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;

public class StatistikWeekDAO extends DAO<StatistikWeek> {
    public StatistikWeekDAO() {
        super(StatistikWeek.class);
    }

    public StatistikWeek selectByDate(Date date, EntityManager em) {
        try {
            return em.createQuery("select w from StatistikWeek w where w.myDate = :date", StatistikWeek.class).setParameter("date", StatistikWeek.getSimpleDateFormatStatic(date)).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public StatistikWeek selectByDate(Date date) {
        EntityManager em = Connector.getInstance().open();
        StatistikWeek out = null;

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
}
