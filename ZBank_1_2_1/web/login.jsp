<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <link rel="shortcut icon" type="image/png" href="https://avatars.mds.yandex.net/i?id=9b727c154ebf81ad18c40bdc4d51d82b1196cfba-5601142-images-thumbs&n=13"/>
    <meta charset="UTF-8">
    <title>Giriş Yap</title>
    <style>
        body { font-family: Arial, sans-serif; background-color: #f2f2f2; background-image: url('https://avatars.mds.yandex.net/i?id=d00615946d05cdfd02f0bd01df39cea21042d1ce-5877635-images-thumbs&n=13'); background-size: cover; background-repeat: no-repeat; background-position: center; margin: 0; padding: 350px; }
        .container { max-width: 800px; margin: auto; background: #000; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0, 0, 0, 0.3); color: #fff; }
        h1 { color: #fff; }
        .form-container { margin: 20px 0; }
        .form-container input { padding: 10px; font-size: 16px; border: 1px solid #ccc; border-radius: 5px; width: 100%; box-sizing: border-box; margin-bottom: 10px; }
        .form-container button { padding: 10px; font-size: 16px; color: #fff; background-color: #007bff; border: none; border-radius: 5px; width: 100%; cursor: pointer; }
        .form-container button:hover { background-color: #0056b3; }
        .message { margin: 20px 0; font-size: 16px; text-align: center; }
        .message.error { color: red; }
        .message.success { color: green; }
        .back-link { display: block; margin-top: 20px; text-align: center; text-decoration: none; color: #007bff; }
        .back-link:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="container">
        <h1>Giriş</h1>

        <div class="message">
            <%
                String error = request.getParameter("error");
                if (error != null) {
            %>
            <p class="error">Geçersiz kullanıcı adı veya şifre.Lütfen tekrar deneyin.</p>
            <% } %>
        </div>

        <div class="form-container">
            <form action="LoginServlet" method="post">
                <input type="text" name="username" placeholder="Kullanıcı Adı" required />
                <input type="password" name="password" placeholder="Şifre" required />
                <button type="submit">Giriş yap</button>
            </form>
        </div>

        <a href="index.jsp" class="back-link">Ana Sayfa</a>
    </div>
</body>
</html>

