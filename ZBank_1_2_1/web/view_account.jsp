<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.math.BigDecimal" %>
<%@page import="jakarta.servlet.http.HttpSession" %>
<%@page import="jakarta.servlet.http.HttpServletRequest" %>
<%@page import="jakarta.servlet.http.HttpServletResponse" %>
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
    <title>Hesap Detayları</title>
    <style>
       body { font-family: Arial, sans-serif; background-color: #000000; background-image: url('Background linkini buraya girin'); background-size: cover; background-repeat: no-repeat; background-position: center; margin: 0; padding: 250px; }
        .container { max-width: 800px; margin: auto; background: #110; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.3); color: #9999ff; }
        h1 { color: #fff; }
        p { font-size: 1.2rem; margin: 0.5rem 0; color:#ddd}
        strong { color: #ccff00; }
        .error-message { color: #dc3545; font-weight: bold; text-align: center; padding: 1rem; border: 1px solid #dc3545; border-radius: 5px; background-color: #f8d7da; }
        .success-message { color: #ddd; font-weight: bold; text-align: center; padding: 1rem; border: 1px solid #28a745; border-radius: 5px; background-color: #000000; }
        a { display: block; text-align: center; margin: 1rem 0; color: #007bff; text-decoration: none; font-size: 1.1rem; }
        a:hover { text-decoration: underline; }
        .back-link { color: #007bff; }
        .back-link:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Hesap Detayları</h1>

        <c:if test="${not empty accountId}">
            <div class="success-message">       
                <p><strong>Hesap NO:</strong> ${user_id}</p>
                <p><strong>Bakiye:</strong> ${balance} TL</p>
                <p><strong>İsim:</strong> ${name}</p>
                <p><strong>Soyisim:</strong> ${last_name}</p>
                <p><strong>Adres:</strong> ${address}</p>
                <p><strong>Email:</strong> ${email}</p>
            </div>
        </c:if>
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
