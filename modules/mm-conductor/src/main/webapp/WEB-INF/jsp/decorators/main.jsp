<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://www.opensymphony.com/sitemesh/decorator" prefix="decorator"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<title>MediaMagpie - <decorator:title default="Welcome!" /></title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	
	<link rel="stylesheet" type="text/css" href="/static/css/styles-all2.css" />
	<link rel="stylesheet" type="text/css" href="/static/bootstrap/css/bootstrap.css" />
	<!-- Custom styles for this template -->
	<link rel="stylesheet" type="text/css" href="/static/bootstrap/css/sticky-footer-navbar.css"></link>
	
	<script src="/static/js/jquery-1.11.0.min.js" type="text/javascript"></script>
	<decorator:head />
</head>
<body>
	<div id="wrap">
		<nav class="navbar navbar-inverse navbar-fixed-top" role="navigation">
		<div class="container">
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
				<!-- main navigation imported from external jsp -->
				<ul class="nav navbar-nav">
					<c:import url="/mainNavigation" />
				</ul>
				<!-- login/logout link -->
				<ul class="nav navbar-right">
					<li><sec:authorize access="isAnonymous()">
							<a id="login" href="<%=request.getContextPath()%>/login">Login</a>
						</sec:authorize> <sec:authorize access="isAuthenticated()">
							<!-- For login user -->
							<c:url value="/j_spring_security_logout" var="logoutUrl" />
							<form action="${logoutUrl}" method="post" id="logoutForm">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
							</form>
							<script>
								function formSubmit() {
									document.getElementById("logoutForm").submit();
								}
							</script>

							<c:if test="${pageContext.request.userPrincipal.name != null}">
								<a href="javascript:formSubmit()"> Logout (${pageContext.request.userPrincipal.name})</a>
							</c:if>

						</sec:authorize></li>
					<sec:authorize access="isRememberMe()">
						<!-- # This user is login by "Remember Me Cookies".-->
					</sec:authorize>
					<sec:authorize access="isFullyAuthenticated()">
						<!-- # This user is login by username / password.-->
					</sec:authorize>
				</ul>
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
	<script src="/static/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
	<script src="/static/js/bootstrap.hoverdropdown.js" type="text/javascript"></script>
</body>
</html>