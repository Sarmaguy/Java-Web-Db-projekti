<%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 03/06/2023
  Time: 19:58
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Create entry</title>
</head>
<body>
<h1>Create entry</h1>
<div style="color: red">
    ${error}
</div>
<form action="create" method="post">
    <label for="title">Naslov</label>
    <input type="text" name="title" id="title" value="${title}" required/><br/>
    <label for="text">Sadržaj:</label>
    <textarea name="text" id="text" cols="30" rows="10" value="${text}" required>Unesi sadržaj</textarea><br/>
    <input type="submit" value="Unesi"/>
</form>
</body>
</html>
