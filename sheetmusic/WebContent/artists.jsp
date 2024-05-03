<!DOCTYPE html>
<html>
<%@ taglib prefix="sheetmusic" uri="WEB-INF/tags.tld"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@page import="com.jeltechnologies.sheetmusic.tags.BaseTag"%>
<head>
<jsp:include page="head.jsp"></jsp:include>
	<script src="app/artists.js"></script>
</head>

<body>
	<sheetmusic:main-menu selected="Artists" showSearch="true" />

	<div id="sheetmusic-main-body">
		<div class="sheetmusic-select">
			<label for="show-selector">Show</label> 
			
			<select id="show-selector" class="filter-sort-select" onchange="handleFilterClick();">
				<option value="SONGBOOKS">only artists with songbooks</option>
				<option value="ALL">all artists</option>
			</select>
			
		</div>

		<div id="artists" class="category-items"></div>
	</div>
	
	<div id="sheetmusic-search-results"></div>

</body>

</html>