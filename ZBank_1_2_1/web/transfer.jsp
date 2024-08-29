<%@page import="jakarta.servlet.http.HttpSession" %>
<%@page import="jakarta.servlet.http.HttpServletRequest" %>
<%@page import="jakarta.servlet.http.HttpServletResponse" %>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);
%>
<%
    HttpSession sessionn = request.getSession(false);
    if (sessionn == null || sessionn.getAttribute("user_id") == null) {
        response.sendRedirect("index.jsp");
        return; 
    }
%>
<!DOCTYPE html>
<html lang="tr">
<head>
    
    <link rel="shortcut icon" type="image/png" href="icon linkini buraya girin"/>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Transfer İşlemi</title>
    <script>
        function setOriginalAmount() {
            var amountInput = document.getElementById('amount');
            var originalAmountInput = document.getElementById('originalAmount');
            var amount = parseFloat(amountInput.value);
            originalAmountInput.value = amount;
        }
    </script>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f2f2f2; background-image: url('Background linkini buraya girin'); background-size: cover; background-repeat: no-repeat; background-position: center; margin: 0; padding: 20px; }
        .container { max-width: 800px; margin: auto; background: #000; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.3); color: #fff; }
        h1 { color: #fff; }
        .form-container { background: #333; border-radius: 8px; padding: 20px; margin: 20px 0; }
        .form-container h2 { margin-bottom: 20px; font-size: 1.5em; color: #fff; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; margin-bottom: 5px; font-weight: bold; color: #fff; }
        .form-group input[type="text"], .form-group select { width: 100%; padding: 10px; border: 1px solid #555; border-radius: 4px; box-sizing: border-box; background: #333; color: #fff; }
        .form-group input[type="submit"], .form-group button { background-color: #007bff; color: #fff; border: none; padding: 10px 15px; border-radius: 4px; cursor: pointer; width: 100%; font-size: 16px; }
        .form-group input[type="submit"]:hover, .form-group button:hover { background-color: #0056b3; }
        .message { margin: 20px 0; font-size: 16px; }
        .message.error { color: red; }
        .message.success { color: green; }
        .bank-image { width: 15%; height: auto; display: block; margin: 0 auto 20px; }
        .back-link { color: #0033ff; }
        .back-link:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Bankamıza Hoş Geldiniz</h1>

        <img src="https://avatars.mds.yandex.net/i?id=33ba3daf073a7bbe9be8a455534ff9c875b31b5c-5219738-images-thumbs&n=13" alt="Bank Image" class="bank-image" />

        <div class="form-container">
            <h2>Transfer İşlemi</h2>
            <form action="TransferServlet" method="post" onsubmit="setOriginalAmount()">
                <div class="form-group">
                    <label for="userId">Kullanıcı IBAN:</label>
                    <input type="text" id="userId" value="<%= request.getSession().getAttribute("account_id") %>" readonly />
                </div>
                <div class="form-group">
                    <label for="name">Alıcı Adı:</label>
                    <input type="text" id="name" name="name" required />
                </div>
                <div class="form-group">
                    <label for="last_name">Alıcı Soyadı:</label>
                    <input type="text" id="last_name" name="last_name" required />
                </div>
                <div class="form-group">
                    <label for="toAccountId">IBAN NO'ya:</label>
                    <input type="text" id="toAccountId" name="toAccountId" required />
                </div>
                <div class="form-group">
                <label for="amount">Miktar:</label>
                <input type="text" id="amount" name="amount" required />
                <input type="hidden" id="originalAmount" name="originalAmount" />
            </div>
                <div class="form-group">
                    <label for="currency">Para Birimi:</label>
                    <select id="currency" name="currency" required>
                        <option value="TRY" selected>TRY</option>
                        <option value="USD">USD</option>
                        <option value="EUR">EUR</option>
                        <option value="KWD">KWD</option>
                        <option value="JPY">JPY</option>
                        <option value="GBP">GBP</option>
                        <option value="CHF">CHF</option>
                    </select>
                </div>
                <div class="form-group">
                    <input type="submit" value="Transfer" />
                </div>
            </form>
        </div>

        <table>
            <thead>
                <tr>
                    <th><a href="account.jsp" class="back-link">Geri Dön</a></th>
                </tr>
            </thead>
        </table>
    </div>
</body>
</html>

