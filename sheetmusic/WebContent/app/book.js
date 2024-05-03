const urlParams = new URLSearchParams(window.location.search);
const id = urlParams.get('id');
var xDown = null;
var yDown = null;

function editClicked() {
	let url = "edit.jsp?id=" + id;
	window.open(url, "_self");
}

function downloadClicked() {
	let location = "download/" + id;
	window.location = location;
}

function userClickedFavorite() {
}

function userClickedRefresh() {
	$("#refreshModal").show();
}

function refreshModalYes() {
	let operation = {};
	operation.operation = "refresh";

	let book = {};
	book.fileChecksum = id;
	operation.book = book;

	console.log(operation);
	postJson("library/book", operation, postEditChangeCompleted);
}

function postEditChangeCompleted() {
	hideRefreshModal();
}

function refreshModalNo() {
	hideRefreshModal();
}

function hideRefreshModal() {
	$("#refreshModal").hide();
}

/**
*   Refresh when back button is clicked, hack for Safari
*
  * If browser back button was used, flush cache
  * This ensures that user will always see an accurate, up-to-date view based on their state
  * https://stackoverflow.com/questions/8788802/prevent-safari-loading-from-cache-when-back-button-is-clicked
  */
(function() {
	window.onpageshow = function(event) {
		if (event.persisted) {
			window.location.reload();
		}
	};
})();

/**
* Refresh when back button is clicked, hack for Chrome
*/
function documentReadyForBook() {
	const [entry] = performance.getEntriesByType("navigation");
	if (entry["type"] === "back_forward") {
		location.reload();
	}
	documentReady();
}

function documentReady() {
	updateFavoriteIcon(false);
	postBookInView();
	document.addEventListener('touchstart', handleTouchStart, false);
	document.addEventListener('touchmove', handleTouchMove, false);
	window.addEventListener('keydown', handleKeyPress, false);
	updateFavoriteStatus();
}

function handleKeyPress(event) {
	//console.log("Key pressed with code " + event.keyCode);
	switch (event.keyCode) {
		case 37: {
			// left key
			left();
			break;
		}
		case 39: {
			// right key
			right();
			break;
		}
	}
}

function getTouches(evt) {
	return evt.touches ||             // browser API
		evt.originalEvent.touches; // jQuery
}

function handleTouchStart(evt) {
	const firstTouch = getTouches(evt)[0];
	xDown = firstTouch.clientX;
	yDown = firstTouch.clientY;
};

function handleTouchMove(evt) {
	if (!xDown || !yDown) {
		return;
	}
	var xUp = evt.touches[0].clientX;
	var yUp = evt.touches[0].clientY;
	var xDiff = xDown - xUp;
	var yDiff = yDown - yUp;

	if (Math.abs(xDiff) > Math.abs(yDiff)) {/*most significant*/
		if (xDiff > 0) {
			/* right swipe */
			right();
		} else {
			/* left swipe */
			left();
		}
	} else {
		if (yDiff > 0) {
			/* down swipe */
		} else {
			/* up swipe */
		}
	}
	/* reset values */
	xDown = null;
	yDown = null;
};

function swipe(type) {
	alert("Swipe " + type);
}

function right() {
	getJson("books-in-view/next", openBookFromView);
}

function left() {
	getJson("books-in-view/previous", openBookFromView);
}

function openBookFromView(data) {
	if (data != '' && data != 'null' && data != undefined) {
		let bookId = data;
		let link = "book.jsp?id=" + bookId;
		window.open(link, "_self");
	}
}

function postBookInView() {
	postJson("books-in-view/opened-book", id, postBookInViewCompleted);
}

function postBookInViewCompleted() {
}

function favoritesBookClicked(yesOrNo) {
	console.log("favoritesBookClicked " + yesOrNo);
	let willBeFavorite;
	if (yesOrNo == 'yes') {
		willBeFavorite = false;
	} else {
		willBeFavorite = true;
	}
	postFavoriteStatus(willBeFavorite);
}

function postFavoriteStatus(willBeFavorite) {
	if (willBeFavorite) {
		let data = {};
		data.favorite = willBeFavorite;
		data.book = {};
		data.book.fileChecksum = id;
		postJson("rest/favorites/book/" + id, data, updateFavoriteStatus);
	} else {
		deleteJson("rest/favorites/book/" + id, deleteCompleted);
	}
}

function deleteCompleted() {
	updateFavoriteStatus();
}

function updateFavoriteStatus() {
	console.log("updateFavoriteStatus");
	var url = "rest/favorites/book/" + id;
	getJson(url, updateFavoriteStatusFromAjax);
}

function updateFavoriteStatusFromAjax(data) {
	console.log(data);
	let isFavorite = data.favorite;
	updateFavoriteIcon(isFavorite);
}
