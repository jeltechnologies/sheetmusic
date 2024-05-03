const startCard = "<li class='card-list'>";
const endCard = "</li>";
var counter = 0;
var showAll = false;
var artists;
var artistsAutoComplete = [];

function artistsPageReady() {
	clearSearch();
	getJson("library/artists?sort=artist", updateArtists);
}

function updateArtists(data) {
	artists = enforceArrayFromElements(data.artists);
	artistsAutoComplete = [];
	let i = 1;
	for (let artist of artists) {
		let lookupItem = {};
		lookupItem.value = artist.artist;
		lookupItem.data = artist.artist;
		artistsAutoComplete.push(lookupItem);
		i++;
	}
	updatePage();
}

function updatePage() {
	let groups = getGroupedOnFirstCharAlphabet(artists);
	let html = "";
	html = html + "<ul class='card-list'>";
	html = html + addArtistsCards(groups);
	html = html + "</ul>";
	$('#artists').html(html);
}

function linkToArtistPage(artistName) {
	return "artist.jsp?artist=" + encodeURIComponent(artistName);
}

function handleFilterClick() {
	let filter = $('#show-selector').val();
	if (filter === 'ALL') {
		showAll = true;
	} else {
		showAll = false;
	}
	updatePage();
}

function addArtistsCards(groups) {
	let html = "";
	counter = 0;
	for (group of groups) {
		let letter = group.letter.toUpperCase();
		let letterDiv = "<div class='table-contents-letter'>" + letter + "</div>";
		html = addCardItem(html, letterDiv, true);
		for (let artist of group.artists) {
			let nrOfBooks = artist.books.length;
			let passFilter = false;
			if (showAll) {
				passFilter = true;
			} else {
				if (nrOfBooks > 0) {
					passFilter = true;
				}
			}
			if (passFilter) {
				let hits = artist.books.length + artist.songs.length;
				let title = artist.artist + " (" + hits + ")";
				let link = linkToArtistPage(artist.artist);
				let label = "<div class='table-contents-link'><a href='" + link + "'>" + title + "</a></div>";
				html = addCardItem(html, label, false);
			}
		}
	}
	html = html + endCard;
	return html;
}

function addCardItem(html, label, enforceNewCard) {
	if (counter == 7 || enforceNewCard === true) {
		html = html + endCard;
		counter = 0;
	}
	if (counter == 0) {
		html = html + startCard;
	}
	html = html + label;
	counter++;
	return html;
}

function getGroupedOnFirstCharAlphabet(artists) {
	let groups = [];
	for (let artist of artists) {
		if (showAll || artist.books.length > 0) {
			let f = artist.artistSortField.charAt(0).toLowerCase();
			if (f >= '0' && f <= '9+') {
				f = '0-9';
			}
			let group = undefined;
			for (let existingGroup of groups) {
				if (existingGroup.letter === f) {
					group = existingGroup;
				}
			}
			if (group === undefined) {
				group = {};
				group.letter = f;
				group.artists = [];
				groups.push(group);
			}
			group.artists.push(artist);
		}
	}
	return groups;
}

function getHtmlForBooks(artist) {
	//console.log(artist);
	let html = "";
	let books = artist.books;
	if (books != undefined) {
		let checksum;
		let page;
		let title = artist.artist + " (" + books.length + ")";
		let link = "artist.jsp?artist=" + encodeURIComponent(artist.artist);
		//console.log(link);
		if (books != undefined && books.length > 0) {
			books = enforceArrayFromElements(books);
			let nrOfBooks = books.length;
			let random = Math.floor(Math.random() * nrOfBooks);
			checksum = books[random].fileChecksum;
			page = 1;
			html = createCardHtml(checksum, page, title, link);
		}
	}
	return html;
}

function getHtmlForSongs(artist) {
	let songs = artist.songs;
	let html = "";
	let checksum;
	let page;

	if (songs != undefined) {
		songs = enforceArrayFromElements(songs);
		let total = songs.length;
		if (total > 0) {
			let title = artist.artist + " (" + total + ")";
			let link = "artist.jsp?artist=" + encodeURIComponent(artist.artist);
			let random = Math.floor(Math.random() * songs.length);
			checksum = songs[random].fileChecksum;
			page = songs[random].pageNr;
			html = createCardHtml(checksum, page, title, link);
		}
	}
	return html;
}

$(document).ready(artistsPageReady());