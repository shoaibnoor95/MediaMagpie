<%@page contentType="text/html;charset=UTF-8"%>
<%@page pageEncoding="UTF-8"%>
<%@ page session="false"%>
<%@ include file="/WEB-INF/jsp/general/taglibs.jsp"%>
<c:set var="title" value="${mediaDetailCommand.name}" scope="request" />
<c:set var="activeMenu" value="media" scope="request" />
<c:set var="activeSubMenu" value="" scope="request" />
<c:set var="urlSubMenu" value="/subNaviMedia" scope="request" />
<head>
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
					$.getJSON( "<%=request.getContextPath()%>
	/ajax/tagsOfUser", {
					term : extractLast(request.term)
				}, response);
			},
			search : function() {
				// custom minLength
				var term = extractLast(this.value);
				if (term.length < 2) {
					return false;
				}
			},
			focus : function() {
				// prevent value inserted on focus
				return false;
			},
			select : function(event, ui) {
				var terms = split(this.value);
				// remove the current input
				terms.pop();
				// add the selected item
				terms.push(ui.item.value);
				// add placeholder to get the comma-and-space at the end
				terms.push("");
				this.value = terms.join(", ");
				return false;
			}
		});
	});
</script>
</head>
<body>
	<h2>${title}</h2>
	<img alt="${mediaDetailCommand.name}" src="<%=request.getContextPath()%>${mediaDetailCommand.imageLink}" />
	<form:form commandName="mediaDetailCommand" cssClass="form-horizontal" role="form">
		<form:hidden path="imageLink" />
		<input id="overviewUrl" name="overviewUrl" type="hidden" value="<c:out value="${mediaDetailCommand.overviewUrl}"/>" />
		<input type="hidden" name="submitSelect" />
		<div class="form-group">
			<label class="col-sm-2 control-label">Created</label>
			<div class="col-sm-4">
				<p class="form-control-static">${mediaDetailCommand.creationDate}</p>
			</div>
		</div>
		<div class="form-group">
			<label for="name" class="col-sm-2 control-label">Name</label>
			<div class="col-sm-4">
				<form:input path="name" cssClass="form-control" />
			</div>
			<span class="help-block">you can set an arbitrary name for your picuture, default is the original file name</span>
		</div>
		<div class="form-group">
			<label for="description" class="col-sm-2 control-label">Description</label>
			<div class="col-sm-4">
				<form:textarea path="description" cols="3" cssClass="form-control" />
			</div>
			<span class="help-block"></span>
		</div>
		<div class="form-group">
			<label for="tagsAsString" class="col-sm-2 control-label">Tags</label>
			<div class="col-sm-4">
				<form:input path="tagsAsString" cssClass="form-control" />
			</div>
			<span class="help-block">add some comma separated tags here</span>
		</div>
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-10">
				<button type="button" class="btn btn-success"
					onclick="$('form input[type=hidden][name=submitSelect]').val('goBack');document.forms[0].submit();">Save and go back</button>
			</div>
		</div>
	</form:form>
</body>
