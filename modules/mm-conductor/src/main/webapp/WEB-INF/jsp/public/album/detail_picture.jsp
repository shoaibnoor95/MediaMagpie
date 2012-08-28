<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%><!-- TODO rwe: remove this line? -->
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.AlbumController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.PublicAlbumController"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>

<c:set var="title" value="Album"  scope="request"/>
<c:set var="activeMenu" value="media" scope="request"/>
<c:set var="activeSubMenu" value="listAlbums" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request"/>
<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/pages/album_detailPicture.js"></script>
			
<div id="content">

    <div class='navigation'>
		<a href="<%=request.getContextPath()+AlbumController.getBaseRequestMappingUrl()+AlbumController.URL_LIST%>">Album</a> &raquo; 
		<a href="<%=request.getContextPath()+PublicAlbumController.getBaseRequestMappingUrl()%>/${mediaDetailCommand.album.uid}/view">${mediaDetailCommand.album.name}</a> &raquo; 
		Media-${mediaDetailCommand.id} 
    </div>

	<h1>${mediaDetailCommand.name}</h1>

	<ul id="nav-bar" class="button-list">
		<!-- prev-button -->
		<li class="first">
			<c:choose>
				<c:when test="${not empty mediaDetailCommand.urlPrev}">
					<a href="${mediaDetailCommand.urlPrev}" class="Butt">
						<span class="ui-icon ui-icon-seek-prev" ></span>
						<span class="text" >previous</span>
					</a>
				</c:when>
			<c:otherwise>
					<span class="Butt">
						<span class="ui-icon ui-icon-seek-prev" ></span>
						<span class="text" >previous</span>
					</span>
			</c:otherwise>
			</c:choose>
		</li>
		<!-- cooliris-button -->
		<li>
			<c:url value="/public/album/${mediaDetailCommand.album.uid}/${pos}" var="url">
				<c:param name="renderer">cooliris</c:param>
			</c:url>
			<a href="${url}" class="Butt">
				<span class="ui-icon ui-icon-newwin" ></span>
				<span class="text">cooliris</span>
			</a>
		</li>
		<!-- next-button -->
		<li class="last">
			<c:choose>
				<c:when test="${not empty mediaDetailCommand.urlNext}">
	 				<a href="${mediaDetailCommand.urlNext}" class="Butt" >
						<span class="text">next</span>
						<span class="ui-icon ui-icon-seek-next" ></span>
					</a>
				</c:when>
				<c:otherwise>
					<span class="Butt">
						<span class="text" >previous</span>
						<span class="ui-icon ui-icon-seek-next" ></span>
					</span>
				</c:otherwise>
			</c:choose>
		</li>
	</ul>
	<br/>
	
	<img alt="${mediaDetailCommand.name}" src="${mediaDetailCommand.imageLink}"/>
	<br/>
	<h2>Description:</h2>
	<h3>${mediaDetailCommand.description}</h3>
	<h2>Details:</h2>
	<dl>
		<dt>
			Shot was:
		</dt>
		<dd><core:date date="${mediaDetailCommand.creationDate}" /></dd>	
	</dl>
