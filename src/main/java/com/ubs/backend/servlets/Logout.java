package com.ubs.backend.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ubs.backend.util.Variables.serverDirectory;

/**
 * Servlet to Logout from the AdminTool
 *
 * @author Magnus
 * @since 17.07.2021
 */
@WebServlet(name = "Logout", value = "/Logout")
public class Logout extends HttpServlet {
    /**
     * handle Get Request -> Ignore
     *
     * @param request  the HttpRequest
     * @param response the HttpResponse
     * @throws ServletException if there was an error with the execution of the Servlet
     * @throws IOException      if there was an error with the File Upload
     * @author Magnus
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // New location to be redirected
        doPost(request, response);
    }

    /**
     * Servlet to check if user login data is correct
     *
     * @param request  the request of this page
     * @param response the response which is being sent back to the user
     * @author Magnus
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        

        request.getSession().setAttribute("error", null);
        request.getSession().setAttribute("user", null);

        // Set response content type
        response.setContentType("text/html");

        // New location to be redirected
        String redirectURL = ("" + request.getRequestURL()).replace(request.getRequestURI(), "");

        // Redirect to Admin Tool
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", redirectURL + serverDirectory + "/pages/login/login.jsp");
    }
}
