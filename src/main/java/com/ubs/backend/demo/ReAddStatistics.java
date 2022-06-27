package com.ubs.backend.demo;

import com.ubs.backend.classes.database.Connector;
import com.ubs.backend.classes.database.Tag;
import com.ubs.backend.classes.database.TypeTag;
import com.ubs.backend.classes.database.dao.TypeTagDAO;
import com.ubs.backend.classes.enums.AnswerType;

import javax.persistence.EntityManager;
import java.util.ArrayList;

public class ReAddStatistics {
    public static void main(String[] args) {
        TypeTagDAO typeTagDAO = new TypeTagDAO();

        Tag statistik = new Tag("statistik");
        ArrayList<TypeTag> typeTags = new ArrayList<>();
        typeTags.add(new TypeTag(statistik, AnswerType.STATISTICS));

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        for (TypeTag typeTag : typeTags) {
            typeTagDAO.insert(typeTag, em);
        }

        em.getTransaction().commit();
        em.close();
    }
}
