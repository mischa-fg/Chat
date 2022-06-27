package com.ubs.backend.servlets;

import com.ubs.backend.classes.database.UserLogin;
import com.ubs.backend.classes.database.dao.UserLoginDAO;
import com.ubs.backend.classes.enums.DataTypeInfo;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

import static com.ubs.backend.util.PrepareString.prepareString;
import static com.ubs.backend.util.PrepareString.stringTooLong;

@WebServlet(name = "CreateUser", value = "/CreateUser")
public class CreateUser extends HttpServlet {

    /*
     * Array with all Possible chars for a new password
     */
    private final char[] possibleChars = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
            'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
            's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '!',
            '#', '$', '%', '&', '\'', '(', ')', '*', '+',
            ',', '-', '.', '/', '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9'
    };

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String email = request.getParameter("email");
        boolean canCreateUser = Boolean.parseBoolean(request.getParameter("canCreateUser"));

        String err = "";
        String password = "undefined";
        if (!stringTooLong(email, DataTypeInfo.USER_EMAIL.getMaxLength())) {
            UserLogin newLogin = new UserLogin();
            newLogin.setCanCreateUsers(canCreateUser);
            newLogin.setEmail(prepareString(email, DataTypeInfo.USER_EMAIL.getMaxLength(), false, true));

            password = generatePassword((int) (Math.random() * 6) + 8);
            newLogin.setPassword(password);

            UserLoginDAO dao = new UserLoginDAO();

            if (dao.selectByEmail(email) != null) {
                err = "Ein Account mit dieser Emailadresse existiert bereits!";
            } else if (!isValid(email)) {
                err = "Dies ist keine valide Emailadresse!";
            } else {
                dao.insert(newLogin);
            }
        } else {
            err = "Die Email ist zu lange!";
        }

        String resp = "{\"email\": \"" + email + "\", \"password\": \"" + password + "\", \"error\":\"" + err + "\"}";

        response.getWriter().print(resp);
    }

    private String generatePassword(int len) {
        char[] chars = new char[len];

        for (int i = 0; i < chars.length; i++) {
            chars[i] = possibleChars[(int) (Math.random() * possibleChars.length)];
        }

        return new String(chars);
    }

    private boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }
}
