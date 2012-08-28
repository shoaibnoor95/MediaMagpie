<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.UserConfiguratonControllerS1"%>

<c:set var="title" value="User's Program Configuration (2/2)" scope="request"/>
<c:set var="activeMenu" value="config" scope="request"/>
<c:set var="activeSubMenu" value="user_config" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request"/>
			
			<div id="content">
				<h1>
					${title}
				</h1>
				
				<div>
					<form:form commandName="userConfigurationCommand"  id="myForm" cssClass="decorated">
					<fieldset>
						<legend>Program</legend>
						<dl>
							<dt>
								<label>Root Media Path:</label>
							</dt>
							<dd>
								<form:textarea path="rootMediaPathes" rows="3" cols="20" />
								<img src="<%=request.getContextPath()%>/static/images/famfamfam_silk/help.png" title="One or more pathes were medias will be searched."/>
							</dd>
							<dd class="help">e.g. '/home/james/pictures'</dd>
							<form:errors path="rootMediaPathes" cssClass="error"/>	
						</dl>
						<dl>
							<dt>
								<label>Thumb Image Size:</label>
							</dt>
							<dd>
								<form:input path="thumbImageSize" cssClass="req"/>
							</dd>
							<dd class="help">e.g. '120'</dd>	
							<form:errors path="thumbImageSize" cssClass="error"/>
						</dl>
						<dl>
							<dt>
								<label>Thumb Image Size Table:</label>
							</dt>
							<dd>
								<form:input path="thumbImageSizeTable" cssClass="req"/>
							</dd>
							<dd class="help">e.g. '60'</dd>	
							<form:errors path="thumbImageSizeTable" cssClass="error"/>
						</dl>
						<dl>
							<dt>
								<label>Detail Image Size:</label>
							</dt>
							<dd>
								<form:input path="detailImageSize" cssClass="req"/>
							</dd>
							<dd class="help">e.g. '1024'</dd>	
							<form:errors path="detailImageSize" cssClass="error"/>
						</dl>
						<dl>
							<dt>
								<label>Re-Scan Media Pathes now:</label>
							</dt>
							<dd>
								<form:checkbox path="syncMediaPahtes" />
							</dd>
							<dd class="help">When selected your complete media path will be synchronized against the databse.</dd>	
						</dl>
						<dl class="buttons">
							<dt>
								&nbsp;
							</dt>
							<dd>
								<button type="button" onclick="document.location.href='<%=request.getContextPath()+UserConfiguratonControllerS1.getBaseRequestMappingUrl()+UserConfiguratonControllerS1.URL_USERCONFIG%>'"><span>Cancel</span></button>
								<button type="submit" class="active" name="_back"><span>Back</span></button>
								<button type="submit" class="active"><span>Save</span></button>
							</dd>	
						</dl>
					</fieldset>
					</form:form>
				</div>
			</div>