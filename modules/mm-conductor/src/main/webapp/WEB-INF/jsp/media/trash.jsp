<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>

<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.MediaDetailController"%>

<c:set var="title" value="Search Pictures" scope="request" />
<c:set var="activeMenu" value="media" scope="request" />
<c:set var="activeSubMenu" value="trash" scope="request" />
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request" />

<head>
    <title>${title}</title>
    <script type="text/javascript" src="/static/js/jquery.pollingThumb.js"></script>
	<script type="text/javascript">
		function executeCommand(action, id) {
			$("#input_action").attr("value", action);
			$("#input_id").attr("value", id);
			$("#mediaForm").submit();
		}
        'use strict';
        $(function() {
            $('img.thumb').pollingThumb();
        });
	</script>
</head>
<body>
	<h2>Trash</h2>

	<!--  TODO rwe: maybe we can outsource the search form into a separate jsp file -->
	<form:form id="mediaForm" class="decorated" commandName="editCommand">
		<input id="input_action" type="hidden" value="" name="action">
		<input id="input_id" type="hidden" value="" name="id">
	</form:form>
	<core:pagination current="${start}" pageSize="${pageSize}" total="${totalHits}" />
	<table id="pictureMarkup" class="table table-striped">
		<thead>
			<tr>
				<th>Thumb</th>
				<th>Name</th>
				<th>Created</th>
				<th>Action</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${pictures}" var="picture">
				<tr>
					<td><a href="<%=request.getContextPath()+MediaDetailController.URL_BASE_DETAIL_PICTURE_EDIT%>${picture.media.id}">
					   <img class="thumb" src="<%=request.getContextPath()%>${picture.urlThumbImage}" /></a></td>
					<td>${picture.media.name}</td>
					<td>${picture.media.creationDate}</td>
					<td><img class="image-action" title="undo" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/arrow_undo.png"
						onclick="javascript:executeCommand('UNDO', '${picture.media.id}');" /> 
						<img class="image-action" title="remove from trash"	src="<%=request.getContextPath()%>/static/images/famfamfam_silk/delete.png"
						onclick="javascript:executeCommand('DELETE', '${picture.media.id}');" />
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<core:pagination current="${start}" pageSize="${pageSize}" total="${totalHits}" />
</body>
