package com.ubs.backend.classes.enums;

/**
 * Enum to handle the status of a File upload
 *
 * @author Marc Andri Fuchs
 * @since 17.07.2021
 */
public enum FileUploadStatus {
    SUCCESS(200, "Datei erfolgreich hochgeladen!"),
    MAX_UPLOAD_SIZE(413, "Diese Datei ist zu gross um hochzuladen!"),
    ILLEGAL_FILE_TYPE(415, "Dieser Dateityp wird nicht unterst&uumltzt!"),
    DUPLICATE_FILE(409, "Eine Datei mit diesem Namen existiert bereits!"),
    NO_FILE_UPLOADED(422, "Es wurde keine Datei hochgeladen!");


    /**
     * The HTTPCode for this Status
     *
     * @since 17.07.2021
     */
    private final int httpCode;

    /**
     * The Response which will be sent
     *
     * @since 17.07.2021
     */
    private final String response;

    /**
     * Default Constructor
     *
     * @param httpCode The HTTPCode for this Status
     * @param response The Response which will be sent
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    FileUploadStatus(int httpCode, String response) {
        this.httpCode = httpCode;
        this.response = response;
    }

    /**
     * @return The HTTPCode for this Status
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public int getHttpCode() {
        return httpCode;
    }

    /**
     * @return The Response which will be sent
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public String getResponse() {
        return response;
    }
}
