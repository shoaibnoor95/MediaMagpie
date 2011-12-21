<%@ page contentType="text/html; charset=UTF-8" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Users</title>
        <link rel="stylesheet" type="text/css" href="/static/styles/workshop.css" />
		<link href="<%=request.getContextPath()%>/static/styles/smoothness/jquery-ui-1.8rc3.custom.css" rel="stylesheet"
			type="text/css" />
		<script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery-1.4.2.min.js"></script>
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
		    <c:set var="active" value="users" scope="request"/>
            <c:import url="/subNaviConfiguration"/>
		</div>
            
        <div id="content">
            <h1>Users</h1>
			<table id="usersMarkup" border="1">
                <thead>
				    <tr>
						<th>Name</th>
						<th>Email</th>
						<th>Role</th>
						<th>Groups</th>
						<th>Operations</th>
					</tr>
				</thead>
				<tbody>
                    <c:forEach items="${users}" var="user">
                        <tr>
			                <td>${user.name}</td>
							<td>${user.email}</td>
							<td>${user.role}</td>
							<td><c:forEach var="group" items="${user.groups}" varStatus="i">
                            <c:if test="${i.index > 0}">, </c:if>${group.name}
                            </c:forEach></td>
                            <td><a href="/user/summary/${user.id}"><img src="/images/icons/magnifier.png" title="Details" border="0" /></a>
                                <a href="/users/edit?id=${user.id}"><img src="/images/icons/edit.png" border="0" title="Edit" /></a>
                                <a href="javascript:showDeleteDialog('${user.id}', '${user.name}')"><img src="/images/icons/delete.png" border="0"
						          title="Delete" /></a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
	    <c:import url="/footer"/>
	    </div>
    </body>
</html>