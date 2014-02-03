<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="expires" content="0" />
<link rel="stylesheet" type="text/css" href="/static/css/styles-all.css" />
<link rel="stylesheet" type="text/css" href="/static/css/effects.css" />
<link rel="stylesheet" type="text/css" href="/static/css/ui-lightness/jquery-ui-1.8.13.custom.css" />
<link rel="stylesheet" type="text/css" href="/static/css/jquery.dropdown.css" />

<!-- TODO rwe: use bootstrap. but before we have to fix the context menue issue. See: http://getbootstrap.com/customize/ -->
<link rel="stylesheet" href="/static/bootstrap/css/bootstrap.css"/>

<!-- TODO rwe: use bootstrap. but before we have to fix the context menue issue. See: http://getbootstrap.com/customize/ -->
<script src="/static/bootstrap/js/bootstrap.min.js"></script>

<script src="https://code.jquery.com/jquery-1.10.2.js"></script>
<script src="https://code.jquery.com/ui/1.10.4/jquery-ui.js"></script>
<script type="text/javascript" src="/static/js/jquery.dropdown.js"></script>
<script type="text/javascript" src="/static/js/utils.js"></script>
<script type="text/javascript" src="/static/js/prefixfree.min.js"></script>
<script type="text/javascript">
	// prefixfree plugin for jquery to enable prefixfree functionality for css changes by jquery
	(function($, self) {
		if (!$ || !self) {
			return;
		}
		for (var i = 0; i < self.properties.length; i++) {
			var property = self.properties[i], camelCased = StyleFix.camelCase(property), PrefixCamelCased = self.prefixProperty(property, true);

			$.cssProps[camelCased] = PrefixCamelCased;
		}
	})(window.jQuery, window.PrefixFree);
</script>

</head>
<body>
	<div id="page">
		<div id="page_top">
			<div id="logo">
				<a href="/"><span>MediaMagpie</span></a>
			</div>






			<nav id="mainNavi">
			<ul class="top_navi">
				<li class="top_navi"><a href="/welcome">Welcome</a></li>
				<li class="top_navi"><a data-dropdown="#dropdown-media" href="/media/search_pictures">Media</a> <!--<img data-dropdown="#dropdown-media" src="/static/images/famfamfam_silk/bullet_arrow_down.png" >-->







					<div id="dropdown-media" class="dropdown dropdown-relative dropdown-tip">
						<ul class="dropdown-menu">
							<li class=""><a href="/media/search_pictures">Medias</a></li>
							<li class=""><a href="/media/album/list">Albums</a></li>
							<li class=""><a href="/upload/file-upload">Upload</a></li>
							<li class=""><a href="/trash/content">Trash</a></li>
						</ul>
					</div></li>
				<li class="top_navi"><a data-dropdown="#dropdown-config" href="/config/user/">Configuration </a>











					<div id="dropdown-config" class="dropdown dropdown-relative dropdown-tip">
						<ul class="dropdown-menu">
							<li class=""><a href="/config/user/">User Configuration</a></li>
							<li class=""><a href="/config/aws/s3/">AWS S3 Configuration</a></li>
							<li class=""><a href="/config/admin/mainconfiguration">Main Configuration</a></li>
							<li class=""><a href="/config/admin/mailserver/configuration">Mail Configuration</a></li>
						</ul>
					</div></li>
			</ul>
			</nav>
			<div id="loginNavi">

				<a href="/login">Login</a>

			</div>
		</div>
		<div id="page_middle">
			<div id="content">
				<h1>mock</h1>
				<a href="#" data-dropdown="#dropdown-media2">dropdown media</a> <img data-dropdown="#dropdown-media2"
					src="/static/images/famfamfam_silk/bullet_arrow_down.png" alt="">
				<!-- context menu for media -->
				<div id="dropdown-media2" class="dropdown dropdown-tip">
					<ul class="dropdown-menu">
						<li><a href="/media/search_pictures">Medias</a></li>
						<li><a href="/media/album/list">Albums</a></li>
						<li><a href="/upload/file-upload">Upload</a></li>
						<li><a href="/trash/content">Trash</a></li>
					</ul>
				</div>

			</div>
		</div>
		<div id="page_bottom">


			<div id="footer">
				&copy; 2010-2014 Ralf Wehner | <a href="mailto:info@ralfwehner.org?subject=%5bMediaMagpie%5d Feedback">Feedback</a> | <a
					href="http://github.com/rwe17/MediaMagpie" target="_blank">Bug Report</a> <br /> Version: <span>unknown</span>, Built Time: <span>unknown</span>
				, Mode: local
			</div>
		</div>
	</div>
</body>
</html>