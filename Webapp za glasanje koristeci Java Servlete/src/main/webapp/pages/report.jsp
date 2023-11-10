<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 14.4.2023.
  Time: 18:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Title</title>
</head>
<body style="background-color: <%= session.getAttribute("pickedBgCol") %>">
    <h1>OS usage</h1>
    <p>Here are the results of OS usage in survey that we completed.</p>
    <img alt="Chart" src="chart">
</body>
</html>
