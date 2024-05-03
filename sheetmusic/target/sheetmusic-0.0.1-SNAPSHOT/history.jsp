<!DOCTYPE html>
<%@page import="com.jeltechnologies.utils.StringUtils"%>
<%@ taglib prefix="sheetmusic" uri="WEB-INF/tags.tld"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<html>

<head>
<jsp:include page="head.jsp"></jsp:include>

</head>

<body>
	<sheetmusic:main-menu selected="History" showSearch="true" />
	<div id="sheetmusic-main-body">
		<sheetmusic:history />
	</div>
	<div id="sheetmusic-search-results"></div>
	<script>
		clearSearch();
	</script>
</body>

</html>