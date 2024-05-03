<!DOCTYPE html>
<%@page import="com.jeltechnologies.utils.StringUtils"%>
<%@ taglib prefix="sheetmusic" uri="WEB-INF/tags.tld"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<html>
<head>
<jsp:include page="head.jsp"></jsp:include>
<title>Statistics | Sheet music</title>
</head>
<body>
	<sheetmusic:main-menu selected="Stats" showSearch="true" />
	<div id="sheetmusic-main-body">
		<div class="pageTitle">Library</div>
		<sheetmusic:library-statistics/>
		<div class="pageTitle">Books - Top 100</div>
		<sheetmusic:mostpopularbooks top="100" />
		<div class="pageTitle">Pages - Top 100</div>
		<sheetmusic:mostpopularpages top="100" />
	</div>
	<div id="sheetmusic-search-results"></div>
</body>
</html>