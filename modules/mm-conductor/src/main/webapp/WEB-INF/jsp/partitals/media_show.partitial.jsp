<c:choose>
	<c:when test="${mediaDetailCommand.photo}">
		<div class="imgHover">
			<img class="center-block thumb" alt="${mediaDetailCommand.name}" src="<%=request.getContextPath()%>${mediaDetailCommand.imageLink}" />
			<div class="hover title">${mediaDetailCommand.name}</div>
			<div class="hover meta">meta rwe...</div>
		</div>
	</c:when>
	<c:otherwise>
		<!-- see: http://www.w3schools.com/TAgs/tag_video.asp  -->
		<video poster="<%=request.getContextPath()%>${mediaDetailCommand.imageLink}" controls preload="none"
			src="<%=request.getContextPath()%>${mediaDetailCommand.videoUrl}"> Your browser does not support the video tag.
		</video>
	</c:otherwise>
</c:choose>