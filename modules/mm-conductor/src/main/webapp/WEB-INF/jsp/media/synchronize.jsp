<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.controller.media.SynchronizeController"%>
<c:set var="title" value="Synchronize Medias from remote storage" scope="request" />
<c:set var="activeMenu" value="media" scope="request" />
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request" />
<c:set var="activeSubMenu" value="sync" scope="request" />

<div id="content">
	<h1>${title}</h1>
	<h2>Synchronize against S3</h2>
	<form:form action="synchronize_s3">
		<button id="pullFromS3" type="button" class="default"
			onclick="$('form input[type=hidden][name=submitSelect]').val('pull');document.forms[0].submit();">
			<span>Pull From S3</span>
		</button>
        <button id="pullFromS3" type="button" class="default"
            onclick="$('form input[type=hidden][name=submitSelect]').val('push');document.forms[0].submit();">
            <span>Push To S3</span>
        </button>
		<input type="hidden" name="submitSelect" />
	</form:form>
</div>