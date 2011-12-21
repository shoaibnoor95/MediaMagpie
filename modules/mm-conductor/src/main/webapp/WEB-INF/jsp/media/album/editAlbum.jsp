<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.AlbumController"%>
<%@ page import="de.wehner.mediamagpie.common.persistence.entity.Visibility" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/core.tld" prefix="core"%>

<c:choose>
	<c:when test="${album.isNew}">
		<c:set var="title" value="New Album"  scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="title" value="Edit Album"  scope="request"/>
	</c:otherwise>
</c:choose>
<c:set var="activeMenu" value="media" scope="request"/>
<c:set var="activeSubMenu" value="listAlbums" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request"/>
<c:import url="/header"/>
<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/pages/album_edit.js"></script>

			<div id="content">
                <h1>${title}</h1>
					<form:form commandName="albumCommand"  id="myForm" cssClass="decorated">
					<form:hidden path="id"/>
					<fieldset>
						<legend></legend>
						<dl><form:errors path="name" cssClass="error"/>
							<dt>
								<label>Name:</label>
							</dt>
							<dd>
								<form:input path="name" cssClass="req"/>
							</dd>
							<dd class="help">Choose a name for your album</dd>	
						</dl>
						<dl><form:errors path="visibility" cssClass="error"/>
							<dt>
								<label>Visibility:</label>
							</dt>
							<dd>
								<form:select path="visibility" items="<%=Visibility.values()%>" cssClass="req"/>
							</dd>
							<dd class="help">Choose a name for your album</dd>	
						</dl>
						<dl>
							<dt>
								<label>Uid:</label>
							</dt>
							<dd>
								${albumCommand.uid}
							</dd>
							<form:hidden path="uid"/>
						</dl>
						<dl class="buttons">
							<dt>
								&nbsp;
							</dt>
							<dd>
								<button type="button" onclick="document.location.href='<%=request.getContextPath()%>/media/album/list'"><span>Cancel</span></button>
								<button type="submit" class="active"><span>Save</span></button>
							</dd>	
						</dl>
					</fieldset>
					<form:hidden path="isNew"/>
					</form:form>
				                
			</div>
<c:import url="/footer"/>