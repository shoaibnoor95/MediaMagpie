<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.AdministrationController"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<c:set var="title" value="Main Configuration" scope="request"/>
<c:set var="activeMenu" value="config" scope="request"/>
<c:set var="activeSubMenu" value="config" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request"/>
			
			<div id="content">
				<h1>
					${title}
				</h1>
				
				<dl>
					<dt>
						<label>Base Path to temporary files:</label>
					</dt>
					<dd>${conf.tempMediaPath}</dd>	
				</dl>
				<dl>
					<dt>
						<label>Base Path to user's upload media files:</label>
					</dt>
					<dd>${conf.baseUploadPath}</dd>	
				</dl>
				<dl>
					<dt>
						<label>Default Thumb Image Size:</label>
					</dt>
					<dd>${conf.defaultThumbSize}</dd>	
				</dl>
				<dl>
					<dt>
						<label>Default Detail Image Size (medium):</label>
					</dt>
					<dd>${conf.defaultGalleryDetailThumbSize}</dd>	
				</dl>
				<dl>
					<dt>
						<label>Default Detail Image Size (large):</label>
					</dt>
					<dd>${conf.defaultDetailThumbSize}</dd>	
				</dl>
				<dl>
				<dl>
					<dt>
						<label>&nbsp;</label>
					</dt>
					<dd><button type="button" onclick="document.location.href='<%=request.getContextPath()+AdministrationController.getBaseRequestMappingUrl()+AdministrationController.URL_MAINCONFIG_EDIT%>'" class="active"><span>Edit</span></button></dd>	
				</dl>
			</div>
