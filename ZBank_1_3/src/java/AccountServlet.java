/*
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@WebServlet("/account")
public class AccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = 1; 

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT a.account_id, a.balance, ui.name, ui.last_name, ui.address, ui.email " +
                         "FROM accounts a " +
                         "JOIN user_info ui ON a.user_id = ui.user_id " +
                         "WHERE a.account_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {                   
                        session.setAttribute("account_id", rs.getString("account_id"));
                        session.setAttribute("balance", rs.getDouble("balance"));
                        session.setAttribute("name", rs.getString("name"));
                        session.setAttribute("last_name", rs.getString("last_name"));
                        session.setAttribute("address", rs.getString("address"));
                        session.setAttribute("email", rs.getString("email"));
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }

        request.getRequestDispatcher("account.jsp").forward(request, response);
    }
}
*/

import transfer.Account2;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
@WebServlet("/account")
public class AccountServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = 1; 
        List<Account2> accounts = new ArrayList<>();
        
        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT a.account_id, a.balance, ui.name, ui.last_name, ui.address, ui.email " +
                         "FROM accounts a " +
                         "JOIN user_info ui ON a.user_id = ui.user_id " +
                         "WHERE a.user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Account2 account = new Account2();
                        account.setAccountId(rs.getInt("account_id"));
                        account.setBalance(rs.getDouble("balance"));
                        account.setName(rs.getString("name"));
                        account.setLastName(rs.getString("last_name"));
                        account.setAddress(rs.getString("address"));
                        account.setEmail(rs.getString("email"));
                        accounts.add(account);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
        session.setAttribute("accounts", accounts);
        request.getRequestDispatcher("account.jsp").forward(request, response);
    }
}


     
    
