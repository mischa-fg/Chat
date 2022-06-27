package com.ubs.backend.demo;

import com.ubs.backend.annotations.json.*;
import com.ubs.backend.annotations.json.handlers.ElementHandler;
import com.ubs.backend.classes.database.Tag;
import com.ubs.backend.classes.database.dao.TagDAO;

import java.math.BigInteger;
import java.util.List;

/**
 * @author Marc
 * @since 17.07.2021
 */
public class JSOnTest {
    /**
     * @param args
     * @author Marc
     * @since 17.07.2021
     */
    public static void main(String[] args) {
        JSONParser parser = new JSONParser();
        TagDAO tagDAO = new TagDAO();

        List<Tag> tags = tagDAO.select();

        try {
            String tagsString = parser.listToJSON(tags, Tag.class, ParserResponseType.LIST);
            
        } catch (JSONParser.JsonSerializationException e) {
            e.printStackTrace();
        }
    }

    /**
     * @author Marc
     */
    @JsonSerializableObject(listName = "persons")
    private static class Person {
        /**
         * the age of the person
         *
         * @since 17.07.2021
         */
        @JsonField(type = JSONType.CUSTOM, customType = "custom")
        private BigInteger age;

        /**
         * the name of the person
         *
         * @since 17.07.2021
         */
        @JsonField(type = JSONType.STRING)
        private String name;

        /**
         * @param age
         * @param name
         * @author Marc
         * @since 17.07.2021
         */
        public Person(BigInteger age, String name) {
            this.age = age;
            this.name = name;
        }
    }

    /**
     * @author marc
     * @since 17.07.2021
     */
    private static class CustomHandler implements ElementHandler {
        @Override
        public String handle(Object o) throws ElementTypeException, JSONParser.JsonSerializationException {
            return "0";
        }

        @Override
        public String getType() {
            return "custom";
        }

        @Override
        public boolean canHandle(Object o) {
            return o != null;
        }
    }
}
