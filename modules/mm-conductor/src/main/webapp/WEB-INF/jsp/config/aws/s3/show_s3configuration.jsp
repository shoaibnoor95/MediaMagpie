<%@ page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.AwsConfigurationController"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<c:set var="title" value="AWS S3 Configuration" scope="request" />
<c:set var="activeMenu" value="config" scope="request" />
<c:set var="activeSubMenu" value="aws_s3" scope="request" />
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request" />

<head>
    <title>${title}</title>
</head>
<body>
	<h2>${title}</h2>

	<c:if test="${not empty checkResultCommand}">
		<div class="${checkResultCommand.divClass} alert alert-info">
			<spring:message code="${checkResultCommand.messageKey}" />
			&nbsp;
			<c:out value="${checkResultCommand.details}"></c:out>
		</div>
	</c:if>

	<c:if test="${!empty conf.accessKey && !empty conf.anonymizedSecretKey}">
		<h4>
			<img src="<%=request.getContextPath()%>/static/images/famfamfam_silk/disconnect.png" /> <a
				href="<%=request.getContextPath() + AwsConfigurationController.getBaseRequestMappingUrl() + AwsConfigurationController.URL_TEST_SETTINGS%>">Test
				AWS settings</a>
		</h4>
	</c:if>

	<div class="form-horizontal">
		<div class="form-group">
			<label class="col-sm-2 control-label">Access Key</label>
			<div class="col-sm-10">
				<p class="form-control-static">${conf.accessKey}</p>
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-2 control-label">Secret Key</label>
			<div class="col-sm-10">
				<p class="form-control-static">${conf.anonymizedSecretKey}</p>
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-2 control-label">Synchronize to S3</label>
			<div class="col-sm-10">
				<form:checkbox path="conf.syncToS3" disabled="true" cssClass="form-control-static" />
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-10">
				<button type="button" class="btn btn-default"
					onclick="document.location.href='<%=request.getContextPath() + AwsConfigurationController.getBaseRequestMappingUrl()%>/edit'">Edit</button>
			</div>
		</div>
	</div>

	<!-- trigger synchronisation -->
	<c:if test="${conf.configurationComplete}">
		<form:form action="/config/aws/s3/synchronize" cssClass="form-horizontal col-sm-10" role="form">
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<button id="syncWithS3" type="submit" class="btn alert-warning">Start Synchronization</button>
					<input type="hidden" name="submitSelect" value="start" />
				</div>
			</div>
		</form:form>
	</c:if>

</body>
