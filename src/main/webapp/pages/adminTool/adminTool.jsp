<%@ page import="static com.ubs.backend.util.Variables.serverDirectory" %>
<%@ page import="com.ubs.backend.util.JSPFunctions" %><%--
  Created by IntelliJ IDEA.
  User: Zwazel
  Date: 21.05.2021
  Time: 09:32
--%>
<%
    if (session.getAttribute("user") == null) {
        String redirectURL = ("" + request.getRequestURL()).replace(request.getRequestURI(), "");
        response.sendRedirect(redirectURL + serverDirectory + "/pages/login/login.jsp" + JSPFunctions.generateLoadPageParams(request));
    }
%>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="de">
<head>
    <title>Admin Tool</title>
    <meta charset="UTF-8">
    <link href="../../assets/css/adminToolStyles.css" rel="stylesheet">
    <link href="../../assets/css/newStyle.css" rel="stylesheet">
    <link href="../../assets/css/generalStyles.css" rel="stylesheet">
    <link href="../../assets/bootstrap/css/bootstrap.css" rel="stylesheet">
    <link rel="icon" type="image/png" sizes="16x16" href="../../assets/images/chatbotImage.png">

    <script type="text/javascript" src="../../assets/js/chart.js"></script>
    <script type="text/javascript" src="../../assets/js/jQuery.js"></script>
    <script type="text/javascript" src="../../assets/js/utilities/tags/tagListInterface.js"></script>
    <script type="text/javascript" src="../../assets/js/utilities/variables.js"></script>
    <script type="text/javascript" src="../../assets/js/utilities/prepareString.js"></script>
    <script type="text/javascript" src="../../assets/js/utilities/charcounter.js"></script>

    <script type="text/javascript" src="../../assets/js/adminTools/classes/IDParent.js"></script>
    <script type="text/javascript" src="../../assets/js/adminTools/classes/AnswerParent.js"></script>
    <script type="text/javascript" src="../../assets/js/adminTools/classes/QuestionParent.js"></script>

    <script type="text/javascript" src="../../assets/js/adminTools/classes/AnswerType.js"></script>
    <script type="text/javascript" src="../../assets/js/adminTools/classes/MyAnswer.js"></script>
    <script type="text/javascript" src="../../assets/js/adminTools/classes/SingleTagAnswer.js"></script>
    <script type="text/javascript" src="../../assets/js/adminTools/classes/MyTag.js"></script>
    <script type="text/javascript" src="../../assets/js/adminTools/classes/MyFile.js"></script>
    <script type="text/javascript" src="../../assets/js/adminTools/classes/Matches.js"></script>
    <script type="text/javascript" src="../../assets/js/adminTools/classes/BlackListEntry.js"></script>
    <script type="text/javascript" src="../../assets/js/adminTools/classes/MyAnsweredQuestion.js"></script>
    <script type="text/javascript" src="../../assets/js/adminTools/classes/DefaultQuestions.js"></script>
    <script type="text/javascript" src="../../assets/js/adminTools/classes/MyUser.js"></script>

    <script type="text/javascript" src="../../assets/js/adminTools/adminTool.js"></script>


</head>
<body>
<nav>
    <%@include file="adminToolNavigation.html" %>
</nav>
<div class="container">
    <div id="pageContent" style="margin-left: 100px"></div> <!-- load all pages in this div -->
</div>
</body>
</html>
