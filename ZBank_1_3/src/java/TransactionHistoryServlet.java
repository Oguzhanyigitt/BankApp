import transferPackage.Transaction;
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

@WebServlet("/TransactionHistoryServlet")
public class TransactionHistoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String accountId = (String) session.getAttribute("account_id");

        if (accountId == null) {
            response.sendRedirect("login.jsp?error=Girilmedi");
            return;
        }

        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT transaction_id, transaction_date, transaction_type, amount, currency, balance_after_transaction, status, account_id, opposite_account_id, opposite_name, opposite_last_name, sender_name, sender_last_name " +
                         "FROM transactions " +
                         "WHERE account_id = ? " +
                         "ORDER BY transaction_date DESC";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, accountId);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Transaction transaction = new Transaction(
                                rs.getInt("transaction_id"),
                                rs.getTimestamp("transaction_date"),
                                rs.getString("transaction_type"),
                                rs.getString("account_id"),
                                rs.getBigDecimal("amount"),
                                rs.getString("currency"),
                                rs.getBigDecimal("balance_after_transaction"),
                                rs.getString("status"),
                                rs.getString("opposite_account_id"),
                                rs.getString("opposite_name"),
                                rs.getString("opposite_last_name"),
                                rs.getString("sender_name"),
                                rs.getString("sender_last_name")
                        );
                        transactions.add(transaction);
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServletException("VeritabaniHatasi.", e);
        }

        session.setAttribute("transactions", transactions); 
        request.getRequestDispatcher("transactionHistory.jsp?searchId=").forward(request, response);
    }
}