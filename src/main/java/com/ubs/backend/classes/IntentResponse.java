package com.ubs.backend.classes;

import com.ubs.backend.classes.database.UploadFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marc
 * @since 17.07.2021
 */
public class IntentResponse {
    private String answer;

    /**
     * @see BotResult
     * @since 17.07.2021
     */
    private List<BotResult> results;

    /**
     * @see UploadFile
     * @since 17.07.2021
     */
    private List<UploadFile> files;

    private long id;

    /**
     * @param answer
     * @param results
     * @param files
     * @param id
     * @author Marc
     * @since 17.07.2021
     */
    public IntentResponse(String answer, List<BotResult> results, List<UploadFile> files, long id) {
        this.answer = answer;
        this.results = results;
        this.files = files;
        this.id = id;
    }

    /**
     * @return
     * @author Marc
     * @since 17.07.2021
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * @param answer
     * @author Marc
     * @since 17.07.2021
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * @return
     * @author Marc
     * @since 17.07.2021
     */
    public List<BotResult> getResults() {
        return results;
    }

    /**
     * @param results
     * @author Marc
     * @since 17.07.2021
     */
    public void setResults(ArrayList<BotResult> results) {
        this.results = results;
    }

    /**
     * @return
     * @author Marc
     * @since 17.07.2021
     */
    public List<UploadFile> getFiles() {
        return files;
    }

    /**
     * @param files
     * @author Marc
     * @since 17.07.2021
     */
    public void setFiles(List<UploadFile> files) {
        this.files = files;
    }

    /**
     * @return
     * @author Marc
     * @since 17.07.2021
     */
    public long getId() {
        return id;
    }

    /**
     * @param id
     * @author Marc
     * @since 17.07.2021
     */
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "IntentResponse{" +
                "answer='" + answer + '\'' +
                ", results=" + results +
                ", files=" + files +
                ", id=" + id +
                '}';
    }
}
