package com.ubs.backend.classes.database.dao;

import com.ubs.backend.classes.database.Connector;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.List;

public abstract class DAO<T> {
    private final Class<T> type;

    public DAO(Class<T> type) {
        this.type = type;
    }

    public List<T> select(EntityManager em) {
        if (!checkHibernateAnnotations(type)) return null;
        List<T> list;
        try {
            list = em.createQuery("select o from " + type.getSimpleName() + " o", type).getResultList();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }

        return list;
    }

    private boolean checkHibernateAnnotations(Class<T> type) {
        return type.isAnnotationPresent(Entity.class);
    }

    public List<T> select() {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        List<T> list = select(em);

        em.getTransaction().commit();
        em.close();

        return list;
    }

    public T select(long id, EntityManager em) {
        if (!checkHibernateAnnotations(type)) {
            return null;
        }
        String idName = getIdParameterName(type);
        if (idName == null) {
            return null;
        }

        T item;
        try {
            item = em.createQuery("select o from " + type.getSimpleName() + " o where o." + idName + " = :id", type)
                    .setParameter("id", id).getSingleResult();
        } catch (NoResultException e) {
            // Couldnt find such an entry
            return null;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }

        return item;
    }

    private String getIdParameterName(Class<T> type) {
        for (Field f : type.getDeclaredFields()) {
            f.setAccessible(true);

            if (f.isAnnotationPresent(Id.class) && f.isAnnotationPresent(Column.class)) {
                return f.getName();
            }
        }
        return null;
    }

    public T select(long id) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        T item = select(id, em);

        em.getTransaction().commit();
        em.close();

        return item;
    }

    public T insert(T data, EntityManager em) {
        try {
            em.persist(data);
            return data;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    public T insert(T data) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        T newData = insert(data, em);
        

        em.getTransaction().commit();
        em.close();

        return newData;
    }

    public void remove(long id, EntityManager em) {
        try {
            em.createQuery("delete from " + type.getSimpleName() + " o where o." + getIdParameterName(type) + " = :id")
                    .setParameter("id", id).executeUpdate();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    public void remove(long id) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        remove(id, em);

        em.getTransaction().commit();
        em.close();
    }

    public void remove(T t, EntityManager em) {
        try {
            em.remove(t);
        } catch (RuntimeException e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    public void merge(T t, EntityManager em) {
        try {
            em.merge(t);
        } catch (RuntimeException e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
