package gigabytegurus.inventorymanagementsystem;

import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

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
	
	    window.setVisible(false);
	    inventoryWindow.setVisible(true);
	}
    
    public static void main(String[] args)
	{
	    new Dashboard();
	}
}