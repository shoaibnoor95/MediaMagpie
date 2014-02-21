<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<c:set var="title" value="Registration successfully done" scope="request" />

<h2>${title}</h2>
<c:if test="${not empty errormessage}">
	<div class="alert alert-danger">${errormessage}</div>
</c:if>
<div class="alert alert-success">
	Congratulations ${user.forename}. The user <b>${user.name}</b> is now registered.<br /> You can not start using this service. Try to login
	with your user and password.
</div>
