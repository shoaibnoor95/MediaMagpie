<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.AlbumController"%>
<%@ page import="de.wehner.mediamagpie.persistence.entity.Visibility"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>

<c:choose>
	<c:when test="${albumCommand.isNew}">
		<c:set var="title" value="New Album" scope="request" />
	</c:when>
	<c:otherwise>
		<c:set var="title" value="Edit Album" scope="request" />
	</c:otherwise>
</c:choose>
<c:set var="activeMenu" value="media" scope="request" />
<c:set var="activeSubMenu" value="listAlbums" scope="request" />
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request" />
<head>
<script type="text/javascript">
    $(function() {
        $('form:first *:input[type!=hidden]:first').focus();
    });
</script>
</head>
<body>
	<h2>${title}</h2>
	<form:form commandName="albumCommand" cssClass="form-horizontal" role="form">
		<form:hidden path="id" />
		<legend>Album Configuration</legend>
        <div class="form-group">
            <label for="name" class="col-sm-2 control-label">Name</label>
            <div class="col-sm-4">
                <form:input path="name" cssClass="form-control" />
                <form:errors path="name" cssClass="error" />
            </div>
            <span class="help-block">Choose a name for your album</span>
        </div>
        <div class="form-group">
            <label for="visibility" class="col-sm-2 control-label">Visability</label>
            <div class="col-sm-4">
                <form:select path="visibility" items="<%=Visibility.values()%>" cssClass="form-control" />
            </div>
            <span class="help-block"></span>
        </div>
        <div class="form-group">
            <label class="col-sm-2 control-label">UUID</label>
            <div class="col-sm-4">
                <p class="form-control-static">${albumCommand.uid}</p>
            </div>
            <form:hidden path="uid" />
        </div>
        <!-- buttons -->
        <div class="form-group">
            <div class="col-sm-offset-2 col-sm-10">
                <button type="button" class="btn btn-default"
                    onclick="document.location.href='<%=request.getContextPath()%>/media/album/list'">Cancel</button>
                <button type="submit" class="btn btn-primary">Save</button>
            </div>
        </div>
		<form:hidden path="isNew" />
	</form:form>
</body>
