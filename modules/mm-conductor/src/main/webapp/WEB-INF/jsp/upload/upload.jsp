<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>

<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.UploadController" %>

<c:set var="title" value="Media Upload"  scope="request"/>
<c:set var="activeMenu" value="media" scope="request"/>
<c:set var="activeSubMenu" value="upload" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request"/>
<html>
    <head>
        <!-- css for upload area: -->
        <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/jquery.fileupload-ui.css">
        <link rel="stylesheet" href="<%=request.getContextPath()%>/static/css/jquery.fileupload.css">
          
		<!-- The jQuery UI widget factory, can be omitted if jQuery UI is already included -->
		<script src="<%=request.getContextPath()%>/static/js/fileupload/vendor/jquery.ui.widget.js"></script>
		<!-- The Templates plugin is included to render the upload/download listings -->
		<script src="<%=request.getContextPath()%>/static/js/tmpl.min.js"></script>
		<!-- The Load Image plugin is included for the preview images and image resizing functionality -->  
		<script src="<%=request.getContextPath()%>/static/js/load-image.min.js"></script>
		<!-- The Canvas to Blob plugin is included for image resizing functionality --> 
		<!-- rwe: really necessary? 
		<script src="https://blueimp.github.io/JavaScript-Canvas-to-Blob/js/canvas-to-blob.min.js"></script>-->
		<!-- blueimp Gallery script -->
		<!-- rwe: really necessary? 
		<script src="https://blueimp.github.io/Gallery/js/jquery.blueimp-gallery.min.js"></script>-->
		<!-- The Iframe Transport is required for browsers without support for XHR file uploads -->
		<script src="<%=request.getContextPath()%>/static/js/fileupload/jquery.iframe-transport.js"></script>
		<!-- The basic File Upload plugin -->
		<script src="<%=request.getContextPath()%>/static/js/fileupload/jquery.fileupload.js"></script>
		<!-- The File Upload processing plugin -->
		<script src="<%=request.getContextPath()%>/static/js/fileupload/jquery.fileupload-process.js"></script>
		<!-- The File Upload image preview & resize plugin -->
		<script src="<%=request.getContextPath()%>/static/js/fileupload/jquery.fileupload-image.js"></script>
		<!-- The File Upload audio preview plugin -->
		<script src="<%=request.getContextPath()%>/static/js/fileupload/jquery.fileupload-audio.js"></script>
		<!-- The File Upload video preview plugin -->
		<script src="<%=request.getContextPath()%>/static/js/fileupload/jquery.fileupload-video.js"></script>
		<!-- The File Upload validation plugin -->
		<script src="<%=request.getContextPath()%>/static/js/fileupload/jquery.fileupload-validate.js"></script>
		<!-- The File Upload user interface plugin -->
		<script src="<%=request.getContextPath()%>/static/js/fileupload/jquery.fileupload-ui.js"></script>
		<!-- The XDomainRequest Transport is included for cross-domain file deletion for IE 8 and IE 9 -->
		<!--[if (gte IE 8)&(lt IE 10)]>
		<script src="<%=request.getContextPath()%>/static/js/fileupload/cors/jquery.xdr-transport.js"></script>
		<![endif]-->
		<script type="text/javascript">
			$(function() {
				'use strict';
		
				// Initialize the jQuery File Upload widget:
				$('#fileupload').fileupload({});
		
				$('#fileupload').fileupload('option', {
					// Enable image resizing, except for Android and Opera,
					// which actually support image resizing, but fail to
					// send Blob objects via XHR requests:
					disableImageResize : true,// /Android(?!.*Chrome)|Opera/.test(window.navigator.userAgent),
					maxFileSize : 100*1024*1024,
	/*				imageMaxWidth : 800,
					imageMaxHeight : 800,
					imageCrop : true,*/ // Force cropped images
					acceptFileTypes : /(\.|\/)(gif|jpe?g|png|mov|mp4|avi|webm)$/i,
					dataType : 'json',
					done : function(e, data) {
						$.each(data.result.files, function(index, file) {
							var fu = $('#fileupload');
							$('<p/>').text(" received file: " + file.name + " with size " + file.size + " bytes").appendTo(fu);
							//$('<p/>').text(file.name).appendTo(document.body);
						});
					}
				});
		
			});
		</script>
    </head>
    <body>
        <div id="content">
            <h1>${title}</h1>
			<div style="border: dotted;">
			    <form id="fileupload" action="<%=request.getContextPath()+UploadController.getUploadUrl()%>" method="POST" enctype="multipart/form-data">
			        <!-- The fileupload-buttonbar contains buttons to add/delete files and start/cancel the upload -->
			        <div class="row fileupload-buttonbar">
			            <div class="col-lg-7">
			                <!-- The fileinput-button span is used to style the file input field as button -->
			                <span class="btn btn-success fileinput-button">
			                    <i class="glyphicon glyphicon-plus"></i>
			                    <span>Add files...</span>
			                    <input type="file" name="files[]" multiple="">
			                </span>&nbsp;
			                <button type="submit" class="btn btn-primary start">
			                    <i class="glyphicon glyphicon-upload"></i>
			                    <span>Start upload</span>
			                </button>
			                <button type="reset" class="btn btn-warning cancel">
			                    <i class="glyphicon glyphicon-ban-circle"></i>
			                    <span>Cancel upload</span>
			                </button>
			                <button type="button" class="btn btn-danger delete">
			                    <i class="glyphicon glyphicon-trash"></i>
			                    <span>Delete</span>
			                </button>
			            </div>
			            <!-- The global progress state -->
			            <div class="col-lg-5 fileupload-progress fade">
			                <!-- The global progress bar -->
			                <div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100">
			                    <div class="progress-bar progress-bar-success" style="width:0%;"></div>
			                </div>
			                <!-- The extended global progress state -->
			                <div class="progress-extended">&nbsp;</div>
			            </div>
			        </div>
			        <!-- The table listing the files available for upload/download -->
                    <table role="presentation" class="table table-striped">
                        <tbody class="files"></tbody>
                    </table>
			    </form>
			    <div id="progress">
                    <div class="bar" style="width: 0%;"></div>
                </div>
			</div>
		</div>

<!-- The template to display files available for upload -->
<script id="template-upload" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
    <tr class="template-upload fade">
        <td>
            <span class="preview"></span>
        </td>
        <td>
            <p class="name">{%=file.name%}</p>
            <strong class="error text-danger"></strong>
        </td>
        <td>
            <p class="size">Processing...</p>
            <div class="progress progress-striped active" role="progressbar" aria-valuemin="0" aria-valuemax="100" aria-valuenow="0"><div class="progress-bar progress-bar-success" style="width:0%;"></div></div>
        </td>
        <td>
            {% if (!i && !o.options.autoUpload) { %}
                <button class="btn btn-primary start" disabled>
                    <i class="glyphicon glyphicon-upload"></i>
                    <span>Start</span>
                </button>
            {% } %}
            {% if (!i) { %}
                <button class="btn btn-warning cancel">
                    <i class="glyphicon glyphicon-ban-circle"></i>
                    <span>Cancel</span>
                </button>
            {% } %}
        </td>
    </tr>
{% } %}
</script>
<!-- The template to display files available for download -->
<script id="template-download" type="text/x-tmpl">
{% for (var i=0, file; file=o.files[i]; i++) { %}
    <tr class="template-download fade">
        <td>
            <span class="preview">
                {% if (file.thumbnailUrl) { %}
                    <a href="{%=file.url%}" title="{%=file.name%}" download="{%=file.name%}" data-gallery><img src="{%=file.thumbnailUrl%}"></a>
                {% } %}
            </span>
        </td>
        <td>
            <p class="name">
                {% if (file.url) { %}
                    <a href="{%=file.url%}" title="{%=file.name%}" download="{%=file.name%}" {%=file.thumbnailUrl?'data-gallery':''%}>{%=file.name%}</a>
                {% } else { %}
                    <span>{%=file.name%}</span>
                {% } %}
            </p>
            {% if (file.error) { %}
                <div><span class="label label-danger">Error</span> {%=file.error%}</div>
            {% } %}
        </td>
        <td>
            <span class="size">{%=o.formatFileSize(file.size)%}</span>
        </td>
        <td>
            {% if (file.deleteUrl) { %}
                <button class="btn btn-danger delete" data-type="{%=file.deleteType%}" data-url="{%=file.deleteUrl%}"{% if (file.deleteWithCredentials) { %} data-xhr-fields='{"withCredentials":true}'{% } %}>
                    <i class="glyphicon glyphicon-trash"></i>
                    <span>Delete</span>
                </button>
                <input type="checkbox" name="delete" value="1" class="toggle">
            {% } else { %}
                <button class="btn btn-warning cancel">
                    <i class="glyphicon glyphicon-ban-circle"></i>
                    <span>Cancel</span>
                </button>
            {% } %}
        </td>
    </tr>
{% } %}
</script>
    </body>
</html>