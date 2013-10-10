<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="de.wehner.mediamagpie.conductor.webapp.controller.media.AlbumController"%>
<%@page import="de.wehner.mediamagpie.conductor.webapp.controller.media.PublicAlbumController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.util.WebAppUtils" %>
<%@ page import="de.wehner.mediamagpie.persistence.entity.Visibility" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>

<c:set var="title" value="Albums" scope="request" />
<c:set var="activeMenu" value="media" scope="request" />
<c:set var="activeSubMenu" value="listAlbums" scope="request" />
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request" />
<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/pages/album_listAlbums.js"></script>

<div id="content">
	<h1>${title}</h1>

	<div class="buttonBar">
		<button type="button" id="button-new"><span>New</span></button>
	</div>
	<table id="albumList" class="pictureMarkup">
		<thead>
			<tr>
				<th>Name</th>
				<th># Medias</th>
				<th>Visibility</th>
				<th>Copy Public Link</th>
				<th>Creation Date</th>
				<th>Actions</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${albums}" var="album" varStatus="status">
	          	<c:set var="tdClass" value=""/>
    	        <c:if test="${status.count % 2 == 0}">
        	       	<c:set var="tdClass" value="alt"/>
            	</c:if>
				<tr class="${tdClass}" id="${album.id}">
					<td><a href="<%=request.getContextPath()+PublicAlbumController.getBaseRequestMappingUrl()%>/${album.uid}/view" >${album.name}</a></td>
					<td>${fn:length(album.medias)}</td>
					<td>${album.visibility}</td>
					<td>
						<c:choose>
							<c:when test="${album.visibility == 'PUBLIC'}">
								<img class="image-action copyLink" alt="<%=WebAppUtils.buildHttpRequestBasedOnServletRequest(request, pageContext)+PublicAlbumController.getBaseRequestMappingUrl()%>/${album.uid}/view"
									title="copy the public album link into clipboard" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/paste_plain.png" />
							</c:when>
							<c:otherwise>
								---
							</c:otherwise>
						</c:choose>
					</td>
					<td><core:date date="${album.creationDate}" /></td>
					<td>
						<img class="image-action view" alt="view" title="show the album settings"
							src="<%=request.getContextPath()%>/static/images/famfamfam_silk/eye.png" /> 
						<img class="image-action edit" alt="edit" title="edit the album settings"
							src="<%=request.getContextPath()%>/static/images/famfamfam_silk/image_edit.png" /> 
						<img class="image-action delete" alt="delete" title="delete this album"
							src="<%=request.getContextPath()%>/static/images/famfamfam_silk/bin_closed.png" />
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<!-- delete dialog -->
	<div id="deleteDialog" class="appDialog">
	    <div class="hd">Delete Album</div>
	    <div class="bd">
	        <form method="POST" action="<%=request.getContextPath()+AlbumController.getBaseRequestMappingUrl()+AlbumController.URL_DELETE%>" id="deleteForm">
	            <p>Do you really want to delete "<span id="deleteName"></span>"?</p>
	            <input type="hidden" name="id" id="deleteId" value=""/>
	            <input type="hidden" name="page" id="deletePage" value="${start}"/>
	        </form>
	    </div>
	</div>
	
</div>
