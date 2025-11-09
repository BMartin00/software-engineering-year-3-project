package gigabytegurus.inventorymanagementsystem;

import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Inventory
{
	private List<Item> items;
	
	public Inventory() {
        this.items = new ArrayList<>();
    }
	
	public boolean testMode = false;
	
	
	
	
	public void addItem(Item item)
	{
		
		
		//VALIDATION CHECKS FOR JUNIT TESTS ON ADD
		if (item == null) {
	        throw new IllegalArgumentException("Item cannot be null.");
	    }
	    if (item.getName() == null || item.getName().trim().isEmpty()) {
	        throw new IllegalArgumentException("Item name cannot be empty.");
	    }
	    if (item.getPrice() < 0) {
	        throw new IllegalArgumentException("Price cannot be negative.");
	    }
	    if (item.getQuantity() < 0) {
	        throw new IllegalArgumentException("Quantity cannot be negative.");
	    }
	    if (item.getCategory() == null || item.getCategory().trim().isEmpty()) {
	        throw new IllegalArgumentException("Category cannot be empty.");
	    }
	    if (item.getColour() == null || item.getColour().trim().isEmpty()) {
	        throw new IllegalArgumentException("Colour cannot be empty.");
	    }
	    if (item.getSize() == null || item.getSize().trim().isEmpty()) {
	        throw new IllegalArgumentException("Size cannot be empty.");
	    }

	    if (items != null) {
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
	            if (!testMode) JOptionPane.showMessageDialog(null, "⚠No item found with ID: " + itemId);
	            selectStmt.close();
	            return;
	        }

	        // Variables used in both GUI mode and test mode
	        String name, category, size, colour;
	        double price;
	        int quantity;

	        // Fill the text fields with the current values so the user can see and edit them
	        // If NOT test mode  show the input GUI normally
	        if (!testMode) {

	            JTextField nameField = new JTextField(rs.getString("itemName"), 15);
	            JTextField categoryField = new JTextField(rs.getString("category"), 15);
	            JTextField sizeField = new JTextField(rs.getString("size"), 10);
	            JTextField colourField = new JTextField(rs.getString("colour"), 10);
	            JTextField priceField = new JTextField(String.valueOf(rs.getDouble("price")), 10);
	            JTextField quantityField = new JTextField(String.valueOf(rs.getInt("quantity")), 10);

	            // Build small form with all of the input fields
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
	                if (!testMode) JOptionPane.showMessageDialog(null, "Edit cancelled. No changes were made.");
	                rs.close();
	                selectStmt.close();
	                return;
	            }

	            // Read the values that the user entered and remove spaces
	            name = nameField.getText().trim();
	            category = categoryField.getText().trim();
	            size = sizeField.getText().trim();
	            colour = colourField.getText().trim();

	            // Try converting price and quantity to numbers; stop if it fails
	            try {
	                price = Double.parseDouble(priceField.getText().trim());
	                quantity = Integer.parseInt(quantityField.getText().trim());
	            } catch (NumberFormatException ex) {
	                if (!testMode) JOptionPane.showMessageDialog(null, "Invalid number format for price or quantity.");
	                rs.close();
	                selectStmt.close();
	                return;
	            }

	        }
	        else {
	        	
	            // ✅ TEST MODE (Jenkins/JUnit): use existing DB values (no GUI popups)
	            name = rs.getString("itemName").trim();
	            category = rs.getString("category").trim();
	            size = rs.getString("size").trim();
	            colour = rs.getString("colour").trim();
	            price = rs.getDouble("price");
	            quantity = rs.getInt("quantity");
	        }

	        // VALIDATION CHECKS 
	        if (name.isEmpty()) throw new IllegalArgumentException("Item name cannot be empty.");
	        if (category.isEmpty()) throw new IllegalArgumentException("Category cannot be empty.");
	        if (size.isEmpty()) throw new IllegalArgumentException("Size cannot be empty.");
	        if (colour.isEmpty()) throw new IllegalArgumentException("Colour cannot be empty.");
	        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
	        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative.");

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
	        if (!testMode) {
	            if (rows > 0) {
	                JOptionPane.showMessageDialog(null, "Item (ID: " + itemId + ") updated successfully!");
	            } else {
	                JOptionPane.showMessageDialog(null, "No changes were made (item may not exist).");
	            }
	        }

	        rs.close();
	        selectStmt.close();
	    }
	    catch (SQLException e)
	    {
	        // Handle any database errors
	        e.printStackTrace();
	        if (!testMode) JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
	    }
	}

	public boolean removeItem(int itemId) {
	    try (Connection conn = DatabaseConnection.getConnection()) {
	        // Delete the item from database
	        String deleteSQL = "DELETE FROM items WHERE itemId = ?";
	        PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);
	        deleteStmt.setInt(1, itemId);

	        int rowsAffected = deleteStmt.executeUpdate();
	        deleteStmt.close();

	        if (rowsAffected > 0) {
	            if (items != null) {
	                items.removeIf(item -> item.getItemId() == itemId);
	            }
	            return true;
	        } else {
	            return false; 
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false; 
	    }
	}
	
	public List<Item> searchItem(String keyword) {
	    List<Item> searchResults = new ArrayList<>();
	    
	    // Enhanced input validation
	    if (keyword == null || keyword.trim().isEmpty()) {
	        if (!testMode) {
	            JOptionPane.showMessageDialog(null, "Please enter a keyword to search.");
	        }
	        return searchResults;
	    }
	    
	    String searchTerm = "%" + keyword.trim() + "%";
	    
	    // More precise SQL query with individual field matching for better performance
	    String sql = """
	        SELECT 
	            i.itemId, i.itemName, i.category, i.size, i.colour, 
	            i.price, i.quantity, i.supplier_id,
	            s.name AS supplierName, s.contact AS supplierContact
	        FROM items i
	        LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id
	        WHERE i.itemName LIKE ? 
	           OR i.category LIKE ? 
	           OR i.size LIKE ? 
	           OR i.colour LIKE ? 
	           OR s.name LIKE ?
	        ORDER BY i.itemName, i.category
	    """;

	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        for (int i = 1; i <= 5; i++) {
	            stmt.setString(i, searchTerm);
	        }
	        
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Supplier supplier = null;
	                int supplierId = rs.getInt("supplier_id");
	                
	                if (!rs.wasNull()) {
	                    supplier = new Supplier(
	                        supplierId,
	                        rs.getString("supplierName"),
	                        rs.getString("supplierContact")
	                    );
	                }

	                Item item = new Item(
	                    rs.getInt("itemId"),
	                    rs.getString("itemName"),
	                    rs.getString("category"),
	                    rs.getString("size"),
	                    rs.getString("colour"),
	                    rs.getDouble("price"),
	                    rs.getInt("quantity"),
	                    supplier
	                );

	                searchResults.add(item);
	            }
	        }
	        
	        if (searchResults.isEmpty() && !testMode) {
	            JOptionPane.showMessageDialog(
	                null, 
	                "No matching items found for: \"" + keyword.trim() + "\"\n\n" +
	                "Try searching by:\n" +
	                "• Item name\n" +
	                "• Category\n" + 
	                "• Size\n" +
	                "• Colour\n" +
	                "• Supplier name",
	                "No Results Found",
	                JOptionPane.INFORMATION_MESSAGE
	            );
	        } else if (!testMode && searchResults.size() > 0) {
	           
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        if (!testMode) {
	            JOptionPane.showMessageDialog(
	                null, 
	                "Database error while searching: " + e.getMessage(),
	                "Search Error",
	                JOptionPane.ERROR_MESSAGE
	            );
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        if (!testMode) {
	            JOptionPane.showMessageDialog(
	                null, 
	                "Unexpected error during search: " + e.getMessage(),
	                "Error",
	                JOptionPane.ERROR_MESSAGE
	            );
	        }
	    }

	    return searchResults;
	}
	
	
	public List<Item> filterItems(String category, String size, String colourOrSupplier) {
	    List<Item> filtered = new ArrayList<>();

	    for (Item item : items) {
	        boolean matchesCategory = category.equals("All Categories") || 
	                                  (item.getCategory() != null && item.getCategory().equals(category));

	        boolean matchesSize = size.equals("All Sizes") || 
	                              (item.getSize() != null && item.getSize().equals(size));

	        boolean matchesColourOrSupplier = colourOrSupplier.equals("All Colours") || 
	                                          (item.getColour() != null && item.getColour().equals(colourOrSupplier)) ||
	                                          (item.getSupplier() != null && item.getSupplier().getName().equals(colourOrSupplier));

	        if (matchesCategory && matchesSize && matchesColourOrSupplier) {
	            filtered.add(item);
	        }
	    }
	    return filtered;
	}



    public List<Item> getLowStockItems() {
        List<Item> lowStock = new ArrayList<>();
        if (items == null) return lowStock;

        for (Item item : items) {
            if (item != null && item.getQuantity() < 20) {
                lowStock.add(item);
            }
        }
        return lowStock;
    }

	public Report generateReport(String format)
	{
		return null;
	}
}
	
	


    
    
	





