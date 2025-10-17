package gigabytegurus.inventorymanagementsystem;

import java.sql.*;

public class User
{
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

    // --- Getters & Setters (same as before) ---

    public boolean login(String username, String password)
    {
        try (Connection conn = DatabaseConnection.getConnection())
        {
            String query = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(query);
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
        try (Connection conn = DatabaseConnection.getConnection())
        {
            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            stmt.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            System.out.println("⚠️ Registration failed: " + e.getMessage());
        }
        return false;
    }

    public void logout()
    {
        System.out.println("User logged out");
    }
}
