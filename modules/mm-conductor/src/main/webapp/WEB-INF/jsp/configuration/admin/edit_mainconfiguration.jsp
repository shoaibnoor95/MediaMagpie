<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.configuration.AdministrationController"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<c:set var="title" value="Main Configuration" scope="request"/>
<c:set var="activeMenu" value="config" scope="request"/>
<c:set var="activeSubMenu" value="config" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviConfiguration" scope="request"/>
<c:import url="/header"/>
			
			<div id="content">
				<h1>
					${title}
				</h1>
				
				<div>
					<form:form commandName="conf"  id="myForm" cssClass="decorated">
					<fieldset>
						<legend>Settings</legend>
						<dl>
							<dt>
								<label>Base Path to temporary files:</label>
							</dt>
							<dd>
								<form:input path="tempMediaPath" cssClass="req"/>
								<img src="/static/images/famfamfam_silk/help.png" title="Path used to store temporary generated files for thumb images."/>
							</dd>
							<dd class="help">e.g. '/tmp/mediabutler/thumbs'</dd>	
							<form:errors path="tempMediaPath" cssClass="error"/>
						</dl>

						<dl>
							<dt>
								<label>Base Path to user's upload medias:</label>
							</dt>
							<dd>
								<form:input path="baseUploadPath" cssClass="req"/>
								<img src="/static/images/famfamfam_silk/help.png" title="Base path where user specific directories will be created used to store uploaded media files."/>
							</dd>
							<dd class="help">e.g. '/tmp/mediabutler/usermedias'</dd>	
							<form:errors path="baseUploadPath" cssClass="error"/>
						</dl>

						<dl>
							<dt>
								<label>Default Thumb Image Size:</label>
							</dt>
							<dd>
								<form:input path="defaultThumbSize" cssClass="req"/>
								<img src="/static/images/famfamfam_silk/help.png" title="The default (non user specific) size of a thumb image."/>
							</dd>
							<dd class="help">e.g. '120'</dd>	
							<form:errors path="defaultThumbSize" cssClass="error"/>
						</dl>

						<dl>
							<dt>
								<label>Default Detail Image Size (medium):</label>
							</dt>
							<dd>
								<form:input path="defaultGalleryDetailThumbSize" cssClass="req"/>
								<img src="/static/images/famfamfam_silk/help.png" title="The detail image size used in galleries."/>
							</dd>
							<dd class="help">e.g. '500'</dd>	
							<form:errors path="defaultGalleryDetailThumbSize" cssClass="error"/>
						</dl>

						<dl>
							<dt>
								<label>Default Detail Image Size (large):</label>
							</dt>
							<dd>
								<form:input path="defaultDetailThumbSize" cssClass="req"/>
								<img src="/static/images/famfamfam_silk/help.png" title="The default (non user specific) detail size of an image."/>
							</dd>
							<dd class="help">e.g. '1024'</dd>	
							<form:errors path="defaultDetailThumbSize" cssClass="error"/>
						</dl>

						<dl>
							<dt>
								<label>Create missing directories:</label>
							</dt>
							<dd>
								<form:checkbox path="createDirectories" />
							</dd>
							<dd class="help">When selected the system tries to create all necessary directories itself.</dd>	
						</dl>

						<dl class="buttons">
							<dt>
								&nbsp;
							</dt>
							<dd>
								<button type="button" onclick="document.location.href='<%=request.getContextPath()+AdministrationController.getBaseRequestMappingUrl()+AdministrationController.URL_MAINCONFIG%>'"><span>Cancel</span></button>
								<button type="submit" class="active"><span>Save</span></button>
							</dd>	
						</dl>
					</fieldset>
					</form:form>
				</div>
			</div>
				                
			<c:import url="/footer"/>
