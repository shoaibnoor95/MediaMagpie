<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<div id="mainNavi">
	<ul>
		<li <c:if test="${activeMenu == 'welcome'}">class="current"</c:if>>
			<a href="<%=request.getContextPath()%>/welcome">Welcome</a>
		</li>
		<li <c:if test="${activeMenu == 'media'}">class="current"</c:if>>
			<a href="<%=request.getContextPath()%>/media/search_pictures">Media</a>
		</li>
		<li <c:if test="${activeMenu == 'config'}">class="current"</c:if>>
			<a href="<%=request.getContextPath()%>/config/user/">Configuration
                <core:ErrorHint requiredSetupTasks="${requiredSetupTasks}" mainItemName="config"/>
            </a>
		</li>
	</ul>
</div>