<!DOCTYPE html>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" isErrorPage="true"%>
<%@ taglib prefix="sheetmusic" uri="WEB-INF/tags.tld"%>
<html>
<head>
<jsp:include page="head.jsp"></jsp:include>
<style>
<jsp:include page="app/sheetmusic.css"></jsp:include>
</style>
</head>

<body>
	<sheetmusic:main-menu selected="Error" showSearch="false"/>
	<div class="pageTitle"><%=response.getStatus()%> - Something went wrong</div>
	<%
		
		if (exception != null) {
		    out.append(exception.getMessage());
		}
		
	%>

</body>
</html>