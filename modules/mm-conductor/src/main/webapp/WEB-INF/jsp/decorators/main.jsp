<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator"
           prefix="decorator" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	<head>
	    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	    <meta http-equiv="expires" content="0"/>
	    <title>${title}</title>
	    <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/styles-all.css"/>" />
	    <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/ui-lightness/jquery-ui-1.8.13.custom.css"/>" />
	    <%@ include file="/WEB-INF/jsp/includes/js.jsp" %>
	    <decorator:head />
	</head>
	<body onload="focusFirstEnabledField()">
	    <div id="page"> 
	        <div id="page_top">
	            <div id="logo">
	                <a href="<%=request.getContextPath()%>/"><span><fmt:message key="main.name"/></span></a>
	            </div>
                <%@ include file="/WEB-INF/jsp/navigation/mainNavi.jsp" %>
	            <div id="loginNavi">
					<%java.security.Principal p = request.getUserPrincipal();
					if(p == null) {%>
	                <a href="<%=request.getContextPath()%>/login">Login</a>
                	<%}else{%>
	                <a href="<%=request.getContextPath()%>/j_spring_security_logout">Logout (<%=p.getName()%>)</a>
                	<%}%>
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
                <decorator:body />
            </div>
	        <div id="page_bottom">
	            <div id="footer">
	                &copy; 2010-2011 Ralf Wehner | 
	                <a href="<c:url value="mailto:info@ralfwehner.org"><c:param name="subject" value="[Media-Butler]"></c:param></c:url> Feedback">Feedback</a> | 
	                <a href="http://ralfwehner.dyndns.org:8082/" >Bug Report</a>
	                <br/>
	                Version: ${version}, 
	                Revision: <span title="${revision}" onclick="this.innerHTML='${revision}'">${fn:substring(revision,0,6)}</span>,  
	                Built on: ${time} 
	                <c:if test="${deployMode != 'live'}">, Mode: ${deployMode}</c:if>
	            </div>
	        </div>
	    </div>
	</body> 
</html>