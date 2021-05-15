<%--
  Created by IntelliJ IDEA.
  User: Anakin Thanainantha
  Date: 5/9/2021
  Time: 19:06
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <%
        String strUid = String.valueOf(session.getAttribute("uid"));
        String strUser = String.valueOf(session.getAttribute("usr"));
        out.print("Uid : " + strUid);
        out.print("<br>");
        out.print("user : " + strUser);
    %>
</body>
</html>
