<%@ page contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<%@ taglib uri="/WEB-INF/tlds/core.tld" prefix="core"%>

<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.SearchController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.MediaDetailController"%>

<c:set var="title" value="Media Pool" scope="request" />
<c:set var="activeMenu" value="media" scope="request" />
<c:set var="activeSubMenu" value="mediaSearch" scope="request" />
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request" />

<!-- <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/pages/searchMedias.js"></script>*/%>-->
<script type="text/javascript">
	
<%@ include file="../../../static/js/pages/searchMedias.js" %>
	
</script>
    <script type="text/javascript">
        $(document).ready(function() {
            $("ul > li.flippable").click(function(event) {
                var $item = $(this);
                var $target = $(event.target);
                var $front = $(".front", $item);
                var $back = $(".back", $item);
                if ($target.is(".image-action.flipBack")) {
                    $front.addClass('transition-rotate');
                    $front.css('transform', 'rotateY(180deg)');
                    $back.addClass('transition-rotate');
                    $back.css('transform', 'rotateY(0deg)');
                    return true;
                } else if ($target.is(".image-action.flipFront")) {
                    $front.css('transform', 'rotateY(0deg)');
                    $back.css('transform', 'rotateY(-180deg)');
                    return true;
                }

                return false;
            });

        });
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
					<button type="submit" class="default">
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
						<p class="metadata">
							<core:date date="${picture.media.creationDate}" />
						</p> 
						<img class="image-action flipBack" alt="Information" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/information.png" />
	                    <img class="image-action delete" alt="delete" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/bin_closed.png" />
	                </div>
                    <div class="back">
                        <h5 class="ui-widget-header">
                            <c:out value="${picture.title}" />
                        </h5>
                        <p class="metadata">
                            <core:date date="${picture.media.creationDate}" />
                        </p> 
                        <img class="image-action flipFront" alt="Picture" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/information.png" />
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
		<button type="submit">
			<span>Select</span>
		</button>
		<input type="hidden" name="start" value="${start}" />
	</form:form>

	<div id="albumArea">
		rwe: include album-media-template.jsp START
		<%@ include file="album-media-template.jsp"%>
		rwe: include album-media-template.jsp END
	</div>
</div>