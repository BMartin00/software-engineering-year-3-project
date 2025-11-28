// A00316625 Nagasai Chintalapati
// A00318851 Martin  Banyanszki
// A00320456 Justas Zabitis
// A00317072 Hamza Hussain

package gigabytegurus.inventorymanagementsystem;

public class Item
{
    private int itemId;
    private String name;
    private String category;
    private String size;
    private String colour;
    private double price;
    private int quantity;
    private Supplier supplier;

    public Item(int itemId, String name, String category, String size, String colour,
                double price, int quantity, Supplier supplier)
    {
        this.itemId = itemId;
        this.name = name;
        this.category = category;
        this.size = size;
        this.colour = colour;
        this.price = price;
        this.quantity = quantity;
        this.supplier = supplier;
    }

    public void updateStock(int newQuantity)
    {
    	this.quantity = newQuantity;
    }
    
    public void updateDetails(String name, String category, String size, String colour, double price)
    {
        this.name = name;
        this.category = category;
        this.size = size;
        this.colour = colour;
        this.price = price;
    }

    public void reduceStock(int amount)
    {
    	this.quantity -= amount;
    }
    public void increaseStock(int amount)
    {
    	this.quantity += amount;
    }

    // Getters
    public int getItemId()
    {
    	return itemId;
    }
    
    public String getName()
    {
    	return name;
    }
    
    public String getCategory()
	{
		return category;
	}

    public String getSize()
	{
		return size;
	}

    public String getColour()
	{
		return colour;
	}

    public double getPrice()
	{
		return price;
	}
    
    public int getQuantity()
	{
		return quantity;
	}

    public Supplier getSupplier()
	{
		return supplier;
	}
}
