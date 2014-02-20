<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.UserConfiguratonControllerS1"%>
<c:set var="title" value="User Configuration" scope="request" />
<c:set var="activeMenu" value="config" scope="request" />
<c:set var="activeSubMenu" value="user_config" scope="request" />
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request" />

<!-- breadcrumps: <ol class="breadcrumb">
    <li><a href="<%=request.getContextPath()%>/welcome">Home</a></li>
    <li>Configuration</li>
    <li class="active">User configuration</li>
</ol>-->

<h2>${title}</h2>

<form class="form-horizontal col-sm-10" role="form">
	<div class="form-group">
		<label class="col-sm-2 control-label">User (Login)</label>
		<div class="col-sm-10">
			<p class="form-control-static">${user.name}</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label">Name</label>
		<div class="col-sm-10">
			<p class="form-control-static">${user.forename}</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label">Surname</label>
		<div class="col-sm-10">
			<p class="form-control-static">${user.surname}</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label">Email</label>
		<div class="col-sm-10">
			<p class="form-control-static">${user.email}</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label">Root Media Path</label>
		<div class="col-sm-10">
			<p class="form-control-static">
				<c:forEach var="path" items="${conf.rootMediaPathes}">
					<c:out value="${path}" />
					<br />
				</c:forEach>
			</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label">Thumb Image Size</label>
		<div class="col-sm-10">
			<p class="form-control-static">${conf.thumbImageSize}</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label">Thumb Image Size in Tables</label>
		<div class="col-sm-10">
			<p class="form-control-static">${conf.thumbImageSizeTable}</p>
		</div>
	</div>
	<div class="form-group">
		<label class="col-sm-2 control-label">Detail Image Size</label>
		<div class="col-sm-10">
			<p class="form-control-static">${conf.detailImageSize}</p>
		</div>
	</div>
	<div class="form-group">
		<div class="col-sm-offset-2 col-sm-10">
			<button type="button" class="btn btn-default"
				onclick="document.location.href='<%=request.getContextPath() + UserConfiguratonControllerS1.getBaseRequestMappingUrl()%>/edit'">Edit</button>
		</div>
	</div>
</form>