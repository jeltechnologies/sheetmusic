const startCard = "<li class='card-list'>";
const endCard = "</li>";
var counter = 0;
var songBooks;
var userPreferences;
var booksInView = [];

function songBooksPageReady() {
	console.log("SongBooksPageReady");
	const sortSelector = document.getElementById('sort-selector');
	if (sortSelector != null) {
		sortSelector.addEventListener('change', () => {
			userChangedSortSelector();
		});
	}
	getUserPreferences(updateUserPreferences);
}

function updateUserPreferences(data) {
	userPreferences = data;
	console.log(userPreferences);
	if (userPreferences.songBooksSorting == undefined) {
		userPreferences.songBooksSorting = "RANDOM";
	}
	getSongBooks();
}

function getSongBooks() {
	let sorting = userPreferences.songBooksSorting;
	if (sorting == undefined) {
		sorting = "RANDOM";
	}
	$("#sort-selector").val(sorting);
	let url = "library/songbooks?sort=" + sorting;
	getJson(url, updateSongBooks);
}

function updateSongBooks(data) {
	songBooks = enforceArrayFromElements(data);
	renderBooks();
}

function renderBooks() {
	booksInView = [];
	let html = "";
	html = html + "<ul class='card-list'>";
	for (let book of songBooks) {
		html = html + getHtmlForBook(book);
		booksInView.push(book.fileChecksum);
	}
	html = html + "</ul>";
	$('#songbooks').html(html);
	postBooksInView(booksInView);
}

function userChangedSortSelector() {
	console.log("userChangedSortSelector");
	let newSorting = $('#sort-selector').val();
	if (newSorting !== userPreferences.songBooksSorting) {
		console.log("User changed sorting to " + newSorting);
		userPreferences.songBooksSorting = newSorting;
		let prefs = {};
		prefs.songBooksSorting = userPreferences.songBooksSorting;
		postUserPreferences(prefs);
		getSongBooks();
	}
}

function getHtmlForBook(book) {
	//console.log(book);
	let page = 1;
	let label = book.label + " (" + book.nrOfPages + ")";
	let link = "book.jsp?id=" + book.fileChecksum;
	let checksum = book.fileChecksum;
	let html = createCardHtml(checksum, page, label, link);
	return html;
}

$(document).ready(songBooksPageReady());