<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<nav id="mainNavi">
	<ul class="top_navi" >
		<li class="top_navi<c:if test="${activeMenu == 'welcome'}"> current</c:if>" >
			<a href="<%=request.getContextPath()%>/welcome">Welcome</a>
		</li>
		<li class="top_navi<c:if test="${activeMenu == 'media'}"> current</c:if>" >
			<a data-dropdown="#dropdown-media" href="<%=request.getContextPath()%>/media/search_pictures">Media</a>
            <!--<img data-dropdown="#dropdown-media" src="/static/images/famfamfam_silk/bullet_arrow_down.png" >-->
            <%@ include file="/WEB-INF/jsp/navigation/subNaviMedia.jsp" %>
		</li>
		<li class="top_navi<c:if test="${activeMenu == 'config'}"> current</c:if>" >
            <a data-dropdown="#dropdown-config" href="<%=request.getContextPath()%>/config/user/">Configuration
                <core:ErrorHint requiredSetupTasks="${requiredSetupTasks}" mainItemName="config"/>
            </a>
            <%@ include file="/WEB-INF/jsp/navigation/subNaviConfiguration.jsp" %>
		</li>
	</ul>
</nav>