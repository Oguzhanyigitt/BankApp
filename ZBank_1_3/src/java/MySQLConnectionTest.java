/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author db2admin
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLConnectionTest {

    
    private static final String URL = "jdbc:mysql://localhost:3306/bankdb2"; 
    private static final String USER = "root";
    private static final String PASSWORD = "12345";

    public static void main(String[] args) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Successfully connected to the database");
            statement = connection.createStatement();
            String query = "SELECT VERSION()";
            resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                String version = resultSet.getString(1);
                System.out.println("MySQL version: " + version);
            }

        } catch (SQLException e) {
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
            }
        }
    }
}

