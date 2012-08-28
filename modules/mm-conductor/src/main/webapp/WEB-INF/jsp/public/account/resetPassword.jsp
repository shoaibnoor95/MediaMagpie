<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<c:choose>
	<c:when test="${not empty message}">
		<c:set var="title" value="New Password sent" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="title" value="Reset your Password" scope="request"/>
	</c:otherwise>
</c:choose>
			<div id="content">
				<h1>
					${title}
				</h1>
				<c:choose>
					<c:when test="${empty message}">
						<c:if test="${not empty errormessage}">
							<span class="error">${errormessage}</span>
						</c:if>
						<h3>After you have entered your login user (or email address) and pressed the send buttond the system will generate a new password and send it via email to you. With the new password you can login to the system and change the password in user's configuration page.</h3>
						<br/><br/>
						<form:form commandName="passwordResetCommand" >
							<dl>
								<dt>
									<label>User or Email:</label>
								</dt>
								<dd>
									<form:input path="user" cssClass="req"/>
								</dd>
								<form:errors path="user" cssClass="error"/>
							</dl>
							<dl class="buttons">
								<dt>
									&nbsp;
								</dt>
								<dd>
									<button type="submit" class="active"><span>Reset Password</span></button>
								</dd>	
							</dl>
						</form:form>
					</c:when>
					<c:otherwise>
						<h3>${message}</h3>
					</c:otherwise>
				</c:choose>
			</div>
