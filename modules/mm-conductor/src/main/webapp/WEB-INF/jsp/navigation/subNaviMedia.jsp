<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<div id="dropdown-media" class="dropdown dropdown-relative dropdown-tip">
<ul class="dropdown-menu">
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
</ul>
</div>