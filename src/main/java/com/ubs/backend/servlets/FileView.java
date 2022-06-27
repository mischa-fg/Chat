package com.ubs.backend.servlets;

import com.ubs.backend.classes.database.UploadFile;
import com.ubs.backend.classes.database.dao.UploadFileDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet to view a File from the Database
 *
 * @author Marc
 * @since 17.07.2021
 */
@WebServlet(name = "FileView", value = "/file")
public class FileView extends HttpServlet {
    /**
     * Handle Get Request:
     * - check if File exists in Database
     * - if file exists get it from the Database and return it in the Response
     *
     * @param request  the HttpRequest
     * @param response the HttpResponse
     * @throws ServletException if there was an error with the execution of the Servlet
     * @throws IOException      if there was an error with the File Upload
     * @author Marc
     * @since 17.07.2021
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long id = Long.parseLong(request.getParameter("id"));

        UploadFileDAO fileDAO = new UploadFileDAO();
        UploadFile requestedFile;

        long count = fileDAO.getFileCount(id);

        if (count == 0) {
            response.getWriter().write("this file does not exist!");
            return;
        } else {
            requestedFile = fileDAO.select(id);
        }

        byte[] content = new byte[0];
        try {
            content = requestedFile.getContent().getBytes(1, (int) requestedFile.getContent().length());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        response.setHeader("content-disposition", "inline; filename=\"" + requestedFile.getFileName() + "\""); // Inline = Display if supported else download
        response.setContentType(requestedFile.getFileType() + "; name\"" + requestedFile.getFileName() + "\"");
        response.getOutputStream().write(content);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    /**
     * handle Post Request -> call Get method as the same thing happens
     *
     * @param request  the HttpRequest
     * @param response the HttpResponse
     * @throws ServletException if there was an error with the execution of the Servlet
     * @throws IOException      if there was an error with the File Upload
     * @author Marc
     * @since 17.07.2021
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
