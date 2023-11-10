<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 03/06/2023
  Time: 20:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    boolean isLogged =  request.getSession().getAttribute("current.user.id") != null;
    String firstName = (String) request.getSession().getAttribute("current.user.fn");
    String lastName = (String) request.getSession().getAttribute("current.user.ln");
%>
<html>
<head>
    <title>Entry</title>
</head>
<body>
<c:if test="<%=isLogged%>">
    <h3>Hello, <%=firstName%> <%=lastName%></h3>
    <a href="/hw05_war_exploded/servleti/logout">Logout</a>
</c:if>
<c:if test="<%=!isLogged%>">
    <h3>Not loged in</h3>
</c:if>
<h1>${entry.title}</h1>
<p>${entry.text}</p>
<p>Posted on : ${entry.createdAt}</p>
<p>Last update : ${entry.lastModifiedAt}</p>
<c:if test="${isAuthor}">
    <a href="${entry.id}/edit">Edit</a>
</c:if>
<hr>

<c:forEach items="${comments}" var="comment">
    <p>User: ${comment.usersEMail}</p>
    <p>${comment.message}</p>
    <p>Posted on: ${comment.postedOn}</p>
    <hr>
</c:forEach>

<h3>Add new comment</h3>
<div style="color: red">${error}</div>
<form action="${entry.id}/addComment" method="post">
    <label for="email">Your email</label>
    <input type="email" name="email" id="email" required><br>
    <label for="message">Your message</label><br>
    <textarea name="message" id="message" cols="30" rows="10" required></textarea><br>
    <input type="submit" value="Add comment">
</form>


</body>
</html>
