/**
 * jQuery Image Reloader plugin This plugin tries to reload an <img>-element for
 * multiple times again if the image is not present. This plugin makes sense
 * when showing thumbs that are not currently present and have to be resized on
 * the server.
 */
;(function($) {

	var defaults = {
		// These are the defaults.
		timeoutFirstPoll : 500,
		timoutNextPoll : 1000,
		maxPollCount : 20
	};

	// global array that stores all broken images
	var brokenThumbs = [];
	var globalSettings = {};

	// pollingThumb static class
	$.pollingThumb = {
		pollCount : 0,
		refreshThumbsFirst : function(val) {
			jQuery.each(brokenThumbs, function(index, thumb) {
				thumb.attr('src', function(i, old) {
					var newSrc = old + "&i=" + (Math.random() * 1000);
					// console.log("Changing thumb image url: " + old + " -> " +
					// newSrc);
					return newSrc;
				});
			});
			$.pollingThumb.pollCount++;
			if (brokenThumbs.length > 0) {
				brokenThumbs = [];
				setTimeout($.pollingThumb.refreshThumbs, globalSettings['timoutNextPoll']);
			}
		},
		refreshThumbs : function(val) {
			jQuery.each(brokenThumbs, function(index, thumb) {
				thumb.attr('src', function(i, old) {
					return old.replace(/\&i=.+/, "&i=" + (Math.random() * 1000));
				});
			});
			$.pollingThumb.pollCount++;
			if (brokenThumbs.length > 0 && $.pollingThumb.pollCount < globalSettings['maxPollCount']) {
				brokenThumbs = [];
				setTimeout($.pollingThumb.refreshThumbs, globalSettings['timoutNextPoll']);
			}
		}
	};

	$.fn.pollingThumb = function(options) {

		// extend the pollingThumb object
		$.extend(this, {

			version : '1.0',

			addErrorHandlerForThumbs : function() {
				this.error(function() {
					// console.log("404 error: " + $(this).attr('src'));
					brokenThumbs.push($(this));
					// $(this).attr("src",
					// "/static/images/ui-anim_basic_16x16.gif")
				});
			},
		});

		// set default settings
		var settings = $.extend(this, defaults, options);

		// initialize the plugin
		globalSettings['timoutNextPoll'] = settings.timoutNextPoll;
		globalSettings['maxPollCount'] = settings.maxPollCount;
		this.addErrorHandlerForThumbs();
		// set timer to reload all broken images
		setTimeout($.pollingThumb.refreshThumbsFirst, settings.timeoutFirstPoll);

		return this;
	};
})(jQuery);
