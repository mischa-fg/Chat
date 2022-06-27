<%@ page import="com.ubs.backend.util.JSPFunctions" %><%--
  Created by IntelliJ IDEA.
  User: magnus
  Date: 20.05.2021
  Time: 08:57
  To change this template use File | Settings | File Templates.
--%>
<% if (session.getAttribute("user") != null) {
    String params = (String) request.getSession().getAttribute("target");
    System.out.println("Params in Login > " + params);
    String redirectURL = "../adminTool/adminTool.jsp" + ((params != null) ? params : "");
    response.sendRedirect(redirectURL);
}
%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html lang="en">
<head>
    <link href="../../assets/css/loginStyle.css" rel="stylesheet">
    <link href="../../assets/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link rel="icon" type="image/png" sizes="16x16" href="../../assets/images/chatbotImage.png">
    <meta charset="UTF-8">
    <title>Login Chatbot Tool</title>
    <%
        if (session.getAttribute("error") != null) {
            response.getWriter().print("<style>#email{ margin-top: 0px !important;}</style>");
        }
        request.getSession().setAttribute("target", JSPFunctions.generateLoadPageParams(request));
    %>
</head>
<body>

<div class="contentBackground">
    <div class="content">
        <h1>Anmeldung</h1>
        <p>Dies ist die Anmeldung zum Admintool</p>
        ${ error }
        <form action="../../LoginValidation${target}" class="form-container" method="post">
            <div class="form-floating mb-3">
                <input type="text" class="form-control" id="email" placeholder="email" name="email" required
                       autofocus>
                <label for="email" class="form-label">E-Mail</label>
            </div>
            <div class="form-floating mb-3">
                <input type="password" class="form-control" id="password" placeholder="password" name="password"
                       required>
                <label for="password" class="form-label">Passwort</label>
            </div>
            <button type="button" class="btn btn-primary" id="showpassword" onclick="passwordShow()">Passwort anzeigen
            </button>

            <div class="form-action-buttons">
                <input id="login" class="btn btn-primary" type="submit" value="Anmelden">
            </div>

        </form>
    </div>
</div>

<script type="text/javascript" src="../../assets/js/login.js"></script>
</body>
</html>