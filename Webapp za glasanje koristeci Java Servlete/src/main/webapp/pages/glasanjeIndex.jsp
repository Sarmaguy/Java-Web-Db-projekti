<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 14.4.2023.
  Time: 23:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<body style="background-color:<%= session.getAttribute("pickedBgCol") %>">
<h1>Vote for your favourite band:</h1>
<ol>
    <jsp:useBean id="data" scope="request" type="java.util.List"/>
    <c:forEach items="${data}" var="row">
        <li>
            <a href="glasanje-glasaj?id=${row[0]}">${row[1]}</a>
        </li>
    </c:forEach>
</ol>

</body>
</html>
