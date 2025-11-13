package gigabytegurus.inventorymanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

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

	
	public void processReturn()
	{
		
	}
}
