package com.ubs.backend.annotations.json;

/**
 * This exception will be throws if an Object was handled with the wrong handler
 *
 * @see JsonField
 * @see com.ubs.backend.annotations.json.handlers.ElementHandler
 */
public class ElementTypeException extends Exception {
    /**
     * Constructor to throw the Exception
     *
     * @param message the message which will be shown
     */
    public ElementTypeException(String message) {
        super(message);
    }
}
