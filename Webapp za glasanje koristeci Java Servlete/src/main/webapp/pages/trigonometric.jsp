<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 14.4.2023.
  Time: 17:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Title</title>
</head>
<body style="background-color: <%= session.getAttribute("pickedBgCol")%> ">
<table>
  <thead>
    <tr>
      <th>x</th>
      <th>sin(x)</th>
      <th>cos(x)</th>
    </tr>
    </thead>
  <tbody>
    <% for(int i = (int) request.getAttribute("a"); i <= (int) request.getAttribute("b"); i++) { %>
      <tr>
        <td><%= i %></td>
        <td><%= Math.sin(i) %></td>
        <td><%= Math.cos(i) %></td>
      </tr>
    <% } %>
  </tbody>
</table>

</body>
</html>
