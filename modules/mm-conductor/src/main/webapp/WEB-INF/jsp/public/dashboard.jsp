<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>

<c:set var="title" value="Dashboard"  scope="request"/>
<c:set var="activeMenu" value="dashboard" scope="request"/>
<c:set var="activeSubMenu" value="dashboard" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviDashboard" scope="request"/>
			<div id="content">
                <h1>${title}</h1>
                
                <h2>Last ${totalHits} medias added to MediaMagpie</h2>
                
                <div id="pictures">
				<core:pagination current="${start}" pageSize="${pageSize}" total="${totalHits}" path="" />
                <table id="pictureMarkup">
                    <thead>
                        <tr>
                            <th>Bild</th>
                            <th>Name</th>
                            <th>Erstellt</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${pictures}" var="picture">
                        <tr>
                            <td><a href="<%=request.getContextPath()%>${picture.detailImageUrl}"><img src="<%=request.getContextPath()%>${picture.thumbImageLink}"/></a></td>
                            <td>
                            	<c:choose>
	                            	<c:when test="${not empty picture.name}">
    	                        		<c:out value="${picture.name}" />
        	                    	</c:when>
            	                	<c:otherwise>
	            	                	${picture.uri}
                    	        	</c:otherwise>
                    	        </c:choose>
                            </td>
                            <td>${picture.creationDate}</td>
                            <td>
                                <button onclick="javascript:showDeleteDialog('${picture.id}', '${picture.name}')"><img src="<%=request.getContextPath()%>/static/images/famfamfam_silk/delete.png" border="0" title="Delete"/></button> 
                            </td>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
				<core:pagination current="${start}" pageSize="${pageSize}" total="${totalHits}" path="" />
				</div>
			</div>
