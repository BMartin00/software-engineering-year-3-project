package gigabytegurus.inventorymanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.swing.JOptionPane;

public class Transaction
{
	private int transactionId;
	private Item item;
	private int quantity;
	private String type;
	private Date date;
	
	
	public static boolean recordSale(int itemId, int quantitySold) {
        if (quantitySold <= 0) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Check stock
            String checkSQL = "SELECT quantity FROM items WHERE itemId = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                checkStmt.setInt(1, itemId);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    return false;
                }
                int currentStock = rs.getInt("quantity");
                if (currentStock < quantitySold) {
                    // Not enough stock, return false
                    return false;
                }
            }

            // Record sale
            String insertSQL = "INSERT INTO sales (item_id, quantity_sold) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
                stmt.setInt(1, itemId);
                stmt.setInt(2, quantitySold);
                stmt.executeUpdate();
            }

            // Update stock
            String updateSQL = "UPDATE items SET quantity = quantity - ? WHERE itemId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
                stmt.setInt(1, quantitySold);
                stmt.setInt(2, itemId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
	
// Process return - adds items back to inventory (uses item_returns table)
public static boolean processReturn(int itemId, int quantityReturned, String reason) {
    if (quantityReturned <= 0) {
        return false;
    }

    try (Connection conn = DatabaseConnection.getConnection()) {
        conn.setAutoCommit(false);

        // Verify item exists
        String checkSQL = "SELECT itemName, quantity FROM items WHERE itemId = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
            checkStmt.setInt(1, itemId);
            ResultSet rs = checkStmt.executeQuery();
            if (!rs.next()) {
                JOptionPane.showMessageDialog(null, "Item not found with ID: " + itemId);
                return false;
            }
        }

        // Record return in item_returns table
        String returnSQL = "INSERT INTO item_returns (item_id, quantity_returned, reason) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(returnSQL)) {
            stmt.setInt(1, itemId);
            stmt.setInt(2, quantityReturned);
            stmt.setString(3, reason);
            stmt.executeUpdate();
        }

        // Update stock - add returned items back to inventory
        String updateSQL = "UPDATE items SET quantity = quantity + ? WHERE itemId = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
            stmt.setInt(1, quantityReturned);
            stmt.setInt(2, itemId);
            int rowsUpdated = stmt.executeUpdate();
            
            if (rowsUpdated == 0) {
                conn.rollback();
                return false;
            }
        }

        conn.commit();
        return true;

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Database error processing return: " + e.getMessage());
        return false;
    }
}

	
	public void processReturn()
	{
		
	}
}
