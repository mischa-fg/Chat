package com.ubs.backend.servlets;

import com.ubs.backend.classes.database.UploadFile;
import com.ubs.backend.classes.database.dao.UploadFileDAO;
import com.ubs.backend.classes.enums.DataTypeInfo;
import com.ubs.backend.classes.enums.FileUploadStatus;
import com.ubs.backend.classes.enums.ResponseFileType;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet to Upload a File to the Database
 *
 * @author Marc
 * @since 17.07.2021
 */
@WebServlet(name = "FileUpload", value = "/FileUpload")
@MultipartConfig
public class FileUpload extends HttpServlet {
    /**
     * defines the max size of a file in MB
     *
     * @since 17.07.2021
     */
    private static final float MAX_FILESIZE_IN_MB = 2.5f;

    /**
     * Handle Get Request -> Ignore, as Files can't be uploaded using Get
     *
     * @param request  the HttpRequest
     * @param response the HttpResponse
     * @author Marc
     * @since 17.07.2021
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
    }

    /**
     * Handle Post Request:
     * - Check if MimeType of file is allowed
     * - Check if Filesize is allowed
     * - Upload File to Database
     *
     * @param request  the HttpRequest
     * @param response the HttpResponse
     * @throws ServletException if there was an error with the execution of the Servlet
     * @throws IOException      if File couldn't be uploaded
     * @author Marc
     * @since 17.07.2021
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");

        Part filePart = request.getPart("file");
        String type = Files.probeContentType(Paths.get(filePart.getSubmittedFileName()));
        InputStream fileContent = filePart.getInputStream();
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        UploadFileDAO fileDAO = new UploadFileDAO();

        FileUploadStatus status;
        boolean isSupported = false;
        for (ResponseFileType rft : ResponseFileType.values()) {
            if (rft.getMimeType().equals(type)) {
                isSupported = true;
                break;
            }
        }
        List<String> fileNames = fileDAO.getFileNames();
        boolean fileExists = false;
        for (String s : fileNames) {
            if (s.equals(filePart.getSubmittedFileName())) {
                fileExists = true;
                break;
            }
        }

        status = checkFileStatus(filePart, isSupported, fileExists);

        // Return if one of the checks above was true
        if (status != FileUploadStatus.SUCCESS) {
            handleResponse(response, status, null, fileDAO);
            return;
        }

        int ch;
        while ((ch = fileContent.read()) != -1) {
            byteStream.write(ch);
        }
        Blob blob = null;
        try {
            blob = new SerialBlob(byteStream.toByteArray());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        byteStream.close();

        UploadFile uploadFile = new UploadFile(type, getSubmittedFileName(filePart), blob);

        handleResponse(response, FileUploadStatus.SUCCESS, uploadFile, fileDAO);
    }

    /**
     * @param response
     * @param status
     * @param file
     * @param fileDAO
     * @throws IOException
     * @author Marc
     * @since 17.07.2021
     */
    private void handleResponse(HttpServletResponse response, FileUploadStatus status, UploadFile file, UploadFileDAO fileDAO) throws IOException {
        response.setStatus(status.getHttpCode());
        response.getWriter().print("{ \"code\": " + status.getHttpCode() + ", \"comment\":\"" + status.getResponse() + "\" }");
        if (status == FileUploadStatus.SUCCESS) {
            fileDAO.insert(file);
        }
    }

    /**
     * method to check the status of a file
     *
     * @param filePart
     * @param isSupported
     * @param fileExists
     * @return
     * @author Marc Andri Fuchs
     * @author Tim Irmler
     * @since 19.07.2021
     */
    // needed to do as of deepsource issue
    // https://deepsource.io/gh/UBS-POf-Chatbot/chatbot/run/d5c68ad0-482f-463c-baa0-9e1540dab751/java/JAVA-E0128
    private synchronized FileUploadStatus checkFileStatus(Part filePart, boolean isSupported, boolean fileExists) {
        FileUploadStatus status = FileUploadStatus.SUCCESS;
        // Check if a file was uploaded
        if (filePart.getSize() < 1) status = FileUploadStatus.NO_FILE_UPLOADED;
            // Check if file is too big
        else if (filePart.getSize() > DataTypeInfo.FILE.getMaxLength())
            status = FileUploadStatus.MAX_UPLOAD_SIZE;
            // Check if FileType is supported
        else if (!isSupported) status = FileUploadStatus.ILLEGAL_FILE_TYPE;
            // Check if File already exists
        else if (fileExists) status = FileUploadStatus.DUPLICATE_FILE;

        return status;
    }
    
    private static String getSubmittedFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE fix.
            }
        }
        return null;
    }
}
