/*
 * jQuery dropdown: A simple dropdown plugin
 *
 * Copyright 2013 Cory LaViska for A Beautiful Site, LLC. (http://abeautifulsite.net/)
 * 
 * See documentation on http://labs.abeautifulsite.net/jquery-dropdown/
 * 
 * Licensed under the MIT license: http://opensource.org/licenses/MIT
 *
*/
if (jQuery) (function ($) {

    $.extend($.fn, {
        dropdown: function (method, data) {

            switch (method) {
                case 'show':
                    show(null, $(this));
                    return $(this);
                case 'hide':
                    hide();
                    return $(this);
                case 'attach':
                    return $(this).attr('data-dropdown', data);
                case 'detach':
                    hide();
                    return $(this).removeAttr('data-dropdown');
                case 'disable':
                    return $(this).addClass('dropdown-disabled');
                case 'enable':
                    hide();
                    return $(this).removeClass('dropdown-disabled');
            }
        }
    });

    function show(event, object) {

        var trigger = event ? $(this) : object,
			dropdown = $(trigger.attr('data-dropdown')),
			isOpen = trigger.hasClass('dropdown-open');

        // In some cases we don't want to show it
        if (event) {
            if ($(event.target).hasClass('dropdown-ignore')) {
            	return;
            }
            event.preventDefault();
            event.stopPropagation();
        } else {
            if (trigger !== object.target && $(object.target).hasClass('dropdown-ignore')) {
            	return;
            }
        }
        var type = event.type;
        if(type != 'mouseenter' && type != 'mouseleave'){
        	hide();
        }

        if (isOpen || trigger.hasClass('dropdown-disabled')) {
        	return;
        }

        // Show it
        trigger.addClass('dropdown-open');
        dropdown
			.data('dropdown-trigger', trigger)
			.show();

        // Position it
        position();

        // Trigger the show callback
        dropdown
			.trigger('show', {
				dropdown: dropdown,
				trigger: trigger
			});
    }

    function mouseLeaveDropDownLink(event) {
        var trigger = event ? $(event.target) : null;

/*        var parentOffset = $(this).parent().offset(); 
/        var relX = event.pageX - parentOffset.left;
        var relY = event.pageY - parentOffset.top;
        trigger.html("rel pos within parent element: X: " + relX + " Y: " + relY);
*/
        var myOffset = $(this).offset(); 
        var height = $(this).height();
        var relX = event.pageX - myOffset.left;
        var relY = event.pageY - myOffset.top;
        //trigger.html("rel pos within this element: X: " + relX + " Y: " + relY+", height: "+height);

        if((relY - height) >= 0) {
        	// mouse leaves the link to the bottom
        	return;
        }
        hideDropDowns();
        
/*        // Does we moved from dropDown link into a dropdown-menue?
        var openDropDownMenues = $(document).find('.dropdown:visible');
        openDropDownMenues.each(function () {
            var dropdown = $(this);
            
            
            var x = event.pageX - this.offsetLeft;
            var y = event.pageY - this.offsetTop;
            trigger.html("X: " + x + " Y: " + y); 
            
//            dropdown
//				.hide()
//				.removeData('dropdown-trigger')
//				.trigger('hide', { dropdown: dropdown });
        });
        if(openDropDownMenues != null) {
        	return;
        }*/
    }
    
    function mouseLeaveDropDown(event) {
//    	setTimeout( function() {  hide(event); }, 1000);
//    	hide(event);
//        var targetGroup = event ? $(event.target).parents().addBack() : null;
        var targetOfEvent = event ? $(event.target) : null;
        var targetGroup = event ? $(event.target).parents().addBack() : null;
  
        // Are we moving anywhere in a dropdown?
        if (targetGroup && targetGroup.is('.dropdown')) {
            // Is it a dropdown menu?
            if (targetGroup.is('.dropdown-menu')) {
                // Did we leave on an option? If so close it.
            	hideDropDowns();
            } else {
                // Nope, it's a panel. Leave it open.
                return;
            }
        }
    }

    function hide(event) {

        // In some cases we don't hide them
        var targetGroup = event ? $(event.target).parents().addBack() : null;

        // Are we clicking anywhere in a dropdown?
        if (targetGroup && targetGroup.is('.dropdown')) {
            // Is it a dropdown menu?
            if (targetGroup.is('.dropdown-menu')) {
                // Did we click on an option? If so close it.
                if (!targetGroup.is('A')) 
                	return;
            } else {
                // Nope, it's a panel. Leave it open.
                return;
            }
        }
        hideDropDowns();
    }

    function hideDropDowns() {
        // Hide any dropdown that may be showing
        $(document).find('.dropdown:visible').each(function () {
            var dropdown = $(this);
            dropdown
				.hide()
				.removeData('dropdown-trigger')
				.trigger('hide', { dropdown: dropdown });
        });

        // Remove all dropdown-open classes
        $(document).find('.dropdown-open').removeClass('dropdown-open');
    }
    
    function position() {

        var dropdown = $('.dropdown:visible').eq(0),
			trigger = dropdown.data('dropdown-trigger'),
			hOffset = trigger ? parseInt(trigger.attr('data-horizontal-offset') || 0, 10) : null,
			vOffset = trigger ? parseInt(trigger.attr('data-vertical-offset') || 0, 10) : null;

        if (dropdown.length === 0 || !trigger) return;

        // Position the dropdown relative-to-parent...
        if (dropdown.hasClass('dropdown-relative')) {
            dropdown.css({
                left: dropdown.hasClass('dropdown-anchor-right') ?
					trigger.position().left - (dropdown.outerWidth(true) - trigger.outerWidth(true)) - parseInt(trigger.css('margin-right'), 10) + hOffset :
					trigger.position().left + parseInt(trigger.css('margin-left'), 10) + hOffset,
                top: trigger.position().top + trigger.outerHeight(true) - parseInt(trigger.css('margin-top'), 10) + vOffset
            });
        } else {
            // ...or relative to document
            dropdown.css({
                left: dropdown.hasClass('dropdown-anchor-right') ?
					trigger.offset().left - (dropdown.outerWidth() - trigger.outerWidth()) + hOffset : trigger.offset().left + hOffset,
                top: trigger.offset().top + trigger.outerHeight() + vOffset
            });
        }
    }
    
    <!-- rwe: for a timer based solution i've found this example: http://jsfiddle.net/i_like_robots/6JbtX/ -->
    $(document).on('mouseover', '[data-dropdown]', show);
    $(document).on('mouseleave.dropdown', '[data-dropdown]', mouseLeaveDropDownLink);
    $(document).on('mouseleave.dropdown', '.dropdown', mouseLeaveDropDown);
    $(document).on('click.dropdown', '[data-dropdown]', show);
    $(document).on('click.dropdown', hide);
    $(window).on('resize', position);

})(jQuery);
// end of plugin

