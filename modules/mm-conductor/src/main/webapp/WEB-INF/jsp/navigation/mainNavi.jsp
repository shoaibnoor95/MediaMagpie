<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<div id="mainNavi">
	<ul>
		<li class="top_navi<c:if test="${activeMenu == 'welcome'}"> current</c:if>" >
			<a href="<%=request.getContextPath()%>/welcome">Welcome</a>
		</li>
		<li class="top_navi<c:if test="${activeMenu == 'media'}"> current</c:if>" >
			<a data-dropdown="#dropdown-media" href="<%=request.getContextPath()%>/media/search_pictures">Media</a>
            <img data-dropdown="#dropdown-media" src="/static/images/famfamfam_silk/bullet_arrow_down.png" alt="">
            <!-- context menu for media -->
            <div id="dropdown-media" class="dropdown dropdown-tip">
                <ul class="dropdown-menu">
                    <li><a href="<%=request.getContextPath()%>/media/search_pictures">Medias</a></li>
                    <li><a href="<%=request.getContextPath()%>/media/album/list">Albums</a></li>
                    <li><a href="<%=request.getContextPath()%>/upload/file-upload">Upload</a></li>
                    <li><a href="<%=request.getContextPath()%>/trash/content">Trash</a></li>
                </ul>
            </div>
		</li>
		<li class="top_navi<c:if test="${activeMenu == 'config'}"> current</c:if>" >
			<a href="<%=request.getContextPath()%>/config/user/">Configuration
                <core:ErrorHint requiredSetupTasks="${requiredSetupTasks}" mainItemName="config"/>
            </a>
		</li>
	</ul>
</div>