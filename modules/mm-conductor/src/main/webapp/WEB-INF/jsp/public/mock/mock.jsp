<head>
<script>
$( document ).ready(function() {
console.log( "document loaded" );
});
$( window ).load(function() {
console.log( "window loaded" );
});
</script>
</head>
<body>
	<h1>mock</h1>

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
		</ul></li>
	<br />
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

</body>