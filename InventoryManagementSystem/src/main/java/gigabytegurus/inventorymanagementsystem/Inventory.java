package gigabytegurus.inventorymanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

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

	        System.out.println("✅ Item added successfully to database.");
	    }
	    catch (SQLException e)
	    {
	        e.printStackTrace();
	        System.err.println("❌ Error adding item: " + e.getMessage());
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
