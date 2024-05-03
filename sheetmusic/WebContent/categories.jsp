<!DOCTYPE html>
<html>
<%@ taglib prefix="sheetmusic" uri="WEB-INF/tags.tld"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@page import="com.jeltechnologies.sheetmusic.tags.BaseTag"%>
<head>
<jsp:include page="head.jsp"></jsp:include>
<script src="app/categories.js"></script>
</head>

<body>
	<sheetmusic:main-menu selected="Categories" showSearch="true" />
	<div id="sheetmusic-main-body">
		<sheetmusic:categories-select id="categories-select" onchange="handleCategorySelectClick();" />
		<div id="categories"></div>
	</div>
	<div id="sheetmusic-search-results"></div>
</body>

</html>