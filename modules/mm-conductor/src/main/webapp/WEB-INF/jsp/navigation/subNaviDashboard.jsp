<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<ul>
	<li class="<c:if test="${activeSubMenu == 'cooliris'}">active </c:if>splitter">
		<a href="<%=request.getContextPath()%>/dashboard_cooliris">open cooliris</a>
	</li>
	<li class="<c:if test="${activeSubMenu == 'dashboard'}">active </c:if>splitter">
		<a href="<%=request.getContextPath()%>/dashboard">open</a>
	</li>
</ul>