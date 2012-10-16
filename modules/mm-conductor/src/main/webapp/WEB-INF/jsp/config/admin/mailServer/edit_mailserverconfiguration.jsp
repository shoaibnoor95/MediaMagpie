<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.MailServerController"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<c:set var="title" value="Mail Server Configuration" scope="request"/>
<c:set var="activeMenu" value="config" scope="request"/>
<c:set var="activeSubMenu" value="mail_config" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request"/>
			
			<div id="content">
				<h1>
					${title}
				</h1>
				
				<div>
					<form:form commandName="conf"  id="myForm" cssClass="decorated">
					<fieldset>
						<legend>Edit Settings</legend>
						<dl>
							<dt>
								<label>Sender Name:</label>
							</dt>
							<dd>
								<form:input path="senderName" cssClass="req"/>
								<form:errors path="senderName" cssClass="error"/>
							</dd>
							<dd class="help">The name used in from address, e.g. MediaMagpie</dd>	
						</dl>
						<dl>
							<dt>
								<label>From address:</label>
							</dt>
							<dd>
								<form:input path="senderAddress" cssClass="req"/>
								<form:errors path="senderAddress" cssClass="error"/>
							</dd>
							<dd class="help"></dd>	
						</dl>
						<dl>
							<dt>
								<label>Email prefix:</label>
							</dt>
							<dd>
								<form:input path="emailPrefix"/>
								<form:errors path="emailPrefix" cssClass="error"/>
							</dd>
							<dd class="help">e.g. [MediaMagpie] or [My Company]</dd>	
						</dl>
						<dl>
							<dt>
								<label>Host name:</label>
							</dt>
							<dd>
								<form:input path="hostName" cssClass="req"/>
								<form:errors path="hostName" cssClass="error"/>
							</dd>
							<dd class="help"></dd>	
						</dl>
						<dl>
							<dt>
								<label>SMTP port:</label>
							</dt>
							<dd>
								<form:input path="port" cssClass="req"/>
								<form:errors path="port" cssClass="error"/>
							</dd>
							<dd class="help"></dd>	
						</dl>
						<dl>
							<dt>
								<label>User name:</label>
							</dt>
							<dd>
								<form:input path="userName" />
								<form:errors path="userName" cssClass="error"/>
							</dd>
							<dd class="help"></dd>	
						</dl>
						<dl>
							<dt>
								<label>Password:</label>
							</dt>
							<dd>
								<form:password path="password" showPassword="true" />
								<form:errors path="password" cssClass="error"/>
							</dd>
							<dd class="help"></dd>	
						</dl>
						<dl>
							<dt>
								<label>Password (confirm):</label>
							</dt>
							<dd>
								<form:password path="passwordConfirm" showPassword="true" />
								<form:errors path="passwordConfirm" cssClass="error"/>
							</dd>
							<dd class="help"></dd>	
						</dl>
						
						<dl class="buttons">
							<dt>
								
							</dt>
							<dd>
								<button type="button" onclick="document.location.href='<%=request.getContextPath()+MailServerController.getBaseRequestMappingUrl()+MailServerController.URL_MAILCONFIG%>'"><span>Cancel</span></button>
								<button type="submit" class="active"><span>Save</span></button>
							</dd>	
						</dl>
					</fieldset>
					</form:form>
				</div>
			</div>
