package com.ubs.backend.classes.database.dao.statistik.time;

import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.dao.DAO;
import com.ubs.backend.classes.database.statistik.times.StatistikMonth;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;

public class StatistikMonthDAO extends DAO<StatistikMonth> {
    public StatistikMonthDAO() {
        super(StatistikMonth.class);
    }

    public StatistikMonth selectByDate(Date date, EntityManager em) {
        try {
            return em.createQuery("select m from StatistikMonth m where m.myDate = :date", StatistikMonth.class).setParameter("date", StatistikMonth.getSimpleDateFormatStatic(date)).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public StatistikMonth selectByDate(Date date) {
        EntityManager em = Connector.getInstance().open();

        em.getTransaction().begin();
        StatistikMonth out = selectByDate(date, em);

        em.getTransaction().commit();
        em.close();

        return out;
    }
}
