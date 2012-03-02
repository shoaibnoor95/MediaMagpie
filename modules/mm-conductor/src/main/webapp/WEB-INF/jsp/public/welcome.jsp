<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@page import="de.wehner.mediamagpie.conductor.webapp.controller.DashboardController" %>

<c:set var="title" value="Welcome to MediaMagpie" scope="request"/>
<c:set var="activeMenu" value="welcome" scope="request"/>
<c:import url="/header"/>

<link rel="stylesheet" type="text/css" href="<c:url value="/static/css/galleriffic.css"/>" />
<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery.galleriffic.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery.opacityrollover.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery.history.js"></script>
<script type="text/javascript"><%@ include file="../../../static/js/pages/welcome.js" %></script>
<!-- We only want the thunbnails to display when javascript is disabled -->
<script type="text/javascript">
	document.write('<style>.noscript { display: none; }</style>');
</script>
 			
<div id="content">
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
	<h2>Last added public pictures</h2>
	
	
	<!-- Start Advanced Gallery Html Containers -->
	<div id="thumbs" class="navigation">
		<ul class="thumbs noscript">
		<c:forEach items="${mediaThumbCommandList}" var="mediaThumbCommand" varStatus="status">
            <li>
                    <a class="thumb" name="leaf" href="${mediaThumbCommand.urlThumbDetail}" title="Title #0">
                            <img src="${mediaThumbCommand.urlThumbImage}" alt="${mediaThumbCommand.title}" />
                    </a>
                    <div class="caption">
                            <div class="download">
                                    <a href="${mediaThumbCommand.urlDownload}">Download Original</a>
                            </div>
                            <div class="image-title"><c:out value="${mediaThumbCommand.title}"/></div>
                            <div class="image-desc"><strong><c:out value="${mediaThumbCommand.description}"/></strong>
                            
								<p>ID: ${mediaThumbCommand.id}</p>
<%/*<div class="meta">
<ul>
<li>Camera: NIKON D50</li>
<li>Resolution: 1999 x 1998</li>
<li>Focal Length: 0.0mm</li>
<li>Exposure Time: 1/10s</li>
<li>ISO: 200</li>
<li>Aperture: 0.0</li>
<li>Metering Mode: Center weighted average</li>
<li>Flash: Flash did not fire</li>
</ul>
</div>*/%> 
                            
							</div>
                    </div>
            </li>
		</c:forEach>

		</ul>
	</div>
	<div id="gallery" class="content">
    	<div id="controls" class="controls"></div>
        <div class="slideshow-container">
        	<div id="loading" class="loader"></div>
            <div id="slideshow" class="slideshow"></div>
        </div>	
        <div id="caption" class="caption-container embox" style="opacity: 1;"></div>
	</div>

		<div style="clear: both;"/>
	
<%
java.security.Principal  principal = request.getUserPrincipal();
if(principal != null) {%>
	<h2>Your last added pictures</h2>
	
<%}%>
</div>
<c:import url="/footer"/>