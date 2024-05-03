var artist;

function artistPageReady() {
	clearSearch();
	artist = getRequestParameter("artist");
	getJson("library/artists?artist=" + encodeURIComponent(artist), updateArtist);
	updateFavoriteStatus();
}

function favoritesBookClicked(yesOrNo) {
	console.log("favoritesBookClicked " + yesOrNo);
	let url = "rest/favorites/artist/" + encodeURIComponent(artist);
	if (yesOrNo == 'yes') {
		deleteJson(url, updateFavoriteStatus);
	} else {
		let data = {};
		data.artist = artist;
		data.favorite = true;
		postJson(url, data, updateFavoriteStatus);
	}
}

function updateFavoriteStatus() {
	let url = "rest/favorites/artist/" + encodeURIComponent(artist);
	getJson(url, function(data) {
		console.log(data);
		let favorite = data.favorite;
		updateFavoriteIcon(favorite);
	});
}

function updateArtist(data) {
	let artist = data.artist;
	$('#artist').html(artist);
	let books = enforceArrayFromElements(data.books);
	let html = "";

	if (books != undefined && books.length > 0) {
		html += createCategoryTitle('Books');
		html = html + "<ul class='card-list'>";
		for (let i = 0; i < books.length; i++) {
			let book = books[i];
			let title = book.title + " (" + book.nrOfPages + ")";
			let link = "book.jsp?id=" + book.fileChecksum;
			let page = 1;
			html = html + createCardHtml(book.fileChecksum, page, title, link);
		}
		html = html + "</ul>";
	}

	$('#books').html(html);

	html = "";
	let songs = enforceArrayFromElements(data.songs);
	if (songs != undefined && songs.length > 0) {
		html += createCategoryTitle('Songs');
		html = html + "<ul class='card-list'>";
		for (let i = 0; i < songs.length; i++) {
			let song = songs[i];
			let checksum = song.fileChecksum;
			let title = song.title;
			let page = song.pageNr;
			let link = "page.jsp?id=" + checksum + "&page=" + page;
			html = html + createCardHtml(checksum, page, title, link);
		}
		html = html + "</ul>";
		$('#songs').show();
	}
	else {
		$('#songs').hide();
	}
	$('#songs').html(html);
}

$(document).ready(artistPageReady());