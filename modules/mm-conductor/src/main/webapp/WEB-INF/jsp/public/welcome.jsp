<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.DashboardController"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<c:set var="title" value="Welcome to MediaMagpie" scope="request" />
<c:set var="activeMenu" value="welcome" scope="request" />
<head>
<link rel="stylesheet" type="text/css" href="<c:url value="/static/css/galleriffic.css"/>" />

<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery.galleriffic.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery.opacityrollover.js"></script>
<!-- 1. We only want the thunbnails to display when javascript is enabled
     2. We need to import the script directly into jsp because it contains some java code -->
<script type="text/javascript"><%@ include file="../../../static/js/pages/welcome.js" %></script>
<script type="text/javascript">
	document.write('<style>.noscript { display: none; }</style>');
</script>
</head>
<body>
	<h1>${title}</h1>
	<h2>
		What is
		<fmt:message key="main.name" />
		?
	</h2>
	MediaMagpie is a web application used to share photos with friends and other registered users. You can easily upload your media files on
	upload web pages, set titles, tags and description to photos, search photos and place them into photo albums. Each photo album can be
	configured to be viewable for public users, only for registered users or only for you. In case of a public albums you can simple send a
	special link (url) to your album to your friends. Currently, MediaMagpie is limited only to share photos but the application is designed to
	share videos as well and this feature will coming soon.
	<h3>Features</h3>
	<ul>
		<li>Upload Photos and Videos easily to the server via Drag&Drop from your desktop</li>
		<li>Optionally, add titles, descriptions or tags to each media</li>
		<li>Search medias by full text search or by a time span</li>
		<li>Arrange medias in 'albums' and share them <i>public</i> (to all users), <i>to registered users</i> or only to <i>yourself</i>.
			<li>All shared photos can be downloaded as original file, so the user gets the original file including all camera's meta information</li>
			<li>Configure the picture size which uses MediaMagpie to present your photos</li>
			<li>MediaMagpie will automatically detect the pictures orientation from meta data and presents the photo in propper rotation</li>
			<li><fmt:message key="main.name" /> uses some awesome javasript plugins to present your photos as a slide show in a window or full
				screen</li>
	</ul>

	<!-- Start Advanced Gallery Html Containers -->
	<div class="row">
		<h2>Last added pictures</h2>
	</div>
	<div id="thumbs" class="navigation col-md-4">
		<ul class="thumbs noscript">
			<c:forEach items="${mediaThumbCommandList}" var="mediaThumbCommand" varStatus="status">
				<li>
				    <a class="thumb" href="${mediaThumbCommand.urlThumbDetail}" title="Title #0"> <img src="${mediaThumbCommand.urlThumbImage}"
						alt="${mediaThumbCommand.title}" /></a>
					<div class="caption">
                        <div class="panel panel-default">
                            <div class="panel-heading">
                                <h3 class="panel-title"><c:out value="${mediaThumbCommand.title}" />
                                <div class="download">
                                    <a href="${mediaThumbCommand.urlDownload}">Show Original</a>
                                </div></h3>
                            </div>
                            <div class="panel-body">
		                        <div class="image-desc">
		                            <i><c:out value="${mediaThumbCommand.description}" /></i><br />
		                            <div style="padding: 3px 0 2px 5px;">
		                                <img class="toggle-img" style="box-shadow: 0 0 0;"
		                                    src="<%=request.getContextPath()%>static/images/famfamfam_silk/bullet_arrow_down.png" alt="hide meta information"
		                                    onclick="toggleMetaInformation();" /> <a class="toggle-link" onclick="toggleMetaInformation();">Show camera meta informations</a>
		                            </div>
		                            <div class="meta" style="display: none;">
		                                ID: ${mediaThumbCommand.id}<br />
		                                <c:if test="${mediaThumbCommand.cameraMetaData != null && not empty mediaThumbCommand.cameraMetaData.exifData}">
		                               -- EXIF-Data --<br />
		                                    <ul>
		                                        <c:forEach items="${mediaThumbCommand.cameraMetaData.exifData}" var="exifData">
		                                            <li>${exifData.key}:${exifData.value}</li>
		                                        </c:forEach>
		                                    </ul>
		                                -- Camera-Data --<br />
		                                    <ul>
		                                        <c:forEach items="${mediaThumbCommand.cameraMetaData.metaData}" var="metaData">
		                                            <li>${metaData.key}:${metaData.value}</li>
		                                        </c:forEach>
		                                    </ul>
		                                </c:if>
		                            </div>
		                        </div><!-- .image-desc -->
                            </div>
                        </div>
					</div><!-- .caption -->
				</li>
			</c:forEach>
		</ul>
	</div>

	<div id="gallery" class="content col-md-4">
		<div id="controls" class="controls"></div>
		<div class="slideshow-container">
			<div id="loading" class="loader"></div>
			<div id="slideshow" class="slideshow"></div>
		</div>
		<div id="caption" class="caption-container embox" style="opacity: 1;"></div>
	</div>
	<!-- 
    <div style="clear: both;">
		<%java.security.Principal principal = request.getUserPrincipal();
            if (principal != null) {%>
			<h2>Your last added pictures</h2>
			... todo add user's pictures here
		<%}%>
    </div>-->
</body>
</html>