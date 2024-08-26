<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="jakarta.servlet.http.HttpServletRequest"%>
<%@page import="jakarta.servlet.http.HttpSession"%>
<%@page import="jakarta.servlet.http.HttpServletRequest" %>
<%@page import="jakarta.servlet.http.HttpServletResponse" %>
<% response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); %>
<% response.setHeader("Pragma", "no-cache"); %>
<% response.setHeader("Expires", "0"); %>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" type="image/png" href="https://avatars.mds.yandex.net/i?id=9b727c154ebf81ad18c40bdc4d51d82b1196cfba-5601142-images-thumbs&n=13"/>
    <meta charset="UTF-8">
    <title>Bankam</title>
    <style>
        body { font-family: Arial, sans-serif; background: url('https://avatars.mds.yandex.net/i?id=d00615946d05cdfd02f0bd01df39cea21042d1ce-5877635-images-thumbs&n=13') no-repeat center center; background-size: cover; margin: 0; padding: 200px; color: #fff; }
        .container { max-width: 800px; margin: auto; background: #000; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.3); }
        h1 { color: #fff; }
        .button { display: inline-block; margin: 10px; padding: 15px 25px; font-size: 18px; color: #fff; text-decoration: none; border-radius: 8px; text-align: center; transition: background-color 0.3s, transform 0.3s; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.3); }
        .button:hover { transform: scale(1.05); box-shadow: 0 6px 12px rgba(0, 0, 0, 0.4); }
        .button.about { background-color: #4caf50; }
        .button.about:hover { background-color: #388e3c; }
        .form-container { margin: 20px 0; }
        .form-container input { padding: 10px; font-size: 16px; border: 1px solid #555; border-radius: 5px; width: 100%; margin-bottom: 10px; background: #333; color: #fff; }
        .form-container button { padding: 10px; font-size: 16px; color: #fff; background-color: #007bff; border: none; border-radius: 5px; width: 100%; cursor: pointer; transition: background-color 0.3s; }
        .form-container button:hover { background-color: #0056b3; }
        .message { margin: 20px 0; font-size: 16px; }
        .message.error { color: red; }
        .message.success { color: green; }
        .bank-image { width: 15%; height: auto; display: block; margin: 0 auto 20px; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Bankamıza Hoş Geldiniz</h1>
        
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
        
        
        <img src="https://avatars.mds.yandex.net/i?id=33ba3daf073a7bbe9be8a455534ff9c875b31b5c-5219738-images-thumbs&n=13" alt="Bank Image" class="bank-image" />

        <div class="form-container">
            <h2>Giriş</h2>
            <form action="LoginServlet" method="post">
                <input type="text" name="username" autocomplete="on" placeholder="Kullanıcı Adı" required  />
                <input type="password" name="password" autocomplete="off" placeholder="Şifre" required />
                <button type="submit">Giriş yap</button>
            </form>
        </div>

        <div class="form-container">
            <h2>Yeni Hesap Oluştur</h2>
            <form action="register.jsp" method="post">
                <button type="submit">Kayıt Ol</button>
            </form>
        </div>
        <a href="about.jsp" class="button about">Hakkımızda</a>
        
    </div>
</body>
</html>
