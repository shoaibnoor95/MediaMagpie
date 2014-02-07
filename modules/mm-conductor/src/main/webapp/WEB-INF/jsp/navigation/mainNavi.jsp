<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.UserConfiguratonControllerS1"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.AdministrationController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.MailServerController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.AwsConfigurationController"%>
<ul class="nav navbar-nav">
	<li class="<c:if test="${activeMenu == 'welcome'}">active</c:if>"><a href="<%=request.getContextPath()%>/welcome">Welcome</a></li>

	<li class="dropdown<c:if test="${activeMenu == 'media'}"> active</c:if>"><a href="#" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown">Media<b
			class="caret"></b></a>
		<ul class="dropdown-menu">
			<li class="<c:if test="${activeSubMenu == 'mediaSearch'}">active</c:if>"><a
				href="<%=request.getContextPath()%>/media/search_pictures">Medias</a></li>
			<li class="<c:if test="${activeSubMenu == 'listAlbums'}">active</c:if>"><a href="<%=request.getContextPath()%>/media/album/list">Albums</a>
			</li>
			<li class="<c:if test="${activeSubMenu == 'upload'}">active</c:if>"><a href="<%=request.getContextPath()%>/upload/file-upload">Upload</a>
			</li>
			<li class="divider"></li>
			<li class="<c:if test="${activeSubMenu == 'trash'}">active</c:if>"><a href="<%=request.getContextPath()%>/trash/content">Trash</a></li>
		</ul></li>

	<li class="dropdown<c:if test="${activeMenu == 'config'}"> active</c:if>"><a href="#" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown">Configuration<b
			class="caret"></b></a> <core:ErrorHint requiredSetupTasks="${requiredSetupTasks}" mainItemName="config" />
		<ul class="dropdown-menu">
			<li class="<c:if test="${activeSubMenu == 'user_config'}">active</c:if>"><a
				href="<%=request.getContextPath()+UserConfiguratonControllerS1.getBaseRequestMappingUrl()+UserConfiguratonControllerS1.URL_USERCONFIG%>${currentUserId}">User
					Configuration</a> <core:ErrorHint requiredSetupTasks="${requiredSetupTasks}" subItemName="user_config" /></li>
			<li class="<c:if test="${activeSubMenu == 'aws_s3'}">active</c:if>"><a
				href="<%=request.getContextPath()+AwsConfigurationController.getBaseRequestMappingUrl()+AwsConfigurationController.URL_S3CONFIG%>${currentUserId}">AWS
					S3 Configuration</a> <core:ErrorHint requiredSetupTasks="${requiredSetupTasks}" subItemName="user_config" /></li>
			<li class="<c:if test="${activeSubMenu == 'config'}">active</c:if>"><a
				href="<%=request.getContextPath() + AdministrationController.getBaseRequestMappingUrl() + AdministrationController.URL_MAINCONFIG%>">Main
					Configuration</a> <core:ErrorHint requiredSetupTasks="${requiredSetupTasks}" subItemName="config" /></li>
			<li class="<c:if test="${activeSubMenu == 'mail_config'}">active</c:if>"><a
				href="<%=request.getContextPath() + MailServerController.getBaseRequestMappingUrl() + MailServerController.URL_MAILCONFIG%>">Mail
					Configuration</a> <core:ErrorHint requiredSetupTasks="${requiredSetupTasks}" subItemName="mail_config" /></li>
		</ul></li>
</ul>