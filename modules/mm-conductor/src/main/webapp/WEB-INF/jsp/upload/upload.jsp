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
        <!-- <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/css/ui-lightness/jquery-ui-1.8.13.custom.css" />
        <link rel="stylesheet" type="text/css" href="<c:url value="/static/css/ui-lightness/jquery-ui-1.8.13.custom.css"/>" />-->
        
		<script src="<%=request.getContextPath()%>/static/js/jquery.tmpl.min.js"></script>
		<script src="<%=request.getContextPath()%>/static/js/jquery.fileupload.js"></script>
		<script src="<%=request.getContextPath()%>/static/js/jquery.fileupload-ui.js"></script>
		<script type="text/javascript"><%@ include file="../../../static/js/pages/upload_jqueryfileupload.js" %></script>
<script id="template-upload" type="text/x-jquery-tmpl">
    <tr class="template-upload{{if error}} ui-state-error{{/if}}">
        <td class="preview"></td>
        <td class="name">\${name}</td>
        <td class="size">\${size}</td>
        {{if error}}
            <td class="error" colspan="2">Error:
                {{if error === 'maxFileSize'}}File is too big
                {{else error === 'minFileSize'}}File is too small
                {{else error === 'acceptFileTypes'}}Filetype not allowed
                {{else error === 'maxNumberOfFiles'}}Max number of files exceeded
                {{else}}\${error}
                {{/if}}
            </td>
        {{else}}
            <td class="progress"><div></div></td>
            <td class="start"><button>Start</button></td>
        {{/if}}
        <td class="cancel"><button>Cancel</button></td>
    </tr>
</script>
<script id="template-download" type="text/x-jquery-tmpl">
    <tr class="template-download{{if error}} ui-state-error{{/if}}">
        {{if error}}
            <td></td>
            <td class="name">\${name}</td>
            <td class="size">\${size}</td>
            <td class="error" colspan="2">Error:
                {{if error === 1}}File exceeds upload_max_filesize (php.ini directive)
                {{else error === 2}}File exceeds MAX_FILE_SIZE (HTML form directive)
                {{else error === 3}}File was only partially uploaded
                {{else error === 4}}No File was uploaded
                {{else error === 5}}Missing a temporary folder
                {{else error === 6}}Failed to write file to disk
                {{else error === 7}}File upload stopped by extension
                {{else error === 'maxFileSize'}}File is too big
                {{else error === 'minFileSize'}}File is too small
                {{else error === 'acceptFileTypes'}}Filetype not allowed
                {{else error === 'maxNumberOfFiles'}}Max number of files exceeded
                {{else error === 'uploadedBytes'}}Uploaded bytes exceed file size
                {{else error === 'emptyResult'}}Empty file upload result
                {{else}}${error}
                {{/if}}
            </td>
        {{else}}
            <td class="preview">
                {{if thumbnail_url}}
                    <a href="\${url}" target="_blank"><img src="\${thumbnail_url}"></a>
                {{/if}}
            </td>
            <td class="name">
                <a href="\${url}"{{if thumbnail_url}} target="_blank"{{/if}}>\${name}</a>
            </td>
            <td class="size">${size}</td>
            <td colspan="2"></td>
        {{/if}}
        <td class="delete">
            <button data-type="\${delete_type}" data-url="\${delete_url}">Delete</button>
        </td>
    </tr>
</script>
    </head>
    <body>
        <div id="content">
            <h1>${title}</h1>
			<div id="fileupload" style="border: dotted;">
			    <form action="<%=request.getContextPath()+UploadController.getUploadUrl()%>" method="POST" enctype="multipart/form-data">
			        <div class="fileupload-buttonbar">
			            <label class="fileinput-button">
			                <span>Add files...</span>
			                <input type="file" name="files[]" multiple>
			            </label>
			            <button type="submit" class="start">Start upload</button>
			            <button type="reset" class="cancel">Cancel upload</button>
			            <button type="button" class="delete">Delete files</button>
			        </div>
			    </form>
			    <div class="fileupload-content">
			        <table class="files"></table>
			        <div class="fileupload-progressbar"></div>
			    </div>
			</div>
		</div>
    </body>
</html>