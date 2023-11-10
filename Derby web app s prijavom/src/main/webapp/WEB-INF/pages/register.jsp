<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 03/06/2023
  Time: 17:02
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register</title>
</head>
<body>
<h1>Register</h1>
<div style="color: red">
    <c:out value="${error}" />
</div>

<form action="register" method="post">
    <label for="nick">nick</label>
    <input type="text" name="nick" id="nick" value="${nick}"/><br/>
    <label for="firstName">First name:</label>
    <input type="text" name="firstName" id="firstName" value="${firstName}"/><br/>
    <label for="lastName">Last name:</label>
    <input type="text" name="lastName" id="lastName" value="${lastName}"/><br/>
    <label for="email">Email:</label>
    <input type="text" name="email" id="email" value="${email}"/><br/>
    <label for="password">Password:</label>
    <input type="password" name="password" id="password" value="${password}"/><br/>

    <input type="submit" value="Register"/>
</form>

</body>
</html>
