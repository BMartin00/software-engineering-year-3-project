package gigabytegurus.inventorymanagementsystem;

import java.sql.*;
import java.util.logging.Logger;

public class User
{
	Logger logger = Logger.getLogger(getClass().getName());
    private int userId;
    private String username;
    private String password;
    private String role;

	public User(int userId, String username, String password, String role)
    {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }
	
	public int getUserId()
	{
		return userId;
	}

	public void setUserId(int userId)
	{
		this.userId = userId;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getRole()
	{
		return role;
	}

	public void setRole(String role)
	{
		this.role = role;
	}

    public boolean login(String username, String password)
    {
    		String query = "SELECT * FROM users WHERE username=? AND password=?";
    	
        try (Connection conn = DatabaseConnection.getConnection();
        		PreparedStatement stmt = conn.prepareStatement(query))
        {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.userId = rs.getInt("user_id");
                this.username = rs.getString("username");
                this.role = rs.getString("role");
                return true;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

    public boolean register(String username, String password, String role)
    {
    		String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)"; 
    	
        try (Connection conn = DatabaseConnection.getConnection();
        		PreparedStatement stmt = conn.prepareStatement(query))
        {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            
            stmt.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            logger.info("⚠️ Registration failed: " + e.getMessage());
        }
        return false;
    }

    public void logout()
    {
        logger.info("User logged out");
    }
}
