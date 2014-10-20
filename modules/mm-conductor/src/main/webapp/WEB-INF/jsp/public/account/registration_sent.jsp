<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false"%>
<%@include file="/WEB-INF/jsp/general/taglibs.jsp"%>

<c:set var="title" value="Activtion link was sent" scope="request" />
<h2>${title}</h2>
<c:if test="${not empty errormessage}">
	<div class="alert alert-danger">${errormessage}</div>
</c:if>
<div class="alert alert-success">
	Your Activation Email was successfully sent to ${registrationCommand.email}.<br /> Please activate your Account now with your Activation
	link your have provides within your Activation Email.
</div>
