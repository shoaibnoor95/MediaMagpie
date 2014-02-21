<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.UserConfiguratonControllerS1"%>

<c:set var="title" value="User Configuration (1/2)" scope="request" />
<c:set var="activeMenu" value="config" scope="request" />
<c:set var="activeSubMenu" value="user_config" scope="request" />
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

	<form:form commandName="userConfigurationCommand" cssClass="form-horizontal" role="form">

		<ol class="breadcrumb">
			<!-- <li><a href="<%=request.getContextPath()%>/welcome">Home</a></li>-->
			<!-- <li class="active"><a href="<%=request.getContextPath() + UserConfiguratonControllerS1.getBaseRequestMappingUrl()%>/edit">Personal Data</a></li>-->
			<li class="active">Personal Data</li>
			<li>Layout and Sync</li>
		</ol>

		<legend>Personal Data</legend>
		<div class="form-group">
			<label class="col-sm-2 control-label">User (Login)</label>
			<div class="col-sm-4">
				<p class="form-control-static">${userConfigurationCommand.name}</p>
			</div>
		</div>
		<div class="form-group">
			<label for="forename" class="col-sm-2 control-label">Name</label>
			<div class="col-sm-4">
				<form:input path="forename" cssClass="form-control" />
				<form:errors path="forename" cssClass="error" />
			</div>
			<span class="help-block"></span>
		</div>
		<div class="form-group">
			<label for="surname" class="col-sm-2 control-label">Surname</label>
			<div class="col-sm-4">
				<form:input path="surname" cssClass="form-control" />
				<form:errors path="surname" cssClass="error" />
			</div>
			<span class="help-block"></span>
		</div>
		<div class="form-group">
			<label for="surname" class="col-sm-2 control-label">Password</label>
			<div class="col-sm-4">
				<form:password path="password" showPassword="false" cssClass="form-control" autocomplete="off" />
				<form:errors path="password" cssClass="error" />
			</div>
			<span class="help-block">Only fill in this field if you want to CHANGE your current password</span>
		</div>
		<div class="form-group">
			<label for="surname" class="col-sm-2 control-label">Password Confirm</label>
			<div class="col-sm-4">
				<form:password path="passwordConfirm" showPassword="false" cssClass="form-control" autocomplete="off" />
				<form:errors path="passwordConfirm" cssClass="error" />
			</div>
			<span class="help-block">Confirm your password only if you want to CHANGE your current password</span>
		</div>
		<!-- buttons -->
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-10">
				<button type="button" class="btn btn-default"
					onclick="document.location.href='<%=request.getContextPath() + UserConfiguratonControllerS1.getBaseRequestMappingUrl()
                        + UserConfiguratonControllerS1.URL_USERCONFIG_CANCEL%>'">Cancel</button>
				<button type="submit" class="btn btn-primary">Next</button>
			</div>
		</div>
	</form:form>
</body>
