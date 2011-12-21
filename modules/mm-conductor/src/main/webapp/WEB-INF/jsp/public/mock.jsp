<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
	<meta http-equiv="expires" content="0"/>
	<title>Welcome to MediaMagpie</title>
	<script type="text/javascript" src="/static/js/jquery-1.6.4.min.js"></script>
	<script type="text/javascript" src="/static/js/jquery-ui-1.8.13.custom.min.js"></script>
	<script type="text/javascript" src="/static/js/utils.js" ></script>
 	<link rel="stylesheet" type="text/css" href="/static/css/ui-lightness/jquery-ui-1.8.13.custom.css" />
<%/*  	<link rel="stylesheet" type="text/css" href="/static/css/styles-all.css" />*/%>
 	<style type="">
 	
/** base.css 
****************/
* {
    font-family: Verdana,Helvetica,Arial,sans-serif;
    font-size: 11px;
}
body, html {
    background: none repeat scroll 0 0 #FFFFFF;
    color: #000000;
    margin: 0;
    padding: 0;
}
a {
//	color: #002c50;
	color: #000;
}
a:hover {
	color: #0191d0;
}

/** grid.css 
****************/
#page {
    background-color: #7a7a7a;
	width: 100%;
	min-width:900px;
	margin: 0 auto;
}

#page_top {
 	background: -moz-linear-gradient(center top , #c3c3c3, #7a7a7a) repeat scroll 0 0 transparent;
    height: 70px;
    padding: 0;
}

#page_middle {
}

#sub_navi {
//    display: block;
    float: left;
    width: 210px;
}

#content {
	background: white;
	padding: 30px;
	margin-left: 210px;
	min-height: 600px;
//	border-left: 1px solid rgb(204, 204, 204);
}

#page_bottom {
	background: #252525;
//    background: none repeat scroll 0 0 #dfdfdf;
    border-top: 1px solid #CDCDCD;
    clear: both;
    color: #999999;
    font-size: 10px;
    padding: 5px 10px;
    text-align: center;
    margin-bottom:0;
}

/** logo etc. 
********************/ 	
#logo {
	position: absolute;
	top: 18px;
	left: 17px;
}
#logo a {
	background: -moz-linear-gradient(right top, #165075, #63C2FF) repeat scroll 0 0 transparent;
	background: -webkit-gradient(linear, right top, left bottom, from(#165075), to(#63C2FF) );
    /* For Internet Explorer 5.5 - 7 */
	filter: progid:DXImageTransform.Microsoft.gradient(GradientType=0,startColorstr=#0191d0,endColorstr=#00a0db);
//	border: 1px solid #4bddf7;
	/*border-radius: 2em 2em 2em 2em;*/
	border-radius: 10em 10em 5em 5em; //
	-webkit-box-shadow: 4px 6px 6px rgba(0, 0, 0, 0.4);
	box-shadow: 4px 6px 6px rgba(0, 0, 0, 0.4);
//	line-height: 100%;
	padding: 13px 18px;
	zoom: 1;
	font-weight: bold;
	font-size: 12px;
	line-height: 33px;
	text-decoration: none;
}

#logo span {
	font-size: 18px;
	color: white;
}

/** navigation.css 
********************/ 	
#mainNavi {
    float:left;
    height: 70px;
    line-height:normal;
    margin-left: 210px;
    }
#mainNavi ul {
    margin:0;
    padding:0;
    list-style:none;
    position: relative;
    top: 32px;
    }
#mainNavi li {
    float:left;
    margin:0 1px 0 1px;
    padding:0 30px 0 30px;
	height: 36px;
	background: -moz-linear-gradient(center top , #f0f0f0, #cccccc) repeat scroll 0 0 transparent;
	background: -webkit-gradient(linear, left top, left bottom, from(#f0f0f0), to(#cccccc) );
    border: 1px solid #cccccc;
    border-radius: 8px 8px 0 0;
    box-shadow: inset 2px 8px 10px #DFDFDF;
    } 
    
#mainNavi a {
    display:block;
	font-weight: bold;
	font-size: 12px;
	line-height: 28px;
	text-decoration: none;
    }    
#mainNavi ul li a {
	padding-top: 4px;
    }
#mainNavi li.current {
	margin: -4px 0 0 0;
	padding-top: 2px;
	height: 39px;
	background: white;
    box-shadow: inset 0px 0px 0px #ffffff;
    border-bottom: 0px;
}
#mainNavi ul li.current a {
	padding-top: 4px;
	font-size: 13px;
	text-decoration: underline;
}
#mainNavi li:hover {
	background: #CCCCCC;
}
#loginNavi {
	float: right;
	margin: 40px 30px 0 30px;
	font-weight: bold;
}
#subNavi {
    margin-top:20px;
    margin-left: 25px;
}
#subNavi ul {
	margin: 0;
	padding: 0;
	list-style: none;
}
#subNavi li {
	margin: 1px 0 1px 0;
	padding: 0;
//	width: 190px;
	height: 33px;
	//background: #dfdfdf;
	background: -moz-linear-gradient(center top , #f0f0f0, #cccccc) repeat scroll 0 0 transparent;
	background: -webkit-gradient(linear, left top, left bottom, from(#f0f0f0), to(#cccccc) );
    border: 1px solid #CCCCCC;
    border-left: 0;
    border-radius: 8px 0 0 8px;
    box-shadow: 2px 8px 10px #DFDFDF inset;
 }
#subNavi li a {
	text-decoration: none;
	font-size: 12px;
	line-height: 33px;
	margin-left: 20px;
	font-weight: bold;
}
#subNavi li.current {
//	margin: -4px 0 0 0;
//	padding-top: 2px;
//	height: 39px;
	background: white;
    box-shadow: inset 0px 0px 0px #ffffff;
    border-right: 0px;
}
#subNavi ul li.current a {
	padding-top: 4px;
	padding-left: 8px;
//	font-size: 13px;
	text-decoration: underline;
}

#subNavi li:hover {
	background: #CCCCCC;
// 	background: -moz-linear-gradient(center top , #F0F0F0, #CCCCCC) repeat scroll 0 0 transparent;
//    box-shadow: 2px 8px 10px #DFDFDF inset;
 }
 	</style>
</head>
<body onload="focusFirstEnabledField()">
	<div id="page"> 
		<div id="page_top">
			<div id="logo">
				<a href="/"><span>MediaMagpie</span></a>
			</div>
			<div id="mainNavi">
			    <ul>
			      <li><a href="#">Home</a></li>
			      <li class="current"><a href="#">News</a></li>
			      <li><a href="#">Products</a></li>
			      <li><a href="#">About</a></li>
			      <li><a href="#">Contact</a></li>
			    </ul>
			</div>		
			<div id="loginNavi">
				<a href="/login">Login</a>
			</div>
		</div>
		<div id="page_middle">
			<div id="sub_navi">
				<div id="subNavi">
					<ul>
						<li>
							<a href="/media/search_pictures">Medias</a>
						</li>
						<li class="current">
							<a href="/media/album/list">Albums</a>
						</li>
						<li>
							<a href="/upload/file-upload">Upload</a>
						</li>
						<li>
							<a href="/trash/content">Trash</a>
					
						</li>
					</ul>
				</div>
			</div>
			<div id="content">
				content
			</div>
		</div>
		<div id="page_bottom">
			footer
		</div>

<%/*

<div id="mainNavi">
	<a href="/" id="logo"><span>MediaMagpie</span></a>


	<div id="header">
    <ul>
      <li id="current"><a href="#">Home</a></li>
      <li><a href="#">News</a></li>
      <li><a href="#">Products</a></li>
      <li><a href="#">About</a></li>
      <li><a href="#">Contact</a></li>
    </ul>
  </div>
  
	<div class="mainNaviRight">
		<a href="/login" style="float: right; margin:10px 20px 0 0; clear:both">Login</a>
	</div>

</div>
<div id="subNavi">
<ul>
	<li class="current">
		<a href="/media/search_pictures">Medias</a>
	</li>
	<li>
		<a href="/media/album/list">Albums</a>
	</li>
	<li>
		<a href="/upload/file-upload">Upload</a>
	</li>
	<li>
		<a href="/trash/content">Trash</a>

	</li>
</ul>
</div>







<div id="content">
	<h1>Welcome to MediaMagpie</h1>
	<h2>What is MediaMagpie?</h2>
		MediaMagpie is a web-portal were you can upload and share your photos. Currently, MediaMagpie is limited only to share photos but the application is designed to to share videos as well and this feature will come soon.  
	<h3>Features</h3>
		<ul>

			<li>Upload Photos and Videos to the server via Drag&Drop from your desktop</li>
			<li>Arrange medias in 'albums' that you can share public or just to registered users</li>
			<li>All shared photos can be downloaded as original file, so the user gets the original quality of the photo including all meta informations your camera provided</li>
			<li>You can add titles and texts to your medias</li>
			<li>MediaMagpie uses some awesome javasript plugins to show your photos as a slide show in a window or full size of screen</li>

		</ul>
	<h2>Last added public pictures</h2>
	
	
	<!-- Start Advanced Gallery Html Containers -->
	<div id="thumbs" class="navigation">
		<ul class="thumbs noscript">
		

		</ul>
	</div>
	<div id="gallery" class="content">

    	<div id="controls" class="controls"></div>
        <div class="slideshow-container">
        	<div id="loading" class="loader"></div>
            <div id="slideshow" class="slideshow"></div>
        </div>	
        <div id="caption" class="caption-container embox" style="opacity: 1;"></div>
	</div>

		<div style="clear: both;"/>
</div>

		<div id="footer">
			&copy; 2010-2011 Ralf Wehner | 
			<a href="mailto:info@ralfwehner.org?subject=%5bMedia-Butler%5d+Feedback">Feedback</a>
			<br/>

			Version: unknown, 
			Revision: <span title="unknown" onclick="this.innerHTML='unknown'">unknow</span>,  
			Built on: unknown 
			, Mode: local
		</div>*/%>
	</div><!-- page -->

</body>	
</html>