/*
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        int toAccountId;
        BigDecimal amount;
        String currency;
        String name = request.getParameter("name");
        String lastName = request.getParameter("last_name");
        
        try {
            toAccountId = Integer.parseInt(request.getParameter("toAccountId"));
            amount = new BigDecimal(request.getParameter("amount"));
            currency = request.getParameter("currency");
        } catch (NumberFormatException e) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Geçersiz Parametre"));
            return;
        }

        if (name == null || name.trim().isEmpty()) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Geçersiz Ad"));
            return;
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Geçersiz Soyad"));
            return;
        }

        HttpSession session = request.getSession();
        String loggedInAccountIdStr = (String) session.getAttribute("account_id");
        if (loggedInAccountIdStr == null) {
            response.sendRedirect("login.jsp?error=" + encodeURIComponent("İşlem Açılmadı"));
            return;
        }

        Integer loggedInAccountId;
        try {
            loggedInAccountId = Integer.parseInt(loggedInAccountIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Geçersiz Hesap No"));
            return;
        }

        if (loggedInAccountId.equals(toAccountId)) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Aynı Hesap"));
            return;
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Geçersiz Miktar"));
            return;
        }

        if ("USD".equalsIgnoreCase(currency)) {
            amount = amount.multiply(new BigDecimal("33")); 
        } else if ("EUR".equalsIgnoreCase(currency)) {
            amount = amount.multiply(new BigDecimal("35")); 
        }
        BigDecimal minimumAmount = new BigDecimal("1.00");
        if (amount.compareTo(minimumAmount) < 0) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Miktar 1.00'den küçük olamaz"));
            return;
        }
        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);

            if (!isRecipientDetailsValid(conn, toAccountId, name, lastName)) {
                conn.rollback();
                response.sendRedirect("account.jsp?error=" + encodeURIComponent("Geçersiz Alıcı Bilgileri"));
                return;
            }

            BigDecimal currentBalanceFrom;
            String balanceCheckSQL = "SELECT balance FROM accounts WHERE account_id = ?";
            String senderDetailsSQL = "SELECT name, last_name FROM user_info WHERE user_id = (SELECT user_id FROM accounts WHERE account_id = ?)";
            try (PreparedStatement balanceCheckStmt = conn.prepareStatement(balanceCheckSQL)) {
                balanceCheckStmt.setInt(1, loggedInAccountId);
                try (ResultSet rs = balanceCheckStmt.executeQuery()) {
                    if (rs.next()) {
                        currentBalanceFrom = rs.getBigDecimal("balance");
                    } else {
                        conn.rollback();
                        response.sendRedirect("account.jsp?error=" + encodeURIComponent("Hesap Bulunamadı"));
                        return;
                    }
                }
            }

            BigDecimal currentBalanceTo;
            String recipientName = null;
            String recipientLastName = null;
            try (PreparedStatement recipientCheckStmt = conn.prepareStatement(balanceCheckSQL)) {
                recipientCheckStmt.setInt(1, toAccountId);
                try (ResultSet rs = recipientCheckStmt.executeQuery()) {
                    if (rs.next()) {
                        currentBalanceTo = rs.getBigDecimal("balance");
                        String recipientDetailsSQL = "SELECT name, last_name FROM user_info WHERE user_id = (SELECT user_id FROM accounts WHERE account_id = ?)";
                        try (PreparedStatement recipientStmt = conn.prepareStatement(recipientDetailsSQL)) {
                            recipientStmt.setInt(1, toAccountId);
                            try (ResultSet rsRecipient = recipientStmt.executeQuery()) {
                                if (rsRecipient.next()) {
                                    recipientName = rsRecipient.getString("name");
                                    recipientLastName = rsRecipient.getString("last_name");
                                } else {
                                    conn.rollback();
                                    response.sendRedirect("account.jsp?error=" + encodeURIComponent("Gönderilmek İstenen Hesap Bulunamadı"));
                                    return;
                                }
                            }
                        }
                    } else {
                        conn.rollback();
                        response.sendRedirect("account.jsp?error=" + encodeURIComponent("Gönderilmek İstenen Hesap Bulunamadı"));
                        return;
                    }
                }
            }

            if (currentBalanceFrom.compareTo(amount) < 0) {
                conn.rollback();
                response.sendRedirect("account.jsp?error=" + encodeURIComponent("Yetersiz Bakiye"));
                return;
            }

            String updateBalanceSQL = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateBalanceSQL)) {
                updateStmt.setBigDecimal(1, amount);
                updateStmt.setInt(2, loggedInAccountId);
                if (updateStmt.executeUpdate() == 0) {
                    conn.rollback();
                    response.sendRedirect("account.jsp?error=" + encodeURIComponent("Gönderme Başarısız"));
                    return;
                }
            }

            String updateRecipientBalanceSQL = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
            try (PreparedStatement updateRecipientStmt = conn.prepareStatement(updateRecipientBalanceSQL)) {
                updateRecipientStmt.setBigDecimal(1, amount);
                updateRecipientStmt.setInt(2, toAccountId);
                if (updateRecipientStmt.executeUpdate() == 0) {
                    conn.rollback();
                    response.sendRedirect("account.jsp?error=" + encodeURIComponent("Gönderim Başarısız"));
                    return;
                }
            }

            String senderDetailsSQLL = "SELECT name, last_name FROM user_info WHERE user_id = (SELECT user_id FROM accounts WHERE account_id = ?)";
            String senderName = null;
            String senderLastName = null;
            try (PreparedStatement senderStmt = conn.prepareStatement(senderDetailsSQLL)) {
                senderStmt.setInt(1, loggedInAccountId);
                try (ResultSet rsSender = senderStmt.executeQuery()) {
                    if (rsSender.next()) {
                        senderName = rsSender.getString("name");
                        senderLastName = rsSender.getString("last_name");
                    } else {
                        conn.rollback();
                        response.sendRedirect("account.jsp?error=" + encodeURIComponent("Gönderici Bilgileri Bulunamadı"));
                        return;
                    }
                }
            }

            Timestamp now = new Timestamp(new Date().getTime());

            logTransaction(conn, "Giden Transfer", amount, currency, loggedInAccountId, currentBalanceFrom.subtract(amount), loggedInAccountId, senderName, senderLastName, String.valueOf(toAccountId), recipientName, recipientLastName, now);
            logTransaction(conn, "Gelen Transfer", amount, currency, toAccountId, currentBalanceTo.add(amount), loggedInAccountId, senderName, senderLastName, String.valueOf(toAccountId), recipientName, recipientLastName, now);

            conn.commit();

            try (PreparedStatement updatedBalanceStmt = conn.prepareStatement(balanceCheckSQL)) {
                updatedBalanceStmt.setInt(1, loggedInAccountId);
                try (ResultSet rs = updatedBalanceStmt.executeQuery()) {
                    if (rs.next()) {
                        session.setAttribute("balance", rs.getBigDecimal("balance").toString());
                    } else {
                        response.sendRedirect("account.jsp?error=" + encodeURIComponent("İşlem Sonrası Hesap Bulunamadı"));
                        return;
                    }
                }
            }

            response.sendRedirect("account.jsp?success=" + encodeURIComponent("Transfer Başarılı"));

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Bir hata oluştu. Lütfen tekrar deneyin."));
        }
    }

    private boolean isRecipientDetailsValid(Connection conn, int toAccountId, String inputName, String inputLastName )throws SQLException {
        String query = "SELECT name, last_name FROM user_info WHERE user_id = (SELECT user_id FROM accounts WHERE account_id = ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, toAccountId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbName = rs.getString("name");
                    String dbLastName = rs.getString("last_name");

                    return inputName.equals(dbName) && inputLastName.equals(dbLastName);
                }
            }
        } 

        return false;
    }

    private void logTransaction(Connection conn, String transactionType, BigDecimal amount, String currency, int accountId, BigDecimal balanceAfterTransaction, int userId, String senderName, String senderLastName, String oppositeAccountId, String oppositeName, String oppositeLastName, Timestamp transactionDate) throws SQLException {
        String logTransactionSQL = "INSERT INTO transactions (transaction_date, transaction_type, amount, currency, account_id, balance_after_transaction, user_id, sender_name, sender_last_name, opposite_account_id, opposite_name, opposite_last_name) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement logTransactionStmt = conn.prepareStatement(logTransactionSQL)) {
            logTransactionStmt.setTimestamp(1, transactionDate);
            logTransactionStmt.setString(2, transactionType);
            logTransactionStmt.setBigDecimal(3, amount);
            logTransactionStmt.setString(4, currency);
            logTransactionStmt.setInt(5, accountId);
            logTransactionStmt.setBigDecimal(6, balanceAfterTransaction);
            logTransactionStmt.setInt(7, userId);
            logTransactionStmt.setString(8, senderName);
            logTransactionStmt.setString(9, senderLastName);
            logTransactionStmt.setString(10, oppositeAccountId);
            logTransactionStmt.setString(11, oppositeName);
            logTransactionStmt.setString(12, oppositeLastName);
            logTransactionStmt.executeUpdate();
        }
    }

    private String encodeURIComponent(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
*/

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

@WebServlet("/transfer")
public class TransferServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");

        Transfers values = valueChanges(request, response);
        if (values == null) return;

        HttpSession session = request.getSession();
        Integer loggedInAccountId = validateSession(session, response);
        if (loggedInAccountId == null) return;

        if (!validateTransferDetails(values, loggedInAccountId, response)) return;

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);

            if (!isRecipientDetailsValid(conn, values.toAccountId, values.name, values.lastName)) {
                conn.rollback();
                response.sendRedirect("account.jsp?error=" + encodeURIComponent("Geçersiz Alıcı Bilgileri"));
                return;
            }
             BigDecimal amountInTargetCurrency = values.amount;
            String currency = values.currency.toUpperCase();
            BigDecimal rate = CurrencyConverter.getExchangeRate(currency);
            if (rate == null) {
                conn.rollback();
                response.sendRedirect("account.jsp?error=" + encodeURIComponent("Kur Bilgileri Alınamadı"));
                return;
            }
            BigDecimal originalAmount = amountInTargetCurrency.divide(rate, BigDecimal.ROUND_HALF_UP);
            values.originalAmount = originalAmount;
            if (!checkAndUpdateBalances(conn, loggedInAccountId, values.toAccountId, values.amount, response)) {
                conn.rollback();
                return;
            }
            
           
            logTransactions(conn, loggedInAccountId, values.toAccountId, values.amount, values.currency, originalAmount);
            conn.commit();
            
            updateSessionBalance(session, conn, loggedInAccountId, response);

            request.setAttribute("originalAmount", originalAmount);
            
        } catch (SQLException e) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Bir hata oluştu. Lütfen tekrar deneyin."));
        }
    }
            
    
    private Transfers valueChanges(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int toAccountId;
        BigDecimal amount;
        String currency;
        String name = request.getParameter("name");
        String lastName = request.getParameter("last_name");

        try {
            toAccountId = Integer.parseInt(request.getParameter("toAccountId"));
            amount = new BigDecimal(request.getParameter("amount"));
            currency = request.getParameter("currency");
        } catch (NumberFormatException e) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Geçersiz Parametre"));
            return null;
        }

        if (name == null || name.trim().isEmpty()) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Geçersiz Ad"));
            return null;
        }

        if (lastName == null || lastName.trim().isEmpty()) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Geçersiz Soyad"));
            return null;
        }

        return new Transfers(toAccountId, amount, currency, name, lastName);
    }

    private Integer validateSession(HttpSession session, HttpServletResponse response) throws IOException {
        String loggedInAccountIdStr = (String) session.getAttribute("account_id");
        if (loggedInAccountIdStr == null) {
            response.sendRedirect("login.jsp?error=" + encodeURIComponent("İşlem Açılmadı"));
            return null;
        }

        try {
            return Integer.valueOf(loggedInAccountIdStr);
        } catch (NumberFormatException e) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Geçersiz Hesap No"));
            return null;
        }
    }
    private boolean validateTransferDetails(Transfers values, Integer loggedInAccountId, HttpServletResponse response) throws IOException {
        if((!"try".equals(values.currency))) {
 
            if (loggedInAccountId.equals(values.toAccountId)) {
                response.sendRedirect("account.jsp?error=" + encodeURIComponent("Aynı Hesap"));
                return false;
            }

            if (values.amount.compareTo(BigDecimal.ZERO) <= 0) {
                response.sendRedirect("account.jsp?error=" + encodeURIComponent("Geçersiz Miktar"));
                return false;
            }

            try {
                if ("TRY".equalsIgnoreCase(values.currency)) {
                    values.amount = CurrencyConverter.convertAmount(values.amount, "TRY", "TRY");
                    values.originalAmount= values.amount;}
                if ("USD".equalsIgnoreCase(values.currency)) {
                    values.amount = CurrencyConverter.convertAmount(values.amount, "USD", "TRY");
                    values.originalAmount= values.amount;
                } else if ("EUR".equalsIgnoreCase(values.currency)) {
                    values.amount = CurrencyConverter.convertAmount(values.amount, "EUR", "TRY");
                    values.originalAmount= values.amount;
                }else if ("KWD".equalsIgnoreCase(values.currency)) {
                    values.amount = CurrencyConverter.convertAmount(values.amount, "KWD", "TRY");
                    values.originalAmount= values.amount;
                }else if ("JPY".equalsIgnoreCase(values.currency)) {
                    values.amount = CurrencyConverter.convertAmount(values.amount, "JPY", "TRY");
                    values.originalAmount= values.amount;
                }else if ("GBP".equalsIgnoreCase(values.currency)) {
                    values.amount = CurrencyConverter.convertAmount(values.amount, "GBP", "TRY");
                    values.originalAmount= values.amount;
                }else if ("CHF".equalsIgnoreCase(values.currency)) {
                    values.amount = CurrencyConverter.convertAmount(values.amount, "CHF", "TRY");
                    values.originalAmount= values.amount;
                }

            } catch (IOException e) {
                response.sendRedirect("account.jsp?error=" + encodeURIComponent("Kur Bilgileri Alınamadı"));
                return false;
            }
        
        BigDecimal minimumAmount = new BigDecimal("1.00");
        if (values.amount.compareTo(minimumAmount) < 0) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Miktar 1.00'den küçük olamaz"));
            return false;
        }

        return true;
    }
        else {
            BigDecimal minimumAmount = new BigDecimal("1.00");
        if (values.amount.compareTo(minimumAmount) < 0) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Miktar 1.00'den küçük olamaz"));
            return false;
        }}return false;
}
    private boolean checkAndUpdateBalances(Connection conn, Integer loggedInAccountId, int toAccountId, BigDecimal amount, HttpServletResponse response) throws SQLException, IOException {
        BigDecimal currentBalanceFrom = getCurrentBalance(conn, loggedInAccountId);
        BigDecimal currentBalanceTo = getCurrentBalance(conn, toAccountId);

        if (currentBalanceFrom == null || currentBalanceTo == null) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Hesap Bulunamadı"));
            return false;
        }

        if (currentBalanceFrom.compareTo(amount) < 0) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Yetersiz Bakiye"));
            return false;
        }

        if (!updateBalance(conn, loggedInAccountId, amount.negate()) || !updateBalance(conn, toAccountId, amount)) {
            response.sendRedirect("account.jsp?error=" + encodeURIComponent("Gönderim Başarısız"));
            return false;
        }

        return true;
    }

    private BigDecimal getCurrentBalance(Connection conn, int accountId) throws SQLException {
        String balanceCheckSQL = "SELECT balance FROM accounts WHERE account_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(balanceCheckSQL)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("balance");
                }
            }
        }
        return null;
    }

    private boolean updateBalance(Connection conn, int accountId, BigDecimal amount) throws SQLException {
        String updateBalanceSQL = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateBalanceSQL)) {
            stmt.setBigDecimal(1, amount);
            stmt.setInt(2, accountId);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private void logTransactions(Connection conn, Integer loggedInAccountId, int toAccountId, BigDecimal amount, String currency, BigDecimal originalAmount) throws SQLException {
    String senderName = getAccountDetails(conn, loggedInAccountId).name;
    String senderLastName = getAccountDetails(conn, loggedInAccountId).lastName;
    String recipientName = getAccountDetails(conn, toAccountId).name;
    String recipientLastName = getAccountDetails(conn, toAccountId).lastName;
    BigDecimal senderBalance = getCurrentBalance(conn, loggedInAccountId);
    BigDecimal recipientBalance = getCurrentBalance(conn, toAccountId);
    Timestamp now = new Timestamp(new Date().getTime());

    logTransaction(conn, "Giden", amount, currency, loggedInAccountId, senderBalance, loggedInAccountId, senderName, senderLastName, String.valueOf(toAccountId), recipientName, recipientLastName, now, originalAmount);
    logTransaction(conn, "Gelen", amount, currency, toAccountId, recipientBalance, loggedInAccountId, senderName, senderLastName, String.valueOf(toAccountId), recipientName, recipientLastName, now, originalAmount);
}

    private AccountDetails getAccountDetails(Connection conn, int accountId) throws SQLException {
        String query = "SELECT name, last_name FROM user_info WHERE user_id = (SELECT user_id FROM accounts WHERE account_id = ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AccountDetails(rs.getString("name"), rs.getString("last_name"));
                }
            }
        }
        return new AccountDetails("", "");
    }

    private void logTransaction(Connection conn, String transactionType, BigDecimal amount, String currency, int accountId, BigDecimal balanceAfterTransaction, int userId, String senderName, String senderLastName, String oppositeAccountId, String oppositeName, String oppositeLastName, Timestamp transactionDate, BigDecimal originalAmount) throws SQLException {
    String logTransactionSQL = "INSERT INTO transactions (transaction_date, transaction_type, amount, currency, account_id, balance_after_transaction, user_id, sender_name, sender_last_name, opposite_account_id, opposite_name, opposite_last_name, original_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement logTransactionStmt = conn.prepareStatement(logTransactionSQL)) {
        logTransactionStmt.setTimestamp(1, transactionDate);
        logTransactionStmt.setString(2, transactionType);
        logTransactionStmt.setBigDecimal(3, amount);
        logTransactionStmt.setString(4, currency);
        logTransactionStmt.setInt(5, accountId);
        logTransactionStmt.setBigDecimal(6, balanceAfterTransaction);
        logTransactionStmt.setInt(7, userId);
        logTransactionStmt.setString(8, senderName);
        logTransactionStmt.setString(9, senderLastName);
        logTransactionStmt.setString(10, oppositeAccountId);
        logTransactionStmt.setString(11, oppositeName);
        logTransactionStmt.setString(12, oppositeLastName);
        logTransactionStmt.setBigDecimal(13, originalAmount);
        logTransactionStmt.executeUpdate();
    }
    }

    private void updateSessionBalance(HttpSession session, Connection conn, int loggedInAccountId, HttpServletResponse response) throws SQLException, IOException {
        String balanceCheckSQL = "SELECT balance FROM accounts WHERE account_id = ?";
        try (PreparedStatement updatedBalanceStmt = conn.prepareStatement(balanceCheckSQL)) {
            updatedBalanceStmt.setInt(1, loggedInAccountId);
            try (ResultSet rs = updatedBalanceStmt.executeQuery()) {
                if (rs.next()) {
                    session.setAttribute("balance", rs.getBigDecimal("balance").toString());
                    response.sendRedirect("account.jsp?success=" + encodeURIComponent("Transfer Başarıyla Gerçekleşti"));
                } else {
                    response.sendRedirect("account.jsp?error=" + encodeURIComponent("İşlem Sonrası Hesap Bulunamadı"));
                }
            }
        }
    }

    private boolean isRecipientDetailsValid(Connection conn, int toAccountId, String name, String lastName) throws SQLException {
    String query = "SELECT name, last_name FROM user_info WHERE user_id = (SELECT user_id FROM accounts WHERE account_id = ?)";
    
    try (PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setInt(1, toAccountId);
        
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String dbName = rs.getString("name");
                String dbLastName = rs.getString("last_name");                
                return name.equals(dbName) && lastName.equals(dbLastName);
            } else {
                return false;
            }
        }
    }
}
    private static class Transfers {
        int toAccountId;
        BigDecimal amount;
        String currency;
        String name;
        String lastName;
        BigDecimal originalAmount;

        Transfers(int toAccountId, BigDecimal amount, String currency, String name, String lastName) {
            this.toAccountId = toAccountId;
            this.amount = amount;
            this.currency = currency;
            this.name = name;
            this.lastName = lastName;
        }
    }

    private static class AccountDetails {
        String name;
        String lastName;

        AccountDetails(String name, String lastName) {
            this.name = name;
            this.lastName = lastName;
        }
    }

    private String encodeURIComponent(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
        
