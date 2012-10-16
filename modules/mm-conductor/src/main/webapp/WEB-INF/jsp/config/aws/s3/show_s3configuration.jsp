<%@ page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.AwsConfigurationController"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<c:set var="title" value="AWS S3 Configuration" scope="request" />
<c:set var="activeMenu" value="config" scope="request" />
<c:set var="activeSubMenu" value="aws_s3" scope="request" />
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request" />

<div id="content">
	<h1>${title}</h1>

	<dl>
		<dt>
			<label>Access Key:</label>
		</dt>
		<dd>${conf.accessKey}</dd>
	</dl>
	<dl>
		<dt>
			<label>Secret Key:</label>
		</dt>
		<dd>${conf.secretKey}</dd>
	</dl>
	<dl>
		<dt>
			<label>&nbsp;</label>
		</dt>
		<dd>
			<button type="button"
				onclick="document.location.href='<%=request.getContextPath() + AwsConfigurationController.getBaseRequestMappingUrl()%>/edit'"
				class="active">
				<span>Edit</span>
			</button>
		</dd>
	</dl>
</div>