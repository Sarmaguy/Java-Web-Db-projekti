<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 03/06/2023
  Time: 21:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Edit entry</title>
</head>
<body>
<div style="color: red;">
    ${error}
</div>
<h1>Edit entry</h1>
<form action="edit" method="post">
    <input type="hidden" name="id" value="${entry.id}">
    <label for="title">Title</label>
    <input type="text" name="title" id="title" value="${entry.title}" required><br>
    <label for="text">Sadržaj</label>
    <textarea name="text" id="text" required>${entry.text}</textarea><br>
    <input type="submit" value="Save">
</form>
</body>
</html>
