package com.ubs.backend.annotations.json;

/**
 * Datatypes which can be set for an object.
 * Always has to be set in a JsonElement and JsonFunction annotation
 *
 * @see JsonField
 * @see JsonMethod
 */
public enum JSONType {
    /**
     * Datatype Float
     *
     * @see Float
     * @see com.ubs.backend.annotations.json.handlers.DefaultHandlers.FloatHandler
     */
    FLOAT,

    /**
     * Datatype Double
     *
     * @see Double
     * @see com.ubs.backend.annotations.json.handlers.DefaultHandlers.DoubleHandler
     */
    DOUBLE,

    /**
     * All Datatypes which are Integers.
     *
     * @see Long
     * @see Integer
     * @see Short
     * @see Byte
     * @see com.ubs.backend.annotations.json.handlers.DefaultHandlers.IntegerHandler
     */
    INTEGER,

    /**
     * Datatype String
     *
     * @see com.ubs.backend.annotations.json.handlers.DefaultHandlers.StringHandler
     */
    STRING,

    /**
     * Datatype Boolean
     *
     * @see com.ubs.backend.annotations.json.handlers.DefaultHandlers.BooleanHandler
     */
    BOOLEAN,

    /**
     * All kinds of lists
     *
     * @see com.ubs.backend.annotations.json.handlers.DefaultHandlers.ListHandler
     */
    LIST,

    /**
     * Class with Json Annotation
     *
     * @see com.ubs.backend.annotations.json.handlers.DefaultHandlers.JSONAnnotatedHandler
     * @see JsonSerializableObject
     * @see JsonField
     * @see JsonMethod
     */
    JSON_ANNOTATED,

    /**
     * Used with custom ElementHandler, customType has to be set in the JsonElement or JsonFunction annotation
     *
     * @see com.ubs.backend.annotations.json.handlers.ElementHandler
     * @see JsonSerializableObject
     * @see JsonMethod
     * @see JsonField
     */
    CUSTOM,

    /**
     * Enum variables.
     * The Enum will be converted to it's ordinal number
     *
     * @see com.ubs.backend.annotations.json.handlers.DefaultHandlers.EnumerationHandlerOrdinal
     * @see Enum
     */
    ENUMERATED_ORDINAL,

    /**
     * Enum variables.
     * The Enum will be converted to it's ordinal number
     *
     * @see com.ubs.backend.annotations.json.handlers.DefaultHandlers.EnumerationHandler
     * @see Enum
     */
    ENUMERATED_STRING
}
