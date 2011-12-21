<%/*<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">*/%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<meta http-equiv="expires" content="0"/>

	<title>${title}</title>
 	<link rel="stylesheet" type="text/css" href="<c:url value="/static/css/styles-all.css"/>" />
	<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery-1.6.4.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery-ui-1.8.13.custom.min.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/utils.js" ></script>
	<link rel="stylesheet" type="text/css" href="<c:url value="/static/css/ui-lightness/jquery-ui-1.8.13.custom.css"/>" />
</head>
<body onload="focusFirstEnabledField()">
	<div id="page"> 
		<div id="page_top">
			<div id="logo">
				<a href="<%=request.getContextPath()%>/"><span><fmt:message key="main.name"/></span></a>
			</div>
			<c:import url="/mainNavi"/>
			<div id="loginNavi">
<%java.security.Principal  principal = request.getUserPrincipal();
if(principal == null) {%>
				<a href="<%=request.getContextPath()%>/login">Login</a>
<%}else{%>
				<a href="<%=request.getContextPath()%>/j_spring_security_logout">Logout (<%=principal.getName()%>)</a>
<%}%>
<%/*<br/>
<a href="?locale=en_us">us</a> | <a href="?locale=de_de">de</a>
*/%>
			</div>
		</div>
		<div id="page_middle">
			<div id="sub_navi">
				<div id="subNavi">
					<c:choose>
						<c:when test="${not empty urlSubMenu}">
							<c:import url="${urlSubMenu}"/>
						</c:when>
						<c:otherwise>
						&nbsp;
						</c:otherwise>
					</c:choose>
				</div>
			</div>