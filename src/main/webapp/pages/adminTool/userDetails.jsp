<%@ page import="com.ubs.backend.classes.database.UserLogin" %>
<%@ page import="com.ubs.backend.classes.database.dao.UserLoginDAO" %><%--
  Created by IntelliJ IDEA.
  User: Tim Irmler (Zwazel)
  Date: 07.08.2021
  Time: 15:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    long userID = (long) request.getSession().getAttribute("user");
    UserLoginDAO userLoginDAO = new UserLoginDAO();
    UserLogin user = userLoginDAO.select(userID);

    if (user != null && user.isCanCreateUsers()) {
%>
<div id="userDetailsContent">
    <h1>Benutzer Details!</h1>
    <div>
        <form id="editData">
            <div class="input-group mb-3">
                <label class="input-group-text" for="userName">Benutzer</label>
                <input type="text" id="userName" class="form-control" name="userName" placeholder="E-Mail" autofocus>
            </div>

            <div class="input-group mb-3">
                <label id="labelOfCanCreateUserInput" class="input-group-text" for="canCreateUserInput">Kann neue
                    Benutzer erstellen</label>
                <input class="form-check-input" id="canCreateUserInput" type="checkbox">
            </div>

            <input type="button" id="userFormSubmitButton" value="Aktualisieren">
            <input type="button" id="resetPassword" value="Passwort zurücksetzen">
            <div id="newPasswordData"></div>
        </form>
    </div>
    <!--    </form>-->

    <div id="userDetailsDetails"></div>

    <div id="contentLoaderAnimation" class="lds-roller">
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
        <div></div>
    </div>

    <div id="allUsersContainer"></div>
</div>
<script src="../../assets/js/adminTools/userDetailsPage.js"></script>
<script>
    loadUser();
</script>
<%
} else {
%>
<script>
    <%
        if(user == null) {
        %>
    loadPage("settings.jsp", "settingsButton", false, -1, true);
    <%
        } else if(!user.isCanCreateUsers()) {
        %>
    loadPage("settings.jsp", "settingsButton", false, -1, true);
    <%
        }
        %>
</script>
<%
    }
%>