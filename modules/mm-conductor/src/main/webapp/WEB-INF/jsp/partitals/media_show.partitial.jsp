<c:choose>
	<c:when test="${mediaDetailCommand.photo}">
		<div class="photo-display-item">
			<img class="center-block thumb" alt="${mediaDetailCommand.name}" src="<%=request.getContextPath()%>${mediaDetailCommand.imageLink}" />
			<div class="thumb-meta" >
				<div class="title"><span class="lead" >${mediaDetailCommand.name}</span>
				<span class="pull-right"><core:date date="${mediaDetailCommand.creationDate}" /></span>
				</div>
				<div class="desc"><c:out value="${mediaDetailCommand.description}" /></div>
			</div>
		</div>
	</c:when>
	<c:otherwise>
		<!-- see: http://www.w3schools.com/TAgs/tag_video.asp  -->
		<video poster="<%=request.getContextPath()%>${mediaDetailCommand.imageLink}" controls preload="none"
			src="<%=request.getContextPath()%>${mediaDetailCommand.videoUrl}"> Your browser does not support the video tag.
		</video>
	</c:otherwise>
</c:choose>