<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<META http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<title><fmt:message key="welcome.title"/></title>
		<link rel="stylesheet" type="text/css" href="/static/css/mediamagpie-all.css" />
		<script type="text/javascript" src="http://www.google.com/jsapi"></script>
		<script type="text/javascript">
//			google.load('prototype', '1.6');
			google.load('jquery', '1.6.2');
		</script>
	</head>
	<body class="yui-skin-sam">
		
		<div id="page"> 
			<c:set var="active" value="data" scope="request"/>
			<c:import url="/mainNavi"/>
			
			<div id="content">
				<h1>
					<fmt:message key="welcome.title"/>
				</h1>
				<p>
					Locale = ${pageContext.response.locale}
				</p>
				<hr>	
				<ul>
					<li> <a href="?locale=en_us">us</a> |  <a href="?locale=en_gb">gb</a> | <a href="?locale=es_es">es</a> | <a href="?locale=de_de">de</a> </li>
				</ul>
				<ul>
					<li><a href="/admin/users/listUsers">list users</a></li>
				</ul>
				<ul>
					<li><a href="/public/google-chart-tools">CT's example of google chart tools (static)</a></li>
				</ul>
				<ul>
					<li><a href="/news/google-jsapi-1">Google chart tools (with AJAX)</a></li>
				</ul>
			</div>
			
			<c:import url="/footer"/>
		</div>
	</body>	
</html>