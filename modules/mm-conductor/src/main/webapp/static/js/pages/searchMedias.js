function initSlider() {
	$( "#slider-select-year" ).slider({
		range:true,
		min:${searchCriteria.sliderYearMinMax.min},
		max:${searchCriteria.sliderYearMinMax.max},
		values:[${searchCriteria.sliderYearValues.min},${searchCriteria.sliderYearValues.max}],
		slide: function( event, ui ) {
			$( "#yearCriteria" ).val( ui.values[ 0 ] + " - " + ui.values[ 1 ] );
		}
	});
	$( "#yearCriteria" ).val( $( "#slider-select-year" ).slider( "values", 0 ) + " - " + $( "#slider-select-year" ).slider( "values", 1 ) );
}
function postDeleteCommand(action, id, arg1) {
	$("#input_action").attr("value", action);
	$("#input_id").attr("value", id);
	$("#mediaForm").submit();
} 
function submitMediaToAlbum(mediaId) {
	$.ajax({
		  type: 'POST',
		  url: "<%=request.getContextPath()%><%=SearchController.getAjaxUrlAddMediaToCurrentAlbum()%>",
		  data: { id: mediaId },
	      complete: function(request, message) {
	    	   initDragAndDropAndButtonListeners();
	      },
	      success: function(data) {
	          $('#albumArea').html(data);
	      }  
		});
}
function submitMediaToS3(mediaId) {
	$.ajax({
		  type: 'POST',
		  url: "<%=request.getContextPath()%><%=S3Controller.getAjaxUrlAddMediaToS3()%>",
		  data: { id: mediaId },
	      complete: function(request, message) {
	    	   ;// initDragAndDropAndButtonListeners();
	      },
	      success: function(data) {
	          ;// $('#albumArea').html(data);
	      }   
		});
}
var $gallery;
var $album;
function initVars(){
	$gallery = $("#gallery"),
	$album = $("#album");
}
function loadTemplateAlbum() {
   $.ajax({
       url: "<%=request.getContextPath()%><%=SearchController.getAjaxUrlCurrentAlbum()%>",
       cache: false,
       data: { },
       complete: function(request, message) {
    	   initDragAndDropAndButtonListeners();
       },
       success: function(data) {
           $('#albumArea').html(data);
       }  
   });
}
function addToAlbum($item){
	var mediaId = $item.attr("id");
	submitMediaToAlbum(mediaId);
	// TODO rwe: maybe remove java script animation
	$item.find( "a.ui-icon-trash" ).remove();
	$item.find( "img.image-action" ).remove();
	$item.find( "p.metadata" ).remove();
	var $list = $( "ul", $album ).length ? $( "ul", $album ) : $( "<ul class='gallery ui-helper-reset'/>" ).appendTo( $album );
    $item.appendTo( $list ).fadeIn(function() {
		$item
			.animate({ width: "48px" })
			.find( "img" )
				.animate({ height: "48px" });
	});
}
function removeFromAlbum(mediaId){
	$.ajax({
		  type: 'POST',
		  url: "<%=request.getContextPath()%><%=SearchController.getAjaxUrlRemoveMediaToCurrentAlbum()%>",
		  data: { id: mediaId },
	      complete: function(request, message) {
	    	   initDragAndDropAndButtonListeners();
	      },
	      success: function(data) {
	          $('#albumArea').html(data);
	      }  
		});
}
function updateSortOrderOfAlbum(mediaIds){
	$.ajax({
		  type: 'get',
		  url: "<%=request.getContextPath()%><%=SearchController.getAjaxUrlUpdateSortOrderOfAlbum()%>",
		  data: { mediaIds: mediaIds }  
		});
}

/*--------- drag and drop -------------*/
function initDragAndDropAndButtonListeners() {
	initVars();
	// let the gallery items be draggable
	$( "li", $gallery ).draggable({
		cancel: "a img,a.ui-icon,a.image-action", // clicking an icon won't
												  // initiate dragging
		revert: "invalid", // when not dropped, the item will revert back to
						   // its initial position
		containment: "document",
		cursor: "move"
	});
	
	$album.droppable({
		accept: "#gallery > li",
		activeClass: "ui-state-highlight",
		drop: function( event, ui ) {
			addToAlbum( ui.draggable );
		}
	});
	
	$gallery.droppable({
		accept: "#album > li",
		activeClass: "ui-state-highlight",
		drop: function( event, ui ) {
			// addToAlbum( ui.draggable );
			// TODO rwe: remove id from array...
		}
	});
	
	// resolve the icons behavior with event delegation
	$( "ul.gallery > li" ).click(function( event ) {
		var $item = $( this ),
			$target = $( event.target );
		var mediaId = $item.attr("id");
		if ( $target.is( "img.image-action.delete" ) ) {
			postDeleteCommand("DELETE", mediaId);
		} else if ( $target.is( "a img" ) ) {
			return true;
		} else {
			return handleFlippable($item, $target, mediaId);
		}

		return false;
	});
	$( "#album div ul li" ).click(function( event ) {
		var $item = $( this ),
			$target = $( event.target );
		if ( $target.is( "img.image-action.remove" ) ) {
			var mediaId = $item.attr("id");
			removeFromAlbum(mediaId);
		} else if ( $target.is( "a img" ) ) {
			return true;
		}

		return false;
	});
	// make album items sortable
	$( "#albumArea div#album ul" ).sortable({
		update: function(event, ui){
			var mediaIds = $(this).sortable('toArray');
			var mediaIdsAsString = mediaIds.join(",");
			updateSortOrderOfAlbum(mediaIdsAsString);
		}
	});
	$( "#albumArea div#album ul" ).disableSelection();
}

function wireUpAutoSubmitAlbumSelect() {
	  $("#albumId").each(function (index) {
	    $(this).change(function () {
	      var form = $(this).closest('form');
	      form.submit();
	    })
	  });
}

/** * flip effect *** */
function handleFlippable($item, $target, mediaId) {
    var $front = $(".front", $item);
    var $back = $(".back", $item);
    if ($target.is(".image-action.flipBack")) {
        $front.addClass('transition-rotate');
        $front.css('transform', 'rotateY(180deg)');
        $back.addClass('transition-rotate');
        $back.css('transform', 'rotateY(0deg)');
        return true;
    } else if ($target.is(".image-action.flipFront")) {
        $front.css('transform', 'rotateY(0deg)');
        $back.css('transform', 'rotateY(-180deg)');
        return true;
    } else if ($target.is("div.back button")) {
    	submitMediaToS3(mediaId);
    	var imgAnimation = $("img.in-process", $item);
    	imgAnimation.show();
    	//imgAnimation.toggle();
    	return true;
    }
    return false;
}

/** init methods after dom is loaded completely */
$(document).ready(function() {
	console.log( "init slider, album template and select box" );
	initSlider();
	loadTemplateAlbum();
	wireUpAutoSubmitAlbumSelect();
	// initFlipEffect();
});
