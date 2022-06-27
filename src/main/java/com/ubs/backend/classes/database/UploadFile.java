package com.ubs.backend.classes.database;

import com.ubs.backend.annotations.json.JSONType;
import com.ubs.backend.annotations.json.JsonField;
import com.ubs.backend.annotations.json.JsonMethod;
import com.ubs.backend.annotations.json.JsonSerializableObject;
import com.ubs.backend.classes.database.dao.AnswerDAO;

import javax.persistence.*;
import java.sql.Blob;
import java.util.List;
import java.util.Objects;

/**
 * Dataclass for Files uploaded to the Database
 *
 * @author Marc Andri Fuchs
 * @since 17.07.2021
 */
@Entity
@Table(name = "Files")
@JsonSerializableObject(listName = "files")
public class UploadFile {
    /**
     * The ID of this UploadFile in the Database
     *
     * @since 17.07.2021
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fileID")
    @JsonField(type = JSONType.INTEGER, key = "id")
    private long fileID;

    /**
     * The Name of this UploadFile
     *
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.STRING, key = "name")
    private String fileName;

    /**
     * The MimeType of this UploadFile
     *
     * @since 17.07.2021
     */
    @JsonField(type = JSONType.STRING, key = "type")
    private String fileType;

    /**
     * The Content (BLOB) of this UploadFile
     *
     * @since 17.07.2021
     */
    @Lob
    private Blob content;

    /**
     * Default Constructor
     *
     * @param fileType the MimeType of the File
     * @param fileName the Name of the File
     * @param content  the Content of the File
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public UploadFile(String fileType, String fileName, Blob content) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.content = content;
    }

    /**
     * No-args constructor
     *
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public UploadFile() {
    }

    /**
     * @return the ID of this UploadFile
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public long getFileID() {
        return fileID;
    }

    /**
     * @param fileID the new ID for this UploadFile
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setFileID(long fileID) {
        this.fileID = fileID;
    }

    /**
     * @return the MimeType of this UploadFile
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public String getFileType() {
        return fileType;
    }

    /**
     * @param filename the new Name for this UploadFile
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setFileType(String filename) {
        this.fileType = filename;
    }

    /**
     * @return the Content of the UploadFile as a BLOB
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public Blob getContent() {
        return content;
    }

    /**
     * @param content the new Content of this UploadFile
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setContent(Blob content) {
        this.content = content;
    }

    /**
     * @return the Name of this UploadFile
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName the new Name for this UploadFile
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * @return
     * @author Marc Andri Fuchs
     * @since 17.07.2021
     */
    @JsonMethod(type = JSONType.INTEGER, key = "answerCount")
    public int answerCount() {

        EntityManager em = Connector.getInstance().open();
        em.getTransaction().begin();

        AnswerDAO answerDAO = new AnswerDAO();

        List<Answer> answers = answerDAO.select();

        int count = 0;
        for (Answer a : answers) {
            for (UploadFile f : a.getFiles()) {
                if (f.fileID == this.fileID) {
                    count++;
                    break;
                }
            }
        }

        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UploadFile that = (UploadFile) o;
        return fileID == that.fileID && Objects.equals(fileName, that.fileName) && Objects.equals(fileType, that.fileType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileID, fileName, fileType, content);
    }

    @Override
    public String toString() {
        return "UploadFile{" +
                "fileID=" + fileID +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
