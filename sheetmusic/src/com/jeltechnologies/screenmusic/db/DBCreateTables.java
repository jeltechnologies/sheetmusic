package com.jeltechnologies.screenmusic.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeltechnologies.screenmusic.config.Admin;

public class DBCreateTables {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBCreateTables.class);

    private final Connection connection;

    private static final boolean UPDATE_FOREIGN_KEYS_TO_BOOK = false;

    public DBCreateTables(Connection connection) {
	this.connection = connection;
    }

    public void prepareDatabase(Admin admin) throws SQLException {
	createUsersTable(admin);
	createBooksTables();
	createFavoritePages();
	createFavoriteBooks();
	createFavoriteArtists();
	createHistory();
	createCategories();
	createPreferencesTable();
	if (UPDATE_FOREIGN_KEYS_TO_BOOK) {
	    updateForeignKeysToBooks();
	}
	if (LOGGER.isInfoEnabled()) {
	    LOGGER.info("Database prepared succesfully");
	}
    }

    private void createUsersTable(Admin admin) throws SQLException {
	StringBuilder b = new StringBuilder();
	b.append("CREATE TABLE IF NOT EXISTS Users (");
	b.append(" name TEXT NOT NULL,");
	b.append(" sheetfolder TEXT NOT NULL,");
	b.append(" preferences TEXT NULL,");
	b.append(" CONSTRAINT userspk PRIMARY KEY (name)");
	b.append(")");
	DBUtils.executeSQL(connection, b.toString());

	PreparedStatement st = null;
	ResultSet rs = null;
	PreparedStatement insSt = null;
	PreparedStatement updateSt = null;
	try {
	    st = connection.prepareStatement("SELECT name FROM Users WHERE name=?");
	    st.clearParameters();
	    st.setString(1, admin.name());
	    rs = st.executeQuery();
	    boolean userExists = rs.next();
	    if (!userExists) {
		String insertSlurp = "INSERT INTO Users(name, sheetfolder) VALUES (?, ?);";
		insSt = connection.prepareStatement(insertSlurp);
		insSt.setString(1, admin.name());
		insSt.setString(2, admin.sheetsFolder());
		insSt.executeUpdate();
		if (LOGGER.isInfoEnabled()) {
		    LOGGER.info("Added admin user '" + admin.name() + "' to database");
		}
	    } else {
		updateSt = connection.prepareStatement("UPDATE Users SET sheetFolder=? WHERE name=?");
		updateSt.setString(1, admin.sheetsFolder());
		updateSt.setString(2, admin.name());
		updateSt.executeUpdate();
		if (LOGGER.isTraceEnabled()) {
		    LOGGER.trace("Admin user '" + admin.name() + "' already in database");
		}
	    }
	} finally {
	    if (rs != null) {
		rs.close();
	    }
	    if (insSt != null) {
		insSt.close();
	    }
	    if (updateSt != null) {
		updateSt.close();
	    }
	    if (st != null) {
		st.close();
	    }
	}
    }

    private void createBooksTables() throws SQLException {
	StringBuilder cb = new StringBuilder();
	cb.append("CREATE TABLE IF NOT EXISTS Books (");
	cb.append(" checksum TEXT,");
	cb.append(" nrOfPages INT,");
	cb.append(" title TEXT,");
	cb.append(" artist TEXT,");
	cb.append(" series TEXT,");
	cb.append(" description TEXT, ");
	cb.append(" CONSTRAINT books_pk PRIMARY KEY (checksum)");
	cb.append(");");
	String createBooksSQL = cb.toString();
	DBUtils.executeSQL(connection, createBooksSQL);

	StringBuilder f = new StringBuilder();
	f.append("CREATE TABLE IF NOT EXISTS Files(");
	f.append(" relativeFileName TEXT,");
	f.append(" books_checksum TEXT,");
	f.append(" dateModified TIMESTAMP,");
	f.append(" size BIGINT,");
	f.append(" CONSTRAINT files_pk PRIMARY KEY(relativeFileName),");
	f.append(" CONSTRAINT files_fk_books FOREIGN KEY(books_checksum) REFERENCES Books(checksum) ON DELETE CASCADE ON UPDATE CASCADE");
	f.append(");");
	String createFilesSQL = f.toString();
	DBUtils.executeSQL(connection, createFilesSQL);

	StringBuilder cp = new StringBuilder();
	cp.append("CREATE TABLE IF NOT EXISTS Pages (");
	cp.append(" books_checksum TEXT,");
	cp.append(" pageNumber INT,");
	cp.append(" artist TEXT,");
	cp.append(" title TEXT,");
	cp.append(" artist2 TEXT,");
	cp.append(" title2 TEXT,");
	cp.append(" description TEXT,");
	cp.append(" text TEXT,");
	cp.append(" CONSTRAINT pages_pk PRIMARY KEY (books_checksum, pageNumber),");
	cp.append(" CONSTRAINT pages_fk_books FOREIGN KEY (books_checksum) REFERENCES Books(checksum) ON DELETE CASCADE ON UPDATE CASCADE");
	cp.append(");");
	String createPagesSQL = cp.toString();
	DBUtils.executeSQL(connection, createPagesSQL);
    }

    private void createFavoritePages() throws SQLException {
	StringBuilder cf = new StringBuilder();
	cf.append("CREATE TABLE IF NOT EXISTS FavoritePages (");
	cf.append(" username TEXT,");
	cf.append(" position INT,");
	cf.append(" userLabel TEXT,");
	cf.append(" books_checksum TEXT,");
	cf.append(" page INT,");
	cf.append(" CONSTRAINT favpagepk PRIMARY KEY (username, books_checksum, page),");
	cf.append(" CONSTRAINT favpagebook FOREIGN KEY (books_checksum) REFERENCES Books(checksum) ON DELETE CASCADE ON UPDATE CASCADE");
	cf.append(");");
	DBUtils.executeSQL(connection, cf.toString());
    }
    
    private void createFavoriteBooks() throws SQLException {
	StringBuilder cf = new StringBuilder();
	cf.append("CREATE TABLE IF NOT EXISTS FavoriteBooks (");
	cf.append(" username TEXT,");
	cf.append(" position INT,");
	cf.append(" books_checksum TEXT,");
	cf.append(" CONSTRAINT favbookpk PRIMARY KEY (username, books_checksum),");
	cf.append(" CONSTRAINT favbookfkbook FOREIGN KEY (books_checksum) REFERENCES Books(checksum) ON DELETE CASCADE ON UPDATE CASCADE");
	cf.append(");");
	DBUtils.executeSQL(connection, cf.toString());
    }
    
    private void createFavoriteArtists() throws SQLException {
	StringBuilder cf = new StringBuilder();
	cf.append("CREATE TABLE IF NOT EXISTS FavoriteArtists(");
	cf.append(" username TEXT,");
	cf.append(" artist TEXT,");
	cf.append(" position INT,");
	cf.append(" CONSTRAINT favartistpk PRIMARY KEY (username, artist)");
	cf.append(");");
	DBUtils.executeSQL(connection, cf.toString());
    }

    private void createHistory() throws SQLException {
	StringBuilder h = new StringBuilder();
	h.append("CREATE TABLE IF NOT EXISTS History (");
	h.append(" username TEXT,");
	h.append(" moment TIMESTAMP,");
	h.append(" books_checksum TEXT,");
	h.append(" page INT,");
	h.append(" CONSTRAINT historypk PRIMARY KEY (username, moment),");
	h.append(" CONSTRAINT historyfkbooks FOREIGN KEY (books_checksum) REFERENCES Books(checksum) ON DELETE CASCADE ON UPDATE CASCADE");
	h.append(");");
	DBUtils.executeSQL(connection, h.toString());
    }

    private void createCategories() throws SQLException {
	StringBuilder c = new StringBuilder();
	c.append("CREATE TABLE IF NOT EXISTS Categories (");
	c.append(" name TEXT,");
	c.append(" CONSTRAINT categoriespk PRIMARY KEY (name)");
	c.append(");");
	DBUtils.executeSQL(connection, c.toString());

	StringBuilder cb = new StringBuilder();
	cb.append("CREATE TABLE IF NOT EXISTS BooksCategories (");
	cb.append(" categories_name TEXT,");
	cb.append(" books_checksum TEXT,");
	cb.append(" CONSTRAINT bookscategories_pk PRIMARY KEY (categories_name, books_checksum), ");
	cb.append(" CONSTRAINT bookscategories_categories FOREIGN KEY (categories_name) REFERENCES categories(name) ON DELETE CASCADE ON UPDATE CASCADE,");
	cb.append(" CONSTRAINT bookscategories_fk_books FOREIGN KEY (books_checksum) REFERENCES books(checksum) ON DELETE CASCADE ON UPDATE CASCADE");
	cb.append(");");
	DBUtils.executeSQL(connection, cb.toString());

	StringBuilder cp = new StringBuilder();
	cp.append("CREATE TABLE IF NOT EXISTS PagesCategories (");
	cp.append(" category_name TEXT,");
	cp.append(" pages_books_checksum TEXT,");
	cp.append(" page_pageNumber INT,");
	cp.append(" CONSTRAINT pagecategories_pk PRIMARY KEY (category_name, pages_books_checksum, page_pageNumber), ");
	cp.append(" CONSTRAINT pagescategories_fk_categories FOREIGN KEY (category_name) REFERENCES categories(name) ON DELETE CASCADE ON UPDATE CASCADE,");
	cp.append(
		" CONSTRAINT pagescategories_fk_pages FOREIGN KEY (pages_books_checksum, page_pageNumber) REFERENCES pages(books_checksum, pageNumber) ON DELETE CASCADE ON UPDATE CASCADE");
	cp.append(");");
	DBUtils.executeSQL(connection, cp.toString());
    }

    private void createPreferencesTable() throws SQLException {
	StringBuilder b = new StringBuilder();
	b.append("CREATE TABLE IF NOT EXISTS Preferences (");
	b.append(" username TEXT,");
	b.append(" preferences TEXT,");
	b.append(" CONSTRAINT preferences_pk PRIMARY KEY(username)");
	b.append(");");
	DBUtils.executeSQL(connection, b.toString());
    }

    /**
     * Trick to update the tables afterwards, to enable change of checksum primary key. Should be only run once
     */
    private void updateForeignKeysToBooks() throws SQLException {
	updateConstraint("favorites", "favorities_fk_books");
	updateConstraint("files", "files_fk_books");
	updateConstraint("history", "history_fk_books");
	updateConstraint("pages", "pages_fk_books");
    }

    /**
     * Trick to update the tables afterwards, to enable change of checksum primary key. Should be only run once
     */
    private void updateConstraint(String table, String constraintName) throws SQLException {
	StringBuilder b = new StringBuilder();
	b.append("ALTER TABLE ").append(table);
	b.append(" DROP CONSTRAINT IF EXISTS ").append(constraintName).append(";");
	String sqlDrop = b.toString();
	try {
	    DBUtils.executeSQL(connection, sqlDrop);
	} catch (SQLException e) {
	    LOGGER.info("Could not drop constraint " + constraintName + " because " + e.getMessage());
	}

	b = new StringBuilder();
	b.append("ALTER TABLE ").append(table);
	b.append(" ADD CONSTRAINT ").append(constraintName);
	b.append(" FOREIGN KEY (books_checksum)");
	b.append(" REFERENCES books(checksum)");
	b.append(" ON UPDATE CASCADE ON DELETE CASCADE;");
	String sqlAdd = b.toString();

	try {
	    DBUtils.executeSQL(connection, sqlAdd);
	} catch (SQLException e) {
	    LOGGER.error("Could not add foreign key constraint " + constraintName + " because " + e.getMessage());
	    throw e;
	}
    }

}
