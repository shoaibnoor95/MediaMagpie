<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%><!-- TODO rwe: remove this line? -->
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.AlbumController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.PublicAlbumController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.DownloadController"%>
<%@ page session="false"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>

<c:set var="title" value="Album" scope="request" />
<c:set var="activeMenu" value="media" scope="request" />
<c:set var="activeSubMenu" value="listAlbums" scope="request" />
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request" />
<head>
<title>${title}</title>
<!-- used for button icons -->
<link rel="stylesheet" type="text/css" href="<c:url value="/static/css/ui-lightness/jquery-ui-1.8.13.custom.css"/>" />
<!-- <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/cupertino/jquery-ui-1.10.4.custom.min.css"/>" />-->
<style type="text/css">
</style>
<!-- We need to import the script directly into jsp because it contains some java code -->
<script type="text/javascript"><%@ include file="../../../../static/js/mm-toggle.js" %></script>
<script type="text/javascript" src="/static/js/jquery.pollingThumb.js"></script>
<script type="text/javascript">
	'use strict';
	$(function() {
		$('img.thumb').pollingThumb();
	});
</script>
</head>
<body>

	<ol class="breadcrumb">
		<!-- <li><a href="<%=request.getContextPath()%>/welcome">Home</a></li>-->
		<li><a href="<%=request.getContextPath() + AlbumController.getBaseRequestMappingUrl() + AlbumController.URL_LIST%>">Albums</a></li>
		<li><a href="<%=PublicAlbumController.getBaseRequestMappingUrl()%>/${mediaDetailCommand.album.uid}/view">${mediaDetailCommand.album.name}</a></li>
		<li class="active">Media-${mediaDetailCommand.id}</li>
	</ol>

	<c:url value="/public/album/${mediaDetailCommand.album.uid}/${pos}" var="coolirisUrl">
		<c:param name="renderer">cooliris</c:param>
	</c:url>

	<div class="btn-toolbar" role="toolbar">
		<div class="btn-group">
			<!-- prev-button -->
			<c:choose>
				<c:when test="${not empty mediaDetailCommand.urlPrev}">
					<a class="btn btn-default" href="${mediaDetailCommand.urlPrev}"> <span class="ui-icon ui-icon-seek-prev pull-left"></span>&nbsp;previous
					</a>
				</c:when>
				<c:otherwise>
					<button type="button" class="btn btn-default">
						<span class="ui-icon ui-icon-seek-prev pull-left"></span> &nbsp; previous
					</button>
				</c:otherwise>
			</c:choose>
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

		<!-- extra block for download and cooliris -->
		<div class="btn-group">
			<!-- cooliris-button -->
			<a href="${coolirisUrl}" class="btn btn-default"> <span class="ui-icon ui-icon-newwin pull-left"></span> &nbsp; cooliris
			</a>
			<!-- download-button -->
			<a href="<%=DownloadController.getBaseRequestMappingUrl()%>/album/${mediaDetailCommand.album.uid}/${mediaDetailCommand.id}"
				class="btn btn-default"> <span class="glyphicon glyphicon-download pull-left"></span> &nbsp; download
			</a>
		</div>
	</div>

	<%@ include file="/WEB-INF/jsp/partitals/media_show.partitial.jsp"%>

	<div style="margin: 5px 0 10px 0;">
		<div>
			<img class="toggle-img" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/bullet_arrow_down.png"
				alt="hide meta information" onclick="toggleMetaInformation();" /> <a class="toggle-link" onclick="toggleMetaInformation();">Show
				camera meta informations</a>
		</div>
		<div class="meta" style="display: none;">
			<c:if test="${mediaDetailCommand.cameraMetaDataObj != null && not empty mediaDetailCommand.cameraMetaDataObj.exifData}">
	                                       -- EXIF-Data --<br />
				<ul>
					<c:forEach items="${mediaDetailCommand.cameraMetaDataObj.exifData}" var="exifData">
						<li>${exifData.key}:${exifData.value}</li>
					</c:forEach>
				</ul>
	                                        -- Camera-Data --<br />
				<ul>
					<c:forEach items="${mediaDetailCommand.cameraMetaDataObj.metaData}" var="metaData">
						<li>${metaData.key}:${metaData.value}</li>
					</c:forEach>
				</ul>
			</c:if>
		</div>
	</div>
</body>
