package gigabytegurus.inventorymanagementsystem;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Dashboard
{
    public User currentUser;

    JFrame window = new JFrame("Clothing Inventory Management System");
    JPanel panel = new JPanel(new GridBagLayout());

    JLabel username = new JLabel("Username:");
    JTextField usernameInput = new JTextField(15);
    JLabel password = new JLabel("Password:");
    JPasswordField passwordInput = new JPasswordField(15);

    JButton registerButton = new JButton("Register");
    JButton loginButton = new JButton("Login");

    public Dashboard()
    {
        GridBagConstraints constraint = new GridBagConstraints();
        constraint.insets = new Insets(15, 15, 15, 15);
        constraint.fill = GridBagConstraints.HORIZONTAL;

        usernameInput.setPreferredSize(new Dimension(250, 35));
        passwordInput.setPreferredSize(new Dimension(250, 35));
        registerButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setPreferredSize(new Dimension(120, 40));

        // Username row
        constraint.gridx = 0; constraint.gridy = 0;
        panel.add(username, constraint);
        constraint.gridx = 1;
        panel.add(usernameInput, constraint);

        // Password row
        constraint.gridx = 0; constraint.gridy = 1;
        panel.add(password, constraint);
        constraint.gridx = 1;
        panel.add(passwordInput, constraint);

        // Buttons row
        constraint.gridx = 0; constraint.gridy = 2;
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
        window.add(panel);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }
    
    public void handleLogin()
    {
    	String username = usernameInput.getText().trim();
        String password = new String(passwordInput.getPassword());

        if (username.isEmpty() || password.isEmpty())
        {
            JOptionPane.showMessageDialog(window, "Please enter username and password");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection())
        {
            String query = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next())
            {
                JOptionPane.showMessageDialog(window, "✅ Login Successful!");
                currentUser = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                openInventoryWindow();
            }
            else
            {
                JOptionPane.showMessageDialog(window, "❌ Invalid username or password");
            }

        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(window, "Database error: " + ex.getMessage());
        }
    }
    
    public void handleRegister()
    {
    	String username = usernameInput.getText().trim();
        String password = new String(passwordInput.getPassword());

        if (username.isEmpty() || password.isEmpty())
        {
            JOptionPane.showMessageDialog(window, "Please fill in both username and password.");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection())
        {
            // Check if username already exists
            String checkQuery = "SELECT * FROM users WHERE username=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next())
            {
                JOptionPane.showMessageDialog(window, "⚠️ Username already exists. Choose another one.");
                return;
            }

            // Insert new user
            String insertQuery = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, "user");

            int rows = insertStmt.executeUpdate();
            if (rows > 0)
            {
                JOptionPane.showMessageDialog(window, "✅ Registration successful! You can now log in.");
                usernameInput.setText("");
                passwordInput.setText("");
            }
            else
            {
                JOptionPane.showMessageDialog(window, "⚠️ Registration failed. Try again.");
            }

        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(window, "Database error: " + ex.getMessage());
        }
    }
    
    
    
    
    public void openInventoryWindow()
    {
	    JFrame inventoryWindow = new JFrame("Inventory Dashboard");
	    inventoryWindow.setExtendedState(JFrame.MAXIMIZED_BOTH);
	    inventoryWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	
	    List<Item> items = loadItemsFromDatabase();
	
	    String[] columns = {"ID", "Name", "Category", "Size", "Colour", "Quantity", "Price (€)", "Supplier"};
	    Object[][] data = new Object[items.size()][columns.length];
	
	    for (int i = 0; i < items.size(); i++)
	    {
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
	
	    JTable table = new JTable(data, columns)
	    {
	        @Override
	        public boolean isCellEditable(int row, int column)
	        {
	            return false;
	        }
	    };
	
	    table.setFont(new Font("SansSerif", Font.PLAIN, 16));
	    table.setRowHeight(30);
	    table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 18));
	
	    JScrollPane scrollPane = new JScrollPane(table);
	    scrollPane.setPreferredSize(new Dimension(900, 700));
	
	    JLabel title = new JLabel("Inventory List", SwingConstants.CENTER);
	    title.setFont(new Font("SansSerif", Font.BOLD, 28));
	
	    JButton backButton = new JButton("Back to Login");
	    backButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
	    backButton.addActionListener(e ->
	    {
	        inventoryWindow.dispose();
	        window.setVisible(true);
	    });
	
	    JPanel centerPanel = new JPanel();
	    centerPanel.add(scrollPane);
	    centerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 40, 40));
	
	    JPanel topPanel = new JPanel(new BorderLayout());
	    topPanel.add(title, BorderLayout.CENTER);
	
	    JPanel bottomPanel = new JPanel();
	    bottomPanel.add(backButton);
	
	    inventoryWindow.add(topPanel, BorderLayout.NORTH);
	    inventoryWindow.add(centerPanel, BorderLayout.CENTER);
	    inventoryWindow.add(bottomPanel, BorderLayout.SOUTH);
	    
	    
	    
	    
	    
	    // ADD
	    JButton addItemButton = new JButton("Add New Item");
	    addItemButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
	    
	    
	    // When the button is clicked, open a popup form for user input
	    addItemButton.addActionListener(e -> {
	    	
	    	// Create text fields for each clothing attribute
	        JTextField nameField = new JTextField(15);
	        JTextField categoryField = new JTextField(15);
	        JTextField sizeField = new JTextField(10);
	        JTextField colourField = new JTextField(10);
	        JTextField priceField = new JTextField(10);
	        JTextField quantityField = new JTextField(10);

	        
	        // Build a small input form panel with labels and text fields
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

	        
	        // Display the form inside a dialog box for user input
	        int result = JOptionPane.showConfirmDialog(
	                inventoryWindow, panel,
	                "Add New Item", JOptionPane.OK_CANCEL_OPTION,
	                JOptionPane.PLAIN_MESSAGE);

	        
	        // If the user presses OK, attempt to add the item
	        if (result == JOptionPane.OK_OPTION) {
	            try {
	                String name = nameField.getText().trim();
	                String category = categoryField.getText().trim();
	                String size = sizeField.getText().trim();
	                String colour = colourField.getText().trim();
	                double price = Double.parseDouble(priceField.getText().trim());
	                int quantity = Integer.parseInt(quantityField.getText().trim());
	                
	                
	                // Default supplier for now
	                Supplier supplier = new Supplier(1, "Default Supplier", "contact@test.com");

	                
	                
	                // Create a new Item object
	                Item newItem = new Item(
	                    0, // placeholder only, DB handles real ID
	                    name, category, size, colour,
	                    price, quantity, supplier
	                );

	                
	                // Add the new item to the database using the Inventory class
	                Inventory inventory = new Inventory();
	                inventory.addItem(newItem);

	                
	                // Confirm success to the user
	                JOptionPane.showMessageDialog(inventoryWindow,
	                        "Item added successfully!\nName: " + name,
	                        "Success", JOptionPane.INFORMATION_MESSAGE);

	                //  Refresh the table to immediately show the new item
	                List<Item> updatedItems = loadItemsFromDatabase();
	                DefaultTableModel model = new DefaultTableModel(
	                        new String[]{"ID", "Name", "Category", "Size", "Colour", "Quantity", "Price (€)", "Supplier"}, 0);

	                
	                // Rebuild the table data with the updated inventory
	                for (Item item : updatedItems) {
	                    model.addRow(new Object[]{
	                        item.getItemId(),
	                        item.getName(),
	                        item.getCategory(),
	                        item.getSize(),
	                        item.getColour(),
	                        item.getQuantity(),
	                        item.getPrice(),
	                        item.getSupplier() != null ? item.getSupplier().getName() : ""
	                    });
	                }
	                
	                // Apply the new data model to the table to update the UI
	                table.setModel(model);

	            } catch (Exception ex) {
	                ex.printStackTrace();
	                JOptionPane.showMessageDialog(inventoryWindow,
	                        "Error adding item: " + ex.getMessage(),
	                        "Error", JOptionPane.ERROR_MESSAGE);
	            }
	        }
	    });

	    // Add the new "Add Item" button next to the "Back to Login" button
	    bottomPanel.add(addItemButton);
	    
	    
	    // Hide the login window and show the inventory dashboard
	    window.setVisible(false);
	    inventoryWindow.setVisible(true);
	    
	    
	    
	    // EDIT ITEM BUTTON — allows the user to update an existing record
	    JButton editItemButton = new JButton("Edit Item");
	    editItemButton.setFont(new Font("SansSerif", Font.PLAIN, 18));

	    // When the button is clicked, the program asks which item the user wants to edit
	    editItemButton.addActionListener(e -> {
	        try {
	            // Ask the user which item they want to edit
	            // Opens a small popup where the user enters the item ID number
	            String idInput = JOptionPane.showInputDialog(inventoryWindow, "Enter the ID of the item you want to edit:");
	            
	            // If the user presses Cancel or leaves it blank, stop the process
	            if (idInput == null || idInput.trim().isEmpty()) {
	                JOptionPane.showMessageDialog(inventoryWindow, "No ID entered. Edit cancelled.");
	                return; // exit the event handler
	            }

	            // Convert the entered text into an integer (the item ID)
	            // Throws NumberFormatException if the user typed something invalid (like letters)
	            int itemId = Integer.parseInt(idInput.trim());

	            // Create a new Inventory object and call the updateItem() method
	            // This method opens a popup where the user can edit item details
	            Inventory inventory = new Inventory();
	            inventory.updateItem(itemId);

	            // After updating, reload the table to show the new data
	            // Fetch the updated list of items from the database
	            List<Item> updatedItems = loadItemsFromDatabase();

	            // Rebuild the table model using the new inventory data
	            DefaultTableModel model = new DefaultTableModel(
	                    new String[]{"ID", "Name", "Category", "Size", "Colour", "Quantity", "Price (€)", "Supplier"}, 0);

	            // Add each item as a new row in the table
	            for (Item item : updatedItems) {
	                model.addRow(new Object[]{
	                    item.getItemId(),
	                    item.getName(),
	                    item.getCategory(),
	                    item.getSize(),
	                    item.getColour(),
	                    item.getQuantity(),
	                    item.getPrice(),
	                    item.getSupplier() != null ? item.getSupplier().getName() : ""
	                });
	            }

	            // Apply the new data to the table to update the display
	            table.setModel(model);

	        } 
	        // Handle invalid input (non-number ID)
	        catch (NumberFormatException ex) {
	            JOptionPane.showMessageDialog(inventoryWindow, "Invalid ID format. Please enter a number.");
	        } 
	        //  Handle other unexpected errors
	        catch (Exception ex) {
	            ex.printStackTrace();
	            JOptionPane.showMessageDialog(inventoryWindow, "Error updating item: " + ex.getMessage());
	        }
	    });

	    // Add the Edit Item button to the bottom panel alongside the buttons
	    bottomPanel.add(editItemButton);

	    
    }

    
    
    
    
    
    public List<Item> loadItemsFromDatabase()
    {
        List<Item> items = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection())
        {
            String query = "SELECT i.*, s.name AS supplierName, s.contact AS supplierContact " +
                    "FROM items i LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();

            while (rs.next())
            {
                Supplier supplier = null;
                if (rs.getInt("supplier_id") != 0)
                {
                    supplier = new Supplier(rs.getInt("supplier_id"),
                            rs.getString("supplierName"),
                            rs.getString("supplierContact"));
                }

                items.add(new Item(
                        rs.getInt("itemId"),
                        rs.getString("itemName"),
                        rs.getString("category"),
                        rs.getString("size"),
                        rs.getString("colour"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        supplier
                ));
            }
        }
        catch (SQLException ex)
        {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(window, "Error loading items: " + ex.getMessage());
        }
        return items;
    }
    
    public static void main(String[] args)
	{
	    new Dashboard();
	}
}