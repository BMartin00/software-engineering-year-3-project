package gigabytegurus.inventorymanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.TestCase;

public class InventoryTest extends TestCase{
	
	private Inventory inventory;
	
	@Override
    protected void setUp() throws Exception
    {
        super.setUp();
        inventory = new Inventory();
    }
	
	
	/* ADD ITEM TESTS */
	
	
	//Test #: 1
	//Obj: Test adding a completely valid item
	//Input(s): name = "Valid Hoodie", category = "Hoodies", size = "L", colour = "Blue", price = 39.99, quantity = 20
	//Expected Output: Item added successfully without errors
	public void testAddValidItem()
	{
	    Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	Item validItem = new Item(0, "Valid Hoodie", "Hoodies", "L", "Blue", 39.99, 20, supplier);
	
	try {
	    inventory.addItem(validItem);
	    // If no exception is thrown, test passes
	assertTrue("Valid item added successfully", true);
	} catch (Exception e) {
	    fail("Unexpected error when adding valid item: " + e.getMessage());
	    }
	}
	
	//Test #: 2
	//Obj: Test adding an item with missing non-essential info (no supplier)
	//Input(s): supplier = null
	//Expected Output: Item added successfully without supplier
	public void testAddItemWithoutSupplier()
	{
	    Item noSupplier = new Item(0, "No Supplier Jacket", "Jackets", "M", "Black", 49.99, 10, null);
	
	try {
	    inventory.addItem(noSupplier);
	    // Should add successfully even without a supplier
	assertTrue("Item without supplier added successfully", true);
	} catch (Exception e) {
	    fail("Unexpected error when adding item without supplier: " + e.getMessage());
	    }
	}
	
	
	  //Test #: 3
	  //Obj: Test adding an item with missing mandatory field (no name)
	  //Input(s): name = "", category = "T-Shirts", size = "M", colour = "White", price = 19.99, quantity = 15
	  //Expected Output: IllegalArgumentException thrown, item not added
	  public void testAddItemWithoutName()
	  {
	      Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	  Item invalidItem = new Item(0, "", "T-Shirts", "M", "White", 19.99, 15, supplier);
	
	  try {
	      inventory.addItem(invalidItem);
	      fail("Expected IllegalArgumentException for empty name");
	  } catch (IllegalArgumentException e) {
	      // Expected result — test passes
	  assertEquals("Item name cannot be empty.", e.getMessage());
	  } catch (Exception e) {
	      fail("Unexpected exception type: " + e);
	      }
	  }
	  
	//Test #: 4
	//Obj: Add item with invalid numeric data (negative price)
	//Input(s): price = -50.0
	//Expected Output: IllegalArgumentException thrown
	public void testAddItemWithNegativePrice()
	{
	    Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	Item item = new Item(0, "Negative Price Jacket", "Jackets", "L", "Red", -50.0, 5, supplier);
	
	try {
	    inventory.addItem(item);
	    fail("Expected IllegalArgumentException for negative price");
	} catch (IllegalArgumentException e) {
	    assertEquals("Price cannot be negative.", e.getMessage());
	} catch (Exception e) {
	    fail("Unexpected exception type: " + e);
	    }
	}
	
	//Test #: 5
	//Obj: Add item with invalid numeric data (negative quantity)
	//Input(s): quantity = -10
	//Expected Output: IllegalArgumentException thrown
	public void testAddItemWithNegativeQuantity()
	{
	    Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	Item item = new Item(0, "Negative Quantity Tee", "T-Shirts", "S", "Green", 15.0, -10, supplier);
	
	try {
	    inventory.addItem(item);
	    fail("Expected IllegalArgumentException for negative quantity");
	} catch (IllegalArgumentException e) {
	    assertEquals("Quantity cannot be negative.", e.getMessage());
	} catch (Exception e) {
	    fail("Unexpected exception type: " + e);
	        }
	    }
	    
	  //Test #: 6
	  //Obj: Test adding an item with missing mandatory field (empty category)
	  //Input(s): category = ""
	  //Expected Output: IllegalArgumentException thrown, item not added
	  public void testAddItemWithoutCategory()
	  {
	      Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	  Item invalidItem = new Item(0, "No Category Shirt", "", "M", "Blue", 19.99, 10, supplier);
	
	  try {
	      inventory.addItem(invalidItem);
	      fail("Expected IllegalArgumentException for empty category");
	  } catch (IllegalArgumentException e) {
	      assertEquals("Category cannot be empty.", e.getMessage());
	  } catch (Exception e) {
	      fail("Unexpected exception type: " + e);
	      }
	  }
	  
	//Test #: 7
	//Obj: Test adding an item with missing mandatory field (empty colour)
	//Input(s): colour = ""
	//Expected Output: IllegalArgumentException thrown, item not added
	public void testAddItemWithoutColour()
	{
	    Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	Item invalidItem = new Item(0, "No Colour Shirt", "T-Shirts", "M", "", 24.99, 12, supplier);
	
	try {
	    inventory.addItem(invalidItem);
	    fail("Expected IllegalArgumentException for empty colour");
	} catch (IllegalArgumentException e) {
	    assertEquals("Colour cannot be empty.", e.getMessage());
	} catch (Exception e) {
	    fail("Unexpected exception type: " + e);
	    }
	}
	
	//Test #: 8
	//Obj: Test adding an item with missing mandatory field (empty size)
	//Input(s): size = ""
	//Expected Output: IllegalArgumentException thrown, item not added
	public void testAddItemWithoutSize()
	{
	    Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	Item invalidItem = new Item(0, "No Size Jacket", "Jackets", "", "Black", 59.99, 5, supplier);
	
	try {
	    inventory.addItem(invalidItem);
	    fail("Expected IllegalArgumentException for empty size");
	} catch (IllegalArgumentException e) {
	    assertEquals("Size cannot be empty.", e.getMessage());
	} catch (Exception e) {
	    fail("Unexpected exception type: " + e);
	    }
	}
	    
	//Test #: 9
	//Obj: Test adding an item with boundary numeric values (zero price and zero quantity)
	//Input(s): price = 0.0, quantity = 0
	//Expected Output: Item added successfully (zero values should be accepted)
	public void testAddItemWithZeroPriceOrQuantity() {
	    Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	Item freeItem = new Item(0, "Free Cap", "Accessories", "M", "White", 0.0, 0, supplier);
	
	try {
	    inventory.addItem(freeItem);
	    assertTrue("Item with zero price and quantity added successfully", true);
	} catch (Exception e) {
	    fail("Unexpected error for zero values: " + e.getMessage());
        }
    }
	    
		   
}
	  
	  


