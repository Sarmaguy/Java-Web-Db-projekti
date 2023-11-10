<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 14.4.2023.
  Time: 20:29
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
</head>
<body style="background-color: <%= session.getAttribute("pickedBgCol") %>" >

<p><%=session.getAttribute("time")%></p>

</body>
</html>
