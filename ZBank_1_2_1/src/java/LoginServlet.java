import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            response.sendRedirect("login.jsp?error=Boş Alan Var");
            return;
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            String sql = "SELECT a.account_id, a.balance,a.user_id, ui.name, ui.last_name, ui.address, ui.email " +
                         "FROM user_info ui " +
                         "JOIN accounts a ON ui.user_id = a.user_id " +
                         "WHERE ui.username = ? AND ui.password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                stmt.setString(2, password);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        HttpSession session = request.getSession();
                        session.setAttribute("account_id", rs.getString("account_id"));
                        session.setAttribute("balance", rs.getDouble("balance"));
                        session.setAttribute("name", rs.getString("name"));
                        session.setAttribute("last_name", rs.getString("last_name"));
                        session.setAttribute("address", rs.getString("address"));
                        session.setAttribute("email", rs.getString("email"));
                        session.setAttribute("user_id", rs.getString("user_id"));

                        response.sendRedirect("account.jsp");
                    } else {
                        response.sendRedirect("login.jsp?error=Geçersiz");
                    }
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Database error", e);
        }
    }
}


