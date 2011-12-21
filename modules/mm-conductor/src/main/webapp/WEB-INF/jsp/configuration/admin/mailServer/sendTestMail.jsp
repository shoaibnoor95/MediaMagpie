<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.MailServerController"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<c:set var="title" value="Send Test Mail" scope="request"/>
<c:set var="activeMenu" value="config" scope="request"/>
<c:set var="activeSubMenu" value="mail_config" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request"/>
<c:import url="/header"/>
			
			<div id="content">
				<h1>
					${title}
				</h1>
				
				<div>
				<form:form id="myForm" commandName="sendTestMailCommand" cssClass="decorated">
					<fieldset>
						<legend>Test your Mail Settings</legend>
						<dl>
							<dt>
								<label>To:</label>
							</dt>
							<dd>
								<form:input path="to" cssClass="req"/>
								<form:errors path="to" cssClass="error"/>
							</dd>
							<dd class="help"></dd>	
						</dl>
						<dl>
							<dt>
								<label>Subject:</label>
							</dt>
							<dd>
								<form:input path="subject" cssClass="req"/>
								<form:errors path="subject" cssClass="error"/>
							</dd>
							<dd class="help"></dd>	
						</dl>
						<dl>
							<dt>
								<label>Message:</label>
							</dt>
							<dd>
								<form:textarea path="message" cssStyle="height:150px" cssClass="req"/>
								<form:errors path="message" cssClass="error"/>
							</dd>
							<dd class="help"></dd>	
						</dl>
						<dl class="buttons">
							<dt>
								
							</dt>
							<dd>
								<button type="button" onclick="document.location.href='<%=MailServerController.getBaseRequestMappingUrl()+MailServerController.URL_MAILCONFIG%>'"><span>Cancel</span></button>
								<button type="submit" class="active"><span>Send</span></button>
							</dd>	
						</dl>
					</fieldset>
					</form:form>
				</div>	
			</div>
			
			<c:import url="/footer"/>