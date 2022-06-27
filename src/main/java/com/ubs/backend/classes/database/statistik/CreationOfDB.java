package com.ubs.backend.classes.database.statistik;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;
import com.ubs.backend.classes.database.dao.statistik.time.StatistikTimesDAO;
import com.ubs.backend.classes.database.statistik.times.StatistikTimes;

import javax.persistence.*;

/**
 * @author Tim Irmler
 * @since 13.08.2021
 */
@Entity
@JsonSerializableObject(listName = "creationOfDBs")
public class CreationOfDB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonField(type = JSONType.INTEGER)
    private long id;

    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JsonField(type = JSONType.LIST)
    private StatistikTimes statistikTimes;

    public CreationOfDB() {
    }

    public CreationOfDB(EntityManager em) {
        StatistikTimesDAO statistikTimesDAO = new StatistikTimesDAO();
        this.statistikTimes = statistikTimesDAO.selectNow(true, em);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public StatistikTimes getStatistikTimes() {
        return statistikTimes;
    }

    public void setStatistikTimes(StatistikTimes statistikTimes) {
        this.statistikTimes = statistikTimes;
    }
}
