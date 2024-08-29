

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.math.BigDecimal" %>
<%@page import="jakarta.servlet.http.HttpSession"%>
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
<html>
<head>
    <link rel="shortcut icon" type="image/png" href="https://avatars.mds.yandex.net/i?id=9b727c154ebf81ad18c40bdc4d51d82b1196cfba-5601142-images-thumbs&n=13"/>
    <title>Hesap Bilgileri</title>
<style>
    body { font-family: Arial, sans-serif; background: url('https://avatars.mds.yandex.net/i?id=d00615946d05cdfd02f0bd01df39cea21042d1ce-5877635-images-thumbs&n=13') center/cover no-repeat #f2f2f2; margin: 0; padding: 20px; }
    .container { max-width: 800px; margin: auto; background: #f2f2f2; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.3); color: #000; }
    h1 { color: #000; text-align: center; }
    table { width: 100%; border-collapse: collapse; margin: auto; }
    table, th, td { border: 1px solid #ddd; }
    th, td { padding: 10px; text-align: left; background: #fff; }
    th { background: #f4f4f4; }
    .button { display: inline-block; padding: 10px 20px; font-size: 16px; color: #fff; background: #007bff; text-decoration: none; border-radius: 5px; }
    .button:hover { background: #0056b3; }
    .notification { padding: 20px; border-radius: 5px; color: white; text-align: center; max-width: 500px; width: 100%; margin: 0 auto; }
    .success { background: #28a745; }
    .error { background: #dc3545; }
    
</style>
</head>
<body>
    <div class="container">
        <% 
            String successMessage = request.getParameter("success");
            String errorMessage = request.getParameter("error");

            if (successMessage != null) {
                if ("transfer_completed".equals(successMessage)) { %>
                    <div class="notification success">Transfer completed successfully!</div>
                <% } else { %>
                    <div class="notification success"><%= successMessage %></div>
                <% }
            } else if (errorMessage != null) { %>
                <div class="notification error"><%= errorMessage %></div>
            <% } 
        %>
        <h1>Hesap Bilgileri</h1>
        <table>
            <thead>
                <tr>
                    <th>IBAN NO</th>
                    <th>Bakiye</th>
                    <th>İsim</th>
                    <th>Soyisim</th>
                    <th>Adres</th>
                    <th>E-mail</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                    <form action="AccountServlet" method="post">
                        <td>${user_id}</td>
                        <td>${balance}</td>
                        <td>${name}</td>
                        <td>${last_name}</td>
                        <td>${address}</td>
                        <td>${email}</td>
                      
                    </form>
                </tr>
                <tr>
                    <td colspan="1">
                        <h2>Hesap Detayları</h2>
                        <a href="view_account.jsp" class="button">Hesap</a>
                    </td>
                    <td colspan="2">
                        <h2>Para Transferi</h2>
                        <a href="transfer.jsp" class="button">Para Transferi</a>
                    </td>
                    <form action="TransactionHistoryServlet" method="get">
                        <td colspan="2">
                            <h2>İşlem Geçmişi</h2>
                            <button type="submit" class="button">Geçmiş</button>
                        </td>
                    </form>
                    <form action="logout.jsp" method="post">
                        <td colspan="1">
                            <h2>Çıkış Yap</h2>
                            <button type="submit" class="button">Çıkış Yap</button>
                        </td>
                    </form>
                </tr>
            </tbody>
        </table>
    </div>
</body>
</html>
<%--
<%@page import="java.util.ArrayList"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="transfer.Account2" %>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page import="jakarta.servlet.http.HttpServletRequest" %>
<%@ page import="jakarta.servlet.http.HttpServletResponse" %>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    response.setHeader("Pragma", "no-cache");
    response.setDateHeader("Expires", 0);

    HttpSession sessionn = request.getSession(false);
    if (sessionn == null || sessionn.getAttribute("user_id") == null) {
        response.sendRedirect("index.jsp");
        return; 
    }

    List<Account2> accounts = (List<Account2>) sessionn.getAttribute("accounts");
    if (accounts == null) {
        accounts = new ArrayList<>();
    }
%>
<html>
<head>
    <link rel="shortcut icon" type="image/png" href="https://avatars.mds.yandex.net/i?id=9b727c154ebf81ad18c40bdc4d51d82b1196cfba-5601142-images-thumbs&n=13"/>
    <title>Hesap Bilgileri</title>
    <style>
        body { font-family: Arial, sans-serif; background: url('https://avatars.mds.yandex.net/i?id=d00615946d05cdfd02f0bd01df39cea21042d1ce-5877635-images-thumbs&n=13') center/cover no-repeat #f2f2f2; margin: 0; padding: 20px; }
        .container { max-width: 800px; margin: auto; background: #f2f2f2; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.3); color: #000; }
        h1 { color: #000; text-align: center; }
        table { width: 100%; border-collapse: collapse; margin: auto; }
        table, th, td { border: 1px solid #ddd; }
        th, td { padding: 10px; text-align: left; background: #fff; }
        th { background: #f4f4f4; }
        .button { display: inline-block; padding: 10px 20px; font-size: 16px; color: #fff; background: #007bff; text-decoration: none; border-radius: 5px; }
        .button:hover { background: #0056b3; }
        .notification { padding: 20px; border-radius: 5px; color: white; text-align: center; max-width: 500px; width: 100%; margin: 0 auto; }
        .success { background: #28a745; }
        .error { background: #dc3545; }
    </style>
</head>
<body>
    <div class="container">
        <% 
            String successMessage = request.getParameter("success");
            String errorMessage = request.getParameter("error");

            if (successMessage != null) {
                if ("transfer_completed".equals(successMessage)) { %>
                    <div class="notification success">Transfer completed successfully!</div>
                <% } else { %>
                    <div class="notification success"><%= successMessage %></div>
                <% }
            } else if (errorMessage != null) { %>
                <div class="notification error"><%= errorMessage %></div>
            <% } 
        %>
        <h1>Hesap Bilgileri</h1>
        <table>
            <thead>
                <tr>
                    <th>IBAN NO</th>
                    <th>Bakiye</th>
                    <th>İsim</th>
                    <th>Soyisim</th>
                    <th>Adres</th>
                    <th>E-mail</th>
                </tr>
            </thead>
            <tbody>
                <tr>
                            <td><strong>Hesap NO:</strong> ${user_id}</td>
                            <td><strong>Bakiye:</strong> ${balance} TL</td>
                            <td><strong>İsim:</strong> ${name}</td>
                            <td><strong>Soyisim:</strong> ${last_name}</td>
                            <td><strong>Adres:</strong> ${address}</td>
                            <td><strong>Email:</strong> ${email}</td>
                        </tr>
            </tbody>
        </table>
        <div style="text-align: center; margin-top: 20px;">
            <h2>Hesap Detayları</h2>
            <a href="view_account.jsp" class="button">Hesap</a>
            <h2>Para Transferi</h2>
            <a href="transfer.jsp" class="button">Para Transferi</a>
            <h2>İşlem Geçmişi</h2>
            <form action="TransactionHistoryServlet" method="get" style="display: inline;">
                <button type="submit" class="button">Geçmiş</button>
            </form>
            <h2>Çıkış Yap</h2>
            <form action="logout.jsp" method="post" style="display: inline;">
                <button type="submit" class="button">Çıkış Yap</button>
            </form>
        </div>
    </div>
</body>
</html>
--%>