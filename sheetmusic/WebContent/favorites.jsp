<!DOCTYPE html>
<%@page import="com.jeltechnologies.utils.StringUtils"%>
<%@ taglib prefix="sheetmusic" uri="WEB-INF/tags.tld"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>

<html>

<head>
<jsp:include page="head.jsp"></jsp:include>
<script src="app/favorites.js"></script>
</head>

<body>
	<sheetmusic:main-menu selected="Favorites" showSearch="true" />
	<div id="sheetmusic-main-body">
		<div id="artists" class="favorite-items"></div>
		<div id="books" class="favorite-items"></div>
		<div id="pages" class="favorite-items"></div>
	</div>
	<script>
	</script>
</body>

</html>