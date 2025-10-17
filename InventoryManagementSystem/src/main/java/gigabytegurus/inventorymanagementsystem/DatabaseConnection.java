package gigabytegurus.inventorymanagementsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection
{
    private static final String URL = "jdbc:mysql://localhost:3306/clothing_inventory?serverTimezone=GMT";
    private static final String USER = "root"; // your MySQL username
    private static final String PASSWORD = "admin"; // your MySQL password (if any)

    private static Connection conn;

    public static Connection getConnection() throws SQLException
    {
        if (conn == null || conn.isClosed())
        {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to MySQL");
        }
        return conn;
    }
}
