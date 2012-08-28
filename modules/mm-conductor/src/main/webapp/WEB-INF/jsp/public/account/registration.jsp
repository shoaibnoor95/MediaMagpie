<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false" %>
<%@include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<c:set var="title" value="Create New Account" scope="request"/>
	        <div id="content">
				<h1>
					${title}
				</h1>
				<c:if test="${not empty errormessage}">
					<span class="error">${errormessage}</span>
				</c:if>
				<div>
					<form:form commandName="registrationCommand" id="myForm" cssClass="decorated">
					<fieldset>
						<legend>New User Settings</legend>
						<dl>
							<dt>
								<label>Forename:</label>
							</dt>
							<dd>
								<form:input path="forename"/>
							</dd>
							<form:errors path="forename" cssClass="error"/>	
						</dl>
						<dl>
							<dt>
								<label>Surname:</label>
							</dt>
							<dd>
								<form:input path="surname"/>
							</dd>
							<form:errors path="surname" cssClass="error"/>
						</dl>
						<dl>
							<dt>
								<label>User:</label>
							</dt>
							<dd>
								<form:input path="user" cssClass="req"/>
							</dd>
							<dd class="help">Your user id you will use to login</dd>	
							<form:errors path="user" cssClass="error"/>
						</dl>
						<dl>
							<dt>
								<label>Password:</label>
							</dt>
							<dd>
								<form:input path="password" cssClass="req"/>
							</dd>
							<dd class="help"></dd>	
							<form:errors path="password" cssClass="error"/>
						</dl>
						<dl>
							<dt>
								<label>Password confirm:</label>
							</dt>
							<dd>
								<form:input path="passwordConfirm" cssClass="req"/>
							</dd>
							<dd class="help">Confirm your password</dd>	
							<form:errors path="passwordConfirm" cssClass="error"/>
						</dl>
						<dl>
							<dt>
								<label>EMail:</label>
							</dt>
							<dd>
								<form:input path="email" cssClass="req"/>
							</dd>
							<dd class="help">Your email address used to receive your activation link</dd>	
							<form:errors path="email" cssClass="error"/>
						</dl>
						<dl class="buttons">
							<dt>
								&nbsp;
							</dt>
							<dd>
								<button type="submit" class="active"><span>Register Now</span></button>
							</dd>	
						</dl>
					</fieldset>
					</form:form>
				</div>
			</div>
