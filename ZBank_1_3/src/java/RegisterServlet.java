
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegisterServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(RegisterServlet.class.getName());

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String lastName = request.getParameter("last_name");
        String address = request.getParameter("address");
        String email = request.getParameter("email");
        
        if (username == null || password == null || name == null || lastName == null || address == null || email == null ||
            username.isEmpty() || password.isEmpty() || name.isEmpty() || lastName.isEmpty() || address.isEmpty() || email.isEmpty()) {
            response.sendRedirect("register.jsp?error=missingFields");
            return;
        }

        try {
            
            if (DatabaseUtil.getUserByEmail(email)) {
                response.sendRedirect("register.jsp?error=userExists");
                return;
            }

            
            int userId = DatabaseUtil.registerUserInfo(name, lastName, address, email, username, password); 
            if (userId != -1) {

                int accountId =  userId; 
                double initialBalance = 0.0;

                if (DatabaseUtil.registerAccount(accountId, initialBalance, userId)) {
                    response.sendRedirect("login.jsp");
                } else {
                    response.sendRedirect("register.jsp?error=accountCreation");
                }
            } else {
                response.sendRedirect("register.jsp?error=userCreation");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error registering user", e);
            response.sendRedirect("register.jsp?error=serverError");
        }
    }
}            