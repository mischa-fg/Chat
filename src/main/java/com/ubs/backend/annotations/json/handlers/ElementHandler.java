package com.ubs.backend.annotations.json.handlers;

import com.ubs.backend.annotations.json.ElementTypeException;
import com.ubs.backend.annotations.json.JSONParser;

/**
 * Interface for all Handlers for JSON Objects
 */
public interface ElementHandler {
    /**
     * Handles the JSON Object generation
     * @param o the Object to convert
     * @return the value for the JSON Object
     */
    String handle(Object o) throws ElementTypeException, JSONParser.JsonSerializationException;

    /**
     * @return the type with which this Handler should be associated with
     */
    String getType();

    /**
     * @param o the object to check
     * @return if an Object can be handled with this handler
     */
    boolean canHandle(Object o);
}