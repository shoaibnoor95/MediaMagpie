<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp" %>
<c:set var="title" value="${mediaDetailCommand.name}" scope="request"/>
<c:set var="activeMenu" value="media" scope="request"/>
<c:set var="activeSubMenu" value="" scope="request"/>
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request"/>
<c:import url="/header"/>

<script type="text/javascript">
	$(function() {
		$("#content").keypress(function(e) {
	        if ((e.which && e.which == 13) || (e.keyCode && e.keyCode == 13)) {
	            $('#saveAndGoBack').click();
	            return true;
	        }
	    });
	});

	$(function() {
		function split( val ) {
			return val.split( /,\s*/ );
		}
		function extractLast( term ) {
			return split( term ).pop();
		}

		$( "#tagsAsString" )
			// don't navigate away from the field on tab when selecting an item
			.bind( "keydown", function( event ) {
				if ( event.keyCode === $.ui.keyCode.TAB &&
						$( this ).data( "autocomplete" ).menu.active ) {
					event.preventDefault();
				}
			})
			.autocomplete({
				source: function( request, response ) {
					$.getJSON( "<%=request.getContextPath()%>/ajax/tagsOfUser", {
						term: extractLast( request.term )
					}, response );
				},
				search: function() {
					// custom minLength
					var term = extractLast( this.value );
					if ( term.length < 2 ) {
						return false;
					}
				},
				focus: function() {
					// prevent value inserted on focus
					return false;
				},
				select: function( event, ui ) {
					var terms = split( this.value );
					// remove the current input
					terms.pop();
					// add the selected item
					terms.push( ui.item.value );
					// add placeholder to get the comma-and-space at the end
					terms.push( "" );
					this.value = terms.join( ", " );
					return false;
				}
			});
	});
</script>
		<div id="content">
			<h1>${title}</h1>
            <form:form commandName="mediaDetailCommand" cssClass="decorated">
				<img alt="${mediaDetailCommand.name}" src="<%=request.getContextPath()%>${mediaDetailCommand.imageLink}"/>
	            <form:hidden path="imageLink"/>
	            	<dl>
	            		<dt>Created:</dt>
	            		<dd>${mediaDetailCommand.creationDate}</dd>
	            	</dl>
	            	<dl>
	                	<dt>Name:</dt>
	                	<dd><form:input path="name"/></dd>
	                </dl>
	            	<dl>
	                	<dt>Description:</dt>
	                	<dd><form:textarea path="description" cols="3"/></dd>
	                </dl>
	            	<dl>
	                	<dt>Tags:</dt>
	                	<dd><form:input path="tagsAsString"/></dd>
	                </dl>
	                <input id="overviewUrl" name="overviewUrl" type="hidden" value="<c:out value="${mediaDetailCommand.overviewUrl}"/>"/>
                	<button id="saveAndGoBack" type="button" class="default" onclick="$('form input[type=hidden][name=submitSelect]').val('goBack');document.forms[0].submit();"><span>Speichern und zurück</span></button>
	                <input type="hidden" name="submitSelect"/>
                </form:form>
		</div>
<c:import url="/footer"/>