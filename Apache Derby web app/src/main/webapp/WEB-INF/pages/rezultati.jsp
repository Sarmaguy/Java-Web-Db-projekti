<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 23/05/2023
  Time: 09:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Rezultati</title>
</head>
<body>
<h1>Rezultati glasanja</h1>
<p>Ovo su rezultati glasanja</p>
<table>
    <thead>
    <tr>
        <th>Bend</th>
        <th>Broj glasova</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${pollOptions}" var="pollOption">
        <tr>
            <td>${pollOption.optionTitle}</td>
            <td>${pollOption.votesCount}</td>
        </tr>
    </c:forEach>
    </tbody>
</table>

<h2>Grafički prikaz rezultata</h2>
<img src="chart?pollID=${pollId}"/>

<h2>Rezultati u XLS formatu</h2>
<p>Rezultati u XLS fromatu su dostupni <a href="XLS?pollID=${pollId}">ovdje</a></p>

<h2>Razno</h2>
<p>Primjeri pobjednickih pjesama</p>
<ul>
    <c:forEach items="${winners}" var="winner">
        <li><a href="${winner.optionLink}">${winner.optionTitle}</a></li>
    </c:forEach>
</ul>
</body>
</html>
