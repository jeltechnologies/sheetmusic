function favoritePageReady() {
	getAllFavorites();
}

function getAllFavorites() {
	getJson('rest/favorites/artist', receivedArtists);
	getJson('rest/favorites/book', receivedBooks);
	getJson('rest/favorites/page', receivedPages);
}

function receivedArtists(data) {
	let favoriteArtists = enforceArrayFromElements(data);
	let html = "";
	if (favoriteArtists != undefined && favoriteArtists.length > 0) {
		html += createCategoryTitle('Favorite artists');
		html += "<ul class='card-list'>";
		for (let i = 0; i < favoriteArtists.length; i++) {
			let fav = favoriteArtists[i];
			let randomBookIndex = Math.floor(Math.random() * fav.bookIds.length);
			let bookId = fav.bookIds[randomBookIndex];
			let title = fav.artist;
			let link = "artist.jsp?artist=" + encodeURIComponent(fav.artist);
			let page = 1;
			html += createCardHtml(bookId, page, title, link);
		}
		html += "</ul>";
	}
	console.log(html);
	$('#artists').html(html);
}

function receivedBooks(data) {
	let favoriteBooks = enforceArrayFromElements(data);
	let html = "";
	if (favoriteBooks != undefined && favoriteBooks.length > 0) {
		html += createCategoryTitle('Favorite songbooks');
		html += "<ul class='card-list'>";
		for (let i = 0; i < favoriteBooks.length; i++) {
			let book = favoriteBooks[i].book;
			let title = book.title + " (" + book.nrOfPages + ")";
			let link = "book.jsp?id=" + book.fileChecksum;
			let page = 1;
			html += createCardHtml(book.fileChecksum, page, title, link);
		}
		html += "</ul>";
	}
	console.log(html);
	$('#books').html(html);
}

function receivedPages(data) {
	let favoritePages = enforceArrayFromElements(data);
	console.log(favoritePages);
	let html = "";
	if (favoritePages  != undefined && favoritePages.length > 0) {
		html += createCategoryTitle('Favorite pages');
		html += "<ul class='card-list'>";
		for (let i = 0; i < favoritePages.length; i++) {
			let bookPage = favoritePages[i].bookPage;
			let title = favoritePages[i].userLabel; // + " (" + book.nrOfPages + ")";
			let bookId = bookPage.bookFileChecksum;
			let link = "book.jsp?id=" + bookId;
			let pageNumber = favoritePages[i].pageNumber;
			html += createCardHtml(bookId, pageNumber, title, link);
		}
		html += "</ul>";
	}
	console.log(html);
	$('#pages').html(html);
	
	//console.log(data);
}

$(document).ready(favoritePageReady());