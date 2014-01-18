<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.AlbumController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.PublicAlbumController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.util.WebAppUtils" %>
<%@ page session="false" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<c:set var="title" value="Album: ${mediaDetailCommand.album.name}"  scope="request"/>
<c:set var="activeMenu" value="media" scope="request"/>
<c:set var="activeSubMenu" value="listAlbums" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request"/>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <!-- cooliris -->
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/pages/album_coolirisViewAlbum.js"></script>
    <script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/swfobject/2.2/swfobject.js"></script>
    <script type="text/javascript">
    $(document).ready(function() {
        openCoolirisWall('<%=WebAppUtils.getRequestUrlUpToContextPath(request)+PublicAlbumController.getBaseRequestMappingUrl()%>/${mediaDetailCommand.album.uid}/rss', ${pos}); 
    });
    </script>       
</head>
<body>
<div id="content">
    <div class="navigation">
		<a href="<%=request.getContextPath()+AlbumController.getBaseRequestMappingUrl()+AlbumController.URL_LIST%>">Album</a> &raquo; 
		<a href="<%=PublicAlbumController.getBaseRequestMappingUrl()%>/${mediaDetailCommand.album.uid}/view">${mediaDetailCommand.album.name}</a> 
    </div>
	<h1>${title}</h1>

 	<div id="wall"><!-- 3D Wall Goes Here --></div>
</div>
</body>
</html>