function toggleMetaInformation() {
	var metaInfoIsVisible = $("div.meta").is(":visible");
	var imgToggle = $("img.toggle-img");
	var linkToggle = $("a.toggle-link");
	if (metaInfoIsVisible) {
		imgToggle.attr("src", "<%=request.getContextPath()%>/static/images/famfamfam_silk/bullet_arrow_down.png");
		linkToggle.text("Show camera meta informations");
	} else {
		imgToggle.attr("src", "<%=request.getContextPath()%>/static/images/famfamfam_silk/bullet_arrow_up.png");
		linkToggle.text("Hide camera meta informations");
	}
	$("div.meta").fadeToggle("fast", "linear");
}
