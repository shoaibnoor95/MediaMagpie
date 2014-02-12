<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ page session="false" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.DashboardController" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<c:set var="title" value="Welcome to MediaMagpie" scope="request"/>
<c:set var="activeMenu" value="welcome" scope="request"/>
<head>
	<link rel="stylesheet" type="text/css" href="<c:url value="/static/css/galleriffic.css"/>" />
	
 	<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery.galleriffic.js"></script>
	<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery.opacityrollover.js"></script>
	<!-- We only want the thunbnails to display when javascript is disabled -->
<!-- 	<script type="text/javascript"><%@ include file="../../../static/js/pages/welcome.js" %></script>-->
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/pages/welcome.js" />
    <script type="text/javascript">
        document.write('<style>.noscript { display: none; }</style>');
    </script>
</head>
<body>
	<h1>${title}</h1>
	<h2>What is <fmt:message key="main.name"/>?</h2>
		MediaMagpie is a web-portal were you can upload and share your photos. Currently, MediaMagpie is limited only to share photos but the application is designed to to share videos as well and this feature will come soon.  
	<h3>Features</h3>
	<ul>
		<li>Upload Photos and Videos to the server via Drag&Drop from your desktop</li>
		<li>Arrange medias in 'albums' that you can share public or just to registered users</li>
		<li>All shared photos can be downloaded as original file, so the user gets the original quality of the photo including all meta informations your camera provided</li>
		<li>You can add titles and texts to your medias</li>
		<li><fmt:message key="main.name"/> uses some awesome javasript plugins to show your photos as a slide show in a window or full size of screen</li>
	</ul>
		
	<!-- Start Advanced Gallery Html Containers -->
    <div class="row">
        <h3>&nbsp;</h3>
    </div>
	<div id="thumbs" class="navigation col-md-4">
		<ul class="thumbs noscript">
        <h2>Last added public pictures</h2>
		<c:forEach items="${mediaThumbCommandList}" var="mediaThumbCommand" varStatus="status">
            <li>
	            <a class="thumb" href="${mediaThumbCommand.urlThumbDetail}" title="Title #0">
	               <img src="${mediaThumbCommand.urlThumbImage}" alt="${mediaThumbCommand.title}" />
	            </a>
	            <div class="caption">
	                <div class="download"><a href="${mediaThumbCommand.urlDownload}">Show Original</a></div>
	                <div class="image-title"><c:out value="${mediaThumbCommand.title}"/></div>
	                <div class="image-desc">
	                   <i><c:out value="${mediaThumbCommand.description}"/></i><br/>
<div style="padding: 3px 0 2px 5px;">
    <img class="toggle-img" style="box-shadow: 0 0 0;" src="<%=request.getContextPath()%>static/images/famfamfam_silk/bullet_arrow_down.png" alt="hide meta information" onclick="toggleMetaInformation();"/>
    <a class="toggle-link" onclick="toggleMetaInformation();">Show camera meta informations</a>
</div>
						<div class="meta" style="display: none;" >
	                        ID: ${mediaThumbCommand.id}<br/>
                            <c:if test="${mediaThumbCommand.cameraMetaData != null && not empty mediaThumbCommand.cameraMetaData.exifData}">
	                           -- EXIF-Data --<br />
							   <ul>
								    <c:forEach items="${mediaThumbCommand.cameraMetaData.exifData}" var="exifData">
									   <li>${exifData.key} : ${exifData.value}</li>
									</c:forEach>
								</ul>
							    -- Camera-Data --<br/>
                                <ul>
                                    <c:forEach items="${mediaThumbCommand.cameraMetaData.metaData}" var="metaData">
                                        <li>${metaData.key} : ${metaData.value}</li>
                                    </c:forEach>
                                </ul>
							</c:if>
                        </div> 
					</div>
                </div>
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
        <div id="caption" class="caption-container embox" style="opacity: 1;">
        </div>
	</div>
	
    <div style="clear: both;">
		<%java.security.Principal  principal = request.getUserPrincipal();
		if(principal != null) {%>
			<h2>Your last added pictures</h2>
			... todo add user's pictures here
		<%}%>
    </div>
</body>
</html>