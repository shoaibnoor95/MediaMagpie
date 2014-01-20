<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.AlbumController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.PublicAlbumController"%>
<%@ page session="false"%>
<%
    /*include file="/WEB-INF/jsp/general/taglibs.jsp"*/
%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="/WEB-INF/tlds/core.tld" prefix="core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<c:set var="title" value="Album" scope="request" />
<c:set var="activeMenu" value="media" scope="request" />
<c:set var="activeSubMenu" value="listAlbums" scope="request" />
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request" />
<html>
<head>
</head>
<body>
	<div id="content">

		<div class='navigation'>
			<a href="<%=request.getContextPath() + AlbumController.getBaseRequestMappingUrl() + AlbumController.URL_LIST%>">Album</a> &raquo; <a
				href="<%=request.getContextPath()+PublicAlbumController.getBaseRequestMappingUrl()%>/${albumCommand.uid}/view">${albumCommand.name}</a>
		</div>

		<h1>${title}</h1>

		<c:choose>
			<c:when test="${not empty error}">
				<div class="error">${error}</div>
			</c:when>
			<c:otherwise>
				<h2>${albumCommand.name}</h2>

				<core:pagination current="${start}" pageSize="${pageSize}" total="${totalHits}" path="" />

				<div class="ui-widget ui-helper-clearfix">
					<ul id="gallery" class="gallery ui-helper-reset ui-helper-clearfix">
						<c:forEach items="${mediaThumbCommandList}" var="mediaThumbCommand" varStatus="status">
							<li class="ui-widget-content ui-corner-tr" id="${picture.id}">
								<h5 class="ui-widget-header">
									<c:out value="${mediaThumbCommand.title}" />
								</h5>
								<p>
									<c:url value="/public/album/${albumCommand.uid}/${status.count + start - 1}" var="url">
									</c:url>
									<a href="${url}"><img src="${mediaThumbCommand.urlThumbImage}" title="${mediaThumbCommand.title}" /></a>
								</p>
								<p class="metadata">
									<core:date date="${mediaThumbCommand.media.creationDate}" />
								</p>
							</li>
						</c:forEach>
					</ul>
				</div>

				<core:pagination current="${start}" pageSize="${pageSize}" total="${totalHits}" path="" />

				<ul id="nav-bar" class="button-list">
					<!-- cooliris-button -->
					<li class="first last"><c:url value="/public/album/${albumCommand.uid}/-1" var="url">
							<c:param name="renderer">cooliris</c:param>
						</c:url> <a href="${url}" class="Butt"> <span class="ui-icon ui-icon-newwin"></span> <span class="text">cooliris</span>
					</a></li>
				</ul>
			</c:otherwise>
		</c:choose>
	</div>
</body>
</html>