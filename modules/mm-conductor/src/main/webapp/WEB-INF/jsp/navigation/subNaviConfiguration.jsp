<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.UserConfiguratonControllerS1"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.AdministrationController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.MailServerController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.AwsConfigurationController"%>
<div id="dropdown-config" class="dropdown dropdown-relative dropdown-tip">
<ul class="dropdown-menu">
	<li class="<c:if test="${activeSubMenu == 'user_config'}">current</c:if>">
		<a href="<%=request.getContextPath()+UserConfiguratonControllerS1.getBaseRequestMappingUrl()+UserConfiguratonControllerS1.URL_USERCONFIG%>${currentUserId}">User Configuration</a>
        <core:ErrorHint requiredSetupTasks="${requiredSetupTasks}" subItemName="user_config"/>
	</li>
    <li class="<c:if test="${activeSubMenu == 'aws_s3'}">current</c:if>">
        <a href="<%=request.getContextPath()+AwsConfigurationController.getBaseRequestMappingUrl()+AwsConfigurationController.URL_S3CONFIG%>${currentUserId}">AWS S3 Configuration</a>
        <core:ErrorHint requiredSetupTasks="${requiredSetupTasks}" subItemName="user_config"/>
    </li>
	<li class="<c:if test="${activeSubMenu == 'config'}">current</c:if>">
		<a href="<%=request.getContextPath()+AdministrationController.getBaseRequestMappingUrl()+AdministrationController.URL_MAINCONFIG%>">Main Configuration</a>
        <core:ErrorHint requiredSetupTasks="${requiredSetupTasks}" subItemName="config"/>
	</li>
	<li class="<c:if test="${activeSubMenu == 'mail_config'}">current</c:if>">
		<a href="<%=request.getContextPath()+MailServerController.getBaseRequestMappingUrl()+MailServerController.URL_MAILCONFIG%>">Mail Configuration</a>
        <core:ErrorHint requiredSetupTasks="${requiredSetupTasks}" subItemName="mail_config"/>
	</li>
</ul>
</div>