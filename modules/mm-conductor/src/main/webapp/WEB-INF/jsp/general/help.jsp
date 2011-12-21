<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Online Help</title>
		<link rel="stylesheet" type="text/css" href="/css/dap/dap-all.css" />
		<script>
			function show(){
				document.getElementById('login').submit();
			}
		</script>
	</head>
	<body onload="show()">
		Loading Online Help ... Please wait.
		<form action="${wikiBaseUrl}/login.action" method="post" id="login" style="display:none">
			<input type="text" name="os_username" value="${userName}"/>
			<input type="password" name="os_password" value="${password}"/>
			<input type="text" name="os_destination" value="${path}"/>
			<input type="text" name="os_cookie" value="true"/>
		</form>
	</body>
</html>	