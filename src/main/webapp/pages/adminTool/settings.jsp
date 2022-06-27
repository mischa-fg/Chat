<%@ page import="com.ubs.backend.classes.database.UserLogin" %>
<%@ page import="com.ubs.backend.classes.database.dao.UserLoginDAO" %><%--
  Created by IntelliJ IDEA.
  User: Marc Andri Fuchs
  Date: 7/23/2021
  Time: 4:30 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div id="settingsContent">
    <h1>Einstellungen</h1>
    <div class="changePassword">
        <h2>Passwort Ändern</h2>
        <form id="changePasswordForm">
            <!-- TODO: after submit, check if good. if not good return to this page and display error -->
            <label for="oldPass" class="hidden">Altes Passwort</label>
            <div class="input-group">
                <span class="input-group-text">Altes Passwort</span>
                <input type="password" id="oldPass" class="form-control" name="oldPass"
                       placeholder="Altes Passwort" required>
            </div>
            <div class="mb-3">
                <small class="form-text text-muted">0 Zeichen übrig</small>
            </div>

            <label for="newPass" class="hidden">Neues Passwort</label>
            <div class="input-group">
                <span class="input-group-text">Neues Passwort</span>
                <input type="password" id="newPass" class="form-control" name="newPass"
                       placeholder="Neues Passwort" required>
            </div>
            <div class="mb-3">
                <small class="form-text text-muted">0 Zeichen übrig</small>
            </div>

            <label for="newPassConfirm" class="hidden">Neues Passwort bestätigen</label>
            <div class="input-group">
                <span class="input-group-text">Neues Passwort bestätigen</span>
                <input type="password" id="newPassConfirm" class="form-control" name="newPassConfirm"
                       placeholder="Neues Passwort bestätigen" required>
            </div>
            <div class="mb-3">
                <small class="form-text text-muted">0 Zeichen übrig</small>
            </div>

        </form>
        <div class="form-floating mb-3 ">
            <p id="notSame"></p>
        </div>

        <button class="form-control button button-dark" onclick="changePW()">Ändern</button>
    </div>

    <%
        long userID = (long) request.getSession().getAttribute("user");
        UserLoginDAO userLoginDAO = new UserLoginDAO();
        UserLogin user = userLoginDAO.select(userID);

        if (user != null && user.isCanCreateUsers()) {
    %>
    <div hidden id="hiddenUserID"><%=userID%>
    </div>

    <div id="createUserContainer">
        <h2>Neuen Benutzer Hinzufügen</h2>
        <form id="createUserForm" method="post" action="">
            <label for="newUserEmailAddress" class="hidden">Email Adresse</label>
            <div class="input-group">
                <span class="input-group-text">Titel</span>
                <input type="text" id="newUserEmailAddress" class="form-control" name="email"
                       placeholder="Email Adresse">
            </div>
            <div class="mb-3">
                <small class="form-text text-muted">0 Zeichen übrig</small>
            </div>
            <div class="form-check mb-3">
                <input class="form-check-input" id="canCreateUserInput" type="checkbox">
                <label for="canCreateUserInput">Kann neue Benutzer erstellen</label>
            </div>
            <button type="button" id="createUserButton" class="form-control button button-dark">Benutzer Hinzufügen
            </button>
        </form>
        <div id="newUserData"></div>
    </div>

    <div id="userListContainer">
        <h2>Benutzer</h2>
        <table id="allUserTable">
            <thead>
            <tr>
                <th>Benutzer</th>
                <th>Kann neue Nutzer erstellen</th>
                <th>Zuletzt angemeldet</th>
                <th>Löschen</th>
            </tr>
            </thead>
            <tbody id="allUserTableBody">
            </tbody>
        </table>
    </div>

    <script src="../../assets/js/adminTools/createUserScripts.js"></script>
    <script src="../../assets/js/adminTools/userPage.js"></script>
    <%
        }
    %>

</div>
<script src="../../assets/js/adminTools/settingsPage.js"></script>
<script>
    <%
        if(user != null && user.isCanCreateUsers()) {
    %>
    loadUsers();
    <%
        }
    %>
    startScript();
</script>