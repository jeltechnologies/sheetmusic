var categories;
var allCategoryNames = [];
var categorySelected = "";
var booksInView = [];
const SELECT_ID_PREFIX = "check-category-";

function handleCategorySelectClick() {
	categorySelected = $('#categories-select').val();
	postSelectedCategory();
	updateHtmlCategories()
}

function categoriesPageReady() {
	clearSearch();
	getJson("library/categories/books", updateCategoriesBooks);
}

function updateCategoriesBooks(data) {
	categories = enforceArrayFromElements(data.categories);
	getUserPreferences(updateUserPreferences);
}

function updateHtmlCategories() {
	let html = "";
	html = html + "<ul class='card-list'>";
	for (let category of categories) {
		let name = category.category.name;
		if (name === categorySelected) {
			html = html + "<div class=\"category-items\"><div class=\"category-title\">" + name + "</div>";
			html = html + getHtmlForBooksCategory(category);
			html = html + "</div>";
		}
	}
	html = html + "</ul>";
	$('#categories').html(html);
	postBooksInView(booksInView);
}

function initCategoriesSelectorFilter(categories) {
	allCategoryNames = [];
	for (let i = 0; i < categories.length; i++) {
		let name = categories[i].category.name;
		allCategoryNames.push(name);
	}
}

function getHtmlForBooksCategory(category) {
	booksInView = [];
	let html = "";
	let books = enforceArrayFromElements(category.books);
	if (books != undefined) {
		let page = 1;
		for (let book of books) {
			let label = book.label + " (" + book.nrOfPages + ")";
			let link = "book.jsp?id=" + book.fileChecksum;
			let checksum = book.fileChecksum;
			html = html + createCardHtml(checksum, page, label, link);
			booksInView.push(checksum);
		}
	}
	return html;
}

function updateUserPreferences(data) {
	categorySelected = data.categorySelected;
	initCategoriesSelectorFilter(categories);
	updateHtmlCategories();
}

function postSelectedCategory() {
	let data = {};
	data.categorySelected = categorySelected;
	postUserPreferences(data);
}

$(document).ready(categoriesPageReady());