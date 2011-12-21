<!DOCTYPE HTML>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<meta http-equiv="expires" content="0"/>

	<title>${title}</title>
	<script src="//ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
	<script src="//ajax.googleapis.com/ajax/libs/jqueryui/1.8.15/jquery-ui.min.js"></script>
	
	<script src="<%=request.getContextPath()%>/static/js/jquery.iframe-transport.js"></script>
	<script src="<%=request.getContextPath()%>/static/js/jquery.fileupload.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/utils.js" ></script>
 	
 	<link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/jquery.fileupload-ui.css">
	<link type="text/css" href="<%=request.getContextPath()%>/static/css/ui-lightness/jquery-ui-1.8.13.custom.css" rel="stylesheet" />
 	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/css/styles-all.css" />
</head>
<body onload="focusFirstEnabledField()">
	TODO rwe: Can header5.jsp be deleted?
	<div id="page"> 
<c:import url="/mainNavi"/>
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