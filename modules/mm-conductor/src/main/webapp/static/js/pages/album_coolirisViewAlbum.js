var g_startPos;
function openCoolirisWall(feedUrl, startPos){
	g_startPos = startPos;
	var flashvars = {
            feed: feedUrl
        };
    var params = {
             allowFullScreen: "true",
             allowscriptaccess: "always"
    };
    swfobject.embedSWF("https://apps.cooliris.com/embed/cooliris.swf?t=1307582197",
    	"wall", "800", "450", "9.0.0", "",
        flashvars, params);
}
var mediaSelected = false;
var cooliris = {
        onEmbedInitialized : function() {
        		if(g_startPos >= 0 && !mediaSelected){
        			mediaSelected = true;
        			setTimeout('selectMedia(g_startPos)', 200);
        		}
        }
}

function selectMedia(startPos) {
	cooliris.embed.selectItemByIndex(startPos);
}