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
	    <decorator:head />
	</head>
	<body >
		<div id="wrap">
			<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
				<div class="container"><!-- TODO rwe: is this container necessary? -->
					<!-- Brand and toggle get grouped for better mobile display -->
					<div class="navbar-header">
						<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
							<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
						</button>
						<a class="navbar-brand" href="<%=request.getContextPath()%>/"> <fmt:message key="main.name" />
						</a>
					</div>
					<!-- Collect the nav links, forms, and other content for toggling -->
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
							<%java.security.Principal p = request.getUserPrincipal();
							  if (p == null) {%>
							<a href="<%=request.getContextPath()%>/login">Login</a>
							<%} else {%>
							<a href="<%=request.getContextPath()%>/j_spring_security_logout">Logout (<%=p.getName()%>)
							</a>
							<%}%>
						</form>
	
					</div>
					<!--/.nav-collapse -->
				</div>
			</nav>
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
	</body> 
</html>