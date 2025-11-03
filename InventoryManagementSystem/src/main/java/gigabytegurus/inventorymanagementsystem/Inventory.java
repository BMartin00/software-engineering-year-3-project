package gigabytegurus.inventorymanagementsystem;

import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Inventory
{
	private List<Item> items;
	
	
	
	
	public void addItem(Item item)
	{
		// Add to in-memory list
	    if (items != null)
	    {
	        items.add(item);
	    }

	    // Save to Database
	    // This permanently stores the new item in the "items" table.
	    try (Connection conn = DatabaseConnection.getConnection())
	    {
	        
	    	// SQL query to insert a new item into the database
	        // The "itemId" column is omitted because it’s auto-generated.
	        String sql = "INSERT INTO items (itemName, category, size, colour, price, quantity, supplier_id) " +
	                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
	        
	        // Create a prepared statement to safely insert data (prevents SQL injection)
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        
	        stmt.setString(1, item.getName());
	        stmt.setString(2, item.getCategory());
	        stmt.setString(3, item.getSize());
	        stmt.setString(4, item.getColour());
	        stmt.setDouble(5, item.getPrice());
	        stmt.setInt(6, item.getQuantity());

	        
	        // If the item has an associated supplier, save its ID
	        // Otherwise, insert NULL for the supplier_id column
	        if (item.getSupplier() != null)
	        {
	            stmt.setInt(7, item.getSupplier().getSupplierId());
	        }
	        else
	        {
	            stmt.setNull(7, java.sql.Types.INTEGER);
	        }

	        stmt.executeUpdate();
	        stmt.close();

	        System.out.println("Item added successfully to database.");
	    }
	    catch (SQLException e)
	    {
	        e.printStackTrace();
	        System.err.println("Error adding item: " + e.getMessage());
	    }
	}
	
	
	
	
	
	public void updateItem(int itemId)
	{
	    try (Connection conn = DatabaseConnection.getConnection())
	    {
	        // Get the existing item data from the database so we can show it to the user
	        String selectSQL = "SELECT * FROM items WHERE itemId = ?";
	        PreparedStatement selectStmt = conn.prepareStatement(selectSQL);
	        selectStmt.setInt(1, itemId);
	        ResultSet rs = selectStmt.executeQuery();

	        // If no record matches the given ID, tell the user and stop
	        if (!rs.next()) {
	            JOptionPane.showMessageDialog(null, "⚠️ No item found with ID: " + itemId);
	            selectStmt.close();
	            return;
	        }

	        // Fill the text fields with the current values so the user can see and edit them
	        JTextField nameField = new JTextField(rs.getString("itemName"), 15);
	        JTextField categoryField = new JTextField(rs.getString("category"), 15);
	        JTextField sizeField = new JTextField(rs.getString("size"), 10);
	        JTextField colourField = new JTextField(rs.getString("colour"), 10);
	        JTextField priceField = new JTextField(String.valueOf(rs.getDouble("price")), 10);
	        JTextField quantityField = new JTextField(String.valueOf(rs.getInt("quantity")), 10);

	        // Build a small form with all of the input fields
	        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
	        panel.add(new JLabel("Item Name:"));
	        panel.add(nameField);
	        panel.add(new JLabel("Category:"));
	        panel.add(categoryField);
	        panel.add(new JLabel("Size:"));
	        panel.add(sizeField);
	        panel.add(new JLabel("Colour:"));
	        panel.add(colourField);
	        panel.add(new JLabel("Price (€):"));
	        panel.add(priceField);
	        panel.add(new JLabel("Quantity:"));
	        panel.add(quantityField);

	        // Show the form to the user
	        int result = JOptionPane.showConfirmDialog(
	                null, panel,
	                "Edit Item (ID: " + itemId + ")", JOptionPane.OK_CANCEL_OPTION,
	                JOptionPane.PLAIN_MESSAGE);

	        // If the user cancels or closes the dialog, stop here
	        if (result != JOptionPane.OK_OPTION) {
	            JOptionPane.showMessageDialog(null, "Edit cancelled. No changes were made.");
	            rs.close();
	            selectStmt.close();
	            return;
	        }

	        // Read the values that the user entered and remove spaces
	        String name = nameField.getText().trim();
	        String category = categoryField.getText().trim();
	        String size = sizeField.getText().trim();
	        String colour = colourField.getText().trim();
	        double price;
	        int quantity;

	        // Try converting price and quantity to numbers; stop if it fails
	        try {
	            price = Double.parseDouble(priceField.getText().trim());
	            quantity = Integer.parseInt(quantityField.getText().trim());
	        } catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(null, "Invalid number format for price or quantity.");
	            rs.close();
	            selectStmt.close();
	            return;
	        }

	        // Update the item in the database with the new values
	        String updateSQL = "UPDATE items SET itemName=?, category=?, size=?, colour=?, price=?, quantity=? WHERE itemId=?";
	        PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
	        updateStmt.setString(1, name);
	        updateStmt.setString(2, category);
	        updateStmt.setString(3, size);
	        updateStmt.setString(4, colour);
	        updateStmt.setDouble(5, price);
	        updateStmt.setInt(6, quantity);
	        updateStmt.setInt(7, itemId);

	        int rows = updateStmt.executeUpdate();
	        updateStmt.close();

	        // Let the user know whether the update was successful
	        if (rows > 0) {
	            JOptionPane.showMessageDialog(null, "Item (ID: " + itemId + ") updated successfully!");
	        } else {
	            JOptionPane.showMessageDialog(null, "No changes were made (item may not exist).");
	        }

	        rs.close();
	        selectStmt.close();
	    }
	    catch (SQLException e)
	    {
	        // Handle any database errors
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
	    }
	}




	
	
	
	
	public void removeItem(int itemId)
	{
		
	}
	
	
	
	
	
	public List<Item> searchItem(String keyword)
	{
		return items;
	}
	
	public List<Item> filterItems(String category, String size, String colour)
	{
		return items;
	}
	
	public Report generateReport(String format)
	{
		return null;
	}
	
	public List<Item> getLowStockItems()
	{
		return items;
	}
}
