package gigabytegurus.inventorymanagementsystem;

import java.util.List;

public class Inventory
{
	private List<Item> items;
	
	public void addItem(Item item)
	{
		
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
