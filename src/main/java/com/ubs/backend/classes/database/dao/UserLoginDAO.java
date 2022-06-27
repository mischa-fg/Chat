package com.ubs.backend.classes.database.dao;

import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.UserLogin;
import com.ubs.backend.classes.database.dao.statistik.time.StatistikTimesDAO;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import static com.ubs.backend.util.PrintDebug.printDebug;

/**
 * DAO for com.ubs.backend.classes.database.UserLogin
 *
 * @author Marc Andri Fuchs
 * @author Magnus
 * @since 17.07.2021
 */
public class UserLoginDAO extends DAO<UserLogin> {

    public UserLoginDAO() {
        super(UserLogin.class);
    }

    /**
     * @param email
     * @return
     * @author Tim Irmler
     */
    public UserLogin selectByEmail(String email) {
        EntityManager em = Connector.getInstance().open();
        UserLogin userLogin = null;

        try {
            em.getTransaction().begin();

            userLogin = selectByEmail(email, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return userLogin;
    }

    /**
     * @param email
     * @param em
     * @return
     * @author Tim Irmler
     */
    public UserLogin selectByEmail(String email, EntityManager em) {
        try {
            return em.createQuery("select u from UserLogin u where lower(u.email) = lower(:email)", UserLogin.class).setParameter("email", email).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Selects a UserLogin by its ID
     *
     * @return the user whose ID is the given id
     */
    public UserLogin select(long id) {
        EntityManager em = Connector.getInstance().open();
        UserLogin userLogin = null;

        try {
            em.getTransaction().begin();

            userLogin = select(id, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
        return userLogin;
    }

    /**
     * Sets the Password of a UserLogin
     *
     * @param userLogin the UserLogin where the Password will be changed
     * @param password  the new Password
     */
    public void setPassword(UserLogin userLogin, String password) {
        EntityManager em = Connector.getInstance().open();

        try {
            em.getTransaction().begin();

            setPassword(userLogin.getUserLoginID(), password, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * @param id
     * @param password
     * @author Tim Irmler
     */
    public void setPassword(long id, String password) {
        EntityManager em = Connector.getInstance().open();

        try {
            em.getTransaction().begin();

            setPassword(id, password, em);

            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * @param id
     * @param password
     * @param em
     * @author Tim Irmler
     */
    public void setPassword(long id, String password, EntityManager em) {
        UserLogin user = select(id, em);
        user.setPassword(password);
    }

    public void updateLastLoginTime(long id) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        updateLastLoginTime(id, em);

        em.getTransaction().commit();
        em.close();
    }

    /**
     * @param id
     * @param em
     * @author Tim Irmler
     * @since 07.08.2021
     */
    public void updateLastLoginTime(long id, EntityManager em) {
        UserLogin userLogin = select(id, em);
        if (userLogin != null) {
            StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
            if (userLogin.getActualLastTimeLoggedIn() != null) {
                userLogin.setTempLastTimeLoggedIn(userLogin.getActualLastTimeLoggedIn());
            }
            userLogin.setActualLastTimeLoggedIn(statistikTimesDAO.selectNow(true, em));
        }
    }

    /**
     * @param userLogin
     * @author Tim Irmler
     * @author Sarah Ambi
     * @since 06.08.2021
     */
    public void update(UserLogin userLogin) {
        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();
        UserLogin userLoginToUpdate = select(userLogin.getUserLoginID(), em);
        if (userLoginToUpdate != null) {
            boolean canCreateUser;
            if (userLogin.isCanCreateUsers() == null) {
                canCreateUser = userLoginToUpdate.isCanCreateUsers();
            } else {
                canCreateUser = userLogin.isCanCreateUsers();
            }
            printDebug("userLogin", userLogin);
            printDebug("userToUpdate", userLoginToUpdate);
            printDebug("canCreateUser", canCreateUser);
            userLoginToUpdate.setEmail(userLogin.getEmail());
            userLoginToUpdate.setCanCreateUsers(canCreateUser);
        }
        em.getTransaction().commit();
        em.close();
    }
}
