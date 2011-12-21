<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.UserConfiguratonControllerS1"%>
<c:set var="title" value="User Configuration" scope="request"/>
<c:set var="activeMenu" value="config" scope="request"/>
<c:set var="activeSubMenu" value="user_config" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request"/>
<c:import url="/header"/>
			
			<div id="content">
				<h1>
					${title}
				</h1>
				
				<dl>
					<dt>
						<label>Login:</label>
					</dt>
					<dd>${user.name}</dd>	
				</dl>
				<dl>
					<dt>
						<label>Name:</label>
					</dt>
					<dd>${user.forename}</dd>	
				</dl>
				<dl>
					<dt>
						<label>Surname:</label>
					</dt>
					<dd>${user.surname}</dd>	
				</dl>
				<dl>
					<dt>
						<label>Email:</label>
					</dt>
					<dd>${user.email}</dd>	
				</dl>
				
				<dl></dl>

				<dl>
					<dt>
						<label>Root Media Path:</label>
					</dt>
					<dd>
						<c:forEach var="path" items="${conf.rootMediaPathes}">
   							<c:out value="${path}" /><br/>
						</c:forEach>
					</dd>	
				</dl>
				<dl>
					<dt>
						<label>Thumb Image Size:</label>
					</dt>
					<dd>${conf.thumbImageSize}</dd>	
				</dl>
				<dl>
					<dt>
						<label>Thumb Image Size Table:</label>
					</dt>
					<dd>${conf.thumbImageSizeTable}</dd>	
				</dl>
				<dl>
					<dt>
						<label>Detail Image Size:</label>
					</dt>
					<dd>${conf.detailImageSize}</dd>	
				</dl>
				<dl>
					<dt>
						<label>&nbsp;</label>
					</dt>
					<dd><button type="button" onclick="document.location.href='<%=request.getContextPath()+UserConfiguratonControllerS1.getBaseRequestMappingUrl()%>/edit'" class="active"><span>Edit</span></button></dd>	
				</dl>
			</div>
				                
			<c:import url="/footer"/>
