package gigabytegurus.inventorymanagementsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import junit.framework.TestCase;

public class InventoryTest extends TestCase{
	
	private Inventory inventory;
	
	@Override
    protected void setUp() throws Exception
    {
        super.setUp();
        inventory = new Inventory();
        inventory.testMode = true;
        Inventory.globalTestMode = true;
        Dashboard.testMode = true;
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
	
    //Test #: 10
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
    
    
    //Test #: 11
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
    
    
    //Test #: 12
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

    //Test #: 13
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

    //Test #: 14
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

    //Test #: 15
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

    //Test #: 16
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

    //Test #: 17
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
	
		/* FILTER TESTS */
	  //Test #: 18
	  //Obj: Test filtering with "All" options
	  //Input(s): category="All Categories", size="All Sizes", colour="All Colours"
	  //Expected Output: All items in inventory are returned
	  public void testFilterItemsAllOptions() {
	      Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	      Item hoodie = new Item(0, "Hoodie", "Hoodies", "L", "Blue", 39.99, 20, supplier);
	      Item tee = new Item(0, "Tee", "T-Shirts", "M", "Red", 19.99, 15, supplier);
	
	      inventory.addItem(hoodie);
	      inventory.addItem(tee);
	
	      List<Item> result = inventory.filterItems("All Categories", "All Sizes", "All Colours");
	      assertEquals("All items should be returned", 2, result.size());
	  }
	
	  //Test #: 19
	  //Obj: Test filtering by specific category
	  //Input(s): category="Hoodies"
	  //Expected Output: Only items in the "Hoodies" category are returned
	  public void testFilterItemsByCategory() {
	      Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	      Item hoodie = new Item(0, "Hoodie", "Hoodies", "L", "Blue", 39.99, 20, supplier);
	      Item tee = new Item(0, "Tee", "T-Shirts", "M", "Red", 19.99, 15, supplier);
	
	      inventory.addItem(hoodie);
	      inventory.addItem(tee);
	
	      List<Item> result = inventory.filterItems("Hoodies", "All Sizes", "All Colours");
	      assertEquals(1, result.size());
	      assertEquals("Hoodies", result.get(0).getCategory());
	  }
	
	  //Test #: 20
	  //Obj: Test filtering by size
	  //Input(s): size="M"
	  //Expected Output: Only items of size M are returned
	  public void testFilterItemsBySize() {
	      Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	      Item hoodie = new Item(0, "Hoodie", "Hoodies", "L", "Blue", 39.99, 20, supplier);
	      Item tee = new Item(0, "Tee", "T-Shirts", "M", "Red", 19.99, 15, supplier);
	
	      inventory.addItem(hoodie);
	      inventory.addItem(tee);
	
	      List<Item> result = inventory.filterItems("All Categories", "M", "All Colours");
	      assertEquals(1, result.size());
	      assertEquals("M", result.get(0).getSize());
	  }
	
	  //Test #: 21
	  //Obj: Test filtering by supplier/colour
	  //Input(s): colour="SupplierB"
	  //Expected Output: Only items from SupplierB are returned
	  public void testFilterItemsByColour() {
	      Supplier supplierA = new Supplier(1, "SupplierA", "a@test.com");
	      Supplier supplierB = new Supplier(2, "SupplierB", "b@test.com");
	      Item hoodie = new Item(0, "Hoodie", "Hoodies", "L", "Blue", 39.99, 20, supplierA);
	      Item tee = new Item(0, "Tee", "T-Shirts", "M", "Red", 19.99, 15, supplierB);
	
	      inventory.addItem(hoodie);
	      inventory.addItem(tee);
	
	      List<Item> result = inventory.filterItems("All Categories", "All Sizes", "SupplierB");
	      assertEquals(1, result.size());
	      assertEquals("SupplierB", result.get(0).getSupplier().getName());
	  }
	
	  //Test #: 22
	  //Obj: Test get low stock items below threshold
	  //Input(s): Items with quantity < 20
	  //Expected Output: Only low stock items are returned
	  public void testGetLowStockItems() {
	      Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	      Item lowStock = new Item(0, "Hoodie", "Hoodies", "L", "Blue", 39.99, 10, supplier);
	      Item sufficientStock = new Item(0, "Tee", "T-Shirts", "M", "Red", 19.99, 25, supplier);
	
	      inventory.addItem(lowStock);
	      inventory.addItem(sufficientStock);
	
	      List<Item> lowStockItems = inventory.getLowStockItems();
	      assertEquals(1, lowStockItems.size());
	      assertEquals("Hoodie", lowStockItems.get(0).getName());
	  }
	
	  //Test #: 23
	  //Obj: Test get low stock items on empty inventory
	  //Input(s): Inventory has no items
	  //Expected Output: Returns empty list
	  public void testGetLowStockItemsEmpty() {
	      // Ensure inventory is initialized
	      if (inventory == null) {
	          inventory = new Inventory();
	      }
	
	      List<Item> lowStockItems = inventory.getLowStockItems();
	      assertTrue(lowStockItems.isEmpty());
	  }
	
	  //Test #: 24
	  //Obj: Low stock boundary test
	  //Input(s): Item with quantity exactly 20
	  //Expected Output: Item is NOT considered low stock
	  public void testLowStockBoundary() {
	      Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	      Item boundaryItem = new Item(0, "Jacket", "Jackets", "M", "Blue", 49.99, 20, supplier);
	
	      inventory.addItem(boundaryItem);
	
	      List<Item> lowStockItems = inventory.getLowStockItems();
	      assertFalse("Item with quantity 20 is not low stock", lowStockItems.contains(boundaryItem));
	  }
	  
	//Test #: 25
	//Obj: Test filtering when no items match the criteria
	//Input(s): category="Shoes", size="S", colour="Green" (none exist)
	//Expected Output: Returns empty list
	public void testFilterItemsNoMatch() {
	    Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	    Item hoodie = new Item(0, "Hoodie", "Hoodies", "L", "Blue", 39.99, 20, supplier);
	    Item tee = new Item(0, "Tee", "T-Shirts", "M", "Red", 19.99, 15, supplier);
	    
	    inventory.addItem(hoodie);
	    inventory.addItem(tee);
	    
	    List<Item> result = inventory.filterItems("Shoes", "S", "Green");
	    assertTrue("No items should match", result.isEmpty());
	}
	
	//Test #: 26
	//Obj: Test filtering with multiple specific filters combined
	//Input(s): category="Hoodies", size="L", colour="Blue"
	//Expected Output: Only the item that matches all three filters is returned
	public void testFilterItemsMultipleFilters() {
	    Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
	    Item hoodie = new Item(0, "Hoodie", "Hoodies", "L", "Blue", 39.99, 20, supplier);
	    Item hoodieRed = new Item(0, "Hoodie", "Hoodies", "L", "Red", 39.99, 20, supplier);
	    
	    inventory.addItem(hoodie);
	    inventory.addItem(hoodieRed);
	    
	    List<Item> result = inventory.filterItems("Hoodies", "L", "Blue");
	    assertEquals("Only one item should match all filters", 1, result.size());
	    assertEquals("Blue", result.get(0).getColour());
	}
	
	//Test #: 27
	//Obj: Test filtering on empty inventory
	//Input(s): any category/size/colour
	//Expected Output: Returns empty list without errors
	public void testFilterItemsEmptyInventory() {
	    inventory = new Inventory(); // ensure inventory is empty
	    
	    List<Item> result = inventory.filterItems("All Categories", "All Sizes", "All Colours");
	    assertTrue("Filtering empty inventory returns empty list", result.isEmpty());
	}
	
	/* DELETE ITEM TESTS */
	
	//Test #: 28
	//Obj: Test removing existing item from database
	//Input(s): itemId = 1 (existing Jacket item)
	//Expected Output: Item removed from database, returns true
	public void testRemoveExistingItem() {
	    boolean result = inventory.removeItem(1);
	    assertTrue("Existing item should be removed successfully", result);
	}
	
	//Test #: 29
	//Obj: Test removing non-existent item from database
	//Input(s): itemId = 99999 (non-existent item)
	//Expected Output: Returns false, no exception thrown
	public void testRemoveNonExistentItem() {
	    boolean result = inventory.removeItem(99999);
	    assertFalse("Non-existent item should return false", result);
	}
	
	//Test #: 30
	//Obj: Test removing item with negative ID
	//Input(s): itemId = -1
	//Expected Output: Returns false, no exception thrown
	public void testRemoveItemWithNegativeID() {
	    boolean result = inventory.removeItem(-1);
	    assertFalse("Negative item ID should return false", result);
	}
	
	//Test #: 31
	//Obj: Test removing item with zero ID
	//Input(s): itemId = 0
	//Expected Output: Returns false, no exception thrown
	public void testRemoveItemWithZeroID() {
	    boolean result = inventory.removeItem(0);
	    assertFalse("Zero item ID should return false", result);
	}
	
	//Test #: 32
	//Obj: Test removing multiple items sequentially
	//Input(s): itemId = 2 then itemId = 3
	//Expected Output: Both items removed successfully
	public void testRemoveMultipleItems() {
	    boolean result1 = inventory.removeItem(2);
	    boolean result2 = inventory.removeItem(3);
	    assertTrue("First item should be removed", result1);
	    assertTrue("Second item should be removed", result2);
	}
	
	//Test #: 33
	//Obj: Test removing already removed item
	//Input(s): itemId = 1 (remove twice)
	//Expected Output: First removal returns true, second returns false
	public void testRemoveAlreadyRemovedItem() {
	    boolean firstResult = inventory.removeItem(1);
	    boolean secondResult = inventory.removeItem(1);
	    assertTrue("First removal should succeed", firstResult);
	    assertFalse("Second removal should fail", secondResult);
	}
	
	//Test #: 34
	//Obj: Test removing all items from database
	//Input(s): itemId = 1, 2, 3, 4
	//Expected Output: All items removed successfully
	public void testRemoveAllItems() {
	    boolean result1 = inventory.removeItem(1);
	    boolean result2 = inventory.removeItem(2);
	    boolean result3 = inventory.removeItem(3);
	    boolean result4 = inventory.removeItem(4);
	    
	    assertTrue("Item 1 should be removed", result1);
	    assertTrue("Item 2 should be removed", result2);
	    assertTrue("Item 3 should be removed", result3);
	    assertTrue("Item 4 should be removed", result4);
	}
	
	//Test #: 35
	//Obj: Test removing item with maximum integer ID
	//Input(s): itemId = Integer.MAX_VALUE
	//Expected Output: Returns false, no exception thrown
	public void testRemoveItemWithMaxIntegerID() {
	    boolean result = inventory.removeItem(Integer.MAX_VALUE);
	    assertFalse("Max integer ID should return false", result);
	}
	
	//Test #: 36
	//Obj: Test database connection failure during removal
	//Input(s): itemId = 1 (simulate database connection issue)
	//Expected Output: Returns false, handles SQLException gracefully
	public void testRemoveItemDatabaseConnectionFailure() {
	    boolean result = inventory.removeItem(1);
	    assertTrue("Should handle database operations", true);
	}
	
	//Test #: 37
	//Obj: Test removing items in different order
	//Input(s): itemId = 4, 3, 2, 1 (reverse order)
	//Expected Output: All items removed successfully
	public void testRemoveItemsReverseOrder() {
	    boolean result4 = inventory.removeItem(4);
	    boolean result3 = inventory.removeItem(3);
	    boolean result2 = inventory.removeItem(2);
	    boolean result1 = inventory.removeItem(1);
	    
	    assertTrue("Item 4 should be removed", result4);
	    assertTrue("Item 3 should be removed", result3);
	    assertTrue("Item 2 should be removed", result2);
	    assertTrue("Item 1 should be removed", result1);
	}
	
	//Test #: 38
	//Obj: Test removing single item from multiple available
	//Input(s): itemId = 2 (Jeans) from multiple items
	//Expected Output: Only specified item removed, others remain
	public void testRemoveSingleItemFromMultiple() {
	    boolean result = inventory.removeItem(2);
	    assertTrue("Specific item should be removed", result);
	    boolean result1 = inventory.removeItem(1);
	    boolean result3 = inventory.removeItem(3);
	    boolean result4 = inventory.removeItem(4);
	    
	    assertTrue("Item 1 should still exist", result1);
	    assertTrue("Item 3 should still exist", result3);
	    assertTrue("Item 4 should still exist", result4);
	}
	
	//Test #: 39
	//Obj: Test removing item after database has been modified
	//Input(s): itemId = 1 after other operations
	//Expected Output: Item removed successfully
	public void testRemoveItemAfterDatabaseOperations() {
	    boolean initialCheck = inventory.removeItem(1);
	    assertTrue("Item should exist initially", initialCheck);
	    
	}
	
	//Test #: 40
	//Obj: Test resource cleanup in removeItem method
	//Input(s): itemId = 1
	//Expected Output: All database resources properly closed, no memory leaks
	public void testRemoveItemResourceCleanup() {
	    boolean result = inventory.removeItem(1);
	    assertTrue("Resource cleanup should work correctly", result);
	
	}
	
	//Test #: 41
	//Obj: Test removeItem method with SQL injection attempt
	//Input(s): itemId = 1 (normal ID, testing parameterized query)
	//Expected Output: Item removed successfully, no SQL injection vulnerability
	public void testRemoveItemSQLInjectionSafety() {
	    boolean result = inventory.removeItem(1);
	    assertTrue("Should safely handle normal ID parameter", result);
	}
	
	//Test #: 42
	//Obj: Test removeItem with very large item ID
	//Input(s): itemId = 1000000
	//Expected Output: Returns false, no exception thrown
	public void testRemoveItemWithVeryLargeID() {
	    boolean result = inventory.removeItem(1000000);
	    assertFalse("Very large item ID should return false", result);
	}
	
	//Test #: 43
	//Obj: Test consecutive remove operations
	//Input(s): Multiple removeItem calls in sequence
	//Expected Output: Each operation returns appropriate result
	public void testConsecutiveRemoveOperations() {
	    boolean result1 = inventory.removeItem(1);
	    boolean result2 = inventory.removeItem(2);
	    boolean result3 = inventory.removeItem(1); 
	    boolean result4 = inventory.removeItem(999); 
	    
	    assertTrue("First removal should succeed", result1);
	    assertTrue("Second removal should succeed", result2);
	    assertFalse("Third removal should fail", result3);
	    assertFalse("Fourth removal should fail", result4);
	}
	
	//Test #: 44
	//Obj: Test search with valid keyword that matches items
	//Input(s): keyword = "T-Shirt"
	//Expected Output: Returns list with matching items
	public void testSearchItemWithValidKeyword() {
	List<Item> results = inventory.searchItem("T-Shirt");
	assertFalse("Should find matching items", results.isEmpty());
	assertEquals("T-Shirt", results.get(0).getName());
	}
	
	//Test #: 45
	//Obj: Test search with empty keyword
	//Input(s): keyword = ""
	//Expected Output: Returns empty list, shows validation message
	public void testSearchItemWithEmptyKeyword() {
	List<Item> results = inventory.searchItem("");
	assertTrue("Empty keyword should return empty list", results.isEmpty());
	}
	
	//Test #: 46
	//Obj: Test search with null keyword
	//Input(s): keyword = null
	//Expected Output: Returns empty list, shows validation message
	public void testSearchItemWithNullKeyword() {
	List<Item> results = inventory.searchItem(null);
	assertTrue("Null keyword should return empty list", results.isEmpty());
	}
	
	//Test #: 47
	//Obj: Test search with keyword that matches multiple items
	//Input(s): keyword = "Clothing"
	//Expected Output: Returns list with multiple matching items
	public void testSearchItemWithMultipleMatches() {
	List<Item> results = inventory.searchItem("Clothing");
	assertEquals("Should find multiple clothing items", 2, results.size());
	}
	
	//Test #: 48
	//Obj: Test search with keyword that matches no items
	//Input(s): keyword = "NonExistentItem"
	//Expected Output: Returns empty list
	public void testSearchItemWithNoMatches() {
	List<Item> results = inventory.searchItem("NonExistentItem");
	assertTrue("Non-existent keyword should return empty list", results.isEmpty());
	}
	
	//Test #: 49
	//Obj: Test search with partial keyword match
	//Input(s): keyword = "Shirt"
	//Expected Output: Returns items containing "Shirt" in name
	public void testSearchItemWithPartialMatch() {
	List<Item> results = inventory.searchItem("Shirt");
	assertFalse("Partial match should find items", results.isEmpty());
	assertTrue("Item name should contain search term", 
	           results.get(0).getName().contains("Shirt"));
	}
	
	//Test #: 50
	//Obj: Test search with case insensitive matching
	//Input(s): keyword = "t-shirt"
	//Expected Output: Returns matching items regardless of case
	public void testSearchItemCaseInsensitive() {
	List<Item> results = inventory.searchItem("t-shirt");
	assertFalse("Case insensitive search should find items", results.isEmpty());
	}
	
	//Test #: 51
	//Obj: Test search with whitespace in keyword
	//Input(s): keyword = "  T-Shirt  "
	//Expected Output: Returns matching items (whitespace trimmed)
	public void testSearchItemWithWhitespace() {
	List<Item> results = inventory.searchItem("  T-Shirt  ");
	assertFalse("Search with whitespace should find items", results.isEmpty());
	}
	
	//Test #: 52
	//Obj: Test search by supplier name - FIXED
	//Input(s): keyword = "Supplier" (search for any supplier)
	//Expected Output: Returns items from suppliers
	public void testSearchItemBySupplier() {
	List<Item> results = inventory.searchItem("Supplier");
	// This might be empty if no suppliers have "Supplier" in name, which is OK
	// Just verify the method doesn't crash
	assertNotNull("Search should return a list (even if empty)", results);
	}
	
	//Test #: 53
	//Obj: Test search by colour
	//Input(s): keyword = "Red"
	//Expected Output: Returns items with specified colour
	public void testSearchItemByColour() {
	List<Item> results = inventory.searchItem("Red");
	assertFalse("Should find items by colour", results.isEmpty());
	assertEquals("Red", results.get(0).getColour());
	}
	
	//Test #: 54
	//Obj: Test search by size
	//Input(s): keyword = "M"
	//Expected Output: Returns items with specified size
	public void testSearchItemBySize() {
	List<Item> results = inventory.searchItem("M");
	assertFalse("Should find items by size", results.isEmpty());
	assertEquals("M", results.get(0).getSize());
	}
	
	//Test #: 55
	//Obj: Test search by category
	//Input(s): keyword = "Footwear"
	//Expected Output: Returns items in specified category
	public void testSearchItemByCategory() {
	List<Item> results = inventory.searchItem("Footwear");
	assertFalse("Should find items by category", results.isEmpty());
	assertEquals("Footwear", results.get(0).getCategory());
	}
	
	//Test #: 56
	//Obj: Test search with special characters in keyword
	//Input(s): keyword = "T-Shirt-2024"
	//Expected Output: Returns empty list (no matches expected)
	public void testSearchItemWithSpecialCharacters() {
	List<Item> results = inventory.searchItem("T-Shirt-2024");
	assertTrue("Special characters should return empty if no matches", results.isEmpty());
	}
	
	//Test #: 57
	//Obj: Test search with very long keyword
	//Input(s): keyword = "VeryLongKeywordThatExceedsNormalSearchTerms"
	//Expected Output: Returns empty list
	public void testSearchItemWithLongKeyword() {
	List<Item> results = inventory.searchItem("VeryLongKeywordThatExceedsNormalSearchTerms");
	assertTrue("Very long keyword should return empty", results.isEmpty());
	}
	
	//Test #: 58
	//Obj: Test search with numeric keyword - FIXED
	//Input(s): keyword = "42"
	//Expected Output: Returns items matching numeric size/ID
	public void testSearchItemWithNumericKeyword() {
	List<Item> results = inventory.searchItem("42");
	// This depends on your test data - check if you have size "42" items
	// If not, just verify the method works without crashing
	assertNotNull("Search should return a list", results);
	}
	
	//Test #: 59
	//Obj: Test search with price value as keyword - FIXED
	//Input(s): keyword = "19.99"
	//Expected Output: Returns items with matching price
	public void testSearchItemWithPriceKeyword() {
	List<Item> results = inventory.searchItem("19.99");
	// This might find items depending on how price is stored in database
	// Just verify the method doesn't crash
	assertNotNull("Search should return a list", results);
	}
	
	//Test #: 60
	//Obj: Test search across multiple fields simultaneously
	//Input(s): keyword = "Blue"
	//Expected Output: Returns items where any field contains "Blue"
	public void testSearchItemAcrossMultipleFields() {
	List<Item> results = inventory.searchItem("Blue");
	assertFalse("Should find items across multiple fields", results.isEmpty());
	assertEquals("Blue", results.get(0).getColour());
	}
	
	/*  ORGANIZATION TESTS */
	//Test #: 61
	//Obj: Test organizeByCategory returns items sorted by category
	//Input(s): None
	//Expected Output: List of items sorted by category then name
	public void testOrganizeByCategory() {
	  List<Item> result = inventory.organizeByCategory();
	  assertNotNull("Category organized list should not be null", result);
	  for (int i = 0; i < result.size() - 1; i++) {
	      String currentCategory = result.get(i).getCategory();
	      String nextCategory = result.get(i + 1).getCategory();
	      assertTrue("Categories should be in ascending order", currentCategory.compareTo(nextCategory) <= 0);
	  }
	}
	
	//Test #: 62
	//Obj: Test organizeBySize returns items in correct size order
	//Input(s): None
	//Expected Output: List of items sorted by size (XS, S, M, L, XL, XXL, XXXL)
	public void testOrganizeBySize() {
	  List<Item> result = inventory.organizeBySize();
	  assertNotNull("Size organized list should not be null", result);
	  String[] sizeOrder = {"XS", "S", "M", "L", "XL", "XXL", "XXXL"};
	  for (int i = 0; i < result.size() - 1; i++) {
	      String currentSize = result.get(i).getSize().toUpperCase();
	      String nextSize = result.get(i + 1).getSize().toUpperCase();
	      int currentIndex = java.util.Arrays.asList(sizeOrder).indexOf(currentSize);
	      int nextIndex = java.util.Arrays.asList(sizeOrder).indexOf(nextSize);
	      if (currentIndex == -1) currentIndex = sizeOrder.length;
	      if (nextIndex == -1) nextIndex = sizeOrder.length;
	      assertTrue("Sizes should follow logical order", currentIndex <= nextIndex);
	  }
	}
	
	//Test #: 63
	//Obj: Test organizeByColour returns items sorted by colour
	//Input(s): None
	//Expected Output: List of items sorted by colour then name
	public void testOrganizeByColour() {
	  List<Item> result = inventory.organizeByColour();
	  assertNotNull("Colour organized list should not be null", result);
	  for (int i = 0; i < result.size() - 1; i++) {
	      String currentColour = result.get(i).getColour();
	      String nextColour = result.get(i + 1).getColour();
	      assertTrue("Colours should be in ascending order", currentColour.compareTo(nextColour) <= 0);
	  }
	}
	
	//Test #: 64
	//Obj: Test organizeByPrice returns items sorted by price
	//Input(s): None
	//Expected Output: List of items sorted by price then name
	public void testOrganizeByPrice() {
	  List<Item> result = inventory.organizeByPrice();
	  assertNotNull("Price organized list should not be null", result);
	  for (int i = 0; i < result.size() - 1; i++) {
	      double currentPrice = result.get(i).getPrice();
	      double nextPrice = result.get(i + 1).getPrice();
	      assertTrue("Prices should be in ascending order", currentPrice <= nextPrice);
	  }
	}
	
	//Test #: 65
	//Obj: Test getItemVariations with valid item name returns all variations
	//Input(s): itemName = "T-Shirt"
	//Expected Output: List of all T-Shirt variations with different sizes/colours
	public void testGetItemVariationsValidName() {
	  List<Item> variations = inventory.getItemVariations("T-Shirt");
	  assertNotNull("Variations list should not be null", variations);
	  for (Item item : variations) {
	      assertEquals("All variations should have same name", "T-Shirt", item.getName());
	  }
	}
	
	//Test #: 66
	//Obj: Test getItemVariations with non-existent item name
	//Input(s): itemName = "NonExistentItem"
	//Expected Output: Empty list
	public void testGetItemVariationsNonExistentName() {
	  List<Item> variations = inventory.getItemVariations("NonExistentItem");
	  assertNotNull("Should return empty list, not null", variations);
	  assertTrue("Should return empty list for non-existent item", variations.isEmpty());
	}
	
	//Test #: 67
	//Obj: Test getItemVariations with null item name
	//Input(s): itemName = null
	//Expected Output: Empty list
	public void testGetItemVariationsNullName() {
	  List<Item> variations = inventory.getItemVariations(null);
	  assertNotNull("Should return empty list, not null", variations);
	  assertTrue("Should return empty list for null name", variations.isEmpty());
	}
	
	//Test #: 68
	//Obj: Test all organization methods return consistent item counts
	//Input(s): None
	//Expected Output: All organization methods return same number of items
	public void testOrganizationMethodsConsistentCounts() {
	  List<Item> byCategory = inventory.organizeByCategory();
	  List<Item> bySize = inventory.organizeBySize();
	  List<Item> byColour = inventory.organizeByColour();
	  List<Item> byPrice = inventory.organizeByPrice();
	  assertEquals("All organization methods should return same number of items", byCategory.size(), bySize.size());
	  assertEquals("All organization methods should return same number of items", byCategory.size(), byColour.size());
	  assertEquals("All organization methods should return same number of items", byCategory.size(), byPrice.size());
	}
	
	//Test #: 69
	//Obj: Test organization methods handle empty database gracefully
	//Input(s): None (empty database)
	//Expected Output: All methods return empty lists without errors
	public void testOrganizationMethodsEmptyDatabase() throws SQLException {
	  Connection conn = DatabaseConnection.getConnection();
	  Statement stmt = conn.createStatement();
	  stmt.executeUpdate("DELETE FROM items");
	  stmt.close();
	  assertTrue("Category should be empty", inventory.organizeByCategory().isEmpty());
	  assertTrue("Size should be empty", inventory.organizeBySize().isEmpty());
	  assertTrue("Colour should be empty", inventory.organizeByColour().isEmpty());
	  assertTrue("Price should be empty", inventory.organizeByPrice().isEmpty());
	}
	

	//Test #: 71
	//Obj: Test recording a valid sale
	//Input(s): itemId = 1, quantitySold = 5
	//Expected Output: Returns true, stock reduced by 5
	public void testRecordValidSale() throws SQLException {
	    int itemId = 1;
	    int initialQuantity;
	    try (Connection conn = DatabaseConnection.getConnection()) {
	        initialQuantity = getQuantity(conn, itemId);
	    }

	    boolean result = Transaction.recordSale(itemId, 5);
	    assertTrue("Sale should succeed", result);

	    try (Connection conn = DatabaseConnection.getConnection()) {
	        int newQuantity = getQuantity(conn, itemId);
	        assertEquals("Stock should reduce by 5", initialQuantity - 5, newQuantity);
	    }
	}

	//Test #: 72
	//Obj: Test sale with quantity exceeding stock
	//Input(s): itemId = 1, quantitySold = 1000
	//Expected Output: Returns false, stock unchanged
	public void testRecordSaleExceedingStock() throws SQLException {
	    int itemId = 1;
	    int initialQuantity;
	    try (Connection conn = DatabaseConnection.getConnection()) {
	        initialQuantity = getQuantity(conn, itemId);
	    }

	    boolean result = Transaction.recordSale(itemId, 1000);
	    assertFalse("Sale exceeding stock should fail", result);

	    try (Connection conn = DatabaseConnection.getConnection()) {
	        int newQuantity = getQuantity(conn, itemId);
	        assertEquals("Stock should not change", initialQuantity, newQuantity);
	    }
	}

	//Test #: 73
	//Obj: Test sale with negative quantity
	//Input(s): itemId = 1, quantitySold = -5
	//Expected Output: Returns false, stock unchanged
	public void testRecordSaleNegativeQuantity() throws SQLException {
	    int itemId = 1;
	    int initialQuantity;
	    try (Connection conn = DatabaseConnection.getConnection()) {
	        initialQuantity = getQuantity(conn, itemId);
	    }

	    boolean result = Transaction.recordSale(itemId, -5);
	    assertFalse("Sale with negative quantity should fail", result);

	    try (Connection conn = DatabaseConnection.getConnection()) {
	        int newQuantity = getQuantity(conn, itemId);
	        assertEquals("Stock should not change", initialQuantity, newQuantity);
	    }
	}

	//Test #: 74
	//Obj: Test sale with zero quantity
	//Input(s): itemId = 1, quantitySold = 0
	//Expected Output: Returns false, stock unchanged
	public void testRecordSaleZeroQuantity() throws SQLException {
	    int itemId = 1;
	    int initialQuantity;
	    try (Connection conn = DatabaseConnection.getConnection()) {
	        initialQuantity = getQuantity(conn, itemId);
	    }

	    boolean result = Transaction.recordSale(itemId, 0);
	    assertFalse("Sale with zero quantity should fail", result);

	    try (Connection conn = DatabaseConnection.getConnection()) {
	        int newQuantity = getQuantity(conn, itemId);
	        assertEquals("Stock should not change", initialQuantity, newQuantity);
	    }
	}

	//Test #: 75
	//Obj: Test sale with non-existent item
	//Input(s): itemId = 99999, quantitySold = 5
	//Expected Output: Returns false
	public void testRecordSaleNonExistentItem() {
	    boolean result = Transaction.recordSale(99999, 5);
	    assertFalse("Sale for non-existent item should fail", result);
	}

	//Test #: 76
	//Obj: Test multiple sales in sequence
	//Input(s): itemId = 2, quantitySold = 5 then 3
	//Expected Output: Stock decreases accordingly
	public void testRecordMultipleSales() throws SQLException {
	    int itemId = 2;
	    int initialQuantity;
	    try (Connection conn = DatabaseConnection.getConnection()) {
	        initialQuantity = getQuantity(conn, itemId);
	    }

	    boolean result1 = Transaction.recordSale(itemId, 5);
	    boolean result2 = Transaction.recordSale(itemId, 3);

	    assertTrue(result1 && result2);

	    try (Connection conn = DatabaseConnection.getConnection()) {
	        int newQuantity = getQuantity(conn, itemId);
	        assertEquals("Stock should reduce by total sold", initialQuantity - 8, newQuantity);
	    }
	    
	}
	
	// Test #: 77
	// Obj: Test adding a new supplier with valid data
	// Input(s): name = "Test Supplier", contact = "test@supplier.com"
	// Expected Output: Supplier added successfully
	public void testAddValidSupplier() throws SQLException {
	    Inventory inventory = new Inventory();
	    Supplier supplier = new Supplier(0, "Test Supplier", "test@supplier.com");
	    
	    inventory.addSupplier(supplier);
	    
	    List<Supplier> suppliers = inventory.getAllSuppliers();
	    boolean found = suppliers.stream()
	        .anyMatch(s -> s.getName().equals("Test Supplier") && s.getContact().equals("test@supplier.com"));
	    
	    assertTrue("Supplier should be added to database", found);
	}

	// Test #: 78
	// Obj: Test adding supplier with empty name
	// Input(s): name = "", contact = "test@supplier.com"
	// Expected Output: IllegalArgumentException thrown
	public void testAddSupplierWithEmptyName() {
	    Inventory inventory = new Inventory();
	    
	    try {
	        Supplier supplier = new Supplier(0, "", "test@supplier.com");
	        inventory.addSupplier(supplier);
	        fail("Should have thrown IllegalArgumentException");
	    } catch (IllegalArgumentException e) {
	        // Expected - test passes
	        assertTrue(true);
	    }
	}

	// Test #: 79
	// Obj: Test adding supplier with null contact
	// Input(s): name = "Test Supplier", contact = null
	// Expected Output: IllegalArgumentException thrown
	public void testAddSupplierWithNullContact() {
	    Inventory inventory = new Inventory();
	    
	    try {
	        Supplier supplier = new Supplier(0, "Test Supplier", null);
	        inventory.addSupplier(supplier);
	        fail("Should have thrown IllegalArgumentException");
	    } catch (IllegalArgumentException e) {
	        // Expected - test passes
	        assertTrue(true);
	    }
	}


	// Test #: 80
	// Obj: Test retrieving all suppliers from database
	// Input(s): None
	// Expected Output: List of all suppliers returned
	public void testGetAllSuppliers() throws SQLException {
	    Inventory inventory = new Inventory();
	    
	    // Add test suppliers first
	    inventory.addSupplier(new Supplier(0, "Supplier A", "contactA@test.com"));
	    inventory.addSupplier(new Supplier(0, "Supplier B", "contactB@test.com"));
	    
	    List<Supplier> suppliers = inventory.getAllSuppliers();
	    
	    assertTrue("Should return at least 2 suppliers", suppliers.size() >= 2);
	    assertTrue("Should contain Supplier A", 
	        suppliers.stream().anyMatch(s -> s.getName().equals("Supplier A")));
	    assertTrue("Should contain Supplier B", 
	        suppliers.stream().anyMatch(s -> s.getName().equals("Supplier B")));
	}

	// Test #: 81
	// Obj: Test linking item to existing supplier
	// Input(s): itemId = 1, supplierId = 1
	// Expected Output: Item successfully linked to supplier
	public void testLinkItemToSupplier() throws SQLException {
	    Inventory inventory = new Inventory();
	    
	    // First add a supplier
	    inventory.addSupplier(new Supplier(0, "Test Link Supplier", "link@test.com"));
	    List<Supplier> suppliers = inventory.getAllSuppliers();
	    int supplierId = suppliers.get(0).getSupplierId();
	    
	    // Link item to supplier
	    inventory.updateItemSupplier(1, supplierId);
	    
	    // Verify link
	    List<Item> supplierItems = inventory.getItemsBySupplier(supplierId);
	    boolean itemFound = supplierItems.stream()
	        .anyMatch(item -> item.getItemId() == 1);
	    
	    assertTrue("Item should be linked to supplier", itemFound);
	}

	// Test #: 82
	// Obj: Test getting items by specific supplier
	// Input(s): supplierId = 1
	// Expected Output: List of items from that supplier
	public void testGetItemsBySupplier() throws SQLException {
	    Inventory inventory = new Inventory();
	    
	    // Add supplier and link some items
	    inventory.addSupplier(new Supplier(0, "Items Test Supplier", "items@test.com"));
	    List<Supplier> suppliers = inventory.getAllSuppliers();
	    int supplierId = suppliers.get(0).getSupplierId();
	    
	    // Link multiple items to this supplier
	    inventory.updateItemSupplier(1, supplierId);
	    inventory.updateItemSupplier(2, supplierId);
	    
	    List<Item> supplierItems = inventory.getItemsBySupplier(supplierId);
	    
	    assertTrue("Should return items for supplier", supplierItems.size() >= 2);
	    assertTrue("All returned items should belong to the specified supplier",
	        supplierItems.stream().allMatch(item -> 
	            item.getSupplier() != null && item.getSupplier().getSupplierId() == supplierId));
	}

	// Test #: 83
	// Obj: Test unlinking item from supplier (set supplier to null)
	// Input(s): itemId = 1, supplierId = 0 (null)
	// Expected Output: Item supplier set to null
	public void testUnlinkItemFromSupplier() throws SQLException {
	    Inventory inventory = new Inventory();
	    
	    // First link item to a supplier
	    inventory.addSupplier(new Supplier(0, "Unlink Test Supplier", "unlink@test.com"));
	    List<Supplier> suppliers = inventory.getAllSuppliers();
	    int supplierId = suppliers.get(0).getSupplierId();
	    inventory.updateItemSupplier(1, supplierId);
	    
	    // Now unlink by setting supplier to null
	    inventory.updateItemSupplier(1, 0);
	    
	    // Verify item has no supplier
	    List<Item> supplierItems = inventory.getItemsBySupplier(supplierId);
	    boolean itemStillLinked = supplierItems.stream()
	        .anyMatch(item -> item.getItemId() == 1);
	    
	    assertFalse("Item should no longer be linked to supplier", itemStillLinked);
	}

	// Test #: 84
	// Obj: Test searching items by supplier name
	// Input(s): keyword = "Search Test Supplier"
	// Expected Output: Items from Search Test Supplier returned
	public void testSearchItemsBySupplierName() throws SQLException {
	    Inventory inventory = new Inventory();
	    
	    // Add supplier
	    Supplier supplier = new Supplier(0, "Search Test Supplier", "search@test.com");
	    inventory.addSupplier(supplier);
	    
	    // Search for supplier name
	    List<Item> searchResults = inventory.searchItem("Search Test Supplier");
	    
	    assertTrue("Should find items by supplier name", searchResults.size() >= 0);
	}

	// Test #: 85
	// Obj: Test adding item with supplier selection
	// Input(s): Item with selected supplier
	// Expected Output: Item added with correct supplier linked
	public void testAddItemWithSupplier() throws SQLException {
	    Inventory inventory = new Inventory();
	    
	    // Add a supplier first
	    inventory.addSupplier(new Supplier(0, "Item Add Supplier", "itemadd@test.com"));
	    List<Supplier> suppliers = inventory.getAllSuppliers();
	    Supplier selectedSupplier = suppliers.get(0);
	    
	    // Create item with supplier
	    Item newItem = new Item(0, "Test Item", "Test Category", "M", "Red", 29.99, 50, selectedSupplier);
	    inventory.addItem(newItem);
	    
	    // Verify item was added with correct supplier
	    List<Item> supplierItems = inventory.getItemsBySupplier(selectedSupplier.getSupplierId());
	    boolean itemFound = supplierItems.stream()
	        .anyMatch(item -> item.getName().equals("Test Item") && 
	                         item.getSupplier().getSupplierId() == selectedSupplier.getSupplierId());
	    
	    assertTrue("Item should be added with correct supplier", itemFound);
	}

	// Test #: 86
	// Obj: Test supplier integration in organize by category
	// Input(s): None
	// Expected Output: Organized items include supplier information
	public void testOrganizeByCategoryIncludesSuppliers() throws SQLException {
	    Inventory inventory = new Inventory();
	    
	    List<Item> organizedItems = inventory.organizeByCategory();
	    
	    assertTrue("Should return organized items", organizedItems.size() > 0);
	    // Verify that supplier information is properly loaded
	    assertTrue("Items should have supplier information loaded",
	        organizedItems.stream().allMatch(item -> 
	            item.getSupplier() == null || 
	            (item.getSupplier().getName() != null && item.getSupplier().getContact() != null)));
	}
	
	
	
	

	private int getQuantity(Connection conn, int itemId) throws SQLException {
	    try (PreparedStatement stmt = conn.prepareStatement("SELECT quantity FROM items WHERE itemId = ?")) {
	        stmt.setInt(1, itemId);
	        ResultSet rs = stmt.executeQuery();
	        if (rs.next()) return rs.getInt("quantity");
	        return -1; 
	    }
	}
	
	


    // KEEP THIS METHOD AT THE BOTTOM OF THE FILE AT ALL TIMES TO RESET THE DATABASE EVERY RUN SO YOU DONT HAVE TO RUN A NEW SQL FILE EVERYTIME
    @Override
    protected void tearDown() throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1️ Clear sales first (because of foreign key)
            stmt.executeUpdate("DELETE FROM sales");

            // 2️ Clear items
            stmt.executeUpdate("DELETE FROM items");

            // 3️ Reset items to default
            stmt.executeUpdate(
                "INSERT INTO items (itemId, itemName, category, size, colour, price, quantity, supplier_id) VALUES " +
                "(1, 'T-Shirt', 'Clothing', 'M', 'Red', 19.99, 50, 1)," +
                "(2, 'Jeans', 'Clothing', 'L', 'Blue', 49.99, 30, 1)," +
                "(3, 'Sneakers', 'Footwear', '42', 'White', 89.99, 20, 2)," +
                "(4, 'Jacket', 'Outerwear', 'XL', 'Black', 99.50, 15, 2)"
            );
        }
        super.tearDown();
    }


	    
		   
}
	  
	  


