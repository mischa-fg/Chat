package com.ubs.backend.classes.database;

import com.ubs.backend.classes.SHA256;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;

import javax.persistence.*;

/**
 * Dataclass to Store UserLogins for the AdminTool
 *
 * @author Marc Andri Fuchs
 * @since 17.07.2021
 */
@Entity
@Table(name = "Logins")
public class UserLogin {
    /**
     * The ID of this UserLogin in the Database
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loginID")
    private long userLoginID;

    /**
     * The Email address of this UserLogin
     *
     * @since 17.07.2021
     */
    @Column(length = 255)
    private String email;

    /**
     * The Password for this UserLogin
     *
     * @since 17.07.2021
     */
    @Column(length = 255)
    private String password;

    /**
     * If a User is allowed to create more users
     */
    @Column(nullable = false)
    private Boolean canCreateUsers = false;

    /**
     * the last time this user was logged in (updated as soon as the user logs in)
     */
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private StatistikTimes actualLastTimeLoggedIn = null;

    /**
     * the last time the user was logged in, before updating the actualLastTime. used for figuring the time out between each log in and show the correct stuff on the overview page
     */
    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    private StatistikTimes tempLastTimeLoggedIn = null;

    /**
     * No-args constructor
     *
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public UserLogin() {

    }

    /**
     * Constructor
     *
     * @param email    the Email Address for this UserLogin
     * @param password the Password for this UserLogin
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public UserLogin(String email, String password) {
        super();
        this.email = email;
        this.password = SHA256.getHexStringInstant(password);
    }

    /**
     * Recommended Constructor
     *
     * @param email    the Email Address for this UserLogin
     * @param password the Password for this UserLogin
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public UserLogin(String email, String password, boolean canCreateUsers) {
        this.email = email;
        this.password = SHA256.getHexStringInstant(password);
        this.canCreateUsers = canCreateUsers;
    }

    public UserLogin(long userLoginID, String email, boolean canCreateUsers) {
        this.userLoginID = userLoginID;
        this.email = email;
        this.canCreateUsers = canCreateUsers;
    }

    public UserLogin(long userLoginID, String email) {
        this.userLoginID = userLoginID;
        this.email = email;
        this.canCreateUsers = null;
    }

    public UserLogin(long userLoginID, String email, boolean canCreateUsers, StatistikTimes actualLastTimeLoggedIn) {
        this.userLoginID = userLoginID;
        this.email = email;
        this.canCreateUsers = canCreateUsers;
        this.actualLastTimeLoggedIn = actualLastTimeLoggedIn;
    }

    public UserLogin(String email, String password, boolean canCreateUsers, StatistikTimes actualLastTimeLoggedIn) {
        this.email = email;
        this.password = SHA256.getHexStringInstant(password);
        this.canCreateUsers = canCreateUsers;
        this.actualLastTimeLoggedIn = actualLastTimeLoggedIn;
    }

    @Override
    public String toString() {
        return "UserLogin{" +
                "userLoginID=" + userLoginID +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", canCreateUsers=" + canCreateUsers +
                ", actualLastTimeLoggedIn=" + actualLastTimeLoggedIn +
                ", tempLastTimeLoggedIn=" + tempLastTimeLoggedIn +
                '}';
    }

    /**
     * @return the ID of this UserLogin
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public long getUserLoginID() {
        return userLoginID;
    }

    /**
     * @param id the new ID for this UserLogin
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setUserLoginID(long id) {
        this.userLoginID = id;
    }

    /**
     * @return the Email Address of this UserLogin
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the new Email Address for this UserLogin
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the Password of this UserLogin
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the new Password for this UserLogin
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setPassword(String password) {
        this.password = SHA256.getHexStringInstant(password);
        ;
    }

    /**
     * @param canCreateUsers if the User is allowed to create more Users
     */
    public void setCanCreateUsers(boolean canCreateUsers) {
        this.canCreateUsers = canCreateUsers;
    }

    /**
     * @return if the User is allowed to create more Users
     */
    public Boolean isCanCreateUsers() {
        return canCreateUsers;
    }

    public StatistikTimes getActualLastTimeLoggedIn() {
        return actualLastTimeLoggedIn;
    }

    public void setActualLastTimeLoggedIn(StatistikTimes lastTimeLoggedIn) {
        this.actualLastTimeLoggedIn = lastTimeLoggedIn;
    }

    public StatistikTimes getTempLastTimeLoggedIn() {
        return tempLastTimeLoggedIn;
    }

    public void setTempLastTimeLoggedIn(StatistikTimes tempLastTimeLoggedIn) {
        this.tempLastTimeLoggedIn = tempLastTimeLoggedIn;
    }
}
