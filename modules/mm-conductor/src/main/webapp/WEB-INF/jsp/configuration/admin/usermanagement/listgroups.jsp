<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Groups</title>
        <link rel="stylesheet" type="text/css" href="/static/styles/workshop.css" />
		<link href="<%=request.getContextPath()%>/static/styles/smoothness/jquery-ui-1.8rc3.custom.css" rel="stylesheet"
			type="text/css" />
		<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery-1.4.2.min.js"></script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery.dataTables.min.js"></script>
		<script type="text/javascript">
		$(document).ready(function(){
		});
		</script>
</head>

    <body>
        <div id="page"> 
            
            <c:set var="active" value="config" scope="request"/>
            <c:import url="/mainNavi"/>
            <div id="subNavi">
                <c:set var="active" value="groups" scope="request"/>
                <c:import url="/subNaviConfiguration"/>
            </div>
            
            <div id="content">
                <h1>Groups</h1>
                <div id="groups">
                <table id="groupsMarkup">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th># Members</th>
                            <th>Operations</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${groups}" var="group">
                        <tr>
                            <td><a href="/group/summary/${group.id}">${group.name}</a></td>
                            <td>${fn:length(group.users)}</td>
                            <td>
                                <a href="/group/members/${group.id}"><img src="/images/icons/group.png" title="Show Members" border="0"/></a>
                                <a href="/group/edit?id=${group.id}"><img src="/images/icons/edit.png" border="0" title="Edit"/></a>
                                <a href="javascript:showDeleteDialog('${group.id}', '${group.name}')"><img src="/images/icons/delete.png" border="0" title="Delete"/></a> 
                            </td>
                        </tr>
                        </c:forEach>
                    </tbody>
                </table>
                </div>
            </div>
            <c:import url="/footer"/>
        </div>
    </body>
</html>