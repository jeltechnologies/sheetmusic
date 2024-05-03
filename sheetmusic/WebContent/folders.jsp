<!DOCTYPE html>
<html>
<%@ taglib prefix="sheetmusic" uri="WEB-INF/tags.tld"%>
<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<head>
<jsp:include page="head.jsp"></jsp:include>
</head>
<body>
	<sheetmusic:main-menu selected="Folders" showSearch="true" />
	<div id="sheetmusic-main-body">
		<br style="clear: both" />
		<div class="category-items">
			<sheetmusic:library />
		</div>
	</div>
	<div id="sheetmusic-search-results"></div>
	<script>
		clearSearch();
	</script>
</body>
</html>