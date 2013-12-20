<div id="content">
    <h1>mock</h1>
    <a href="#" data-dropdown="#dropdown-media2">dropdown media</a>
    <img data-dropdown="#dropdown-media2" src="/static/images/famfamfam_silk/bullet_arrow_down.png" alt="">
    <!-- context menu for media -->
    <div id="dropdown-media2" class="dropdown dropdown-tip">
        <ul class="dropdown-menu">
            <li><a href="<%=request.getContextPath()%>/media/search_pictures">Medias</a></li>
            <li><a href="<%=request.getContextPath()%>/media/album/list">Albums</a></li>
            <li><a href="<%=request.getContextPath()%>/upload/file-upload">Upload</a></li>
            <li><a href="<%=request.getContextPath()%>/trash/content">Trash</a></li>
        </ul>
    </div>
    
</div>