<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	<head>
	    <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1" />
	    <meta http-equiv="expires" content="0"/>
        <c:if test="${not empty title}"><title>${title}</title></c:if>
	    
	    <link rel="stylesheet" type="text/css" href="/static/css/styles-all2.css" />
	    <link rel="stylesheet" href="/static/bootstrap/css/bootstrap.css" />
	    <!-- Custom styles for this template -->
	    <link rel="stylesheet" href="/static/bootstrap/css/sticky-footer-navbar.css"></link>
	    
        <script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
<!-- 	    <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/styles-all.css"/>" />
        <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/effects.css"/>" />
	    <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/ui-lightness/jquery-ui-1.8.13.custom.css"/>" />
        <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/jquery.dropdown.css"/>" />

        <script src="https://code.jquery.com/jquery-1.10.2.js"></script>
        <script src="https://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery.dropdown.js" ></script>-->
<!-- 		<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/prefixfree.min.js"></script>
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
		</script>-->
		
	    <decorator:head />
	</head>
	<body >
		<div id="wrap">
			<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
				<div class="container">
					<div class="navbar-header">
						<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
							<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
						</button>
						<a class="navbar-brand" href="<%=request.getContextPath()%>/"> <fmt:message key="main.name" />
						</a>
					</div>
					<div class="collapse navbar-collapse">
						<ul class="nav navbar-nav">
							<c:import url="/mainNavigation" />
						</ul>
						<form class="navbar-form navbar-right" role="form" name="f" action="/loginProcess" method="post">
							<!-- <div class="form-group">
	                            <input placeholder="Email" class="form-control" type="text" name="j_username">
	                        </div>
	                        <div class="form-group">
	                            <input placeholder="Password" class="form-control" type="password" name="j_password">
	                        </div>
	                        <button type="submit" class="btn btn-success">Sign in</button>-->
							<%
							    java.security.Principal p = request.getUserPrincipal();
							    if (p == null) {
							%>
							<a href="<%=request.getContextPath()%>/login">Login</a>
							<%
							    } else {
							%>
							<a href="<%=request.getContextPath()%>/j_spring_security_logout">Logout (<%=p.getName()%>)
							</a>
							<%
							    }
							%>
						</form>
	
					</div>
					<!--/.nav-collapse -->
				</div>
			</div>
			<!-- /.navbar -->
			<div class="container">
				<decorator:body />
			</div>
		</div>
		<!-- wrap -->
		<div id="footer">
			<div class="container">
				<c:import url="/footer" />
			</div>
		</div>
	
	
		<!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="/static/bootstrap/js/bootstrap.min.js"></script>
    <script src="/static/js/bootstrap.hoverdropdown.js"></script>
    <!-- <script type="text/javascript">
    // prefixfree plugin for jquery to enable prefixfree functionality for css changes by jquery
					(function($, self) {
						if (!$ || !self) {
							return;
						}
						for (var i = 0; i < self.properties.length; i++) {
							var property = self.properties[i], camelCased = StyleFix.camelCase(property), PrefixCamelCased = self
									.prefixProperty(property, true);

							$.cssProps[camelCased] = PrefixCamelCased;
						}
					})(window.jQuery, window.PrefixFree);
				</script>-->
	</body> 
</html>