<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
	<meta charset="utf-8" />
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta name="viewport" content="width=device-width, initial-scale=1" />
	
	<link rel="stylesheet" type="text/css" href="/static/css/styles-all2.css" />
	<link rel="stylesheet" href="/static/bootstrap/css/bootstrap.css" />
	<!-- Custom styles for this template -->
    <link rel="stylesheet" href="/static/bootstrap/css/sticky-footer-navbar.css"></link>
	<style type="text/css">
    </style>
</head>
<body>
	<div id="wrap">
		<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
			<div class="container">
				<div class="navbar-header">
					<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
						<span class="sr-only">Toggle navigation</span> <span class="icon-bar"></span> <span class="icon-bar"></span> <span class="icon-bar"></span>
					</button>
					<a class="navbar-brand" href="#">MediaMagpie</a>
				</div>
				<!-- for integrated login, please see: http://getbootstrap.com/examples/jumbotron/ -->
				<div class="navbar-collapse collapse">
					<ul class="nav navbar-nav">
						<li class="active"><a href="/welcome">Welcome</a></li>
						<li><a href="#about">About</a></li>
						<li class="dropdown">
						  <a href="#" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" >Media <b class="caret"></b></a>
							<ul class="dropdown-menu">
								<li><a href="/media/search_pictures">Medias</a></li>
								<li><a href="/media/album/list">Albums</a></li>
								<li><a href="/upload/file-upload">Upload</a></li>
								<li class="divider"></li>
								<li class="dropdown-header">Nav header</li>
								<li><a href="/trash/content">Trash</a></li>
							</ul></li>
						<li class="dropdown">
						    <a href="#" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" >Configuration <b class="caret"></b></a>
							<ul class="dropdown-menu">
								<li><a href="/config/user/">User Configuration</a></li>
								<li><a href="/config/aws/s3/">AWS S3 Configuration</a></li>
								<li class="divider"></li>
								<li class="dropdown-header">Admin Configuration</li>
								<li><a href="/config/admin/mainconfiguration">Main Configuration</a></li>
								<li><a href="/config/admin/mailserver/configuration">Mail Configuration</a></li>
							</ul></li>
					</ul>

					<form class="navbar-form navbar-right" role="form" name="f" action="/loginProcess" method="post">
						<!-- <div class="form-group">
							<input placeholder="Email" class="form-control" type="text" name="j_username">
						</div>
						<div class="form-group">
							<input placeholder="Password" class="form-control" type="password" name="j_password">
						</div>
						<button type="submit" class="btn btn-success">Sign in</button>-->
						<a href="/login">Login</a>
					</form>

				</div>
				<!--/.nav-collapse -->
			</div>
		</div>
		<div class="container">
			<div class="page-header">
				<h1>Sticky footer with fixed navbar</h1>
			</div>
			<h1>mock</h1>

			<h2>bootstrap context menu</h2>
			<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-delay="300"
				data-close-others="false"> Account <b class="caret"></b>
			</a>
				<ul class="dropdown-menu dropdown-tip">
					<li><a tabindex="-1" href="#">My Account</a></li>
					<li class="divider"></li>
					<li><a tabindex="-1" href="#">Change Email</a></li>
					<li><a tabindex="-1" href="#">Change Password</a></li>
					<li class="divider"></li>
					<li><a tabindex="-1" href="#">Logout</a></li>
				</ul></li> <br />
			<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-delay="300"
				data-close-others="false"> Account 2<b class="caret"></b>
			</a>
				<ul class="dropdown-menu dropdown-tip">
					<li><a tabindex="-1" href="#">My Account 2</a></li>
					<li class="divider"></li>
					<li><a tabindex="-1" href="#">Change Email</a></li>
					<li><a tabindex="-1" href="#">Change Password</a></li>
					<li class="divider"></li>
					<li><a tabindex="-1" href="#">Logout</a></li>
				</ul></li> <br />
			<div class="btn-group">
				<button class="btn dropdown-toggle" data-hover="dropdown" data-toggle="dropdown">
					Button <span class="caret"></span>
				</button>
				<ul class="dropdown-menu">
					<li><a href="#">a</a></li>
					<li><a href="#">b</a></li>
					<li><a href="#">Ralle</a></li>
					<li class="divider"></li>
					<li><a href="#"></a></li>
				</ul>
			</div>
			<li class="dropdown"><a class="dropdown-toggle" data-toggle="dropdown" data-hover="dropdown" data-close-others="false"
				href="/config/user/">Configuration </a>
				<ul class="dropdown-menu dropdown-tip">
					<li class=""><a href="/config/user/">User Configuration</a></li>
					<li class=""><a href="/config/aws/s3/">AWS S3 Configuration</a></li>
					<li class="divider"></li>
					<li class=""><a href="/config/admin/mainconfiguration">Main Configuration</a></li>
					<li class=""><a href="/config/admin/mailserver/configuration">Mail Configuration</a></li>
				</ul></li>
		</div>
	</div>
	<div id="footer">
		<div class="container">
			&copy; 2010-2014 Ralf Wehner | <a href="mailto:info@ralfwehner.org?subject=%5bMediaMagpie%5d Feedback">Feedback</a> | <a
				href="http://github.com/rwe17/MediaMagpie" target="_blank">Bug Report</a> <br /> Version: <span>unknown</span>, Built Time: <span>unknown</span>
			, Mode: local
		</div>
	</div>
	
	<!-- Bootstrap core JavaScript
    ================================================== -->
    <!-- Placed at the end of the document so the pages load faster -->
    <script src="https://code.jquery.com/jquery-1.10.2.min.js"></script>
    <script src="/static/bootstrap/js/bootstrap.min.js"></script>
    <!-- <script src="https://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>-->
    <!-- <script type="text/javascript" src="/static/js/jquery.dropdown.js"></script>-->
    <!-- <script type="text/javascript" src="/static/js/utils.js"></script>-->
    <!-- <script type="text/javascript" src="/static/js/prefixfree.min.js"></script>-->
    <script type="text/javascript">
    /**
     * Project: Bootstrap Hover Dropdown
     * Author: Cameron Spear
     * Contributors: Mattia Larentis
     *
     * Dependencies: Bootstrap's Dropdown plugin, jQuery
     *
     * A simple plugin to enable Bootstrap dropdowns to active on hover and provide a nice user experience.
     *
     * License: MIT
     *
     * http://cameronspear.com/blog/bootstrap-dropdown-on-hover-plugin/
     * https://github.com/CWSpear/bootstrap-hover-dropdown
     */
  ;
    (function($, window, undefined) {
        // outside the scope of the jQuery plugin to
        // keep track of all dropdowns
        var $allDropdowns = $();

        // if instantlyCloseOthers is true, then it will instantly
        // shut other nav items when a new one is hovered over
        $.fn.dropdownHover = function(options) {
            // don't do anything if touch is supported
            // (plugin causes some issues on mobile)
            if ('ontouchstart' in document)
                return this; // don't want to affect chaining

            // the element we really care about
            // is the dropdown-toggle's parent
            $allDropdowns = $allDropdowns.add(this.parent());

            return this.each(function() {
                var $this = $(this), $parent = $this.parent(), defaults = {
                    delay : 300,
                    instantlyCloseOthers : true
                }, data = {
                    delay : $(this).data('delay'),
                    instantlyCloseOthers : $(this).data('close-others')
                }, showEvent = 'show.bs.dropdown', hideEvent = 'hide.bs.dropdown',
                // shownEvent = 'shown.bs.dropdown',
                // hiddenEvent = 'hidden.bs.dropdown',
                settings = $.extend(true, {}, defaults, options, data), timeout;

                $parent.hover(function(event) {
                    // so a neighbor can't open the dropdown
                    if (!$parent.hasClass('open') && !$this.is(event.target)) {
                        // stop this event, stop executing any code
                        // in this callback but continue to propagate
                        return true;
                    }

                    if (settings.instantlyCloseOthers === true)
                        $allDropdowns.removeClass('open');

                    window.clearTimeout(timeout);
                    $parent.addClass('open');
                    $this.trigger(showEvent);
                }, function() {
                    timeout = window.setTimeout(function() {
                        $parent.removeClass('open');
                        $this.trigger(hideEvent);
                    }, settings.delay);
                });

                // this helps with button groups!
                $this.hover(function() {
                    if (settings.instantlyCloseOthers === true)
                        $allDropdowns.removeClass('open');

                    window.clearTimeout(timeout);
                    $parent.addClass('open');
                    $this.trigger(showEvent);
                });

                // handle submenus
                $parent.find('.dropdown-submenu').each(function() {
                    var $this = $(this);
                    var subTimeout;
                    $this.hover(function() {
                        window.clearTimeout(subTimeout);
                        $this.children('.dropdown-menu').show();
                        // always close submenu siblings instantly
                        $this.siblings().children('.dropdown-menu').hide();
                    }, function() {
                        var $submenu = $this.children('.dropdown-menu');
                        subTimeout = window.setTimeout(function() {
                            $submenu.hide();
                        }, settings.delay);
                    });
                });
            });
        };

        $(document).ready(function() {
            // apply dropdownHover to all elements with the data-hover="dropdown" attribute
            $('[data-hover="dropdown"]').dropdownHover();
        });
    })(jQuery, this);


    // prefixfree plugin for jquery to enable prefixfree functionality for css changes by jquery
    /*  (function($, self) {
     if (!$ || !self) {
     return;
     }
     for (var i = 0; i < self.properties.length; i++) {
     var property = self.properties[i], camelCased = StyleFix.camelCase(property), PrefixCamelCased = self.prefixProperty(property, true);

     $.cssProps[camelCased] = PrefixCamelCased;
     }
     })(window.jQuery, window.PrefixFree);*/
     </script>
	
</body>
</html>