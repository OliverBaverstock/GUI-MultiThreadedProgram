package assign2package;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DBQueries {

	// SQL account user name
	private final String userName = "root";
	// SQL account password
	private final String password = "roottoor";
	// Computer Name running SQL
	private final String serverName = "localhost";
	// Port of SQL Server
	private final int portNumber = 3306;
	// Name of DB
	private final String dbName = "Assign2";
	// Name of DB table
	private final String tableName = "students";
	// Name of connection variable
	private Connection conn = null;

	// String used to tell if the database connected or not
	String ConnectedOrNot;
	// Setting clientID of the user who logged in
	public static int clientID;
	// Setting first and last name of the user who logged in
	public static String clientFName;
	public static String clientSName;
	// Setting the number of times the user has logged in
	public static int clientTotReq;

	// Get new DB connection
	// Throws Exception if error
	public Connection getConnection() throws SQLException {
		Properties connectionProps = new Properties();
		// Database user name and password
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		conn = DriverManager.getConnection(
				"jdbc:mysql://" + this.serverName + ":" + this.portNumber + "/" + this.dbName, connectionProps);

		return conn;
	}

	// Connect to DB
	// Print Successful Connection
	// If error throw exception and print error message
	public void run() {

		// Connect to MySQL
		try {
			conn = getConnection();
			ConnectedOrNot = "Connected to database";
		} catch (SQLException e) {
			ConnectedOrNot = "ERROR: Could not connect to the database";
			e.printStackTrace();
			return;
		}
	}

	public boolean executeUpdate(Connection conn, String command) throws SQLException {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(command); // This will throw a SQLException if it fails
			return true;
		} finally {

			// This will run whether we throw an exception or not
			if (stmt != null) {
				stmt.close();
			}
		}
	}

	// Used to validate the user name(FNAME) and password(STUD_ID) is within the
	// database
	public boolean login(String uName, String pWord) {	
		try {
			Connection conn = getConnection();
			PreparedStatement s;
			// Selects the user where it matches the user name(FNAME) and password(STUD_ID)
			s = conn.prepareStatement(
					"select * from " + tableName + " where FNAME ='" + uName + "' and STUD_ID='" + pWord + "'");
			// Adds results to ResultSet rs
			ResultSet rs = s.executeQuery();

			// If rs returns a match return true and set the chosen variables
			if (rs.next()) {
				clientID = rs.getInt("SID");
				clientFName = rs.getString("FNAME");
				clientSName = rs.getString("SNAME");
				clientTotReq = rs.getInt("TOT_REQ");
				s.close();
				// Calls updateRequests() to update the logins attempts(TOT_REQ) after each
				// successful login by 1
				updateRequests();
				return true;
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return false;
	}

	// Used to update the users number of logins(TOT_REQ)
	public void updateRequests() {
		try {
			clientTotReq++;
			Connection conn = getConnection();
			PreparedStatement s;
			s = conn.prepareStatement("update " + tableName + " set TOT_REQ = ? where SID = ?");
			s.setInt(1, clientTotReq);
			s.setInt(2, clientID);
			s.executeUpdate();
			s.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
}