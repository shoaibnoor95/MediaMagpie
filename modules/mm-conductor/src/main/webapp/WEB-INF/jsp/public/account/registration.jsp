<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@page session="false"%>
<%@include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<c:set var="title" value="Create New Account" scope="request" />
<head>
    <title>${title}</title>
	<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/utils.js"></script>
	<script type="text/javascript">
		$(function() {
			focusFirstEnabledField(1);
			$("[data-toggle='tooltip']").tooltip();
			//$('form:first *:input[type!=hidden]:first').focus();
		});
	</script>
</head>
<body>
	<h2>${title}</h2>
	<c:if test="${not empty errormessage}">
		<div class="alert alert-danger">${errormessage}</div>
	</c:if>
	<form:form commandName="registrationCommand" cssClass="form-horizontal col-sm-12" role="form">
		<fieldset>
			<legend>New User Settings</legend>
			<div class="form-group">
				<label for="forename" class="col-sm-2 control-label">Forename</label>
				<div class="col-sm-4">
					<form:input path="forename" cssClass="form-control" />
					<form:errors path="forename" cssClass="error" />
				</div>
			</div>
			<div class="form-group">
				<label for="surname" class="col-sm-2 control-label">Surname</label>
				<div class="col-sm-4">
					<form:input path="surname" cssClass="form-control" />
					<form:errors path="surname" cssClass="error" />
				</div>
			</div>
			<div class="form-group">
				<label for="user" class="col-sm-2 control-label">User</label>
				<div class="col-sm-4">
					<form:input path="user" cssClass="form-control" />
					<form:errors path="user" cssClass="error" />
				</div>
				<span class="help-block">The 'User' is used for login</span>
			</div>
			<div class="form-group">
				<label for="password" class="col-sm-2 control-label">Password</label>
				<div class="col-sm-4">
					<form:password path="password" cssClass="form-control" />
					<form:errors path="password" cssClass="error" />
				</div>
			</div>
			<div class="form-group">
				<label for="passwordConfirm" class="col-sm-2 control-label">Password Confirm</label>
				<div class="col-sm-4">
					<form:password path="passwordConfirm" cssClass="form-control" />
					<form:errors path="passwordConfirm" cssClass="error" />
				</div>
				<span class="help-block">Confirm your password</span>
			</div>
			<div class="form-group">
				<label for="email" class="col-sm-2 control-label">Email</label>
				<div class="col-sm-4">
					<form:input path="email" type="email" cssClass="form-control" />
					<form:errors path="email" cssClass="error" />
				</div>
				<span class="help-block"></span>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-4">
					<button type="submit" class="btn btn-success">Register Now</button>
				</div>
			</div>
		</fieldset>
	</form:form>
</body>