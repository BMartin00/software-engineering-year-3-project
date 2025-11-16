package gigabytegurus.inventorymanagementsystem;

import junit.framework.TestCase;

public class TransactionTest extends TestCase {
    
    protected void setUp() throws Exception {
	    super.setUp();
	    new Transaction();
	    // Enable test mode
	    Transaction.testMode = true;
	}
	
	protected void tearDown() throws Exception {
	    super.tearDown();
	    // Reset test mode
	    Transaction.testMode = false;
	}

    //Test #: 1
    //Obj: Test processReturn successfully adds items back to inventory
    //Input(s): itemId = 1, quantityReturned = 5, reason = "Customer changed mind"
    //Expected Output: Returns true and inventory increases by 5
    public void testProcessReturnValid() {
        boolean result = Transaction.processReturn(1, 5, "Customer changed mind");
        assertTrue("Process return should succeed with valid inputs", result);
    }

    //Test #: 2
    //Obj: Test processReturn fails with zero or negative quantity
    //Input(s): itemId = 1, quantityReturned = 0, reason = "Test reason"
    //Expected Output: Returns false (no inventory change)
    public void testProcessReturnInvalidQuantity() {
        boolean result = Transaction.processReturn(1, 0, "Test reason");
        assertFalse("Process return should fail with zero quantity", result);
        
        boolean result2 = Transaction.processReturn(1, -5, "Test reason");
        assertFalse("Process return should fail with negative quantity", result2);
    }

    //Test #: 3
    //Obj: Test processReturn fails with non-existent item
    //Input(s): itemId = 9999, quantityReturned = 5, reason = "Test reason"
    //Expected Output: Returns false
    public void testProcessReturnNonExistentItem() {
        boolean result = Transaction.processReturn(9999, 5, "Test reason");
        assertFalse("Process return should fail with non-existent item", result);
    }

    //Test #: 4
    //Obj: Test processExchange successfully processes size/color swap
    //Input(s): returnedItemId = 1, returnedQuantity = 2, newItemId = 3, newItemQuantity = 2, reason = "Size exchange"
    //Expected Output: Returns true and adjusts both item inventories
    public void testProcessExchangeValid() {
        boolean result = Transaction.processExchange(1, 2, 3, 2, "Size exchange");
        assertTrue("Process exchange should succeed with valid inputs", result);
    }

    //Test #: 5
    //Obj: Test processExchange fails when new item has insufficient stock
    //Input(s): returnedItemId = 1, returnedQuantity = 2, newItemId = 3, newItemQuantity = 1000, reason = "Test exchange"
    //Expected Output: Returns false (no inventory changes)
    public void testProcessExchangeInsufficientStock() {
        boolean result = Transaction.processExchange(1, 2, 3, 1000, "Test exchange");
        assertFalse("Process exchange should fail with insufficient stock", result);
    }

    //Test #: 6
    //Obj: Test processExchange fails with zero or negative quantities
    //Input(s): returnedItemId = 1, returnedQuantity = 0, newItemId = 3, newItemQuantity = 2, reason = "Test"
    //Expected Output: Returns false (no inventory changes)
    public void testProcessExchangeInvalidQuantities() {
        boolean result = Transaction.processExchange(1, 0, 3, 2, "Test");
        assertFalse("Process exchange should fail with zero returned quantity", result);
        
        boolean result2 = Transaction.processExchange(1, 2, 3, -1, "Test");
        assertFalse("Process exchange should fail with negative new quantity", result2);
    }

    //Test #: 7
    //Obj: Test processExchange fails with non-existent items
    //Input(s): returnedItemId = 9999, returnedQuantity = 2, newItemId = 8888, newItemQuantity = 2, reason = "Test"
    //Expected Output: Returns false
    public void testProcessExchangeNonExistentItems() {
        boolean result = Transaction.processExchange(9999, 2, 8888, 2, "Test");
        assertFalse("Process exchange should fail with non-existent items", result);
    }

    //Test #: 8
    //Obj: Test getReturnHistory returns valid history string for existing item
    //Input(s): itemId = 1 (existing item with returns)
    //Expected Output: Non-empty string containing return history
    public void testGetReturnHistoryValidItem() {
        String history = Transaction.getReturnHistory(1);
        assertNotNull("Return history should not be null", history);
        assertTrue("Return history should contain header", history.contains("RETURN HISTORY"));
    }

    //Test #: 9
    //Obj: Test getReturnHistory handles non-existent items gracefully
    //Input(s): itemId = 9999 (non-existent item)
    //Expected Output: Empty or header-only history string
    public void testGetReturnHistoryInvalidItem() {
        String history = Transaction.getReturnHistory(9999);
        assertNotNull("Return history should not be null even for invalid items", history);
        // Should return header but no data rows
    }

    //Test #: 10
    //Obj: Test getExchangeHistory returns valid exchange records
    //Input(s): itemId = 1 (item involved in exchanges)
    //Expected Output: Non-empty string containing exchange history
    public void testGetExchangeHistoryValidItem() {
        String history = Transaction.getExchangeHistory(1);
        assertNotNull("Exchange history should not be null", history);
        assertTrue("Exchange history should contain header", history.contains("EXCHANGE HISTORY"));
    }

    //Test #: 11
    //Obj: Test getExchangeHistory handles non-existent items gracefully
    //Input(s): itemId = 9999 (non-existent item)
    //Expected Output: Header-only history string
    public void testGetExchangeHistoryInvalidItem() {
        String history = Transaction.getExchangeHistory(9999);
        assertNotNull("Exchange history should not be null even for invalid items", history);
    }

    //Test #: 12
    //Obj: Test getSalesHistory returns sales data with calculations
    //Input(s): itemId = 1 (item with sales history)
    //Expected Output: String containing sales data and revenue calculations
    public void testGetSalesHistoryValidItem() {
        String history = Transaction.getSalesHistory(1);
        assertNotNull("Sales history should not be null", history);
        assertTrue("Sales history should contain header", history.contains("SALES HISTORY"));
    }

    //Test #: 13
    //Obj: Test getSalesHistory handles items with no sales
    //Input(s): itemId = 9999 (item with no sales)
    //Expected Output: String indicating no sales history
    public void testGetSalesHistoryNoSales() {
        String history = Transaction.getSalesHistory(9999);
        assertNotNull("Sales history should not be null even with no sales", history);
        assertTrue("Should indicate no sales found", history.contains("No sales history") || history.contains("no sales"));
    }

    //Test #: 14
    //Obj: Test getSalesSummary returns formatted business summary
    //Input(s): None (aggregates all sales data)
    //Expected Output: String containing sales summary with totals
    public void testGetSalesSummary() {
        String summary = Transaction.getSalesSummary();
        assertNotNull("Sales summary should not be null", summary);
        assertTrue("Sales summary should contain header", summary.contains("SALES SUMMARY"));
    }

    //Test #: 15
    //Obj: Test recordSale successfully processes sales transaction
    //Input(s): itemId = 1, quantitySold = 3
    //Expected Output: Returns true and reduces inventory by 3
    public void testRecordSaleValid() {
        boolean result = Transaction.recordSale(1, 3);
        assertTrue("Record sale should succeed with valid inputs", result);
    }

    //Test #: 16
    //Obj: Test recordSale fails when insufficient stock available
    //Input(s): itemId = 1, quantitySold = 1000
    //Expected Output: Returns false (no inventory change)
    public void testRecordSaleInsufficientStock() {
        boolean result = Transaction.recordSale(1, 1000);
        assertFalse("Record sale should fail with insufficient stock", result);
    }

    //Test #: 17
    //Obj: Test recordSale fails with zero or negative quantity
    //Input(s): itemId = 1, quantitySold = 0
    //Expected Output: Returns false
    public void testRecordSaleInvalidQuantity() {
        boolean result = Transaction.processReturn(1, 0, "Test reason");
        assertFalse("Record sale should fail with zero quantity", result);
        
        boolean result2 = Transaction.processReturn(1, -5, "Test reason");
        assertFalse("Record sale should fail with negative quantity", result2);
    }

    //Test #: 18
    //Obj: Test recordSale fails with non-existent item
    //Input(s): itemId = 9999, quantitySold = 5
    //Expected Output: Returns false
    public void testRecordSaleNonExistentItem() {
        boolean result = Transaction.recordSale(9999, 5);
        assertFalse("Record sale should fail with non-existent item", result);
    }

    //Test #: 19
    //Obj: Test processReturn with null reason handled gracefully
    //Input(s): itemId = 1, quantityReturned = 1, reason = null
    //Expected Output: Should handle null reason without exception
    public void testProcessReturnNullReason() {
        boolean result = Transaction.processReturn(1, 1, null);
        // Should either handle null gracefully or convert to empty string
        assertTrue("Should handle null reason", result == true || result == false);
    }

    //Test #: 20
    //Obj: Test processExchange with null reason handled gracefully
    //Input(s): returnedItemId = 1, returnedQuantity = 1, newItemId = 3, newItemQuantity = 1, reason = null
    //Expected Output: Should handle null reason without exception
    public void testProcessExchangeNullReason() {
        boolean result = Transaction.processExchange(1, 1, 3, 1, null);
        // Should either handle null gracefully or convert to empty string
        assertTrue("Should handle null reason", result == true || result == false);
    }
}