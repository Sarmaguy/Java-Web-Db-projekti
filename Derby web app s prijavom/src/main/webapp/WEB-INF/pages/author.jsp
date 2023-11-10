<%@ page import="hr.fer.zemris.java.tecaj_13.model.BlogUser" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 03/06/2023
  Time: 18:35
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
    <title>Author page</title>
</head>
<body>
<c:if test="<%=isLogged%>">
  <h3>Hello, <%=firstName%> <%=lastName%></h3>
  <a href="/hw05_war_exploded/servleti/logout">Logout</a>
</c:if>
<c:if test="<%=!isLogged%>">
  <h3>Not loged in</h3>
</c:if>
<h2>Postovi:</h2>
<c:if test="${entries.size() == 0}">
    <h3>No entries</h3>
</c:if>
<ul>
  <c:forEach items="${entries}" var="entry">
    <li>
      <a href="${user.nick}/${entry.id}">${entry.title}</a>
    </li>
    </c:forEach>
</ul>
    <c:if test="${isAuthor}">
      <a href="${user.nick}/new">New entry</a>
    </c:if>

</body>
</html>
