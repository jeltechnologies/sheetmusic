<!DOCTYPE html>
<%@page import="com.jeltechnologies.utils.StringUtils"%>
<%@ taglib prefix="sheetmusic" uri="WEB-INF/tags.tld"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@page import="com.jeltechnologies.sheetmusic.tags.BaseTag"%>
<%
String artist = request.getParameter("artist");
String artistEncoded = StringUtils.encodeURL(artist);
%>
<html>

<head>
<jsp:include page="head.jsp"></jsp:include>
<script src="app/series.js"></script>
</head>

<body>
	<sheetmusic:main-menu selected="Series" showSearch="true" />
	<div id="sheetmusic-main-body">
		<sheetmusic:series-select id="series-select" onchange="handleSeriesSelectClick();" />
		<div id="books" class="category-items"></div>
	</div>
	<div id="sheetmusic-search-results"></div>
</body>

</html>