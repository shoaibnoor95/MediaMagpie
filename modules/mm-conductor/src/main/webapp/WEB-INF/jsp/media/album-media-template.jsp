<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.AlbumController"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ taglib uri="/WEB-INF/tlds/core.tld" prefix="core"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.MediaDetailController" %>

<c:if test="${not empty albumCommand}">
		<div id="album" class="ui-widget-content ui-state-default">
			<h4 class="ui-widget-header"><span class="ui-icon ui-icon-album">Album</span>${albumCommand.name}</h4>

				<div class="ui-widget ui-helper-clearfix">
					<ul class="gallery ui-helper-reset ui-helper-clearfix">
                      <c:forEach items="${mediasInAlbum}" var="picture">
                          <li class="ui-widget-content ui-corner-tr" id="${picture.id}" style="width:80px;">
                       		<h5 class="ui-widget-header">
                   	        <c:out value="${picture.title}" />
                       		</h5>
                       		<p>
                           <a href="<%=request.getContextPath()+MediaDetailController.URL_BASE_DETAIL_PICTURE_EDIT%>${picture.id}"><img src="${picture.urlThumbImage}" style="width:80px;" 
                           		alt="${picture.title}"/></a>
                           </p>
                           <!-- <p class="metadata"><core:date date="${picture.media.creationDate}" /></p>-->
							<img class="image-action remove" alt="remove" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/cross.png"/>
							</li>
						</c:forEach>
					</ul>
				</div>
		</div>
</c:if>