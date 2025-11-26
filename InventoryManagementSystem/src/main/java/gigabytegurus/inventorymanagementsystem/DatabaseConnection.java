package gigabytegurus.inventorymanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseConnection
{
	static Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
	
    private static final String URL = "jdbc:mysql://localhost:3306/clothing_inventory?serverTimezone=GMT";
    private static final String USER = "root";

    private DatabaseConnection() {
        
    }
    
    private static Connection conn;

    public static Connection getConnection() throws SQLException
    {
        if (conn == null || conn.isClosed())
        {
        		String password = System.getProperty("database.PASSWORD", "");
            conn = DriverManager.getConnection(URL, USER, password);
            logger.info("✅ Connected to MySQL");
        }
        return conn;
    }
}
