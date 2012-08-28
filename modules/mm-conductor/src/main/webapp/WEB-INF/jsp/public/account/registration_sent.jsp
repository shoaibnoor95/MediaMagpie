<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false" %>
<%@include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<c:set var="title" value="Activtion link was sent" scope="request"/>
			<div id="content">
				<h1>
					${title}
				</h1>
				<c:if test="${not empty errormessage}">
					<span class="error">${errormessage}</span>
				</c:if>
				<h2>Your Activation Email was successfully sent to ${registrationCommand.email}.</h2>
				Please activate your Account now with your Activation link your have provides within your Activation Email.
			</div>
