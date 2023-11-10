<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 03/06/2023
  Time: 15:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<%
    boolean isLogged =  request.getSession().getAttribute("current.user.id") != null;
    String firstName = (String) request.getSession().getAttribute("current.user.fn");
    String lastName = (String) request.getSession().getAttribute("current.user.ln");
%>
<head>
    <title>Home</title>
</head>
<body>
<c:if test="<%=isLogged%>">
    <h3>Hello, <%=firstName%> <%=lastName%></h3>
    <a href="logout">Logout</a>
</c:if>

<c:if test="<%=!isLogged%>">
    <h3>Not loged in</h3>
<div style="color: red">
    <c:out value="${error}" />
    </div>
    <form action="login" method="post">
        <input type="text" name="nick" placeholder="Nick">
        <input type="password" name="password" placeholder="Password">
        <input type="submit" value="Login">
    </form>
    <a href="register">Register</a>
</c:if>
<h2>Autori:</h2>
<ul>
    <c:forEach var="user" items="${users}">
        <li><a href="author/${user.nick}">${user.nick}</a></li>
    </c:forEach>
</ul>

</body>
</html>
