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
	  

}
