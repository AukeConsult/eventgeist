<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Serverinfo</title>
<style type="text/css">
body {
	background-image: url('https://cdn.crunchify.com/bg.png');
}
</style>
</head>

<%
String uri = request.getServerName() +       
        ":" +                           
        request.getServerPort();                
%>
<body>

	<div style="font-family: verdana; padding: 10px; border-radius: 10px; font-size: 12px; text-align: center;">
            
            <li>Public ip addresse : <%=uri %></li>
	
	</div>            

</body>
</html>