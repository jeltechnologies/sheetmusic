const APPLICATION_NAME = "Sheet music";

history.navigationMode = 'compatible';

const LOGGING = false;
const LIST_TYPE_FILE = 1;
const LIST_TYPE_FOLDER = 2;
const MAX_TITLE_WIDTH = 65;
const WIDTH_THUMBNAIL = 200;
const HEIGHT_THUMBNAIL = 283;
const SEARCH_MIN_CHARS = 4;
const LAST_QUERY = "sheetmusic-last-query";

function documentReady() {
	document.addEventListener('keydown', userPressedKey);
}

function mainMenuSiteLogoClicked() {
	window.open("categories.jsp", "_self");
}

function getRequestParameter(name) {
	return new URLSearchParams(window.location.search).get(name);
}

function userPressedKey(event) {
	//alert("Ja");
	let keyCode = event.keyCode;
	if (event.key === '-' || keyCode == '37') {
		// left
		let folder = getUrlParameter("folder");
		if (folder == undefined || folder != "") {
			window.history.back();
		}
	} else {
		if (keyCode == '39') {
			// right		
			window.history.forward();
		}
	}
}

function getJson(url, callbackFunction) {
	$.ajax({
		url: url,
		type: 'GET',
		contentType: "application/json",
		error: function(xhr) {
			console.log("An error occured: " + xhr.status + " " + xhr.statusText);
		},
		success: function(result) {
			callbackFunction(result);
		}
	});
}

function postJson(url, dataToSend, callbackFunction) {
	$.ajax({
		url: url,
		type: 'POST',
		data: JSON.stringify(dataToSend),
		contentType: "application/json",
		error: function(xhr) {
			console.log("An error occured: " + xhr.status + " " + xhr.statusText);
		},
		success: callbackFunction
	});
}

function putJson(url, dataToSend, callbackFunction) {
	$.ajax({
		url: url,
		type: 'PUT',
		data: JSON.stringify(dataToSend),
		contentType: "application/json",
		error: function(xhr) {
			console.log("An error occured: " + xhr.status + " " + xhr.statusText);
		},
		success: callbackFunction
	});
}

function deleteJson(url, callbackFunction) {
	$.ajax({
		url: url,
		type: 'DELETE',
		contentType: "application/json",
		error: function(xhr) {
			console.log("An error occured: " + xhr.status + " " + xhr.statusText);
		},
		complete: callbackFunction
	});
}

function getUrlParameter(name) {
	name = name.replace(/[\[]/, '\\[').replace(/[\]]/, '\\]');
	var regex = new RegExp('[\\?&]' + name + '=([^&#]*)');
	var results = regex.exec(location.search);

	if (results != null) {
		var partToDecode = results[1];
		partToDecode = partToDecode;
		// alert("decodeURIComponent " + partToDecode );
		return results === null ? '' : decodeURIComponent(partToDecode.replace(/\+/g, ' '));
	}
}

function addParameterToUrl(url, name, value) {
	var result = url;
	var qPos = url.indexOf('?');
	var isFirstParam = (qPos == undefined || qPos < 0);
	if (isFirstParam) {
		result = result + "?";
	} else {
		result = result + "&";
	}
	var encodedValue = encodeURIComponent(value);
	result = result + name + "=" + encodedValue;
	return result;
}

function isTablet() {
	var platform = navigator.platform;
	return platform == "MacIntel" || platform == "iPad" || platform == "iPhone" || platform == "Android";
}

function getScreenOrientation() {
	if (isTablet()) {
		var orientation = window.orientation;
		var result;
		if (orientation == 90 || orientation == -90) {
			result = "landscape";
		} else {
			result = "portrait";
		}
	} else {
		result = "landscape";
	}
	return result;
}

function enforceArrayFromElements(filesData) {
	var result;
	if (filesData == undefined) {
		result = undefined;
	} else {
		if (Array.isArray(filesData)) {
			result = filesData;
		} else {
			var filesArray = new Array();
			filesArray[0] = filesData;
			result = filesArray;
		}
	}
	return result;
}

function startResults() {
	var html;
	html = '<p><ul class="container float">';
	return html;
}

function endResults() {
	var html;
	html = "</ul></p>";
	return html;
}

function getFileNameWithoutParams(uriLink) {
	var link = decodeURIComponent(decodeURIComponent(uriLink));
	var linkWithoutParams;
	var pos = link.indexOf('&');
	if (pos > -1) {
		linkWithoutParams = link.substring(0, pos);
	} else {
		linkWithoutParams = link;
	}
	return linkWithoutParams;
}

function removeExtension(filename) {
	var without = filename.split('.').slice(0, -1).join('.');
	if (without == undefined || without == "") {
		without = filename;
	}
	return without;
}

function removeFolder(fileName) {
	console.log(fileName);
	var pos = fileName.lastIndexOf("/");
	var result;
	if (pos > 0) {
		result = fileName.substr(pos + 1);
	} else {
		result = fileName;
	}
	return result;
}

function replaceAll(str, term, replacement) {
	return str.replace(new RegExp(escapeRegExp(term), 'g'), replacement);
}

function openModalWebPage(modal, webPageUrl) {
	// @see https://github.com/kylefox/jquery-modal
	$.ajax({
		url: webPageUrl,
		success: function(newHTML, textStatus, jqXHR) {
			modal.body = "";
			$(newHTML).appendTo('body').modal();
			$.modal.getCurrent().options.showClose = false;
		},
		error: function(jqXHR, textStatus, errorThrown) {
			alert("AJAX error, cannot openModalWebPage");
		}
	});
	modal.modal();
}

function closeModal() {
	$.modal.close();
}

function createCardHtml(checksum, page, title, link) {
	let image = "page?size=small&checksum=" + checksum + "&page=" + page;
	let html = "<a href='" + link + "'>";
	html = html + "<li class='card-list'><img class='img-card-list' loading='lazy' src='" + image + "'>";
	html = html + "<div class='card-title'>" + title;
	html = html + "</div></li></a>";
	return html;
}

function createCategoryTitle(title) {
	return "<div class='category-title'>" + title + "</div>";
}

function capitalizeFirstLetter(string) {
	return string.charAt(0).toUpperCase() + string.slice(1);
}

function formatDateTime(isoDate) {
	let dateLocale = window.navigator.userLanguage || window.navigator.language;
	let DateTime = luxon.DateTime;
	let date = DateTime.fromISO(isoDate).setLocale(dateLocale);
	let format = date.toLocaleString(DateTime.DATE_HUGE) + ", ";
	if (dateLocale === 'nl') {
		format = format + date.toFormat("H.mm") + " uur";
	} else {
		if (dateLocale === 'sv') {
			format = format + "kl. " + date.toFormat("HH.mm");
		} else {
			format = format + date.toLocaleString(DateTime.TIME_24_SIMPLE);
		}
	}
	return format;
}

function formatDate(isoDate) {
	let dateLocale = window.navigator.userLanguage || window.navigator.language;
	let DateTime = luxon.DateTime;
	let date = DateTime.fromISO(isoDate).setLocale(dateLocale);
	let format = date.toLocaleString(DateTime.DATE_FULL);
	return format;
}

function formatTime(isoDate) {
	let dateLocale = window.navigator.userLanguage || window.navigator.language;
	let DateTime = luxon.DateTime;
	let date = DateTime.fromISO(isoDate).setLocale(dateLocale);
	let format = date.toLocaleString(DateTime.TIME_24_SIMPLE);
	return format;
}

function getTimeAgo(isoDate) {
	let DateTime = luxon.DateTime;
	let dateLocale = window.navigator.userLanguage || window.navigator.language;
	let date = DateTime.fromISO(isoDate).setLocale(dateLocale);
	let ago = date.toRelativeCalendar();
	ago = capitalizeFirstLetter(ago);
	return ago;
}

function getDateLabel(isoDate) {
	let fullDateTime = formatDate(isoDate);
	let ago = getTimeAgo(isoDate);
	let dateLabel = ago + ", " + fullDateTime;
	return dateLabel;
}

function getTimeAgoAndDate(isoDate) {
	let timeAgo = getTimeAgo(isoDate);
	let date = formatDate(isoDate);
	return timeAgo + ", " + date;
}

// ============================
// User preferences
// ============================
function getUserPreferences(callBackFunction) {
	let url = "users/preferences";
	getJson(url, callBackFunction);
}

function postUserPreferences(data) {
	postJson("users/preferences", data, postSelectedCategoryCompleted);
}

function postSelectedCategoryCompleted() {
	if (LOGGING) {
		console.log("Posted user preferences successfully");
	}
}

// ===========================================================================================
// Books in view, enabling users to swipe left and right between books while in book view
// ===========================================================================================
function postBooksInView(booksInView) {
	postJson("books-in-view/view", booksInView, postBooksInViewCompleted);
}

function postBooksInViewCompleted() {
	if (LOGGING) {
		console.log("Books in view updated successfully");
	}
}

// =============================================================
// Search
// =============================================================

// // Shorthand for $( document ).ready()
$(function() {
	$('#main-menu-search-input').autocomplete({
		serviceUrl: 'autocomplete-search',
		minChars: SEARCH_MIN_CHARS,
		onSelect: search
	});

	$('#main-menu-search-input').on('keyup', search);

	let lastQuery = sessionStorage.getItem(LAST_QUERY);
	console.log(lastQuery);

	if (lastQuery != null && lastQuery.length > 0) {
		$('#main-menu-search-input').val(lastQuery);
		search();
	} else {
		hideSearchDiv();
	}

	console.log("search init done");
});

function search() {
	let searchText = $("#main-menu-search-input").val();
	if (searchText != undefined) {
		sessionStorage.setItem(LAST_QUERY, searchText);
	}
	if (searchText != undefined && searchText.length >= SEARCH_MIN_CHARS) {
		getNewSearchResults(searchText);
	} else {
		hideSearchDiv();
	}
}

function clearSearch() {
	sessionStorage.removeItem(LAST_QUERY);
}

function hideSearchDiv() {
	$("#sheetmusic-search-results").hide();
	$("#sheetmusic-main-body").show();
	$("#sheetmusic-menu-items").show();
	$("#main-menu-search-input").width(100);
}

function showSearchDiv() {
	$("#sheetmusic-main-body").hide();
	$("#sheetmusic-search-results").show();
	$("#sheetmusic-menu-items").hide();
	$("#main-menu-search-input").width(300);
}

function clearSearchText() {
	let old = $("#main-menu-search-input").val();
	console.log(old);
	$("#main-menu-search-input").val("");
	if (old != undefined && old.length > 0) {
		search();
	}
}

function getNewSearchResults(search) {
	console.log("Searching for " + search);
	let url = "search?q=" + search;
	getJson(url, updateSearchResults);
}

function updateSearchResults(data) {
	let results = enforceArrayFromElements(data.results);
	//console.log(results);
	let html = "<ul class='card-list'><div class='category-items'>";
	for (let result of results) {
		let label = result.label;
		let checksum = result.checksum;
		let page = result.page;
		let link = "page.jsp?id=" + checksum + "&page=" + page;
		html += createCardHtml(checksum, page, label, link);
	}
	html += "</div></ul>";
	$("#sheetmusic-search-results").html(html);
	showSearchDiv();
}

function updateFavoriteIcon(favorite) {
	console.log("updateFavoriteIcon(" + favorite + ")");
	if (favorite === true) {
		$("#favorites-yes").show();
		$("#favorites-no").hide();
	} else {
		$("#favorites-yes").hide();
		$("#favorites-no").show();
	}
}


