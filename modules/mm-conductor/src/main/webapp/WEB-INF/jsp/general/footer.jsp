<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
            <div id="footer">
                &copy; 2010-2011 Ralf Wehner | 
                <a href="<c:url value="mailto:info@ralfwehner.org"><c:param name="subject" value="[Media-Butler]"></c:param></c:url> Feedback">Feedback</a> | 
                <a href="http://ralfwehner.dyndns.org:8082/" target="_blank">Bug Report</a>
                <br/>
                Version: ${version}, 
                Revision: <span title="${revision}" onclick="this.innerHTML='${revision}'">${fn:substring(revision,0,6)}</span>,  
                Built on: ${time} 
                <c:if test="${deployMode != 'live'}">, Mode: ${deployMode}</c:if>
            </div>