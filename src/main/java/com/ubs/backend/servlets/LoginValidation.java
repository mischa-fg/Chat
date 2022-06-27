package com.ubs.backend.servlets;

import com.ubs.backend.classes.SHA256;
import com.ubs.backend.classes.database.UserLogin;
import com.ubs.backend.classes.database.dao.UserLoginDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.ubs.backend.util.PrintDebug.printDebug;
import static com.ubs.backend.util.Variables.serverDirectory;

/**
 * Servlet to validate a Login
 *
 * @author Magnus
 * @author Tim Irmler
 * @since 17.07.2021
 */
@WebServlet(name = "LoginValidation", value = "/LoginValidation")
public class LoginValidation extends HttpServlet {
    /**
     * handle Get Request -> Ignore, as Login Data should not be contained in a Get Request
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
        String redirectURL = ("" + request.getRequestURL()).replace(request.getRequestURI(), "");
        request.getRequestDispatcher(redirectURL + serverDirectory + "/pages/login/login.jsp").forward(request, response);
    }

    /**
     * handle doPost:
     * - get All Users from the Database
     * - check if User with email / password exists
     * - add attributes to the Session
     *
     * @param request  the request of this page
     * @param response the response which is being sent back to the user
     * @author Magnus
     * @author Tim Irmler
     * @since 17.07.2021
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Get all users from the Database
        UserLoginDAO userLoginDAO = new UserLoginDAO();
        UserLogin loggedInUser = userLoginDAO.selectByEmail(email);

        if (loggedInUser == null) {
            request.getSession().setAttribute("error", "<p id=\"errorField\">Wir können kein Konto mit dieser E-Mail finden!</p>");
            request.getSession().setAttribute("user", null);
        } else {
            // Convert Password to SHA256 String
            String shaPassword = SHA256.getHexStringInstant(password);
            printDebug("!Hash", password);
            printDebug("Input Password", shaPassword);
            printDebug("Expected Password", loggedInUser.getPassword());

            // Check if user and password exist
            if (loggedInUser.getPassword().equals(shaPassword)) {
                userLoginDAO.updateLastLoginTime(loggedInUser.getUserLoginID());
                request.getSession().setAttribute("error", null);
                request.getSession().setAttribute("user", loggedInUser.getUserLoginID());
            } else {
                request.getSession().setAttribute("error", "<p id=\"errorField\">Bitte überprüfen sie ihr Passwort und E-Mail und versuchen sie es erneut!</p>");
                request.getSession().setAttribute("user", null);
            }
        }

        // Set response content type
        response.setContentType("text/html");

        // New location to be redirected
        String redirectURL = ("" + request.getRequestURL()).replace(request.getRequestURI(), "");


        // Redirect to Admin Tool
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.setHeader("Location", redirectURL + serverDirectory + "/pages/login/login.jsp");
    }
}
