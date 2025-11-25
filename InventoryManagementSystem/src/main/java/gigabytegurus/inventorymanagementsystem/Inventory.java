package gigabytegurus.inventorymanagementsystem;

import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Inventory
{
	Logger logger = Logger.getLogger(getClass().getName());
	
	private List<Item> items;
	
	private static final String ITEM_ID = "itemId";
    private static final String ITEM_NAME = "itemName";
    private static final String CATEGORY = "category";
    private static final String COLOUR = "colour";
    private static final String PRICE = "price";
    private static final String QUANTITY = "quantity";
    
    private static final String SUPPLIER_ID = "supplier_id";
    private static final String SUPPLIER_NAME = "supplierName";
    private static final String SUPPLIER_CONTACT = "supplierContact";
	
	public Inventory() {
        this.items = new ArrayList<>();
        initializeDatabaseTables();
    }
	
	public boolean testMode = false;
	
	// FIXED: Initialize database tables
	private void initializeDatabaseTables() {
	    try (Connection conn = DatabaseConnection.getConnection();
	         Statement stmt = conn.createStatement()) {
	        
	        // Create suppliers table if it doesn't exist
	        String createSuppliersTable = """
	            CREATE TABLE IF NOT EXISTS suppliers (
	                supplier_id INTEGER PRIMARY KEY AUTO_INCREMENT,
	                name TEXT NOT NULL,
	                contact TEXT NOT NULL
	            )
	        """;
	        stmt.execute(createSuppliersTable);
	        
	        logger.info("Database tables initialized successfully.");
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        logger.info("Error initializing database tables: " + e.getMessage());
	    }
	}
	
	// SUPPLIER MANAGEMENT METHODS
	public void addSupplier(Supplier supplier) {
	    if (supplier == null) {
	        throw new IllegalArgumentException("Supplier cannot be null.");
	    }
	    if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
	        throw new IllegalArgumentException("Supplier name cannot be empty.");
	    }
	    if (supplier.getContact() == null || supplier.getContact().trim().isEmpty()) {
	        throw new IllegalArgumentException("Supplier contact cannot be empty.");
	    }

	    String sql = "INSERT INTO suppliers (name, contact) VALUES (?, ?)";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	    		PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setString(1, supplier.getName());
	        stmt.setString(2, supplier.getContact());

	        stmt.executeUpdate();
	        
	        logger.info("Supplier '" + supplier.getName() + "' added successfully!");
	        
	        if (!testMode) {
	            JOptionPane.showMessageDialog(null, "Supplier '" + supplier.getName() + "' added successfully!");
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	        if (!testMode) {
	            JOptionPane.showMessageDialog(null, 
	                "Error adding supplier: " + e.getMessage(), 
	                "Database Error", 
	                JOptionPane.ERROR_MESSAGE);
	        }
	    }
	}

	public List<Supplier> getAllSuppliers() {
	    List<Supplier> suppliers = new ArrayList<>();
	    String sql = "SELECT supplier_id, name, contact FROM suppliers ORDER BY name";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	        
	        while (rs.next()) {
	            Supplier supplier = new Supplier(
	                rs.getInt(SUPPLIER_ID),
	                rs.getString("name"),
	                rs.getString("contact")
	            );
	            suppliers.add(supplier);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return suppliers;
	}

	public void updateItemSupplier(int itemId, int supplierId) {
		String sql = "UPDATE items SET supplier_id = ? WHERE itemId = ?";
		
	    try (Connection conn = DatabaseConnection.getConnection();
	    		PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        if (supplierId > 0) {
	            stmt.setInt(1, supplierId);
	        } else {
	            stmt.setNull(1, java.sql.Types.INTEGER);
	        }
	        
	        stmt.setInt(2, itemId);
	        stmt.executeUpdate();
	        
	        logger.info("Item supplier updated successfully.");
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	public List<Item> getItemsBySupplier(int supplierId) {
	    List<Item> supplierItems = new ArrayList<>();
	    String sql = "SELECT i.*, s.name AS supplierName, s.contact AS supplierContact " +
	                 "FROM items i LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id " +
	                 "WHERE i.supplier_id = ? " +
	                 "ORDER BY i.itemName";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, supplierId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Supplier supplier = new Supplier(
	                    supplierId,
	                    rs.getString(SUPPLIER_NAME),
	                    rs.getString(SUPPLIER_CONTACT)
	                );

	                Item item = new Item(
	                    rs.getInt(ITEM_ID),
	                    rs.getString(ITEM_NAME),
	                    rs.getString(CATEGORY),
	                    rs.getString("size"),
	                    rs.getString(COLOUR),
	                    rs.getDouble(PRICE),
	                    rs.getInt(QUANTITY),
	                    supplier
	                );

	                supplierItems.add(item);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return supplierItems;
	}
	
	// ALL YOUR EXISTING METHODS BELOW (UNCHANGED)
	public List<Item> organizeByCategory() {
	    List<Item> organizedItems = new ArrayList<>();
	    String sql = "SELECT i.*, s.name AS supplierName, s.contact AS supplierContact " +
	                 "FROM items i LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id " +
	                 "ORDER BY category, itemName";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	        
	        while (rs.next()) {
	            Supplier supplier = null;
	            int supplierId = rs.getInt(SUPPLIER_ID);
	            
	            if (!rs.wasNull()) {
	                supplier = new Supplier(
	                    supplierId,
	                    rs.getString(SUPPLIER_NAME),
	                    rs.getString(SUPPLIER_CONTACT)
	                );
	            }

	            Item item = new Item(
	                rs.getInt(ITEM_ID),
	                rs.getString(ITEM_NAME),
	                rs.getString(CATEGORY),
	                rs.getString("size"),
	                rs.getString(COLOUR),
	                rs.getDouble(PRICE),
	                rs.getInt(QUANTITY),
	                supplier
	            );

	            organizedItems.add(item);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return organizedItems;
	}
	
	public List<Item> organizeBySize() {
	    List<Item> organizedItems = new ArrayList<>();
	    String sql = """
	        SELECT i.*, s.name AS supplierName, s.contact AS supplierContact 
	        FROM items i LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id 
	        ORDER BY 
	        CASE 
	            WHEN UPPER(size) = 'XS' THEN 1
	            WHEN UPPER(size) = 'S' THEN 2
	            WHEN UPPER(size) = 'M' THEN 3
	            WHEN UPPER(size) = 'L' THEN 4
	            WHEN UPPER(size) = 'XL' THEN 5
	            WHEN UPPER(size) = 'XXL' THEN 6
	            WHEN UPPER(size) = 'XXXL' THEN 7
	            ELSE 8
	        END, 
	        itemName
	    """;
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	        
	        while (rs.next()) {
	            Supplier supplier = null;
	            int supplierId = rs.getInt(SUPPLIER_ID);
	            
	            if (!rs.wasNull()) {
	                supplier = new Supplier(
	                    supplierId,
	                    rs.getString(SUPPLIER_NAME),
	                    rs.getString(SUPPLIER_CONTACT)
	                );
	            }

	            Item item = new Item(
	                rs.getInt(ITEM_ID),
	                rs.getString(ITEM_NAME),
	                rs.getString(CATEGORY),
	                rs.getString("size"),
	                rs.getString(COLOUR),
	                rs.getDouble(PRICE),
	                rs.getInt(QUANTITY),
	                supplier
	            );

	            organizedItems.add(item);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return organizedItems;
	}
	
	public List<Item> organizeByColour() {
	    List<Item> organizedItems = new ArrayList<>();
	    String sql = "SELECT i.*, s.name AS supplierName, s.contact AS supplierContact " +
	                 "FROM items i LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id " +
	                 "ORDER BY colour, itemName";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	        
	        while (rs.next()) {
	            Supplier supplier = null;
	            int supplierId = rs.getInt(SUPPLIER_ID);
	            
	            if (!rs.wasNull()) {
	                supplier = new Supplier(
	                    supplierId,
	                    rs.getString(SUPPLIER_NAME),
	                    rs.getString(SUPPLIER_CONTACT)
	                );
	            }

	            Item item = new Item(
	                rs.getInt(ITEM_ID),
	                rs.getString(ITEM_NAME),
	                rs.getString(CATEGORY),
	                rs.getString("size"),
	                rs.getString(COLOUR),
	                rs.getDouble(PRICE),
	                rs.getInt(QUANTITY),
	                supplier
	            );

	            organizedItems.add(item);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return organizedItems;
	}
	
	public List<Item> organizeByPrice() {
	    List<Item> organizedItems = new ArrayList<>();
	    String sql = "SELECT i.*, s.name AS supplierName, s.contact AS supplierContact " +
	                 "FROM items i LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id " +
	                 "ORDER BY price, itemName";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql);
	         ResultSet rs = stmt.executeQuery()) {
	        
	        while (rs.next()) {
	            Supplier supplier = null;
	            int supplierId = rs.getInt(SUPPLIER_ID);
	            
	            if (!rs.wasNull()) {
	                supplier = new Supplier(
	                    supplierId,
	                    rs.getString(SUPPLIER_NAME),
	                    rs.getString(SUPPLIER_CONTACT)
	                );
	            }

	            Item item = new Item(
	                rs.getInt(ITEM_ID),
	                rs.getString(ITEM_NAME),
	                rs.getString(CATEGORY),
	                rs.getString("size"),
	                rs.getString(COLOUR),
	                rs.getDouble(PRICE),
	                rs.getInt(QUANTITY),
	                supplier
	            );

	            organizedItems.add(item);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return organizedItems;
	}
	
	public List<Item> getItemVariations(String itemName) {
	    List<Item> variations = new ArrayList<>();
	    String sql = "SELECT i.*, s.name AS supplierName, s.contact AS supplierContact " +
	                 "FROM items i LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id " +
	                 "WHERE i.itemName = ? " +
	                 "ORDER BY size, colour";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setString(1, itemName);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                Supplier supplier = null;
	                int supplierId = rs.getInt(SUPPLIER_ID);
	                
	                if (!rs.wasNull()) {
	                    supplier = new Supplier(
	                        supplierId,
	                        rs.getString(SUPPLIER_NAME),
	                        rs.getString(SUPPLIER_CONTACT)
	                    );
	                }

	                Item item = new Item(
	                    rs.getInt(ITEM_ID),
	                    rs.getString(ITEM_NAME),
	                    rs.getString(CATEGORY),
	                    rs.getString("size"),
	                    rs.getString(COLOUR),
	                    rs.getDouble(PRICE),
	                    rs.getInt(QUANTITY),
	                    supplier
	                );

	                variations.add(item);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return variations;
	}
	
	public void addItem(Item item)
	{
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
	    
	    String sql = "INSERT INTO items (itemName, category, size, colour, price, quantity, supplier_id) " +
	                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

	    try (Connection conn = DatabaseConnection.getConnection();
	    		PreparedStatement stmt = conn.prepareStatement(sql))
	    {	        
	        
	        stmt.setString(1, item.getName());
	        stmt.setString(2, item.getCategory());
	        stmt.setString(3, item.getSize());
	        stmt.setString(4, item.getColour());
	        stmt.setDouble(5, item.getPrice());
	        stmt.setInt(6, item.getQuantity());

	        if (item.getSupplier() != null)
	        {
	            stmt.setInt(7, item.getSupplier().getSupplierId());
	        }
	        else
	        {
	            stmt.setNull(7, java.sql.Types.INTEGER);
	        }

	        stmt.executeUpdate();

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
		String selectSQL = "SELECT itemName, category, size, colour, price, quantity FROM items WHERE itemId = ?";
		String updateSQL = "UPDATE items SET itemName=?, category=?, size=?, colour=?, price=?, quantity=? WHERE itemId=?";
		
	    try (Connection conn = DatabaseConnection.getConnection();
	    		PreparedStatement updateStmt = conn.prepareStatement(updateSQL);
	    		PreparedStatement selectStmt = conn.prepareStatement(selectSQL))
	    {
	        selectStmt.setInt(1, itemId);
	        ResultSet rs = selectStmt.executeQuery();

	        if (!rs.next()) {
	            if (!testMode) JOptionPane.showMessageDialog(null, "⚠No item found with ID: " + itemId);
	            return;
	        }

	        String name, category, size, colour;
	        double price;
	        int quantity;

	        if (!testMode) {

	            JTextField nameField = new JTextField(rs.getString(ITEM_NAME), 15);
	            JTextField categoryField = new JTextField(rs.getString(CATEGORY), 15);
	            JTextField sizeField = new JTextField(rs.getString("size"), 10);
	            JTextField colourField = new JTextField(rs.getString(COLOUR), 10);
	            JTextField priceField = new JTextField(String.valueOf(rs.getDouble(PRICE)), 10);
	            JTextField quantityField = new JTextField(String.valueOf(rs.getInt(QUANTITY)), 10);

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

	            int result = JOptionPane.showConfirmDialog(
	                    null, panel,
	                    "Edit Item (ID: " + itemId + ")", JOptionPane.OK_CANCEL_OPTION,
	                    JOptionPane.PLAIN_MESSAGE);

	            if (result != JOptionPane.OK_OPTION) {
	                if (!testMode) JOptionPane.showMessageDialog(null, "Edit cancelled. No changes were made.");
	                rs.close();
	                return;
	            }

	            name = nameField.getText().trim();
	            category = categoryField.getText().trim();
	            size = sizeField.getText().trim();
	            colour = colourField.getText().trim();

	            try {
	                price = Double.parseDouble(priceField.getText().trim());
	                quantity = Integer.parseInt(quantityField.getText().trim());
	            } catch (NumberFormatException ex) {
	                if (!testMode) JOptionPane.showMessageDialog(null, "Invalid number format for price or quantity.");
	                rs.close();
	                return;
	            }

	        }
	        else {
	            name = rs.getString(ITEM_NAME).trim();
	            category = rs.getString(CATEGORY).trim();
	            size = rs.getString("size").trim();
	            colour = rs.getString(COLOUR).trim();
	            price = rs.getDouble(PRICE);
	            quantity = rs.getInt(QUANTITY);
	        }

	        if (name.isEmpty()) throw new IllegalArgumentException("Item name cannot be empty.");
	        if (category.isEmpty()) throw new IllegalArgumentException("Category cannot be empty.");
	        if (size.isEmpty()) throw new IllegalArgumentException("Size cannot be empty.");
	        if (colour.isEmpty()) throw new IllegalArgumentException("Colour cannot be empty.");
	        if (price < 0) throw new IllegalArgumentException("Price cannot be negative.");
	        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative.");

	        updateStmt.setString(1, name);
	        updateStmt.setString(2, category);
	        updateStmt.setString(3, size);
	        updateStmt.setString(4, colour);
	        updateStmt.setDouble(5, price);
	        updateStmt.setInt(6, quantity);
	        updateStmt.setInt(7, itemId);

	        int rows = updateStmt.executeUpdate();

	        if (!testMode) {
	            if (rows > 0) {
	                JOptionPane.showMessageDialog(null, "Item (ID: " + itemId + ") updated successfully!");
	            } else {
	                JOptionPane.showMessageDialog(null, "No changes were made (item may not exist).");
	            }
	        }

	        rs.close();
	    }
	    catch (SQLException e)
	    {
	        e.printStackTrace();
	        if (!testMode) JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
	    }
	}

	public List<Item> searchItem(String keyword) {
	    List<Item> searchResults = new ArrayList<>();
	    
	    if (keyword == null || keyword.trim().isEmpty()) {
	        if (!testMode) {
	            JOptionPane.showMessageDialog(null, "Please enter a keyword to search.");
	        }
	        return searchResults;
	    }
	    
	    String searchTerm = "%" + keyword.trim() + "%";
	    
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
	                int supplierId = rs.getInt(SUPPLIER_ID);
	                
	                if (!rs.wasNull()) {
	                    supplier = new Supplier(
	                        supplierId,
	                        rs.getString(SUPPLIER_NAME),
	                        rs.getString(SUPPLIER_CONTACT)
	                    );
	                }

	                Item item = new Item(
	                    rs.getInt(ITEM_ID),
	                    rs.getString(ITEM_NAME),
	                    rs.getString(CATEGORY),
	                    rs.getString("size"),
	                    rs.getString(COLOUR),
	                    rs.getDouble(PRICE),
	                    rs.getInt(QUANTITY),
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
	
	public boolean removeItem(int itemId) {
		String deleteSQL = "DELETE FROM items WHERE itemId = ?";
		
	    try (Connection conn = DatabaseConnection.getConnection();
	    		PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL)) {
	        deleteStmt.setInt(1, itemId);

	        int rowsAffected = deleteStmt.executeUpdate();

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