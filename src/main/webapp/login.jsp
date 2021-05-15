<%--
  Created by IntelliJ IDEA.
  User: Anakin Thanainantha
  Date: 5/9/2021
  Time: 17:00
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h1><%= "Login..!" %>
</h1>
<br/>
<form action="api/auth/login" method="post">
    Username: <input type="text" name="username" required />
    <br/>
    Password: <input type="text" name="password" required />
    <br/>
    <input type="submit" />
</form>
</body>
</html>