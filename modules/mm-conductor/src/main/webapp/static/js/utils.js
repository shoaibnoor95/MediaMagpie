function lpad(ContentToSize, PadLength, PadChar) {
	var PaddedString = ContentToSize.toString();
	for (i = ContentToSize.length + 1; i <= PadLength; i++) {
		PaddedString = PadChar + PaddedString;
	}
	return PaddedString;
}

function formatTime(args) {
	var id = args.id;
	var date = args.date;
	var v = new Date(args.date);
	var html = '-';
	// wrapper for lpad to pad to 2 symbols with '0'
	var lpw = function(st) {
		return lpad(st, 2, '0');
	}
	html = lpw(v.getDate()) + '.' + lpw(v.getMonth() + 1) + '.'
			+ v.getFullYear() + ' ' + lpw(v.getHours()) + ':'
			+ lpw(v.getMinutes()) + ':' + lpw(v.getSeconds());
	var el = document.getElementById(args.id);
	el.innerHTML = html;
}

function focusFirstEnabledField(formNum) {
	var form = document.getElementsByTagName("form")[formNum];
	if (form != undefined) {
		var allInputsInForm = $(":input", form);
		var firstInput = allInputsInForm.filter(function(i){
			return !$(this).attr("readOnly");
			}).first();
		if(firstInput != null){
			//firstInput.css("border", "3px double red")
			firstInput.focus();
		}
	}
}

function setCursorToEnd(obj) { 
    if(obj.createTextRange) { 
        /* Create a TextRange, set the internal pointer to
           a specified position and show the cursor at this
           position
        */ 
        var range = obj.createTextRange(); 
        range.move("character", obj.value.length); 
        range.select(); 
    } else if(obj.selectionStart) { 
        /* Gecko is a little bit shorter on that. Simply
           focus the element and set the selection to a
           specified position
        */ 
        obj.focus(); 
        obj.setSelectionRange(obj.value.length, obj.value.length); 
    } 
} 
