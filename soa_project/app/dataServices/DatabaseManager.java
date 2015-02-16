package dataServices;
import org.sql2o.*;
import play.Logger;

public class DatabaseManager {

	public static String connString = "jdbc:sqlite:sakila.sqlite";
	public static Sql2o sql2o;
	
	// Drops and recreates all data in the database, and populates example data.
	public static void initialize() {

		openConnection();
		
		// Drop existing objects
		//dropExistingObjects();

		// Create database objects
		//createObjects();
		
		// Populate example data
		//populateTestData();

	}

	private static void openConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			
			// just attempt to get the connection but don't do anything with it. This is 
			// just to test that the JDBC driver can be initialized successfully.
			java.sql.DriverManager.getConnection(connString);
			Logger.info("Connection opened!");
		} catch (Exception e) {
			Logger.error(e.getClass().getName() + ": " + e.getMessage());
			Logger.info("SQLite connection failed to open");
		}
		
		// No username or password are necessary when using SQLite
		String username = "";
		String password = "";
		
		sql2o = new Sql2o(connString, username, password);
	}
	
	private static void dropExistingObjects() {
		try (org.sql2o.Connection conn = sql2o.open()) {
			conn.createQuery("drop table if exists customer").executeUpdate();
		}
	}

	private static void createObjects() {
		try (org.sql2o.Connection conn = sql2o.open()) {
			conn.createQuery("create table customer (" +
				"Id INTEGER PRIMARY KEY ASC NOT NULL," +
				"FirstName TEXT NOT NULL," + 
				"LastName TEXT NOT NULL)").executeUpdate();
		}
	}

	private static void populateTestData() {
		try (org.sql2o.Connection conn = sql2o.open()) {
			conn.createQuery("insert into customer (FirstName, LastName) values ('Matt', 'Meisinger')").executeUpdate();
			conn.createQuery("insert into customer (FirstName, LastName) values ('Joe', 'Smith')").executeUpdate();
		}
	}
}
