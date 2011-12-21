<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.MailServerController"%>
<c:set var="title" value="Mail Server Configuration" scope="request"/>
<c:set var="activeMenu" value="config" scope="request"/>
<c:set var="activeSubMenu" value="mail_config" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request"/>
<c:import url="/header"/>
			
			<div id="content">
				<h1>
					${title}
				</h1>
				
				<c:if test="${!empty conf.hostName}">
				<h2><img src="<%=request.getContextPath()%>/static/images/famfamfam_silk/email_edit.png" align="absmiddle" /> 
					<a href="<%=request.getContextPath()+MailServerController.getBaseRequestMappingUrl()+MailServerController.URL_SEND_TEST_MAIL%>">Send Test Mail</a></h2>
				</c:if>
				
				<dl>
					<dt>
						<label>Sender Name:</label>
					</dt>
					<dd>${conf.senderName}</dd>	
				</dl>
				<dl>
					<dt>
						<label>From address:</label>
					</dt>
					<dd>${conf.senderAddress}</dd>	
				</dl>
				<dl>
					<dt>
						<label>Email prefix:</label>
					</dt>
					<dd>${conf.emailPrefix}</dd>	
				</dl>
				<dl>
					<dt>
						<label>Host name:</label>
					</dt>
					<dd>${conf.hostName}</dd>	
				</dl>
				<dl>
					<dt>
						<label>SMTP port:</label>
					</dt>
					<dd>${conf.port}</dd>	
				</dl>
				<dl>
					<dt>
						<label>User name:</label>
					</dt>
					<dd>${conf.userName}</dd>	
				</dl>
				
				<dl>
					<dt>
						<label>&nbsp;</label>
					</dt>
					<dd><button type="button" onclick="document.location.href='<%=request.getContextPath()+MailServerController.getBaseRequestMappingUrl()+MailServerController.URL_MAILCONFIG_EDIT%>'" class="active"><span>Edit</span></button></dd>	
				</dl>
			</div>
				                
			<c:import url="/footer"/>
