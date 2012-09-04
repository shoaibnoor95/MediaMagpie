<div id="mainNavi">
	<ul>
		<li <c:if test="${activeMenu == 'welcome'}">class="current"</c:if>>
			<a href="<%=request.getContextPath()%>/welcome">Welcome</a>
		</li>
		<li <c:if test="${activeMenu == 'media'}">class="current"</c:if>>
			<a href="<%=request.getContextPath()%>/media/search_pictures">Media</a>
		</li>
		<li <c:if test="${activeMenu == 'config'}">class="current"</c:if>>
			<a href="<%=request.getContextPath()%>/config/user/">Configuration</a>
		</li>
	</ul>
</div>