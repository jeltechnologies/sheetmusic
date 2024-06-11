<!DOCTYPE html>
<html>
<%@ taglib prefix="sheetmusic" uri="WEB-INF/tags.tld"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@page import="com.jeltechnologies.screenmusic.tags.BaseTag"%>
<head>
<jsp:include page="head.jsp"></jsp:include>
<script src="app/books.js"></script>
</head>

<body>
	<sheetmusic:main-menu selected="Books" showSearch="true" />
	<div id="sheetmusic-main-body">
		<div class="sheetmusic-select">
			<label for="sort-selector">Sort</label> <select id="sort-selector" class="filter-sort-select">
				<option value="A_Z">A-Z</option>
				<option value="Z_A">Z-A</option>
				<option value="RANDOM">Random</option>
			</select>
		</div>

		<div id="songbooks" class="category-items"></div>
	</div>
	<div id="sheetmusic-search-results"></div>
</body>

</html>