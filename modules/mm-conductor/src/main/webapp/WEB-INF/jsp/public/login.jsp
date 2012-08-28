<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ page import="org.springframework.security.web.WebAttributes" %>
<%@ page import="org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter" %>
<%@ page import="org.springframework.security.core.AuthenticationException" %>

<c:set var="title" value="Login" scope="request"/>
	        <div id="content">
		        <h1>Login</h1>
	            
				<c:if test="${not empty param.login_error}">
				    <div class="error">
				        <fmt:message key="login.msg.failure"/><br />
				        <fmt:message key="login.failure.reason"/>: <%= ((AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)).getMessage() %><br/>
				    </div>
				</c:if>
				<div class="section">
				    <form name="f" action="<c:url value="/loginProcess" />" method="post">
				        <fieldset>
				            <div class="field">
				                <div class="label"><label for="j_username"><fmt:message key="login.username"/>:</label></div>
				                <div class="output">
				                    <input type="text" name="j_username" id="j_username" <c:if test="${not empty param.login_error}">value="<%= session.getAttribute(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_LAST_USERNAME_KEY) %>"</c:if> />
				                </div>
				            </div>
				            <div class="field">
				                <div class="label"><label for="j_password"><fmt:message key="login.password"/>:</label></div>
				                <div class="output">
				                    <input type="password" name="j_password" id="j_password" />
				                </div>
				            </div>
				            <div class="field">
				                <div class="label"><label for="remember_me"><fmt:message key="login.rememberMe"/>:</label></div>
				                <div class="output">
				                    <input type="checkbox" name="_spring_security_remember_me" id="remember_me" />
				                </div>
				            </div>
				            <div class="form-buttons">
								<button type="submit" class="active"><span><fmt:message key="button.login"/></span></button>
				            </div>
				            <a <%/*onclick="requestNewPassword();"*/%> href="<c:url value="/public/account/resetPassword"></c:url>"><fmt:message key="login.password.forgotton"/></a>
				        </fieldset>
				    </form>
				    <br/>
				    <i>If you want to test <fmt:message key="main.name"/> you can use 'guest' / 'guest' for login.</i>
				</div>
				
				<br/>	
				<hr/>
				<div class="section">
  					<h2>New on MediaMagpie?</h2>
  					Create an account, it's easy and free.
  					<br/>
  					<br/>
  					<div class="button"><a href="<%=request.getContextPath()%>/public/account/signup"><span>Create a new Account</span></a></div>
  					<br>
				 </div>
	        </div>