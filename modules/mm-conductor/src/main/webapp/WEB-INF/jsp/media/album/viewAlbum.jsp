<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.AlbumController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.PublicAlbumController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.util.WebAppUtils"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>

<%@ page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<c:set var="activeMenu" value="media" scope="request" />
<c:set var="activeSubMenu" value="listAlbums" scope="request" />
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request" />
<c:set var="linkBase"
	value="<%=WebAppUtils.buildHttpRequestBasedOnServletRequest(request, pageContext) + PublicAlbumController.getBaseRequestMappingUrl()%>" />
<head>
    <title>Album details</title>
</head>
<body>
	<h2>Album details</h2>
	<div class="form-horizontal">
		<div class="form-group">
			<label class="col-sm-2 control-label">Name</label>
			<div class="col-sm-10">
				<p class="form-control-static">${albumCommand.name}</p>
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-2 control-label">Creation Date</label>
			<div class="col-sm-10">
				<p class="form-control-static">
					<core:date date="${albumCommand.creationDate}" />
				</p>
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-2 control-label">Visibility</label>
			<div class="col-sm-10">
				<p class="form-control-static">${albumCommand.visibility}</p>
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-2 control-label">Public Link</label>
			<div class="col-sm-10">
				<c:set var="link" value="${linkBase}/${albumCommand.uid}/view" />
				<img src="<%=request.getContextPath()%>/static/images/famfamfam_silk/paste_plain.png"
					onclick="window.prompt ('Copy to clipboard: Ctrl+C, Enter', '${link}');" /> <a href="${link}">${link}</a>
			</div>
		</div>
		<div class="form-group">
			<label class="col-sm-2 control-label">Number of images</label>
			<div class="col-sm-10">
				<p class="form-control-static">${fn:length(albumCommand.medias)}</p>
			</div>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-10">
				<button type="button" class="btn btn-primary"
					onclick="document.location.href='<%=AlbumController.getBaseRequestMappingUrl()%>/${albumCommand.id}/edit'">Edit</button>
			</div>
		</div>
	</div>
</body>
