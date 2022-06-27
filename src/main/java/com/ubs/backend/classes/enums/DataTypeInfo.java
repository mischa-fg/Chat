package com.ubs.backend.classes.enums;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonSerializableObject;

@JsonSerializableObject(listName = "types")
public enum DataTypeInfo {
    ANSWER_TEXT(2048, "ANSWER_TEXT"),
    ANSWER_TITLE(255, "ANSWER_TITLE"),
    TAG(64, "TAG"),
    USER_PASSWORD(255, "USER_PASSWORD"),
    USER_EMAIL(255, "USER_EMAIL"),
    USER_QUESTION_INPUT(255, "USER_QUESTION_INPUT"), // also used for default question length
    BLACK_LIST_ENTRY(64, "BLACK_LIST_ENTRY"),
    FILE(2621440, "FILE");

    @JsonField(type = JSONType.INTEGER)
    private final int maxLength;

    @JsonField(type = JSONType.STRING)
    private final String name;
    private static final DataTypeInfo[] values = values();

    DataTypeInfo(int maxLength, String name) {
        this.maxLength = maxLength;
        this.name = name;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public String getName() {
        return name;
    }

    public static DataTypeInfo[] getValues() {
        return values;
    }
}
