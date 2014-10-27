<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page session="true"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<%@ page import="org.springframework.security.web.WebAttributes"%>
<%@ page import="org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter"%>
<%@ page import="org.springframework.security.core.AuthenticationException"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<title>Login</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/signin.css"></link>
<script type="text/javascript">
	/*	$(document).ready(function() {
	 $('form:first *:input[type!=hidden]:first').focus();
	 });*/
</script>
</head>
<body onload='document.loginForm.username.focus();'>
	<form class="form-signin" role="form" name="loginForm" action="<c:url value='/auth/login_check?targetUrl=${targetUrl}' />" method="post">
		<c:if test="${not empty error}">
			<div class="alert alert-danger" role="alert">${error}</div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="alert alert-success" role="alert">${msg}</div>
		</c:if>
		<h2 class="form-signin-heading">Please sign in</h2>
		<c:if test="${not empty param.login_error}">
			<div class="alert alert-danger" role="alert">
				<fmt:message key="login.msg.failure" />
				<br />
				<fmt:message key="login.failure.reason" />
				:
				<%=((AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)).getMessage()%><br />
			</div>
		</c:if>

		<input type="text" name="username" id="j_username" class="form-control" placeholder="<fmt:message key="login.username" />" required
			autofocus />
		<input type="password" name="password" id="j_password" class="form-control" placeholder="<fmt:message key="login.password"/>" required />
		<!-- if this is login for update, ignore remember me check -->
		<c:if test="${empty loginUpdate}">
			<label class="checkbox"> <input type="checkbox" name="remember-me" id="remember_me"></input> <fmt:message key="login.rememberMe" />
			</label>
		</c:if>

		<button class="btn btn-lg btn-primary btn-block" type="submit">
			<fmt:message key="button.login" />
		</button>
		<br />
		<a <%/*onclick="requestNewPassword();"*/%> href="<c:url value="/public/account/resetPassword"></c:url>"><fmt:message
				key="login.password.forgotton" /></a>
		<br />
		<span class="help-block">If you want to test <fmt:message key="main.name" />, please sign in with 'guest' / 'guest'.
		</span>
	</form>

	<h2>New on MediaMagpie?</h2>
	Create an account, it's easy and free.
	<br />
	<button type="button" class="btn btn-success" onclick="document.location.href='<%=request.getContextPath()%>/public/account/signup'">Create
		a new Account</button>
</body>
</html>