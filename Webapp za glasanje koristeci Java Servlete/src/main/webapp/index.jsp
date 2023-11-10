<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<body style="background-color: <%= session.getAttribute("pickedBgCol")%> ">
<h2>Hello World!</h2>
<p><a href="<c:url value="colors"/>">Background color chooser</a></p>
<p><a href="<c:url value="trigonometric?a=0&b=90"/>">Trigonometric values calculator</a></p>
<form action="trigonometric" method="GET">
    Početni kut:<br><input type="number" name="a" min="0" max="360" step="1" value="0"><br>
    Završni kut:<br><input type="number" name="b" min="0" max="360" step="1" value="360"><br>
    <input type="submit" value="Tabeliraj"><input type="reset" value="Reset">
</form>
<p><a href="<c:url value="stories/funny.jsp"/>">Funny story</a></p>
<p><a href="<c:url value="report"/>">Review OS usage reports</a></p>
<p><a href="<c:url value="powers?a=1&b=100&n=3"/>">Excel doc</a></p>
<p><a href="<c:url value="appInfo"/>">See how has server been up</a></p>
<p><a href="<c:url value="glasanje"/>">Glasaj</a></p>

</body>
</html>
