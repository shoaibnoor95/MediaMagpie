<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.UserConfiguratonControllerS1"%>

<c:set var="title" value="User Configuration (2/2)" scope="request" />
<c:set var="activeMenu" value="config" scope="request" />
<c:set var="activeSubMenu" value="user_config" scope="request" />
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request" />

<head>
    <title>${title}</title>
	<script type="text/javascript">
		$(function() {
			$("[rel='tooltip']").tooltip();
            $('form:first *:input[type!=hidden]:first').focus();
		});
	</script>
</head>
<body>
	<h2>${title}</h2>
	<form:form commandName="userConfigurationCommand" cssClass="form-horizontal" role="form">

		<ol class="breadcrumb">
			<li>Personal Data</li>
			<li class="active">Layout and Sync</li>
		</ol>

			<legend>Layout and Sync</legend>
			<div class="form-group">
				<label for="rootMediaPathes" class="col-sm-2 control-label">Root Media Path</label>
				<div class="col-sm-4">
					<form:textarea path="rootMediaPathes" rows="3" cols="20" cssClass="form-control" />
					<div class="checkbox">
						<label> <input type="checkbox" value=""> <form:checkbox path="syncMediaPahtes" /> Rescan Media Pathes now
						</label> <img src="<%=request.getContextPath()%>/static/images/famfamfam_silk/help.png" rel="tooltip" data-placement="right"
							title="When selected your complete root media path will be synchronized against the databse." />
					</div>
				</div>
				<span class="help-block">One or more pathes were medias will be searched. <br />e.g. '/home/james/pictures'
				</span>
			</div>
			<div class="form-group">
				<label for="thumbImageSize" class="col-sm-2 control-label">Thumb Image Size</label>
				<div class="col-sm-2">
					<form:input path="thumbImageSize" cssClass="form-control" />
					<form:errors path="thumbImageSize" cssClass="error" />
				</div>
				<span class="help-block">e.g. '120'</span>
			</div>
			<div class="form-group">
				<label for="thumbImageSizeTable" class="col-sm-2 control-label">Thumb Image Size Table</label>
				<div class="col-sm-2">
					<form:input path="thumbImageSizeTable" cssClass="form-control" />
					<form:errors path="thumbImageSizeTable" cssClass="error" />
				</div>
				<span class="help-block">e.g. '60'</span>
			</div>
			<div class="form-group">
				<label for="detailImageSize" class="col-sm-2 control-label">Detail Image Size</label>
				<div class="col-sm-2">
					<form:input path="detailImageSize" cssClass="form-control" />
					<form:errors path="detailImageSize" cssClass="error" />
				</div>
				<span class="help-block">e.g. '1024'</span>
			</div>
	        <!-- buttons -->
	        <div class="form-group">
	            <div class="col-sm-offset-2 col-sm-10">
	                <button type="button" class="btn btn-default"
	                    onclick="document.location.href='<%=request.getContextPath() + UserConfiguratonControllerS1.getBaseRequestMappingUrl() + UserConfiguratonControllerS1.URL_USERCONFIG_CANCEL%>'">Cancel</button>
                    <button type="submit" class="btn btn-default" name="_back">
                        <span>Back</span>
                    </button>
	                <button type="submit" class="btn btn-primary">Save</button>
	            </div>
	        </div>
	</form:form>
</body>
