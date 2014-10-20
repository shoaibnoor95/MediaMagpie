<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>

<c:set var="title" value="Reset Password"  scope="request"/>
<head>
    <title>${title}</title>
</head>
<body>
	<c:choose>
		<c:when test="${not empty message}">
			<c:set var="title" value="New Password sent" scope="request" />
		</c:when>
		<c:otherwise>
			<c:set var="title" value="Reset your Password" scope="request" />
		</c:otherwise>
	</c:choose>
	<h2>${title}</h2>
	<c:choose>
		<c:when test="${empty message}">
			<c:if test="${not empty errormessage}">
				<span class="error">${errormessage}</span>
			</c:if>
			<p>After you have entered your login user (or email address) and pressed the send button the system will generate a new password and
				send it via email to you.</p>
			<p>With your new password you can login into the system and change your password on user's configuration page.</p>
			<br />
			<form:form commandName="passwordResetCommand" cssClass="form-inline" role="form">
				<div class="form-group">
					<label class="sr-only" for="user">User or Email address</label>
					<form:input path="user" cssClass="form-control" placeholder="User or Email address" />
					<form:errors path="user" cssClass="error" />
				</div>
				<button type="submit" class="btn btn-success">Reset Password</button>
			</form:form>
		</c:when>
		<c:otherwise>
			<div class="alert alert-info">${message}</div>
		</c:otherwise>
	</c:choose>
</body>
