<%@ page import="de.wehner.mediamagpie.conductor.webapp.util.security.SecurityUtil" %>
<%@ page import="org.springframework.security.core.userdetails.UserDetails" %>
<%@ page import="de.wehner.mediamagpie.common.persistence.entity.User" %>
                <div id="mainNavi">
					<ul>
						<li <c:if test="${activeMenu == 'welcome'}">class="current"</c:if>>
							<a href="<%=request.getContextPath()%>/welcome">Welcome</a>
						</li>
						<li <c:if test="${activeMenu == 'media'}">class="current"</c:if>>
							<a href="<%=request.getContextPath()%>/media/search_pictures">Media</a>
						</li>
						<li <c:if test="${activeMenu == 'config'}">class="current"</c:if>>
							<a href="<%=request.getContextPath()%>/config/user/">Configuration</a>
						</li>
					</ul>
					<div class="mainNaviRight">
				<%
				java.security.Principal  principal = request.getUserPrincipal();
				if(principal == null) {%>
						<a href="<%=request.getContextPath()%>/login" style="float: right; margin:10px 20px 0 0; clear:both">Login</a>
				<%}else{%>
						<a href="<%=request.getContextPath()%>/j_spring_security_logout" style="float: right; margin:10px 20px 0 0; clear:both">Logout (<%=principal.getName()%>)</a>
				<%}%>
				<%/*<br/>
				<a href="?locale=en_us">us</a> | <a href="?locale=de_de">de</a>
				*/%>
					</div>
				</div>