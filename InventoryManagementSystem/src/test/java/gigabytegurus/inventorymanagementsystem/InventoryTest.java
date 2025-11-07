package gigabytegurus.inventorymanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

public class InventoryTest extends TestCase{
	
	private Inventory inventory;
	
	@Override
    protected void setUp() throws Exception
    {
        super.setUp();
        inventory = new Inventory();
        inventory.testMode = true;
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
	
	
	
	/* UPDATE ITEM TESTS */

    //Test #: 1
    //Obj: Test updating an item with valid data
    //Input(s): itemId = 1, name = "Valid Hoodie", category = "Hoodies", size = "L", colour = "Blue", price = 39.99, quantity = 10
    //Expected Output: Update completes without error
    public void testUpdateValidItem() throws Exception
    {
        try (Connection conn = DatabaseConnection.getConnection())
        {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE items SET itemName='Valid Hoodie', category='Hoodies', size='L', colour='Blue', price=39.99, quantity=10 WHERE itemId=1");
            stmt.executeUpdate();
        }

        try {
            inventory.updateItem(1);
            assertTrue("Valid update should not cause an error", true);
        } catch (Exception e) {
            fail("Unexpected exception for valid update: " + e.getMessage());
        }
    }
    
    
    //Test #: 2
    //Obj: Test updating an item with missing mandatory field (name)
    //Input(s): itemName = ""
    //Expected Output: IllegalArgumentException thrown
    public void testUpdateItemWithoutName() throws Exception
    {
        try (Connection conn = DatabaseConnection.getConnection())
        {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE items SET itemName='', category='Hoodies', size='L', colour='Blue', price=39.99, quantity=10 WHERE itemId=1");
            stmt.executeUpdate();
        }

        try {
            inventory.updateItem(1);
            fail("Expected IllegalArgumentException for empty item name");
        } catch (IllegalArgumentException e) {
            assertEquals("Item name cannot be empty.", e.getMessage());
        }
    }
    
    
    //Test #: 3
    //Obj: Test updating an item with invalid numeric data (negative price)
    //Input(s): price = -50.0
    //Expected Output: IllegalArgumentException thrown
    public void testUpdateItemWithNegativePrice() throws Exception
    {
        try (Connection conn = DatabaseConnection.getConnection())
        {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE items SET itemName='Jacket', category='Jackets', size='M', colour='Black', price=-50.0, quantity=5 WHERE itemId=1");
            stmt.executeUpdate();
        }

        try {
            inventory.updateItem(1);
            fail("Expected IllegalArgumentException for negative price");
        } catch (IllegalArgumentException e) {
            assertEquals("Price cannot be negative.", e.getMessage());
        }
    }

    //Test #: 4
    //Obj: Test updating an item with invalid numeric data (negative quantity)
    //Input(s): quantity = -3
    //Expected Output: IllegalArgumentException thrown
    public void testUpdateItemWithNegativeQuantity() throws Exception
    {
        try (Connection conn = DatabaseConnection.getConnection())
        {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE items SET itemName='Tee', category='T-Shirts', size='S', colour='Green', price=15.0, quantity=-3 WHERE itemId=1");
            stmt.executeUpdate();
        }

        try {
            inventory.updateItem(1);
            fail("Expected IllegalArgumentException for negative quantity");
        } catch (IllegalArgumentException e) {
            assertEquals("Quantity cannot be negative.", e.getMessage());
        }
    }

    //Test #: 5
    //Obj: Test updating an item with missing mandatory field (category)
    //Input(s): category = ""
    //Expected Output: IllegalArgumentException thrown
    public void testUpdateItemWithoutCategory() throws Exception
    {
        try (Connection conn = DatabaseConnection.getConnection())
        {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE items SET itemName='Shirt', category='', size='L', colour='White', price=19.99, quantity=12 WHERE itemId=1");
            stmt.executeUpdate();
        }

        try {
            inventory.updateItem(1);
            fail("Expected IllegalArgumentException for empty category");
        } catch (IllegalArgumentException e) {
            assertEquals("Category cannot be empty.", e.getMessage());
        }
    }

    //Test #: 6
    //Obj: Test updating an item with missing mandatory field (size)
    //Input(s): size = ""
    //Expected Output: IllegalArgumentException thrown
    public void testUpdateItemWithoutSize() throws Exception
    {
        try (Connection conn = DatabaseConnection.getConnection())
        {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE items SET itemName='Jacket', category='Jackets', size='', colour='Black', price=59.99, quantity=5 WHERE itemId=1");
            stmt.executeUpdate();
        }

        try {
            inventory.updateItem(1);
            fail("Expected IllegalArgumentException for empty size");
        } catch (IllegalArgumentException e) {
            assertEquals("Size cannot be empty.", e.getMessage());
        }
    }

    //Test #: 7
    //Obj: Test updating an item with missing mandatory field (colour)
    //Input(s): colour = ""
    //Expected Output: IllegalArgumentException thrown
    public void testUpdateItemWithoutColour() throws Exception
    {
        try (Connection conn = DatabaseConnection.getConnection())
        {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE items SET itemName='Coat', category='Coats', size='M', colour='', price=79.99, quantity=7 WHERE itemId=1");
            stmt.executeUpdate();
        }

        try {
            inventory.updateItem(1);
            fail("Expected IllegalArgumentException for empty colour");
        } catch (IllegalArgumentException e) {
            assertEquals("Colour cannot be empty.", e.getMessage());
        }
    }

    //Test #: 8
    //Obj: Test updating a non-existent item
    //Input(s): itemId = 99999
    //Expected Output: No crash, updateItem returns safely
    public void testUpdateNonExistentItem()
    {
        try {
            inventory.updateItem(99999);
            assertTrue("Non-existent item update should not throw exception", true);
        } catch (Exception e) {
            fail("Should not throw exception for non-existent item");
        }
    }

	
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    // KEEP THIS METHOD AT THE BOTTOM OF THE FILE AT ALL TIMES TO RESET THE DATABASE EVERY RUN SO YOU DONT HAVE TO RUN A NEW SQL FILE EVERYTIME
    @Override
    protected void tearDown() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Statement stmt = conn.createStatement();

            // 1️ Remove everything
            stmt.executeUpdate("DELETE FROM items");

            // 2️ Restart database to orignal
            stmt.executeUpdate(
                "INSERT INTO items (itemId, itemName, category, size, colour, price, quantity, supplier_id) VALUES " +
                "(1, 'Jacket', 'Clothing', 'M', 'Black', 59.99, 5, 1)," +
                "(2, 'Jeans', 'Clothing', 'L', 'Blue', 49.99, 30, 1)," +
                "(3, 'Sneakers', 'Footwear', '42', 'White', 89.99, 20, 2)," +
                "(4, 'Jacket', 'Outerwear', 'XL', 'Black', 99.50, 15, 2)"
            );

            stmt.close();
        }
        super.tearDown();
    }

	    
		   
}
	  
	  


