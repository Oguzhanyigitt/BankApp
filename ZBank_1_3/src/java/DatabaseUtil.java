
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/example";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";
    private static final Logger LOGGER = Logger.getLogger(DatabaseUtil.class.getName());

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found", e);
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static Account getAccount(String accountId) {
        String query = "SELECT * FROM accounts WHERE account_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Account(
                            rs.getString("account_id"),
                            rs.getDouble("balance"),                       
                            rs.getInt("user_id")
                    );
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error retrieving account", e);
        }
        return null;
    }

    public static boolean getUserByEmail(String email) {
        String sql = "SELECT * FROM user_info WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); 
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking email", e);
        }
        return false;
    }
    public static int getUserIdByAccountId(Connection conn, String accountId) throws SQLException {
        String query = "SELECT user_id FROM accounts WHERE account_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        }
        return -1; 
    }


    public static int registerUserInfo(String name, String lastName, String address, String email,String username,String password) {
        String sql = "INSERT INTO user_info (name, last_name, address, email,username,password) VALUES (?, ?, ?, ? ,?, ?)";
        try (Connection conn = getConnection();  
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, lastName);
            stmt.setString(3, address);
            stmt.setString(4, email);
            stmt.setString(5, username);
            stmt.setString(6, password);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registering user info", e);
        }
        return -1;
    }
    
   
    public static boolean registerAccount(int accountId, double balance, int userId) {
        String query = "INSERT INTO accounts (account_id, balance, user_id) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            stmt.setDouble(2, balance);
            stmt.setInt(3, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error registering account", e);
        }
        return false;
    }
}
