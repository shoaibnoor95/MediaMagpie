<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.UserConfiguratonControllerS1"%>

<c:set var="title" value="User Configuration (1/2)" scope="request"/>
<c:set var="activeMenu" value="config" scope="request"/>
<c:set var="activeSubMenu" value="user_config" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request"/>
			
			<div id="content">
				<h1>
					${title}
				</h1>
				
				<div>
					<form:form commandName="userConfigurationCommand"  id="myForm" cssClass="decorated">
					<fieldset>
						<legend>Person</legend>
						<dl>
							<dt>
								<label>Login:</label>
							</dt>
							<dd>
								${userConfigurationCommand.name}
							</dd>
						</dl>
						<dl>
							<dt>
								<label>Name:</label>
							</dt>
							<dd>
								<form:input path="forename" cssClass="req"/>
							</dd>
							<dd class="help"></dd>	
							<form:errors path="forename" cssClass="error"/>
						</dl>
						<dl>
							<dt>
								<label>Surname:</label>
							</dt>
							<dd>
								<form:input path="surname" cssClass="req"/>
							</dd>
							<dd class="help"></dd>	
							<form:errors path="surname" cssClass="error"/>
						</dl>
						<dl>
							<dt>
								<label>Password:</label>
							</dt>
							<dd>
								<form:password path="password" showPassword="false" cssClass="req"/>
							</dd>
							<dd class="help">Only fill in this field if you want to CHANGE your current password</dd>	
							<form:errors path="password" cssClass="error"/>
						</dl>
						<dl>
							<dt>
								<label>Password Confirm:</label>
							</dt>
							<dd>
								<form:password path="passwordConfirm" showPassword="false" cssClass="req"/>
							</dd>
							<dd class="help">Confirm your password only if you want to CHANGE your current password</dd>	
							<form:errors path="passwordConfirm" cssClass="error"/>
						</dl>
						<dl class="buttons">
							<dt>
								&nbsp;
							</dt>
							<dd>
								<button type="button" onclick="document.location.href='<%=request.getContextPath()+UserConfiguratonControllerS1.getBaseRequestMappingUrl()+UserConfiguratonControllerS1.URL_USERCONFIG%>'"><span>Cancel</span></button>
								<button type="submit" class="active"><span>Next</span></button>
							</dd>	
						</dl>
					</fieldset>
					</form:form>
				</div>
			</div>