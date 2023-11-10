<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %><%--
  Created by IntelliJ IDEA.
  User: Jura
  Date: 14.4.2023.
  Time: 18:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Title</title>
  <%
    //generate random color from predefined color list
    List<String> colors = new ArrayList<String>();
    colors.add("red");
    colors.add("green");
    colors.add("blue");
    colors.add("yellow");
    colors.add("orange");
    colors.add("purple");
    colors.add("pink");
    colors.add("brown");
    colors.add("cyan");
    colors.add("magenta");
    colors.add("lime");

    String color1 = colors.get((int) (Math.random() * colors.size()));
    String color2 = colors.get((int) (Math.random() * colors.size()));
  %>
</head>
<body>
  <h3 STYLE="color: <%= color1%>">Why was the math book sad?</h3>
  <h4 STYLE="color: <%= color2%>">Because it had so many problems!</h4>
</body>
</html>
