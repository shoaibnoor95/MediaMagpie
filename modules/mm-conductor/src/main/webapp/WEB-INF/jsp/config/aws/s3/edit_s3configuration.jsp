<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.AwsConfigurationController"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<c:set var="title" value="AWS S3 Configuration" scope="request" />
<c:set var="activeMenu" value="config" scope="request" />
<c:set var="activeSubMenu" value="aws_s3" scope="request" />
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request" />

<head>
<script type="text/javascript">
	$(function() {
        $("[data-toggle='tooltip']").tooltip();
		$("[data-toggle='popover']").popover();
		$('form:first *:input[type!=hidden]:first').focus();
	});
</script>
</head>
<body>
	<h2>${title}</h2>

		<form:form commandName="conf" id="myForm" cssClass="form-horizontal" role="form">
				<legend>Settings</legend>
				<div class="form-group">
					<label for="accessKey" class="col-sm-2 control-label">Access Key</label>
					<div class="col-sm-3">
                        <form:input path="accessKey" cssClass="form-control" placeholder="enter your access key here..."/>
						<form:errors path="accessKey" cssClass="error" />
					</div>
					<span class="help-block">
	                    <img src="<%=request.getContextPath()%>/static/images/famfamfam_silk/help.png" data-toggle="tooltip" data-placement="right"
	                        title="You will find your access key under 'Security Credenticals' of our account. Go to: https://portal.aws.amazon.com/gp/aws/securityCredentials" />
                        <button type="button" class="btn btn-xs btn-default" data-container="body" data-toggle="popover" data-placement="right" data-content="You will find your access key under 'Security Credenticals' of our account. Go to: https://portal.aws.amazon.com/gp/aws/securityCredentials">?</button>
					&nbsp;e.g. 'LKUAJAFDEFYMGTKDNBUP'
					</span>
				</div>
                <div class="form-group">
                    <label for="secretKey" class="col-sm-2 control-label">Secret Key</label>
                    <div class="col-sm-3">
                        <form:password path="secretKey" showPassword="false" cssClass="form-control" placeholder="enter your secret key here..."/>
                        <form:errors path="secretKey" cssClass="error" />
                    </div>
                    <span class="help-block">
                        <img src="<%=request.getContextPath()%>/static/images/famfamfam_silk/help.png" data-toggle="tooltip" data-placement="right"
                            title="You will find your secret key under 'Security Credenticals' of our account. Go to: https://portal.aws.amazon.com/gp/aws/securityCredentials" />
                        <button type="button" class="btn btn-xs btn-default" data-container="body" data-toggle="popover" data-placement="right" data-content="You will find your secret key under 'Security Credenticals' of our account. Go to: https://portal.aws.amazon.com/gp/aws/securityCredentials">?</button>
                    &nbsp;e.g. 'kirpdotW6lY9zjoddtbjzcD0Oy30jkguw9DTjfOr'<br>Leave blank if you dont't want to change existing secret key.
                    </span>
                </div>
	            <div class="form-group">
	                <div class="col-sm-offset-2  col-sm-5">
	                    <div class="checkbox">
	                        <label> <input type="checkbox" value=""> <form:checkbox path="syncToS3" />Synchronize Media to S3
	                        </label> 
	                        <img src="<%=request.getContextPath()%>/static/images/famfamfam_silk/help.png" data-toggle="tooltip" data-placement="right"
	                            title="When you set this option mediamagpie will synchronize all medias to your S3 buckets." />
	                    </div>
	                </div>
	            </div>
	            <!-- buttons -->
	            <div class="form-group">
	                <div class="col-sm-offset-2 col-sm-5">
	                    <button type="button" class="btn btn-default"
	                        onclick="document.location.href='<%=request.getContextPath() + AwsConfigurationController.getBaseRequestMappingUrl() + AwsConfigurationController.URL_S3CONFIG%>'">Cancel</button>
	                    <button type="submit" class="btn btn-primary">Save</button>
	                </div>
	            </div>
		</form:form>
</body>
