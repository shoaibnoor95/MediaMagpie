<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/core.tld" prefix="core"%>

<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.MediaDetailController" %>

<c:set var="title" value="Search Pictures"  scope="request"/>
<c:set var="activeMenu" value="media" scope="request"/>
<c:set var="activeSubMenu" value="trash" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request"/>
			<div id="content">
                <h1>Trash</h1>
                
                
<script type="text/javascript">
	function executeCommand(action, id) {
		$("#input_action").attr("value", action);
		$("#input_id").attr("value", id);
		$("#mediaForm").submit();
		} 
</script>
<!--  TODO rwe: maybe we can outsource the search form into a separate jsp file -->
				<form:form id="mediaForm" class="decorated" commandName="editCommand">
					<input id="input_action" type="hidden" value="" name="action">
					<input id="input_id" type="hidden" value="" name="id">
				</form:form>
				<br/>
				<core:pagination current="${start}" pageSize="${pageSize}" total="${totalHits}" query="<%=request.getContextPath()%>" />
                <table id="pictureMarkup" >
                    <thead>
                        <tr>
                            <th>Bild</th>
                            <th>Name</th>
                            <th>Erstellt</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${pictures}" var="picture" varStatus="status">
                        	<c:set var="tdClass" value=""/>
                        <c:if test="${status.count % 2 == 0}">
                        	<c:set var="tdClass" value="alt"/>
                        </c:if>
                        <tr class="${tdClass}">
                            <td><a href="<%=request.getContextPath()+MediaDetailController.URL_BASE_DETAIL_PICTURE_EDIT%>${picture.id}"><img src="<%=request.getContextPath()%>${picture.thumbImageLink}"/></a></td>
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
                                <img class="image-action" alt="delete" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/accept.png" onclick="javascript:executeCommand('UNDO', '${picture.id}');"> 
                                <img class="image-action" alt="delete" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/delete.png" onclick="javascript:executeCommand('DELETE', '${picture.id}');"> 
                            </td>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
				<core:pagination current="${start}" pageSize="${pageSize}" total="${totalHits}" query="<%=request.getContextPath()%>" />
				<input type="submit"/>
			</div>
