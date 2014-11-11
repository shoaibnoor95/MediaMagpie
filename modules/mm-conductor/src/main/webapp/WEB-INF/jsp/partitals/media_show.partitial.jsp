<c:if test="${mediaDetailCommand.photo == false}">
	<c:set var="offsetMetaBlock" value="margin-bottom: 40px;" />
</c:if>
<div class="photo-display-item">
	<c:choose>
		<c:when test="${mediaDetailCommand.photo}">
			<img class="thumb center-block" alt="${mediaDetailCommand.name}" src="<%=request.getContextPath()%>${mediaDetailCommand.imageLink}" />
		</c:when>
		<c:otherwise>
			<!-- see: http://www.w3schools.com/TAgs/tag_video.asp  -->
			<video class="center-block" poster="<%=request.getContextPath()%>${mediaDetailCommand.imageLink}" controls preload="none"
				src="<%=request.getContextPath()%>${mediaDetailCommand.videoUrl}"> Your browser does not support the video tag.
			</video>
		</c:otherwise>
	</c:choose>
	<div class="thumb-meta" style="${offsetMetaBlock}">
		<div class="title">
			<span class="lead">${mediaDetailCommand.name}</span> <span class="pull-right"><core:date date="${mediaDetailCommand.creationDate}" /></span>
		</div>
		<div class="desc">
			<c:out value="${mediaDetailCommand.description}" />
		</div>
	</div>
</div>