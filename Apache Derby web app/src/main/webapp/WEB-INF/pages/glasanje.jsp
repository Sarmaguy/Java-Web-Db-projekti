<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 23/05/2023
  Time: 09:25
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Glasanje</title>
</head>
<body>
<h1>${Poll.title}</h1>
<h2>${Poll.message}</h2>
<ol>
    <c:forEach items="${PollOptions}" var="option">
        <li><a href="glasanje-glasaj?id=${option.id}">${option.optionTitle}</a></li>
    </c:forEach>
</ol>
</body>
</html>
