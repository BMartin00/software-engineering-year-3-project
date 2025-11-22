package gigabytegurus.inventorymanagementsystem;

import junit.framework.TestCase;
import java.sql.*;

public class DashboardTest extends TestCase
{
    private Dashboard dashboard;

    @Override
	protected void setUp() throws Exception {
	    super.setUp();
	    Dashboard.testMode = true;       // Enable test mode
	    dashboard = new Dashboard();      // GUI will not show
	}
	
	@Override
	protected void tearDown() throws Exception {
	    super.tearDown();
	    Dashboard.testMode = false;      // Reset after test
	}

    /* LOGIN TESTS */

    //Test #: 1
    //Obj: Test successful login with valid credentials
    //Input(s): username = "admin", password = "1234"
    //Expected Output: currentUser is not null, username and role match
    public void testSuccessfulLogin() throws SQLException
    {
        dashboard.usernameInput.setText("admin");
        dashboard.passwordInput.setText("1234");

        dashboard.handleLogin();

        assertNotNull("User should be logged in", dashboard.currentUser);
        assertEquals("Username should match", "admin", dashboard.currentUser.getUsername());
        assertEquals("Role should match", "admin", dashboard.currentUser.getRole());
    }

    //Test #: 2
    //Obj: Test login fails with wrong password
    //Input(s): username = "admin", password = "wrongpass"
    //Expected Output: currentUser is null
    public void testFailedLoginWrongPassword() throws SQLException
    {
        dashboard.usernameInput.setText("admin");
        dashboard.passwordInput.setText("wrongpass");

        dashboard.handleLogin();

        assertNull("Login should fail for wrong password", dashboard.currentUser);
    }

    //Test #: 3
    //Obj: Test login fails for non-existent user
    //Input(s): username = "fakeuser", password = "1234"
    //Expected Output: currentUser is null
    public void testFailedLoginNonExistentUser() throws SQLException
    {
        dashboard.usernameInput.setText("fakeuser");
        dashboard.passwordInput.setText("1234");

        dashboard.handleLogin();

        assertNull("Login should fail for non-existent user", dashboard.currentUser);
    }

    //Test #: 4
    //Obj: Test login fails with empty username
    //Input(s): username = "", password = "1234"
    //Expected Output: currentUser is null
    public void testLoginEmptyUsername() throws SQLException
    {
        dashboard.usernameInput.setText("");
        dashboard.passwordInput.setText("1234");

        dashboard.handleLogin();

        assertNull("Login should fail for empty username", dashboard.currentUser);
    }

    //Test #: 5
    //Obj: Test login fails with empty password
    //Input(s): username = "admin", password = ""
    //Expected Output: currentUser is null
    public void testLoginEmptyPassword() throws SQLException
    {
        dashboard.usernameInput.setText("admin");
        dashboard.passwordInput.setText("");

        dashboard.handleLogin();

        assertNull("Login should fail for empty password", dashboard.currentUser);
    }

    //Test #: 6
    //Obj: Test login fails with whitespace-only username
    //Input(s): username = "   ", password = "1234"
    //Expected Output: currentUser is null
    public void testLoginWhitespaceUsername() throws SQLException
    {
        dashboard.usernameInput.setText("   ");
        dashboard.passwordInput.setText("1234");

        dashboard.handleLogin();

        assertNull("Login should fail for whitespace username", dashboard.currentUser);
    }

    /* REGISTRATION TESTS */

    //Test #: 7
    //Obj: Test successful registration
    //Input(s): username = "new_user", password = "mypassword"
    //Expected Output: user exists in DB
    public void testSuccessfulRegistration() throws SQLException
    {
        String testUser = "new_user";

        dashboard.usernameInput.setText(testUser);
        dashboard.passwordInput.setText("mypassword");

        // Remove user if exists from previous test runs
        try (Connection conn = DatabaseConnection.getConnection())
        {
            PreparedStatement stmt = conn.prepareStatement("DELETE FROM users WHERE username=?");
            stmt.setString(1, testUser);
            stmt.executeUpdate();
        }

        dashboard.handleRegister();

        // Verify user was added in DB
        try (Connection conn = DatabaseConnection.getConnection())
        {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username=?");
            stmt.setString(1, testUser);
            ResultSet rs = stmt.executeQuery();
            assertTrue("User should exist in DB after registration", rs.next());
            assertEquals("Username should match", testUser, rs.getString("username"));
        }
    }

    //Test #: 8
    //Obj: Test registration fails for duplicate username
    //Input(s): username = "admin", password = "1234"
    //Expected Output: currentUser is null
    public void testFailedRegistrationDuplicateUsername() throws SQLException
    {
        dashboard.usernameInput.setText("admin"); // Already exists
        dashboard.passwordInput.setText("1234");

        dashboard.handleRegister();

        assertNull(dashboard.currentUser);
    }

    //Test #: 9
    //Obj: Test registration fails for empty username
    //Input(s): username = "", password = "password"
    //Expected Output: currentUser is null
    public void testRegistrationEmptyUsername() throws SQLException
    {
        dashboard.usernameInput.setText("");
        dashboard.passwordInput.setText("password");

        dashboard.handleRegister();

        assertNull(dashboard.currentUser);
    }

    //Test #: 10
    //Obj: Test registration fails for empty password
    //Input(s): username = "testuser", password = ""
    //Expected Output: currentUser is null
    public void testRegistrationEmptyPassword() throws SQLException
    {
        dashboard.usernameInput.setText("testuser");
        dashboard.passwordInput.setText("");

        dashboard.handleRegister();

        assertNull(dashboard.currentUser);
    }

    //Test #: 11
    //Obj: Test registration fails for whitespace-only username
    //Input(s): username = "   ", password =" password"
    //Expected Output: currentUser is null
    public void testRegistrationWhitespaceUsername() throws SQLException
    {
        dashboard.usernameInput.setText("   ");
        dashboard.passwordInput.setText("password");

        dashboard.handleRegister();

        assertNull(dashboard.currentUser);
    }
}
