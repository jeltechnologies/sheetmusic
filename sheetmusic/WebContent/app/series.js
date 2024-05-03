const SELECT_ID_PREFIX = "check-select-";
var seriesSelected;
var allSeries;
var booksInView = [];

function seriesPageReady() {
	getJson("library/series", updateSeries);
}

function handleSeriesSelectClick() {
	seriesSelected = $('#series-select').val();
	postSelectedSeries();
	updateBooksForSeries();
}

function updateSeries(data) {
	let series = enforceArrayFromElements(data);
	allSeries = series;
	getUserPreferences(updateSeriesUserPreferences);
}

function updateBooksForSeries() {
	let url = "library/series?series=" + encodeURIComponent(seriesSelected);
	getJson(url, updateHtmlBooksForSeries);
}

function updateHtmlBooksForSeries(data) {
	booksInView = [];
	let books = enforceArrayFromElements(data);
	let html = "";
	html = "<ul class='card-list'>";
	if (books != undefined) {
		for (let i = 0; i < books.length; i++) {
			let book = books[i];
			let checksum = book.fileChecksum;
			let title = book.title + " (" + book.nrOfPages + ")";
			let link = "book.jsp?id=" + encodeURIComponent(book.fileChecksum);
			let page = 1;
			html = html + createCardHtml(checksum, page, title, link);
			booksInView.push(checksum);
		}
	}
	html = html + "</ul>";
	$('#books').html(html);
	postBooksInView(booksInView);
}

function updateSeriesUserPreferences(data) {
	seriesSelected = data.seriesSelected;
	updateBooksForSeries();
}

function postSelectedSeries() {
	let data = {};
	data.seriesSelected = seriesSelected;
	postUserPreferences(data);
}

$(document).ready(seriesPageReady());