<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.SearchController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.MediaDetailController"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.S3Controller"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.WelcomeController"%>
<c:set var="title" value="Media Pool" scope="request" />
<c:set var="activeMenu" value="media" scope="request" />
<c:set var="activeSubMenu" value="mediaSearch" scope="request" />
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request" />
<html>
    <head>
        <title>${title}</title>
        <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/effects.css"/>" />

        <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/ui-lightness/jquery-ui-1.8.13.custom.css"/>" />
        <script src="https://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
        
        <!-- include directly searchMedias.js to substitude jsp variables in java script -->
        <script type="text/javascript"><%@ include file="../../../static/js/pages/searchMedias.js" %></script>
        <!-- prefixfree modifies loaded css and handles them as loaded directly into page. -->
        <script type="text/javascript" src="/static/js/prefixfree.min.js"></script>
        <script type="text/javascript">
            // prefixfree plugin for jquery to enable prefixfree functionality for css changes by jquery
                    (function($, self) {
                        if (!$ || !self) {
                            return;
                        }
                        for (var i = 0; i < self.properties.length; i++) {
                            var property = self.properties[i], camelCased = StyleFix.camelCase(property), PrefixCamelCased = self
                                    .prefixProperty(property, true);

                            $.cssProps[camelCased] = PrefixCamelCased;
                        }
                    })(window.jQuery, window.PrefixFree);
                  
            
            // rwe: test , test, test
/*                    jQuery(document).ready(function() {
                        window.setInterval("updatelapse();", 1000);
                     });*/
	

                                    var brokenThumbs = [];
									function addErrorHandlerForThumbs() {
										var thumbs = $('img.thumb');
										thumbs.error(function() {
											console.log("404 error: " + $(this).attr('src'));
											brokenThumbs.push($(this));
											//$(this).attr("src", "/static/images/ui-anim_basic_16x16.gif")
										});
									};
									function refreshCameras() {
/*										$('img.thumb').attr('src', function(i, old) {
											return old.replace(/\&i=.+/, "&i=" + (Math.random() * 1000));
										});*/
                                        jQuery.each(brokenThumbs, function(index, thumb) {
                                        	thumb.attr('src', function(i, old) {
                                                return old.replace(/\&i=.+/, "&i=" + (Math.random() * 1000));
                                            });
                                        });
										if(brokenThumbs.length > 0) {
										                       brokenThumbs = [];
										    setTimeout(refreshCameras, 1000);
										}
									};
									function refreshCamerasFirst() {
										/*var thumbs = $('img.thumb');
										thumbs.attr('src', function(i, old) {
											var newSrc = old + "&i=" + (Math.random() * 1000);
											//console.log("Changing thumb image url: "+old+" -> "+newSrc);
											return newSrc;
										});*/
										//get404Images();
										jQuery.each(brokenThumbs, function(index, thumb) {
											thumb.attr('src', function(i, old) {
												var newSrc = old + "&i=" + (Math.random() * 1000);
												console.log("Changing thumb image url: " + old + " -> " + newSrc);
												return newSrc;
											});
										});
                                        brokenThumbs = [];
										setTimeout(refreshCameras, 1000);
									};

									$(function() {
										addErrorHandlerForThumbs();
										setTimeout(refreshCamerasFirst, 1000);
									});
								</script>
    </head>
    <body>
<!--         <ol class="breadcrumb">
            <li><a href="<%=request.getContextPath()+WelcomeController.WELCOME_URL%>">Home</a></li>
            <li class="active">Medias</li>
        </ol>
-->    
	<h2>${title}</h2>

	<form:form id="mediaForm" cssClass="form-horizontal" role="form" commandName="searchCriteria">
        <legend>Search Criterias</legend>
		<input id="input_action" type="hidden" value="" name="action" />
		<input id="input_id" type="hidden" value="" name="id" />
		<form:hidden path="sliderYearMinMax" />
		<form:hidden path="sliderYearValues" />
        <div class="form-group">
            <label for="yearCriteria" class="col-sm-2 control-label">Year</label>
            <div class="col-sm-5">
                <div id="slider-select-year" style="float: left;"></div>
                <br/>
                <form:input path="yearCriteria" style="border:0; font-weight:bold; width:auto;" size="12" />
                <form:errors path="yearCriteria" cssClass="error" />
             </div>
        </div>
        <div class="form-group">
            <label for="buzzword" class="col-sm-2 control-label">Search</label>
            <div class="col-sm-4">
                <form:input path="buzzword" cssClass="form-control" placeholder="some key word"/>
            </div>
        </div>
        <div class="form-group">
            <label for="sortOrder" class="col-sm-2 control-label">Sort Order</label>
            <div class="col-sm-2">
				<form:select path="sortOrder" onchange="this.form.submit();" class="form-control input-sm">
				    <form:options items="${mediaSortOrders}" itemLabel="displayName" />
				</form:select>
            </div>
            <button type="submit" class="btn btn-primary">Search</button>
        </div>
	</form:form>

    <br/>
    <div class="panel panel-default">
    <div class="panel-heading"><strong>Result</strong></div>
    <div class="panel-body">
<div id="imageContainer"></div>    
  	<core:pagination cssClass="" current="${start}" pageSize="${pageSize}" total="${totalHits}" path="" />
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
							<a href="<%=request.getContextPath()+MediaDetailController.URL_BASE_DETAIL_PICTURE_EDIT%>${picture.id}">
						      <img class="thumb" src="${picture.urlThumbImage}" /></a>
						</p>
						<img class="image-action flipBack" title="get more information" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/information.png" />
						<img class="image-action delete" title="move to trash" src="<%=request.getContextPath()%>/static/images/famfamfam_silk/bin_closed.png" />
					</div>
					<div class="back small" >
						<h5 class="ui-widget-header">
							<c:out value="${picture.title}" />
						</h5>
                        <table class="small small" >
                            <tr>
                                <td><c:out value="${picture.media.id}"/></td>
                            </tr>
                            <tr>
	                            <td><c:out value="${picture.fileName}"/></td>
                            </tr>
                            <tr>
	                            <td><core:date date="${picture.media.creationDate}"/></td>
                            </tr>
                            <tr>
	                            <td><c:out value="${picture.hashValueShort}"/></td>
                            </tr>
                        </table>
                        <br/>
						<p>
							<c:if test="${picture.s3Available}">
								<c:choose>
									<c:when test="${picture.media.exportedToS3}">
									    <button title="overwrite your media to S3 bucket" type="button" class="btn btn-warning btn-xs small">Overwrite to S3</button>
									</c:when>
									<c:otherwise>
                                        <button title="insert your media to S3 bucket" type="button" class="btn btn-primary btn-xs">Export to S3</button>
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
    <core:pagination current="${start}" pageSize="${pageSize}" total="${totalHits}" path="search_pictures" />
    </div>
    </div>
    
    <div class="panel panel-default">
    <div class="panel-heading">
    <!--  album selection and droppable area -->
    <form:form class="form-inline" role="form" action="select_album" commandName="albumSelectionCommand">
        <div class="form-group">
            <label for="albumId" class="control-label">selected Album: &nbsp;</label>
            <form:select class="form-control" path="albumId" items="${availableAlbums}" itemValue="id" itemLabel="name" multiple="false" />
            <button type="submit" class="btn btn-success">OK</button>
            <input type="hidden" name="start" value="${start}" />
            <span class="text-info">&nbsp;Drag and Drop the media to add or reorder your album.</span>
        </div>
    </form:form>
    </div>
    <div class="panel-body">
	<div id="albumArea">
		<%@ include file="album-media-template.jsp"%>
	</div>
    </div>
    </div>
    </body>
</html>
