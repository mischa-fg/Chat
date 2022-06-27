package com.ubs.backend.annotations.json;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If a Class is annotated with this Annotation it can be converted into a JSON Object
 * all values which should be contained in the JSON Object need to be annotated with @JsonElement or if it is a method @JsonFunction
 *
 * @see JsonField
 * @see JsonMethod
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JsonSerializableObject {
    /**
     * @return the name of the Object if it is a single list
     */
    String listName();
}
