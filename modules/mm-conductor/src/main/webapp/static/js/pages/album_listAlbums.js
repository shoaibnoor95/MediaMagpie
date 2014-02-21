$(document).ready(function() {
	var BASE_URL = "";

	$("#albumList > tbody > tr").click(function(event) {
		var $item = $(this); // the tr containing the album id
		var $target = $(event.target);
		var id = $item.attr("id");

		if ($target.is(".delete")) {
			var $tdWithName = $(":first", $item);
			var name = $tdWithName.text();
			bootbox.confirm("Do you really want to delete album '" + name + "'?", function(result) {
				if (result == true) {
					deleteAlbum(id);
				}
			});
		} else if ($target.is(".view")) {
			document.location.href = BASE_URL + id + "/view";
		} else if ($target.is(".edit")) {
			document.location.href = BASE_URL + id + "/edit";
		} else if ($target.is(".copyLink")) {
			var link = $target.attr("alt");
			window.prompt("Copy to clipboard: Ctrl+C", link);
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