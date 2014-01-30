<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div id="footer">
    &copy; 2010-2014 Ralf Wehner | 
    <a href="<c:url value="mailto:info@ralfwehner.org"><c:param name="subject" value="[MediaMagpie]"></c:param></c:url> Feedback">Feedback</a> | 
    <a href="http://github.com/rwe17/MediaMagpie" target="_blank">Bug Report</a>
    <br/>
    Version: <span>${version}</span>,  
    <%/** Version: <span title="${version}" onclick="this.innerHTML='${version}'">${fn:substring(version,0,3)}</span>,*/ %>  
    Built Time: <span>${buildTime}</span> 
    <c:if test="${deployMode != 'live'}">, Mode: ${deployMode}</c:if>
</div>