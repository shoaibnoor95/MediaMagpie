<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/tlds/core.tld" prefix="core"%>

<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.SearchController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.MediaDetailController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.S3Controller"%>

<c:set var="title" value="Media Pool" scope="request" />
<c:set var="activeMenu" value="media" scope="request" />
<c:set var="activeSubMenu" value="mediaSearch" scope="request" />
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request" />

<!-- <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/pages/searchMedias.js"></script>*/%>-->
<script type="text/javascript">
    <%@ include file="../../../static/js/pages/searchMedias.js" %>
</script>
<div id="content">
	<h1>${title}</h1>

	<form:form id="mediaForm" class="decorated" commandName="searchCriteria">
		<input id="input_action" type="hidden" value="" name="action" />
		<input id="input_id" type="hidden" value="" name="id" />
		<fieldset>
			<legend>Search Criterias</legend>
			<dl>
				<dt>
					<label for="yearCriteria">Year:</label>
				</dt>
				<dd>
					<div id="slider-select-year" style="float: left;"></div>
				</dd>
				<dd style="width: 100px;">
					<form:input path="yearCriteria" style="border:0; font-weight:bold; width:auto;" size="12" />
				</dd>
				<form:errors path="yearCriteria" cssClass="error" />
			</dl>
			<dl>
				<dt>
					<label>Buzzwords:</label>
				</dt>
				<dd>
					<form:input path="buzzword" />
				</dd>
			</dl>
			<form:hidden path="sliderYearMinMax" />
			<form:hidden path="sliderYearValues" />
			<dl>
				<dt></dt>
				<dd>
					<button type="submit" class="default" title="find all medias for selected search criterias">
						<span>Search</span>
					</button>
				</dd>
			</dl>
		</fieldset>
		<fieldset>
			<legend>Sort Order</legend>
			<dl>
				<dt>
					<label for="sortOrder">Sort by:</label>
				</dt>
				<dd>
					<form:select path="sortOrder" onchange="this.form.submit();">
						<form:options items="${mediaSortOrders}" itemLabel="displayName" />
					</form:select>
				</dd>
			</dl>
		</fieldset>
	</form:form>

	<br />
	<core:pagination current="${start}" pageSize="${pageSize}" total="${totalHits}" query="<%=request.getContextPath()%>" />

	<div class="ui-widget ui-helper-clearfix">
		<ul id="gallery" class="gallery ui-helper-reset ui-helper-clearfix">
			<c:forEach items="${pictures}" var="picture">
				<li class="ui-widget-content ui-corner-tr flippable" id="${picture.id}">
					<div class="front">
						<h5 class="ui-widget-header">
							<c:out value="${picture.title}" />
						</h5>
						<p>
							<!-- <a class="ui-icon ui-icon-trash" href="link/to/trash/script/when/we/have/js/off" title="Delete this image" >Delete image</a>-->
							<a href="<%=request.getContextPath()+MediaDetailController.URL_BASE_DETAIL_PICTURE_EDIT%>${picture.id}"><img
								src="${picture.urlThumbImage}" /></a>
						</p>
						<img class="image-action flipBack" title="get more information" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/information.png" />
						<img class="image-action delete" title="move to trash" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/bin_closed.png" />
					</div>
					<div class="back">
						<h5 class="ui-widget-header">
							<c:out value="${picture.title}" />
						</h5>
                        <dl class="metadata">
							<dt>ID:</dt> <dd><c:out value="${picture.media.id}"/></dd>
                        </dl>
						<dl class="metadata">
							<dt>date:</dt> <dd><core:date date="${picture.media.creationDate}"/><dd/>
						</dl>
                        <dl class="metadata">
                            <dt>File:</dt> <dd><c:out value="${picture.fileName}"/></dd>
						</dl>
						<p>
							<c:if test="${picture.s3Available}">
								<c:choose>
									<c:when test="${picture.media.exportedToS3}">
										<button title="copy your media to S3 bucket">Export to S3 (overwrite)</button>
									</c:when>
									<c:otherwise>
										<button title="overwrite your media to S3 bucket">Export to S3</button>
									</c:otherwise>
								</c:choose>
								<img class="in-process" title="exporting to S3" width="20px" src="<%=request.getContextPath()%>/static/images/loader.gif" />
							</c:if>
						</p>
						<img class="image-action flipFront" title="go back to image" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/bullet_go.png" />
					</div>
				</li>
			</c:forEach>
		</ul>
	</div>

	<core:pagination current="${start}" pageSize="${pageSize}" total="${totalHits}" query="<%=request.getContextPath()%>" />
	<br />

	<!--  album selection and droppable area -->
	<form:form action="select_album" commandName="albumSelectionCommand">
		select Album: <form:select path="albumId" items="${availableAlbums}" itemValue="id" itemLabel="name" multiple="false" />
		<button type="submit" title="select an album">
			<span>Select</span>
		</button>
		<input type="hidden" name="start" value="${start}" />
	</form:form>

	<div id="albumArea">
		<%@ include file="album-media-template.jsp"%>
	</div>
</div>