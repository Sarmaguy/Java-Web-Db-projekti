
<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 14.4.2023.
  Time: 12:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Colors</title>
</head>
<body style="background-color: <%= session.getAttribute("pickedBgCol") %>" >
<p><a href="<c:url value="/setColor/FFFFFF"/>">WHITE</a></p>
<p><a href="<c:url value="/setColor/FF0000"/>">RED</a></p>
<p><a href="<c:url value="/setColor/00FF00"/>">GREEN</a></p>
<p><a href="<c:url value="/setColor/00FFFF"/>">CYAN</a></p>
</body>
</html>
