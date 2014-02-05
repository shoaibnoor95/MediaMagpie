<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	<head>
	    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	    <meta http-equiv="expires" content="0"/>
        <c:if test="${not empty title}"><title>${title}</title></c:if>
	    
	    <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/styles-all.css"/>" />
        <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/effects.css"/>" />
	    <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/ui-lightness/jquery-ui-1.8.13.custom.css"/>" />
        <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/jquery.dropdown.css"/>" />

        <!-- TODO rwe: use bootstrap. but before we have to fix the context menue issue. See: http://getbootstrap.com/customize/ -->
        <!-- <link rel="stylesheet" href="<%=request.getContextPath()%>/static/bootstrap/css/bootstrap.css" />-->

        <!-- TODO rwe: use bootstrap. but before we have to fix the context menue issue. See: http://getbootstrap.com/customize/ -->
        <!-- <script src="<%=request.getContextPath()%>/static/bootstrap/js/bootstrap.min.js"></script>-->
        
        <script src="https://code.jquery.com/jquery-1.10.2.js"></script>
        <script src="https://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery.dropdown.js" ></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/utils.js" ></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/prefixfree.min.js"></script>
		<script type="text/javascript">
		     // prefixfree plugin for jquery to enable prefixfree functionality for css changes by jquery
			(function($, self){
				if(!$ || !self) {
				    return;
				}
				for(var i=0; i<self.properties.length; i++) {
				    var property = self.properties[i],
				        camelCased = StyleFix.camelCase(property),
				        PrefixCamelCased = self.prefixProperty(property, true);
				    
				    $.cssProps[camelCased] = PrefixCamelCased;
				}
				})(window.jQuery, window.PrefixFree);
		</script>
	    <decorator:head />
	</head>
	<body >
	    <div id="page"> 
	        <div id="page_top">
	            <div id="logo">
	                <a href="<%=request.getContextPath()%>/"><span><fmt:message key="main.name"/></span></a>
	            </div>
                <c:import url="/mainNavigation"/>
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
                <decorator:body />
            </div>
	        <div id="page_bottom">
	           <c:import url="/footer"/>
	        </div>
	    </div>
	</body> 
</html>