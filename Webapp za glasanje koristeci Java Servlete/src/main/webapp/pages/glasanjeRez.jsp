<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 15.4.2023.
  Time: 11:36
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<body style="background-color:<%= session.getAttribute("pickedBgCol") %>">
<h1>Rezultati glasovanja:</h1>

<table>
  <thead>
    <tr>
      <th>Band</th>
      <th>Broj glasova</th>
    </tr>
    </thead>
    <tbody>
    <jsp:useBean id="votes" scope="request" type="java.util.List"/>
    <c:forEach items="${votes}" var="row">
        <tr>
            <td>${row[0]}</td>
            <td>${row[1]}</td>
        </tr>
    </c:forEach>
</table>
<h2>Grafički prikaz rezultata</h2>
<img src="glasanjeChart" alt="Grafički prikaz rezultata"/>
<h2>Rezultati u obliku tablice u Excelu</h2>
<a href="glasanjeExcel">Prikaz tablice u Excelu</a>
<h2>Pobjednici:</h2>
      <ul>
      <jsp:useBean id="winners" scope="request" type="java.util.List"/>
      <c:forEach items="${winners}" var="winner">
        <li><a href="${winner[1]}">${winner[0]}</a></li>
      </c:forEach>
      </ul>
</body>
</html>
