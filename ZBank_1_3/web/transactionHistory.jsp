<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.Comparator"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.List"%>
<%@page import="transfer.Transaction"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="java.util.stream.IntStream"%>
<%@page import="jakarta.servlet.http.HttpServletRequest"%>
<%@page import="jakarta.servlet.http.HttpSession"%>
<%@page import="jakarta.servlet.http.HttpServletRequest" %>
<%@page import="jakarta.servlet.http.HttpServletResponse" %>
<%
    HttpSession sessionn = request.getSession(false);
    if (sessionn == null || sessionn.getAttribute("user_id") == null) {
        response.sendRedirect("index.jsp");
        return; 
    }
%>
<%
    SimpleDateFormat turkish = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    int pageSize = 10;
    int pageNumber = 1;

    String pageNumberStr = request.getParameter("page");
    if (pageNumberStr != null && !pageNumberStr.isEmpty()) {
        try {
            pageNumber = Integer.parseInt(pageNumberStr);
        } catch (NumberFormatException e) {
            pageNumber = 1;
        }
    }

    List<Transaction> transactions = (List<Transaction>) session.getAttribute("transactions");
    if (transactions == null) {
        transactions = new ArrayList<>();
    }

    final String searchId = request.getParameter("searchId");
    if (searchId != null && !searchId.isEmpty()) {
        transactions = transactions.stream()
                                   .filter(t -> String.valueOf(t.getTransactionId()).equals(searchId))
                                   .collect(Collectors.toList());
    }
    
    int totalTransactions = transactions.size();
    int totalPages = (int) Math.ceil((double) totalTransactions / pageSize);
    int start = (pageNumber - 1) * pageSize;
    int end = Math.min(start + pageSize, totalTransactions);

    if (start >= totalTransactions) {
        start = totalTransactions - pageSize;
        end = totalTransactions;
    }
    if (start < 0) {
        start = 0;
    }

    List<Transaction> pagedTransactions = transactions.subList(start, end);
    List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" type="image/png" href="https://avatars.mds.yandex.net/i?id=9b727c154ebf81ad18c40bdc4d51d82b1196cfba-5601142-images-thumbs&n=13"/>
    <meta charset="UTF-8">
    <title>İşlem Geçmişi</title>
    <style>
        body {font-family: Arial, sans-serif;background-color: #f2f2f2;background-image: url('https://avatars.mds.yandex.net/i?id=d00615946d05cdfd02f0bd01df39cea21042d1ce-5877635-images-thumbs&n=13'); background-size: cover;background-repeat: no-repeat;background-position: center;margin: 0;padding: 225px;}
        .container {max-width: 1000px; margin: auto;background: rgba(255, 255, 255, 0.9);padding: 30px; border-radius: 8px;box-shadow: 0 0 15px rgba(0, 0, 0, 0.4); color: #333; }
        h1 {color: #333; text-align: center; margin-bottom: 20px; }
        .form-container {text-align: center;margin-bottom: 20px;}
        form div {margin-bottom: 20px;}
        form input[type="text"] {padding: 10px;font-size: 16px;border: 1px solid #ddd;border-radius: 4px;width: calc(100% - 120px);box-sizing: border-box;}
        form button {padding: 10px 20px;font-size: 16px;color: #fff;background-color: #007bff; border: none;border-radius: 4px;cursor: pointer;}
        form button:hover {background-color: #0056b3;}
        .message {margin: 20px 0;font-size: 16px;}
        .message.error {color: red;}
        .message.success {color: green;}
        .button {display: inline-block;margin: 10px;padding: 15px 25px;font-size: 18px;color: #fff;text-decoration: none;border-radius: 8px;text-align: center;transition: background-color 0.3s, transform 0.3s;box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);}
        .button:hover { transform: scale(1.05);box-shadow: 0 6px 12px rgba(0, 0, 0, 0.4);}
        .button.back {background-color: #ff5722;}
        .button.back:hover {background-color: #e64a19; }
        .pagination {text-align: center;margin-top: 20px; padding: 12px;}
        .page-links {display: inline-block;}
        .page-link {display: inline-block;padding: 10px 15px;margin: 0 5px;text-decoration: none; background-color: #007bff;color: white;border-radius: 4px;transition: background-color 0.3s;         }
        .page-link.active {background-color: #0056b3;}
        .page-link:hover {background-color: #0056b3;}
        .table-container {gap: 0; display: flex;flex-direction: column;}
        .row {display: flex; flex-wrap: nowrap;  padding: 0; background-color: #fff;border: 0px solid #ddd;border-radius: 4px;}
        .cell {flex: 1;padding: 10px;text-align: center;border-right: 1px solid #ddd;box-sizing: border-box;}
        .cell:last-child {border-right: none;}
        .row.header {background-color: #f2f2f2;font-weight: bold;}
        .row:nth-child(even) {background-color: #f9f9f9;}
        .row:nth-child(odd) {background-color: #fff;}
    </style>
</head>
<body>
    <div class="container">
        <h1>İşlem Geçmişi</h1>

        <div class="message">
            <%
                String error = request.getParameter("error");
                String success = request.getParameter("success");
                if (error != null) {
            %>
            <p class="error">Error: <%= error %></p>
            <%
                }
                if (success != null) {
            %>
            <p class="success">Success: <%= success %></p>
            <% } %>
        </div>
        <form action="transactionHistory.jsp" method="get">
            <div class="form-container">
                <input type="text" name="searchId" placeholder="İşlem No ile Ara" value="<%= searchId != null ? searchId : "" %>" />
                <button type="submit" class="button">Ara</button>
            </div>
        </form>
        <div class="table-container">
            <div class="row header">
                <div class="cell">İşlem No</div>
                <div class="cell">Tarih</div>
                <div class="cell">Transfer</div>
                <div class="cell">TL Miktarı</div>
                <div class="cell">Para Birimi</div>
                <div class="cell">İşlem Sonrası Bakiye</div>
                <div class="cell">İşlem Yapılan Hesap No</div>
                <div class="cell">Alıcı Ad Soyad</div>
                <div class="cell">Gönderen Ad Soyad</div>
            </div>
            <%
                if (!pagedTransactions.isEmpty()) {
                    for (Transaction transaction : pagedTransactions) {
                        Date transactionDate = transaction.getTransactionDate();
            %>
            <div class="row">
                <div class="cell"><%= transaction.getTransactionId() %></div>
                <div class="cell"><%= turkish.format(transactionDate) %></div>
                <div class="cell"><%= transaction.getTransactionType() %></div>
                <div class="cell"><%= transaction.getAmount() %> TL</div>
                <div class="cell"><%= transaction.getCurrency() %></div>
                <div class="cell"><%= transaction.getBalanceAfterTransaction() %> TL</div>
                <div class="cell"><%= transaction.getOppositeAccountId() %></div>
                <div class="cell"><%= transaction.getOppositeName() %> <%= transaction.getOppositeLastName() %></div>
                <div class="cell"><%= transaction.getSenderName() %> <%= transaction.getSenderLastName() %></div>
            </div>
            <% 
                    }
                } else {
            %>
            <div class="row">
                <div class="cell" colspan="9">İşlem Bulunamadı.</div>
            </div>
            <% } %>
        </div>
        <div class="pagination">
            <% if (totalPages > 1) { %>
                <div class="page-links">
                    <% for (int i : pageNumbers) { %>
                        <a href="transactionHistory.jsp?page=<%= i %>&searchId=<%= searchId != null ? searchId : "" %>" class="page-link <%= (i == pageNumber) ? "active" : "" %>">
                            <%= i %>
                        </a>
                    <% } %>
                </div>
            <% } %>
        </div>

        <a href="account.jsp" class="button back">Geri Dön</a>
    </div>
</body>
</html>

