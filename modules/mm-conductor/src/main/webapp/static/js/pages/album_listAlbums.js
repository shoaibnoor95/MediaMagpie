$(document).ready(function() {
	var BASE_URL = "";

	$("#albumList > tbody > tr").click(function(event) {
		var $item = $(this); // the tr containing the album id
		var $target = $(event.target);
		var id = $item.attr("id");

		if ($target.is(".delete")) {
			var $tdWithName = $(":first", $item);
			var name = $tdWithName.text();
			if (confirm("Do you really want to delete album '" + name + "'?")) {
				deleteAlbum(id);
			} else {
				return false;
			}
		} else if ($target.is(".view")) {
			document.location.href = BASE_URL + id + "/view";
		} else if ($target.is(".edit")) {
			document.location.href = BASE_URL + id + "/edit";
		} else if ($target.is(".copyLink")) {
			var link = $target.attr("alt");
			 window.prompt ("Copy to clipboard: Ctrl+C, Enter", link);
		}

		return true;
	});
	$("button#button-new").click(function(event) {
		document.location.href = BASE_URL + "create";
	});
});
function deleteAlbum(id) {
	$("#deleteId").attr("value", id);
	$("#deleteForm").submit();
}

/*
 * function executeCommand(action, id, arg1) { $("#input_action").attr("value",
 * action); $("#input_id").attr("value", id); $("#input_arg1").attr("value",
 * arg1); $("#mediaForm").submit(); }
 * 
 * $(function() { $( "img.image-action" ).click(function( event ) { var $item = $(
 * this ), $target = $( event.target );
 * 
 * if ( $target.is( "img.image-action.delete" ) ) { var mediaId =
 * $item.attr("id"); executeCommand("DELETE", mediaId); } else if ( $target.is(
 * "a img" ) ) { return true; }
 * 
 * return false; }); };
 */
