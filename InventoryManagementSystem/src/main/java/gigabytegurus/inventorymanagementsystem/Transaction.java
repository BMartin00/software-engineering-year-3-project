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

	// Process exchange - return one item and take another (uses item_exchanges table)
    public static boolean processExchange(int returnedItemId, int returnedQuantity, 
                                        int newItemId, int newItemQuantity, String reason) {
        if (returnedQuantity <= 0 || newItemQuantity <= 0) {
            return false;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            // Check if new item has enough stock
            String checkStockSQL = "SELECT quantity, itemName FROM items WHERE itemId = ?";
            String newItemName = "";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkStockSQL)) {
                checkStmt.setInt(1, newItemId);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null, "New item not found with ID: " + newItemId);
                    return false;
                }
                int currentStock = rs.getInt("quantity");
                newItemName = rs.getString("itemName");
                if (currentStock < newItemQuantity) {
                    JOptionPane.showMessageDialog(null, 
                        "Not enough stock for exchange. " + newItemName + " available: " + currentStock + ", requested: " + newItemQuantity);
                    return false;
                }
            }

            // Check if returned item exists
            String returnedItemName = "";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkStockSQL)) {
                checkStmt.setInt(1, returnedItemId);
                ResultSet rs = checkStmt.executeQuery();
                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null, "Returned item not found with ID: " + returnedItemId);
                    return false;
                }
                returnedItemName = rs.getString("itemName");
            }

            // Record exchange in item_exchanges table
            String exchangeSQL = "INSERT INTO item_exchanges (returned_item_id, returned_quantity, new_item_id, new_quantity, reason) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(exchangeSQL)) {
                stmt.setInt(1, returnedItemId);
                stmt.setInt(2, returnedQuantity);
                stmt.setInt(3, newItemId);
                stmt.setInt(4, newItemQuantity);
                stmt.setString(5, reason);
                stmt.executeUpdate();
            }

            // Add returned items back to inventory
            String returnUpdateSQL = "UPDATE items SET quantity = quantity + ? WHERE itemId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(returnUpdateSQL)) {
                stmt.setInt(1, returnedQuantity);
                stmt.setInt(2, returnedItemId);
                stmt.executeUpdate();
            }

            // Remove exchanged items from inventory
            String exchangeUpdateSQL = "UPDATE items SET quantity = quantity - ? WHERE itemId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(exchangeUpdateSQL)) {
                stmt.setInt(1, newItemQuantity);
                stmt.setInt(2, newItemId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error processing exchange: " + e.getMessage());
            return false;
        }
    }
    
    // Get return history for an item
    public static String getReturnHistory(int itemId) {
        StringBuilder history = new StringBuilder();
        String sql = "SELECT r.return_date, r.quantity_returned, r.reason, " +
                    "i.itemName " +
                    "FROM item_returns r " +
                    "JOIN items i ON r.item_id = i.itemId " +
                    "WHERE r.item_id = ? " +
                    "ORDER BY r.return_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();

            history.append("=== RETURN HISTORY ===\n");
            while (rs.next()) {
                history.append("Date: ").append(rs.getTimestamp("return_date"))
                      .append(" | Quantity: ").append(rs.getInt("quantity_returned"))
                      .append(" | Item: ").append(rs.getString("itemName"))
                      .append(" | Reason: ").append(rs.getString("reason"))
                      .append("\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history.toString();
    }

	
	public void processReturn()
	{
		
	}
}
