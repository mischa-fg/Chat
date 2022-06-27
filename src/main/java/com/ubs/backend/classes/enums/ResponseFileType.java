package com.ubs.backend.classes.enums;

/**
 * Enum to specify which file types should be allowed on a File Upload
 *
 * @author Marc Andri Fuchs
 * @since 17.07.2021
 */
public enum ResponseFileType {
    // Images
    /**
     * .PNG Files
     *
     * @since 17.07.2021
     */
    IMAGE_PNG("image/png", "png"),

    /**
     * .JPG/.JPEG Files
     *
     * @since 17.07.2021
     */
    IMAGE_JPEG("image/jpeg", "jpeg"),

    /**
     * .GIF Files
     *
     * @since 17.07.2021
     */
    IMAGE_GIF("image/gif", "gif"),

    /**
     * .PDF Files
     *
     * @since 17.07.2021
     */
    APPLICATION_PDF("application/pdf", "pdf"),

    /**
     * .TXT Files
     *
     * @since 17.07.2021
     */
    TEXT_PLAIN("text/plain", "txt"),

    // Office Documents
    /**
     * .XLSX Files (Excel 2007)
     *
     * @since 17.07.2021
     */
    EXCEL_2007("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx"),

    /**
     * .XLS Files (Excel 1997)
     *
     * @since 17.07.2021
     */
    EXCEL_1997("application/vnd.ms-excel", "xls"),

    /**
     * .DOCX Files (Word 2007)
     *
     * @since 17.07.2021
     */
    WORD_2007("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),

    /**
     * .DOC Files (Word 2003)
     *
     * @since 17.07.2021
     */
    WORD_2003("application/msword", "doc"),

    /**
     * .PPTX Files (PowerPoint 2007)
     *
     * @since 17.07.2021
     */
    POWERPOINT_2007("application/vnd.openxmlformats-officedocument.presentationml.presentation", "pptx"),

    /**
     * .PPT Files (PowerPoint 2004)
     *
     * @since 17.07.2021
     */
    POWERPOINT_2004("application/vnd.ms-powerpoint", "ppt");

    /**
     * The MimeType of the FileType
     *
     * @since 17.07.2021
     */
    private final String mimeType;

    /**
     * The FileExtension of the File
     *
     * @since 17.07.2021
     */
    private final String extension;

    /**
     * Default Constructor
     *
     * @param mimeType  The MimeType for the FileType
     * @param extension The FileExtension of the File
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    ResponseFileType(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    /**
     * @param mimeType
     * @return
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public static ResponseFileType getByMimeType(String mimeType) {
        for (ResponseFileType t : values()) {
            if (t.getMimeType().equals(mimeType)) return t;
        }
        return null;
    }

    /**
     * @return The MimeType of the FileType
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @return The FileExtension of the File
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public String getExtension() {
        return extension;
    }
}
