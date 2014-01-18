<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<%@ page import="de.wehner.mediamagpie.conductor.webapp.util.WebAppUtils" %>

<c:set var="activeMenu" value="dashboard" scope="request"/>
<c:set var="activeSubMenu" value="cooliris" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviDashboard" scope="request"/>
<head>
    <title>Cooliris Wall</title>
</head>
		<div id="content">
			<h1>
				cooliris - <fmt:message key="dashboard.title"/>
			</h1>
			
            <h1>Cooliris Wall</h1>
            <object id="o" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" width="600" height="450">
		    <param name="movie" value="http://apps.cooliris.com/embed/cooliris.swf" />
		    <param name="allowFullScreen" value="true" />
		    <param name="allowScriptAccess" value="always" />
		    <param name="flashvars" value="feed=<%=WebAppUtils.getRequestUrlUpToContextPath(request)%>/dashboard/rss" />
		    <embed type="application/x-shockwave-flash"
		      src="http://apps.cooliris.com/embed/cooliris.swf"
		      flashvars="feed=<%=WebAppUtils.getRequestUrlUpToContextPath(request)%>/dashboard/rss"
		      width="800" 
		      height="450"
		      allowFullScreen="true"
		      allowScriptAccess="always" />
		    </object>
               
		</div>
