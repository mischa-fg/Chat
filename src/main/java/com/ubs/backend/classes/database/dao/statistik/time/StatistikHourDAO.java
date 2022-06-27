package com.ubs.backend.classes.database.dao.statistik.time;

import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.dao.DAO;
import com.ubs.backend.classes.database.statistik.times.StatistikHour;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import java.util.Date;

public class StatistikHourDAO extends DAO<StatistikHour> {
    public StatistikHourDAO() {
        super(StatistikHour.class);
    }

    public StatistikHour selectByDate(Date date, EntityManager em) {
        try {
            return em.createQuery("select h from StatistikHour h where h.myDate = :date", StatistikHour.class).setParameter("date", StatistikHour.getSimpleDateFormatStatic(date)).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public StatistikHour selectByDate(Date date) {
        EntityManager em = Connector.getInstance().open();
        StatistikHour out = null;

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
