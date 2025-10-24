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
	    try (Connection conn = DatabaseConnection.getConnection())
	    {
	        String sql = "INSERT INTO items (itemId, itemName, category, size, colour, price, quantity, supplier_id) " +
	                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	        PreparedStatement stmt = conn.prepareStatement(sql);
	        stmt.setInt(1, item.getItemId());
	        stmt.setString(2, item.getName());
	        stmt.setString(3, item.getCategory());
	        stmt.setString(4, item.getSize());
	        stmt.setString(5, item.getColour());
	        stmt.setDouble(6, item.getPrice());
	        stmt.setInt(7, item.getQuantity());
	        
	        
	        if (item.getSupplier() != null)
	        {
	            stmt.setInt(8, item.getSupplier().getSupplierId());
	        }
	        else
	        {
	            stmt.setNull(8, java.sql.Types.INTEGER);
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
