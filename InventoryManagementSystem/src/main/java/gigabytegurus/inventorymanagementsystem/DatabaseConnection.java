package gigabytegurus.inventorymanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

public class DatabaseConnection
{
	static Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
	
    private static final String URL = "jdbc:mysql://localhost:3307/clothing_inventory?serverTimezone=GMT";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private DatabaseConnection() {
        
    }
    
    private static Connection conn;

    public static Connection getConnection() throws SQLException
    {
        if (conn == null || conn.isClosed())
        {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.info("✅ Connected to MySQL");
        }
        return conn;
    }
}
