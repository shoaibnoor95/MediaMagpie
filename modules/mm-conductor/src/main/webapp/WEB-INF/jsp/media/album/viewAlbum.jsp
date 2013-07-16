<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.AlbumController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.PublicAlbumController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.util.WebAppUtils" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/core.tld" prefix="core"%>

<%@ page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<c:set var="activeMenu" value="media" scope="request"/>
<c:set var="activeSubMenu" value="listAlbums" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request"/>
<c:set var="linkBase" value="<%=WebAppUtils.buildHttpRequestBasedOnServletRequest(request, pageContext)+PublicAlbumController.getBaseRequestMappingUrl()%>" />
<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/pages/album_edit.js"></script>
			
			<div id="content">
				<h1>
					${title}
				</h1>
				
				<dl>
					<dt>
						<label>Name:</label>
					</dt>
					<dd>${albumCommand.name}</dd>	
				</dl>
				<dl>
					<dt>
						<label>Creation Date:</label>
					</dt>
					<dd><core:date date="${albumCommand.creationDate}"/></dd>	
				</dl>
				<dl>
					<dt>
						<label>Visibility:</label>
					</dt>
					<dd>${albumCommand.visibility}</dd>	
				</dl>
				<dl>
					<dt>
						<label>Public Link:</label>
					</dt>
					<dd>
					   <c:set var="link" value="${linkBase}/${albumCommand.uid}/view" />
						<img src="<%=request.getContextPath()%>/static/images/famfamfam_silk/paste_plain.png" 
							onclick="window.prompt ('Copy to clipboard: Ctrl+C, Enter', '${link}');"/>
						<a href="${link}" >${link}</a>
					</dd>	
				</dl>
				<dl>
					<dt>
						<label>Image Count:</label>
					</dt>
					<dd>${fn:length(albumCommand.medias)}</dd>	
				</dl>
				<dl>
					<dt>
						<label>&nbsp;</label>
					</dt>
					<dd><button type="button" onclick="document.location.href='<%=AlbumController.getBaseRequestMappingUrl()%>/${albumCommand.id}/edit'" class="active"><span>Edit</span></button></dd>	
				</dl>
			</div>
