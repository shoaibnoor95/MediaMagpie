<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<c:set var="title" value="Registration successfully done" scope="request"/>
			
			<div id="content">
				<h1>
					${title}
				</h1>
				<c:if test="${not empty errormessage}">
					<span class="error">${errormessage}</span>
				</c:if>
				<h2>Congratulations ${user.forename}. The user <b>${user.name}</b> is now registered.</h2>
				You can not start using this service. Try to login with your user and password.
			</div>
