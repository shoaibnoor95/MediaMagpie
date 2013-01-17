<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.SynchronizeController"%>
<ul>
	<li class="<c:if test="${activeSubMenu == 'mediaSearch'}">current</c:if>">
		<a href="<%=request.getContextPath()%>/media/search_pictures">Medias</a>
	</li>
	<li class="<c:if test="${activeSubMenu == 'listAlbums'}">current</c:if>">
		<a href="<%=request.getContextPath()%>/media/album/list">Albums</a>
	</li>
	<li class="<c:if test="${activeSubMenu == 'upload'}">current</c:if>">
		<a href="<%=request.getContextPath()%>/upload/file-upload">Upload</a>
	</li>
	<li class="<c:if test="${activeSubMenu == 'trash'}">current</c:if>">
		<a href="<%=request.getContextPath()%>/trash/content">Trash</a>
	</li>
    <li class="<c:if test="${activeSubMenu == 'sync'}">current</c:if>">
        <a href="<%=request.getContextPath() + SynchronizeController.getIndexUrl()%>">Synchronize</a>
    </li>
</ul>