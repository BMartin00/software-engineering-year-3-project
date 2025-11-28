// A00316625 Nagasai Chintalapati
// A00318851 Martin  Banyanszki
// A00320456 Justas Zabitis
// A00317072 Hamza Hussain

package gigabytegurus.inventorymanagementsystem;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Dashboard {
	
    public User currentUser;
    public static final String CATEGORY = "Category";
    public static final String COLOUR = "Colour";
    public static final String PRICE_EURO = "Price (€)";
    public static final String QUANTITY = "Quantity";
    public static final String SUPPLIER = "Supplier";
    public static final String SANSSERIF_FONT = "SansSerif";
    public static final String PROCESS_RETURN = "Process Return";
    public static final String RECORD_SALE = "Record Sale";
    public static final String INVALID_ITEM_ID = "Invalid item ID.";
    public static final String INVALID_NUMBER = "Invalid number entered. Please try again.";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_COLOUR = "colour";
    public static final String FIELD_ITEM_ID = "itemId";
    public static final String FIELD_ITEM_NAME = "itemName";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_QUANTITY = "quantity";
    public static final String FIELD_SUPPLIER_CONTACT = "supplierContact";
    public static final String FIELD_SUPPLIER_NAME = "supplierName";
    public static final String FIELD_SUPPLIER_ID = "supplier_id";
    
    // Add test mode flag to prevent GUI popups during testing
    public static boolean testMode = false;

    JFrame window = new JFrame("Clothing Inventory Management System");
    JPanel panel = new JPanel(new GridBagLayout());
    JLabel username = new JLabel("Username:");
    JTextField usernameInput = new JTextField(15);
    JLabel password = new JLabel("Password:");
    JPasswordField passwordInput = new JPasswordField(15);
    JButton registerButton = new JButton("Register");
    JButton loginButton = new JButton("Login");

    public Dashboard() {
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.insets = new Insets(15, 15, 15, 15);
        constraint.fill = GridBagConstraints.HORIZONTAL;

        usernameInput.setPreferredSize(new Dimension(250, 35));
        passwordInput.setPreferredSize(new Dimension(250, 35));
        registerButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setPreferredSize(new Dimension(120, 40));

        // Username row
        constraint.gridx = 0;
        constraint.gridy = 0;
        panel.add(username, constraint);

        constraint.gridx = 1;
        panel.add(usernameInput, constraint);

        // Password row
        constraint.gridx = 0;
        constraint.gridy = 1;
        panel.add(password, constraint);

        constraint.gridx = 1;
        panel.add(passwordInput, constraint);

        // Buttons row
        constraint.gridx = 0;
        constraint.gridy = 2;
        constraint.gridwidth = 2;
        constraint.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(loginButton);
        panel.add(buttonPanel, constraint);

        // Action listeners
        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());

        // Setup window
        if (!testMode) {
            window.add(panel);
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setVisible(true);
        }
    }

    public void handleLogin() {
        String username = usernameInput.getText().trim();
        String password = new String(passwordInput.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            if (!testMode) {
                JOptionPane.showMessageDialog(window, "Please enter username and password");
            }
            return;
        }

        String query = "SELECT user_id, username, password, role FROM users WHERE username=? AND password=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentUser = new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                );
                if (!testMode) {
                    JOptionPane.showMessageDialog(window, "Login Successful!");
                    openInventoryWindow();
                }
            } else {
                if (!testMode) {
                    JOptionPane.showMessageDialog(window, "Invalid username or password");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            if (!testMode) {
                JOptionPane.showMessageDialog(window, "Database error: " + ex.getMessage());
            }
        }
    }

    public void handleRegister() {
        String username = usernameInput.getText().trim();
        String password = new String(passwordInput.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            if (!testMode) {
                JOptionPane.showMessageDialog(window, "Please fill in both username and password.");
            }
            return;
        }

        String checkQuery = "SELECT username FROM users WHERE username=?";
        String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {

            // Check if username already exists
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                if (!testMode) {
                    JOptionPane.showMessageDialog(window, "Username already exists. Choose another one.");
                }
                return;
            }

            // Insert new user
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, "user");
            int rows = insertStmt.executeUpdate();

            if (rows > 0) {
                if (!testMode) {
                    JOptionPane.showMessageDialog(window, "Registration successful! You can now log in.");
                    usernameInput.setText("");
                    passwordInput.setText("");
                }
            } else {
                if (!testMode) {
                    JOptionPane.showMessageDialog(window, "Registration failed. Try again.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            if (!testMode) {
                JOptionPane.showMessageDialog(window, "Database error: " + ex.getMessage());
            }
        }
    }

    public void openInventoryWindow() {
        JFrame inventoryWindow = new JFrame("Inventory Dashboard");
        inventoryWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
        inventoryWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        List<Item> items = loadItemsFromDatabase();
        String[] columns = {"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER};
        Object[][] data = new Object[items.size()][columns.length];
        
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            data[i][0] = item.getItemId();
            data[i][1] = item.getName();
            data[i][2] = item.getCategory();
            data[i][3] = item.getSize();
            data[i][4] = item.getColour();
            data[i][5] = item.getQuantity();
            data[i][6] = item.getPrice();
            data[i][7] = item.getSupplier() != null ? item.getSupplier().getName() : "";
        }
        
        JTable table = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 16));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font(SANSSERIF_FONT, Font.BOLD, 18));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(900, 700));
        
        JLabel title = new JLabel("Inventory List", SwingConstants.CENTER);
        title.setFont(new Font(SANSSERIF_FONT, Font.BOLD, 28));

        // Create buttons (same as before)
        JButton backButton = new JButton("Back to Login");
        backButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));
        backButton.addActionListener(e -> {
            inventoryWindow.dispose();
            window.setVisible(true);
        });

        JButton addItemButton = new JButton("Add New Item");
        addItemButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));

        JButton editItemButton = new JButton("Edit Item");
        editItemButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));

        JButton deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));

        JButton searchItemButton = new JButton("Search Item");
        searchItemButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));

        JButton filterButton = new JButton("Filter Items");
        filterButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));

        JButton organizeButton = new JButton("Organize Items");
        organizeButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));

        JButton recordSaleButton = new JButton(RECORD_SALE);
        recordSaleButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));

        JButton processReturnButton = new JButton(PROCESS_RETURN);
        processReturnButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));

        JButton processExchangeButton = new JButton("Process Exchange");
        processExchangeButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));

        JButton viewSalesHistoryButton = new JButton("View Sales History");
        viewSalesHistoryButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 16));

        JButton viewReturnHistoryButton = new JButton("View Return History");
        viewReturnHistoryButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 16));

        JButton viewExchangeHistoryButton = new JButton("View Exchange History");
        viewExchangeHistoryButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 16));

        JButton viewSalesSummaryButton = new JButton("Sales Summary");
        viewSalesSummaryButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 16));

        JButton pdfButton = new JButton("Export PDF Report");
        pdfButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));

        JButton excelButton = new JButton("Export Excel Report");
        excelButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));

        JButton supplierButton = new JButton("Manage Suppliers");
        supplierButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));

        JButton linkSupplierButton = new JButton("Link Item to Supplier");
        linkSupplierButton.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 18));

        // === TOOLBAR LAYOUT IMPLEMENTATION ===
        JPanel mainButtonPanel = new JPanel(new BorderLayout());

        // Top Toolbar - Core functions
        JToolBar coreToolbar = new JToolBar();
        coreToolbar.setFloatable(false);
        coreToolbar.add(addItemButton);
        coreToolbar.add(editItemButton);
        coreToolbar.add(deleteButton);
        coreToolbar.addSeparator();
        coreToolbar.add(searchItemButton);
        coreToolbar.add(filterButton);
        coreToolbar.add(organizeButton);

        // Middle Toolbar - Sales
        JToolBar salesToolbar = new JToolBar();
        salesToolbar.setFloatable(false);
        salesToolbar.add(recordSaleButton);
        salesToolbar.add(processReturnButton);
        salesToolbar.add(processExchangeButton);

        // Bottom Toolbar - Reports & Navigation
        JToolBar reportToolbar = new JToolBar();
        reportToolbar.setFloatable(false);
        reportToolbar.add(viewSalesHistoryButton);
        reportToolbar.add(viewReturnHistoryButton);
        reportToolbar.add(viewExchangeHistoryButton);
        reportToolbar.add(viewSalesSummaryButton);
        reportToolbar.addSeparator();
        reportToolbar.add(pdfButton);
        reportToolbar.add(excelButton);
        reportToolbar.add(supplierButton);
        reportToolbar.add(linkSupplierButton);
        reportToolbar.add(Box.createHorizontalGlue());
        reportToolbar.add(backButton);

        mainButtonPanel.add(coreToolbar, BorderLayout.NORTH);
        mainButtonPanel.add(salesToolbar, BorderLayout.CENTER);
        mainButtonPanel.add(reportToolbar, BorderLayout.SOUTH);

        // Center panel with table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        // Top panel with title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(title, BorderLayout.CENTER);

        // Main layout
        inventoryWindow.add(topPanel, BorderLayout.NORTH);
        inventoryWindow.add(centerPanel, BorderLayout.CENTER);
        inventoryWindow.add(mainButtonPanel, BorderLayout.SOUTH);

        // === BUTTON ACTION LISTENERS (unchanged from your original code) ===
        
        // Add Item Button
        addItemButton.addActionListener(e -> {
            JTextField nameField = new JTextField(15);
            JTextField categoryField = new JTextField(15);
            JTextField sizeField = new JTextField(10);
            JTextField colourField = new JTextField(10);
            JTextField priceField = new JTextField(10);
            JTextField quantityField = new JTextField(10);

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
                inventoryWindow, panel, "Add New Item", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                try {
                    String name = nameField.getText().trim();
                    String category = categoryField.getText().trim();
                    String size = sizeField.getText().trim();
                    String colour = colourField.getText().trim();
                    double price = Double.parseDouble(priceField.getText().trim());
                    int quantity = Integer.parseInt(quantityField.getText().trim());

                    Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");
                    Item newItem = new Item(0, name, category, size, colour, price, quantity, supplier);

                    Inventory inventory = new Inventory();
                    inventory.addItem(newItem);

                    JOptionPane.showMessageDialog(inventoryWindow, "Item added successfully!\nName: " + name, "Success", JOptionPane.INFORMATION_MESSAGE);

                    List<Item> updatedItems = loadItemsFromDatabase();
                    DefaultTableModel model = new DefaultTableModel(
                        new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                    for (Item item : updatedItems) {
                        model.addRow(new Object[]{
                            item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                            item.getColour(), item.getQuantity(), item.getPrice(),
                            item.getSupplier() != null ? item.getSupplier().getName() : ""
                        });
                    }
                    table.setModel(model);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(inventoryWindow, "Error adding item: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Edit Item Button
        editItemButton.addActionListener(e -> {
            try {
                String idInput = JOptionPane.showInputDialog(inventoryWindow, "Enter the ID of the item you want to edit:");
                if (idInput == null || idInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No ID entered. Edit cancelled.");
                    return;
                }
                int itemId = Integer.parseInt(idInput.trim());
                Inventory inventory = new Inventory();
                inventory.updateItem(itemId);

                List<Item> updatedItems = loadItemsFromDatabase();
                DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                for (Item item : updatedItems) {
                    model.addRow(new Object[]{
                        item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                        item.getColour(), item.getQuantity(), item.getPrice(),
                        item.getSupplier() != null ? item.getSupplier().getName() : ""
                    });
                }
                table.setModel(model);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inventoryWindow, "Invalid ID format. Please enter a number.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error updating item: " + ex.getMessage());
            }
        });

        // Search Item Button
        searchItemButton.addActionListener(e -> {
            try {
                String keyword = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter keyword to search (name, category, size, colour, supplier):",
                    "Search Item", JOptionPane.QUESTION_MESSAGE);
                if (keyword == null || keyword.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No keyword entered. Search cancelled.");
                    return;
                }
                Inventory inventory = new Inventory();
                List<Item> searchResults = inventory.searchItem(keyword);

                DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                for (Item item : searchResults) {
                    model.addRow(new Object[]{
                        item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                        item.getColour(), item.getQuantity(), item.getPrice(),
                        item.getSupplier() != null ? item.getSupplier().getName() : ""
                    });
                }
                table.setModel(model);
                highlightLowStock(table);
                if (!searchResults.isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "Found " + searchResults.size() + " item(s) matching: " + keyword, "Search Results", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error searching items: " + ex.getMessage());
            }
        });

        // Delete Button
        deleteButton.addActionListener(e -> {
            try {
                String idInput = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter the ID of the item you want to delete:",
                    "Delete Item", JOptionPane.QUESTION_MESSAGE);
                if (idInput == null || idInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No ID entered. Deletion cancelled.");
                    return;
                }
                int itemId = Integer.parseInt(idInput.trim());
                Inventory inventory = new Inventory();
                inventory.removeItem(itemId);

                List<Item> updatedItems = loadItemsFromDatabase();
                DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                for (Item item : updatedItems) {
                    model.addRow(new Object[]{
                        item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                        item.getColour(), item.getQuantity(), item.getPrice(),
                        item.getSupplier() != null ? item.getSupplier().getName() : ""
                    });
                }
                table.setModel(model);
                highlightLowStock(table);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inventoryWindow, "Invalid ID format. Please enter a valid number.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error deleting item: " + ex.getMessage());
            }
        });

        // Record Sale Button
        recordSaleButton.addActionListener(e -> {
            try {
                String idInput = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter the ID of the item sold:", RECORD_SALE, JOptionPane.QUESTION_MESSAGE);
                if (idInput == null || idInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No ID entered. Operation cancelled.");
                    return;
                }
                int itemId = Integer.parseInt(idInput.trim());
                String qtyInput = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter quantity sold:", RECORD_SALE, JOptionPane.QUESTION_MESSAGE);
                if (qtyInput == null || qtyInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No quantity entered. Operation cancelled.");
                    return;
                }
                int quantitySold = Integer.parseInt(qtyInput.trim());

                boolean success = Transaction.recordSale(itemId, quantitySold);
                if (success) {
                    List<Item> updatedItems = loadItemsFromDatabase();
                    DefaultTableModel model = new DefaultTableModel(
                        new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                    for (Item item : updatedItems) {
                        model.addRow(new Object[]{
                            item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                            item.getColour(), item.getQuantity(), item.getPrice(),
                            item.getSupplier() != null ? item.getSupplier().getName() : ""
                        });
                    }
                    table.setModel(model);
                    highlightLowStock(table);
                } else {
                    JOptionPane.showMessageDialog(inventoryWindow, "Sale could not be recorded. Check item ID or stock quantity.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inventoryWindow, INVALID_NUMBER);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error recording sale: " + ex.getMessage());
            }
        });

        // Process Return Button
        processReturnButton.addActionListener(e -> {
            try {
                String idInput = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter the ID of the item being returned:", PROCESS_RETURN, JOptionPane.QUESTION_MESSAGE);
                if (idInput == null || idInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No ID entered. Operation cancelled.");
                    return;
                }
                int itemId = Integer.parseInt(idInput.trim());
                String qtyInput = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter quantity being returned:", PROCESS_RETURN, JOptionPane.QUESTION_MESSAGE);
                if (qtyInput == null || qtyInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No quantity entered. Operation cancelled.");
                    return;
                }
                int quantityReturned = Integer.parseInt(qtyInput.trim());
                String reason = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter reason for return:", "Process Return - Reason", JOptionPane.QUESTION_MESSAGE);
                if (reason == null || reason.trim().isEmpty()) {
                    reason = "No reason provided";
                }

                boolean success = Transaction.processReturn(itemId, quantityReturned, reason.trim());
                if (success) {
                    List<Item> updatedItems = loadItemsFromDatabase();
                    DefaultTableModel model = new DefaultTableModel(
                        new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                    for (Item item : updatedItems) {
                        model.addRow(new Object[]{
                            item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                            item.getColour(), item.getQuantity(), item.getPrice(),
                            item.getSupplier() != null ? item.getSupplier().getName() : ""
                        });
                    }
                    table.setModel(model);
                    highlightLowStock(table);
                    JOptionPane.showMessageDialog(inventoryWindow, "Return processed successfully!\n" + quantityReturned + " items added back to inventory.");
                } else {
                    JOptionPane.showMessageDialog(inventoryWindow, "Return could not be processed. Please check the item ID and try again.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inventoryWindow, INVALID_NUMBER);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error processing return: " + ex.getMessage());
            }
        });

        // Process Exchange Button
        processExchangeButton.addActionListener(e -> {
            try {
                String returnIdInput = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter the ID of the item being returned:", "Process Exchange - Return Item", JOptionPane.QUESTION_MESSAGE);
                if (returnIdInput == null || returnIdInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No return ID entered. Operation cancelled.");
                    return;
                }
                int returnedItemId = Integer.parseInt(returnIdInput.trim());
                String returnQtyInput = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter quantity being returned:", "Process Exchange - Return Quantity", JOptionPane.QUESTION_MESSAGE);
                if (returnQtyInput == null || returnQtyInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No return quantity entered. Operation cancelled.");
                    return;
                }
                int returnedQuantity = Integer.parseInt(returnQtyInput.trim());
                String exchangeIdInput = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter the ID of the new item for exchange:", "Process Exchange - New Item", JOptionPane.QUESTION_MESSAGE);
                if (exchangeIdInput == null || exchangeIdInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No exchange ID entered. Operation cancelled.");
                    return;
                }
                int newItemId = Integer.parseInt(exchangeIdInput.trim());
                String exchangeQtyInput = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter quantity for new item:", "Process Exchange - New Quantity", JOptionPane.QUESTION_MESSAGE);
                if (exchangeQtyInput == null || exchangeQtyInput.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No exchange quantity entered. Operation cancelled.");
                    return;
                }
                int newItemQuantity = Integer.parseInt(exchangeQtyInput.trim());
                String reason = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter reason for exchange:", "Process Exchange - Reason", JOptionPane.QUESTION_MESSAGE);
                if (reason == null || reason.trim().isEmpty()) {
                    reason = "No reason provided";
                }

                boolean success = Transaction.processExchange(returnedItemId, returnedQuantity, newItemId, newItemQuantity, reason.trim());
                if (success) {
                    List<Item> updatedItems = loadItemsFromDatabase();
                    DefaultTableModel model = new DefaultTableModel(
                        new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                    for (Item item : updatedItems) {
                        model.addRow(new Object[]{
                            item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                            item.getColour(), item.getQuantity(), item.getPrice(),
                            item.getSupplier() != null ? item.getSupplier().getName() : ""
                        });
                    }
                    table.setModel(model);
                    highlightLowStock(table);
                    JOptionPane.showMessageDialog(inventoryWindow, "Exchange processed successfully!\n" + returnedQuantity + " items returned and " + newItemQuantity + " new items issued.");
                } else {
                    JOptionPane.showMessageDialog(inventoryWindow, "Exchange could not be processed. Please check item IDs and stock availability.");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inventoryWindow, INVALID_NUMBER);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error processing exchange: " + ex.getMessage());
            }
        });

        // View Return History Button
        viewReturnHistoryButton.addActionListener(e -> {
            try {
                String idInput = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter item ID to view return history:", "View Return History", JOptionPane.QUESTION_MESSAGE);
                if (idInput == null || idInput.trim().isEmpty()) {
                    return;
                }
                int itemId = Integer.parseInt(idInput.trim());
                String history = Transaction.getReturnHistory(itemId);
                JTextArea textArea = new JTextArea(20, 50);
                textArea.setText(history);
                textArea.setEditable(false);
                JScrollPane returnHistoryScrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(inventoryWindow, returnHistoryScrollPane, "Return History for Item ID: " + itemId, JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inventoryWindow, INVALID_ITEM_ID);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error retrieving history: " + ex.getMessage());
            }
        });

        // View Exchange History Button
        viewExchangeHistoryButton.addActionListener(e -> {
            try {
                String idInput = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter item ID to view exchange history:", "View Exchange History", JOptionPane.QUESTION_MESSAGE);
                if (idInput == null || idInput.trim().isEmpty()) {
                    return;
                }
                int itemId = Integer.parseInt(idInput.trim());
                String history = Transaction.getExchangeHistory(itemId);
                JTextArea textArea = new JTextArea(20, 50);
                textArea.setText(history);
                textArea.setEditable(false);
                JScrollPane exchangeHistoryScrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(inventoryWindow, exchangeHistoryScrollPane, "Exchange History for Item ID: " + itemId, JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inventoryWindow, INVALID_ITEM_ID);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error retrieving history: " + ex.getMessage());
            }
        });

        // View Sales History Button
        viewSalesHistoryButton.addActionListener(e -> {
            try {
                String idInput = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter item ID to view sales history:", "View Sales History", JOptionPane.QUESTION_MESSAGE);
                if (idInput == null || idInput.trim().isEmpty()) {
                    return;
                }
                int itemId = Integer.parseInt(idInput.trim());
                String history = Transaction.getSalesHistory(itemId);
                JTextArea textArea = new JTextArea(20, 60);
                textArea.setText(history);
                textArea.setEditable(false);
                JScrollPane salesHistoryScrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(inventoryWindow, salesHistoryScrollPane, "Sales History for Item ID: " + itemId, JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inventoryWindow, INVALID_ITEM_ID);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error retrieving sales history: " + ex.getMessage());
            }
        });

        // View Sales Summary Button
        viewSalesSummaryButton.addActionListener(e -> {
            try {
                String summary = Transaction.getSalesSummary();
                JTextArea textArea = new JTextArea(20, 60);
                textArea.setText(summary);
                textArea.setEditable(false);
                JScrollPane salesSummaryScrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(inventoryWindow, salesSummaryScrollPane, "Overall Sales Summary", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error retrieving sales summary: " + ex.getMessage());
            }
        });

        // Filter Button
        filterButton.addActionListener(e -> {
            try {
                JTextField categoryField = new JTextField(10);
                JTextField sizeField = new JTextField(10);
                JTextField supplierField = new JTextField(10);
                JPanel filterPanel = new JPanel(new GridLayout(0, 2, 10, 10));
                filterPanel.add(new JLabel("Category:"));
                filterPanel.add(categoryField);
                filterPanel.add(new JLabel("Size:"));
                filterPanel.add(sizeField);
                filterPanel.add(new JLabel("Supplier:"));
                filterPanel.add(supplierField);

                int result = JOptionPane.showConfirmDialog(
                    inventoryWindow, filterPanel, "Filter Items", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    String category = categoryField.getText().trim();
                    String size = sizeField.getText().trim();
                    String supplier = supplierField.getText().trim();
                    List<Item> filteredItems = new ArrayList<>();
                    try (Connection conn = DatabaseConnection.getConnection()) {
                        StringBuilder query = new StringBuilder(
                            "SELECT i.*, s.name AS supplierName, s.contact AS supplierContact " +
                            "FROM items i LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id WHERE 1=1");
                        if (!category.isEmpty()) query.append(" AND i.category LIKE ?");
                        if (!size.isEmpty()) query.append(" AND i.size LIKE ?");
                        if (!supplier.isEmpty()) query.append(" AND s.name LIKE ?");
                        PreparedStatement stmt = conn.prepareStatement(query.toString());
                        int index = 1;
                        if (!category.isEmpty()) stmt.setString(index++, "%" + category + "%");
                        if (!size.isEmpty()) stmt.setString(index++, "%" + size + "%");
                        if (!supplier.isEmpty()) stmt.setString(index++, "%" + supplier + "%");
                        ResultSet rs = stmt.executeQuery();
                        while (rs.next()) {
                            Supplier sup = null;
                            if (rs.getInt(FIELD_SUPPLIER_ID) != 0) {
                                sup = new Supplier(
                                    rs.getInt(FIELD_SUPPLIER_ID),
                                    rs.getString(FIELD_SUPPLIER_NAME),
                                    rs.getString(FIELD_SUPPLIER_CONTACT)
                                );
                            }
                            filteredItems.add(new Item(
                                rs.getInt(FIELD_ITEM_ID),
                                rs.getString(FIELD_ITEM_NAME),
                                rs.getString(FIELD_CATEGORY),
                                rs.getString("size"),
                                rs.getString(FIELD_COLOUR),
                                rs.getDouble(FIELD_PRICE),
                                rs.getInt(FIELD_QUANTITY),
                                sup
                            ));
                        }
                    }
                    DefaultTableModel model = new DefaultTableModel(
                        new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                    for (Item item : filteredItems) {
                        model.addRow(new Object[]{
                            item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                            item.getColour(), item.getQuantity(), item.getPrice(),
                            item.getSupplier() != null ? item.getSupplier().getName() : ""
                        });
                    }
                    table.setModel(model);
                    highlightLowStock(table);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error filtering items: " + ex.getMessage());
            }
        });

        // Organize Button with Popup Menu
        JPopupMenu organizeMenu = new JPopupMenu();
        JMenuItem byCategoryItem = new JMenuItem("By Category");
        JMenuItem bySizeItem = new JMenuItem("By Size");
        JMenuItem byColourItem = new JMenuItem("By Colour");
        JMenuItem byPriceItem = new JMenuItem("By Price");
        JMenuItem bySupplierItem = new JMenuItem("View by Supplier");
        organizeMenu.add(byCategoryItem);
        organizeMenu.add(bySizeItem);
        organizeMenu.add(byColourItem);
        organizeMenu.add(byPriceItem);
        organizeMenu.add(bySupplierItem);

        organizeButton.addActionListener(e -> {
            organizeMenu.show(organizeButton, 0, organizeButton.getHeight());
        });

        // Organize menu actions
        byCategoryItem.addActionListener(e -> {
            try {
                Inventory inventory = new Inventory();
                List<Item> organizedItems = inventory.organizeByCategory();
                DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                for (Item item : organizedItems) {
                    model.addRow(new Object[]{
                        item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                        item.getColour(), item.getQuantity(), item.getPrice(),
                        item.getSupplier() != null ? item.getSupplier().getName() : ""
                    });
                }
                table.setModel(model);
                highlightLowStock(table);
                JOptionPane.showMessageDialog(inventoryWindow, "Items organized by category!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error organizing by category: " + ex.getMessage());
            }
        });

        bySizeItem.addActionListener(e -> {
            try {
                Inventory inventory = new Inventory();
                List<Item> organizedItems = inventory.organizeBySize();
                DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                for (Item item : organizedItems) {
                    model.addRow(new Object[]{
                        item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                        item.getColour(), item.getQuantity(), item.getPrice(),
                        item.getSupplier() != null ? item.getSupplier().getName() : ""
                    });
                }
                table.setModel(model);
                highlightLowStock(table);
                JOptionPane.showMessageDialog(inventoryWindow, "Items organized by size!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error organizing by size: " + ex.getMessage());
            }
        });

        byColourItem.addActionListener(e -> {
            try {
                Inventory inventory = new Inventory();
                List<Item> organizedItems = inventory.organizeByColour();
                DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                for (Item item : organizedItems) {
                    model.addRow(new Object[]{
                        item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                        item.getColour(), item.getQuantity(), item.getPrice(),
                        item.getSupplier() != null ? item.getSupplier().getName() : ""
                    });
                }
                table.setModel(model);
                highlightLowStock(table);
                JOptionPane.showMessageDialog(inventoryWindow, "Items organized by colour!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error organizing by colour: " + ex.getMessage());
            }
        });

        byPriceItem.addActionListener(e -> {
            try {
                Inventory inventory = new Inventory();
                List<Item> organizedItems = inventory.organizeByPrice();
                DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                for (Item item : organizedItems) {
                    model.addRow(new Object[]{
                        item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                        item.getColour(), item.getQuantity(), item.getPrice(),
                        item.getSupplier() != null ? item.getSupplier().getName() : ""
                    });
                }
                table.setModel(model);
                highlightLowStock(table);
                JOptionPane.showMessageDialog(inventoryWindow, "Items organized by price!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error organizing by price: " + ex.getMessage());
            }
        });

        bySupplierItem.addActionListener(e -> {
            try {
                String supplierName = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter supplier name to view items:", "View by Supplier", JOptionPane.QUESTION_MESSAGE);
                if (supplierName == null || supplierName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No supplier name entered.");
                    return;
                }
                Inventory inventory = new Inventory();
                List<Item> supplierItems = new ArrayList<>();
                String sql = "SELECT i.*, s.name AS supplierName, s.contact AS supplierContact " +
                           "FROM items i LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id " +
                           "WHERE s.name LIKE ? " + "ORDER BY i.itemName";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, "%" + supplierName.trim() + "%");
                    try (ResultSet rs = stmt.executeQuery()) {
                        while (rs.next()) {
                            Supplier supplier = null;
                            int supplierId = rs.getInt(FIELD_SUPPLIER_ID);
                            if (!rs.wasNull()) {
                                supplier = new Supplier(
                                    supplierId,
                                    rs.getString(FIELD_SUPPLIER_NAME),
                                    rs.getString(FIELD_SUPPLIER_CONTACT)
                                );
                            }
                            Item item = new Item(
                                rs.getInt(FIELD_ITEM_ID),
                                rs.getString(FIELD_ITEM_NAME),
                                rs.getString(FIELD_CATEGORY),
                                rs.getString("size"),
                                rs.getString(FIELD_COLOUR),
                                rs.getDouble(FIELD_PRICE),
                                rs.getInt(FIELD_QUANTITY),
                                supplier
                            );
                            supplierItems.add(item);
                        }
                    }
                }
                if (supplierItems.isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No items found for supplier: " + supplierName + "\nOr supplier does not exist.", "No Items Found", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                for (Item item : supplierItems) {
                    model.addRow(new Object[]{
                        item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                        item.getColour(), item.getQuantity(), item.getPrice(),
                        item.getSupplier() != null ? item.getSupplier().getName() : ""
                    });
                }
                table.setModel(model);
                highlightLowStock(table);
                JOptionPane.showMessageDialog(inventoryWindow, "Found " + supplierItems.size() + " item(s) from supplier: " + supplierName);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error viewing supplier items: " + ex.getMessage());
            }
        });

        // PDF Export Button
        pdfButton.addActionListener(e -> {
            try {
                List<Item> itemsForReport = loadItemsFromDatabase();
                Report report = new Report();
                String filePath = "Inventory_Report.pdf";
                report.generatePDF(itemsForReport, filePath);
                JOptionPane.showMessageDialog(inventoryWindow, "PDF Report generated successfully!\nSaved to: " + filePath);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error generating PDF: " + ex.getMessage());
            }
        });

        // Excel Export Button
        excelButton.addActionListener(e -> {
            try {
                List<Item> itemsForReport = loadItemsFromDatabase();
                Report report = new Report();
                String filePath = "Inventory_Report.xlsx";
                report.generateExcel(itemsForReport, filePath);
                JOptionPane.showMessageDialog(inventoryWindow, "Excel Report generated successfully!\nSaved to: " + filePath);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error generating Excel file: " + ex.getMessage());
            }
        });

        // Supplier Management Button
        supplierButton.addActionListener(e -> {
            JFrame supplierFrame = new JFrame("Supplier Management");
            supplierFrame.setSize(600, 400);
            supplierFrame.setLocationRelativeTo(inventoryWindow);
            JPanel supplierPanel = new JPanel(new BorderLayout());
            Inventory inventory = new Inventory();
            List<Supplier> suppliers = inventory.getAllSuppliers();
            String[] supplierColumns = {"ID", "Name", "Contact"};
            DefaultTableModel supplierModel = new DefaultTableModel(supplierColumns, 0);
            for (Supplier supplier : suppliers) {
                supplierModel.addRow(new Object[]{
                    supplier.getSupplierId(), supplier.getName(), supplier.getContact()
                });
            }
            JTable supplierTable = new JTable(supplierModel);
            supplierTable.setFont(new Font(SANSSERIF_FONT, Font.PLAIN, 14));
            supplierTable.setRowHeight(25);
            JScrollPane supplierScrollPane = new JScrollPane(supplierTable);
            JPanel addSupplierPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            JTextField supplierNameField = new JTextField(20);
            JTextField supplierContactField = new JTextField(20);
            addSupplierPanel.add(new JLabel("Supplier Name:"));
            addSupplierPanel.add(supplierNameField);
            addSupplierPanel.add(new JLabel("Contact Info:"));
            addSupplierPanel.add(supplierContactField);
            JButton addSupplierBtn = new JButton("Add New Supplier");
            addSupplierPanel.add(addSupplierBtn);
            addSupplierBtn.addActionListener(ev -> {
                String name = supplierNameField.getText().trim();
                String contact = supplierContactField.getText().trim();
                if (name.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(supplierFrame, "Please fill in all supplier fields.");
                    return;
                }
                try {
                    Supplier newSupplier = new Supplier(0, name, contact);
                    inventory.addSupplier(newSupplier);
                    List<Supplier> updatedSuppliers = inventory.getAllSuppliers();
                    DefaultTableModel model = new DefaultTableModel(supplierColumns, 0);
                    for (Supplier supplier : updatedSuppliers) {
                        model.addRow(new Object[]{
                            supplier.getSupplierId(), supplier.getName(), supplier.getContact()
                        });
                    }
                    supplierTable.setModel(model);
                    supplierNameField.setText("");
                    supplierContactField.setText("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(supplierFrame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            supplierPanel.add(supplierScrollPane, BorderLayout.CENTER);
            supplierPanel.add(addSupplierPanel, BorderLayout.SOUTH);
            supplierFrame.add(supplierPanel);
            supplierFrame.setVisible(true);
        });

        // Link Supplier Button
        linkSupplierButton.addActionListener(e -> {
            try {
                String itemIdInput = JOptionPane.showInputDialog(
                    inventoryWindow, "Enter the ID of the item you want to link to a supplier:",
                    "Link Item to Supplier", JOptionPane.QUESTION_MESSAGE);
                if (itemIdInput == null || itemIdInput.trim().isEmpty()) {
                    return;
                }
                int itemId = Integer.parseInt(itemIdInput.trim());
                Inventory inventory = new Inventory();
                List<Supplier> suppliers = inventory.getAllSuppliers();
                if (suppliers.isEmpty()) {
                    JOptionPane.showMessageDialog(inventoryWindow, "No suppliers available. Please add suppliers first.");
                    return;
                }
                String[] supplierNames = new String[suppliers.size()];
                for (int i = 0; i < suppliers.size(); i++) {
                    supplierNames[i] = suppliers.get(i).getSupplierId() + " - " + suppliers.get(i).getName();
                }
                String selectedSupplier = (String) JOptionPane.showInputDialog(
                    inventoryWindow, "Select a supplier for item ID " + itemId + ":", "Select Supplier",
                    JOptionPane.QUESTION_MESSAGE, null, supplierNames, supplierNames[0]);
                if (selectedSupplier != null) {
                    int supplierId = Integer.parseInt(selectedSupplier.split(" - ")[0]);
                    inventory.updateItemSupplier(itemId, supplierId);
                    JOptionPane.showMessageDialog(inventoryWindow, "Item successfully linked to supplier!");
                    List<Item> updatedItems = loadItemsFromDatabase();
                    DefaultTableModel model = new DefaultTableModel(
                        new String[]{"ID", "Name", CATEGORY, "Size", COLOUR, QUANTITY, PRICE_EURO, SUPPLIER}, 0);
                    for (Item item : updatedItems) {
                        model.addRow(new Object[]{
                            item.getItemId(), item.getName(), item.getCategory(), item.getSize(),
                            item.getColour(), item.getQuantity(), item.getPrice(),
                            item.getSupplier() != null ? item.getSupplier().getName() : ""
                        });
                    }
                    table.setModel(model);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(inventoryWindow, "Invalid ID format. Please enter a valid number.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(inventoryWindow, "Error linking item to supplier: " + ex.getMessage());
            }
        });

        // Hide the login window and show the inventory dashboard
        window.setVisible(false);
        inventoryWindow.setVisible(true);

        // Highlight low stock when the window opens
        highlightLowStock(table);
    }

    private void highlightLowStock(JTable table) {
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, column);
                int quantityColumn = 5;
                try {
                    int qty = Integer.parseInt(tbl.getValueAt(row, quantityColumn).toString());
                    if (qty < 20) {
                        c.setBackground(new Color(255, 102, 102));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                } catch (Exception e) {
                    c.setBackground(Color.WHITE);
                }
                if (isSelected) {
                    c.setBackground(new Color(184, 207, 229));
                }
                return c;
            }
        });
    }

    public List<Item> loadItemsFromDatabase() {
        List<Item> items = new ArrayList<>();
        String query = "SELECT i.*, s.name AS supplierName, s.contact AS supplierContact " +
                     "FROM items i LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Supplier supplier = null;
                if (rs.getInt(FIELD_SUPPLIER_ID) != 0) {
                    supplier = new Supplier(rs.getInt(FIELD_SUPPLIER_ID), 
                                          rs.getString(FIELD_SUPPLIER_NAME), 
                                          rs.getString(FIELD_SUPPLIER_CONTACT));
                }
                items.add(new Item(
                    rs.getInt(FIELD_ITEM_ID),
                    rs.getString(FIELD_ITEM_NAME),
                    rs.getString(FIELD_CATEGORY),
                    rs.getString("size"),
                    rs.getString(FIELD_COLOUR),
                    rs.getDouble(FIELD_PRICE),
                    rs.getInt(FIELD_QUANTITY),
                    supplier
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(window, "Error loading items: " + ex.getMessage());
        }
        return items;
    }

    public static void main(String[] args) {
        new Dashboard();
    }
}