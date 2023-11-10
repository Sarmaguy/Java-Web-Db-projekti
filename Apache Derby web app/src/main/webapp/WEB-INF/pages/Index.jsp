<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 22/05/2023
  Time: 23:44
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Polls</title>
</head>
<body>
<h1>Izaberi glasanje:</h1>
<c:forEach items="${Polls}" var="poll">
    <a href="glasanje?pollID=${poll.id}">${poll.title}</a><br/>
</c:forEach>
</body>
</html>
