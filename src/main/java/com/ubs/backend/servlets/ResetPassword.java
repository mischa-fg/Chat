package com.ubs.backend.servlets;

import com.ubs.backend.classes.database.UserLogin;
import com.ubs.backend.classes.database.dao.UserLoginDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet to reset password
 *
 * @author Sarah
 * @since 04.08.2021
 */
@WebServlet(name = "ResetPassword", value = "/ResetPassword")
public class ResetPassword extends HttpServlet {
    private final char[] possibleChars = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '!',
            '#', '$', '%', '&', '\'', '(', ')', '*', '+',
            ',', '-', '.', '/', '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9'
    };

    /**
     * handle get request
     * call Post method
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException
     * @author Sarah
     * @since 04.08.2021
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    /**
     * handle post request:
     * - generate password
     * - set new password
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @throws IOException
     * @author Sarah
     * @since 04.08.2021
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Long id = Long.parseLong(request.getParameter("userId"));


        String password = generatePassword((int) (Math.random() * 6) + 8);
        UserLoginDAO dao = new UserLoginDAO();
        UserLogin userLogin = dao.select(id);
        String email = userLogin.getEmail();
        dao.setPassword(id,password);

        String resp = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}";
        
        response.getWriter().print(resp);
    }

    /**
     * generate a password
     *
     * @param len the length of the password
     * @return new password
     * @author Sarah
     * @since 04.08.2021
     */
    private String generatePassword(int len) {
        char[] chars = new char[len];

        for (int i = 0; i < chars.length; i++) {
            chars[i] = possibleChars[(int) (Math.random() * possibleChars.length)];
        }

        return new String(chars);
    }
}
