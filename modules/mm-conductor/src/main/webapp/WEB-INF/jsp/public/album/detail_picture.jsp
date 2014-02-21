<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%><!-- TODO rwe: remove this line? -->
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.AlbumController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.PublicAlbumController"%>
<%@ page session="false"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>

<c:set var="title" value="Album" scope="request" />
<c:set var="activeMenu" value="media" scope="request" />
<c:set var="activeSubMenu" value="listAlbums" scope="request" />
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request" />
<head>
    <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/ui-lightness/jquery-ui-1.8.13.custom.css"/>" />
</head>
<body>

	<!-- <div class='navigation'>
		<a href="<%=request.getContextPath() + AlbumController.getBaseRequestMappingUrl() + AlbumController.URL_LIST%>">Album</a> &raquo; <a
			href="<%=request.getContextPath() + PublicAlbumController.getBaseRequestMappingUrl()%>/${mediaDetailCommand.album.uid}/view">${mediaDetailCommand.album.name}</a>
		&raquo; Media-${mediaDetailCommand.id}
	</div>-->
	<ol class="breadcrumb">
		<!-- <li><a href="<%=request.getContextPath()%>/welcome">Home</a></li>-->
		<li><a href="<%=request.getContextPath() + AlbumController.getBaseRequestMappingUrl() + AlbumController.URL_LIST%>">Albums</a></li>
		<li><a href="<%=PublicAlbumController.getBaseRequestMappingUrl()%>/${mediaDetailCommand.album.uid}/view">${mediaDetailCommand.album.name}</a></li>
		<li class="active">Media-${mediaDetailCommand.id}</li>
	</ol>

	<h2>${mediaDetailCommand.name}</h2>

	<c:url value="/public/album/${mediaDetailCommand.album.uid}/${pos}" var="coolirisUrl">
		<c:param name="renderer">cooliris</c:param>
	</c:url>
	<div class="form-group">
	<div class="btn-group">
		<!-- prev-button -->
		<c:choose>
			<c:when test="${not empty mediaDetailCommand.urlPrev}">
				<a class="btn btn-default" href="${mediaDetailCommand.urlPrev}"> <span class="ui-icon ui-icon-seek-prev pull-left"></span> previous
				</a>
			</c:when>
			<c:otherwise>
				<button type="button" class="btn btn-default">
					<span class="ui-icon ui-icon-seek-prev pull-left"></span> &nbsp; previous
				</button>
			</c:otherwise>
		</c:choose>
		<!-- cooliris-button -->
		<a href="${coolirisUrl}" class="btn btn-default"> <span class="ui-icon ui-icon-newwin pull-left"></span> &nbsp; cooliris
		</a>
		<!-- next-button -->
		<c:choose>
			<c:when test="${not empty mediaDetailCommand.urlNext}">
				<a class="btn btn-default" href="${mediaDetailCommand.urlNext}"> <span class="pull-left">next &nbsp;</span> <span
					class="ui-icon ui-icon-seek-next pull-right"></span>
				</a>
			</c:when>
			<c:otherwise>
				<button type="button" class="btn btn-default">
					<span class="pull-left">next &nbsp;</span> <span class="ui-icon ui-icon-seek-next pull-right"></span>
				</button>
			</c:otherwise>
		</c:choose>
	</div>
	</div>
    
	<img alt="${mediaDetailCommand.name}" src="${mediaDetailCommand.imageLink}" />
	<br />
	<h3>Description:</h3>
	${mediaDetailCommand.description}
	<h3>Details:</h3>
	<dl>
		<dt>Creation Date:</dt>
		<dd>
			<core:date date="${mediaDetailCommand.creationDate}" />
		</dd>
	</dl>
</body>
