<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
	String style = (String) request.getAttribute("style");
	com.sitescape.team.domain.FileAttachment thumbnail = (com.sitescape.team.domain.FileAttachment) request.getAttribute("thumbnail");
	String photo_folder = (String) request.getAttribute("photo_folder");
	String photo_entry = (String) request.getAttribute("photo_entry");
%>
<c:set var="style" value="<%= style %>"/>
<c:set var="thumbnail" value="<%= thumbnail %>"/>
<c:set var="photo_folder" value="<%= photo_folder %>"/>
<c:set var="photo_entry" value="<%= photo_entry %>"/>
<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/business_card/thumbnail.js"></script>

<div class="<c:if test="${!empty style}">${style}</c:if><c:if test="${empty style}">ss_thumbnail_small_buddies_list</c:if>"><div>
  	<c:if test="${!empty thumbnail}">
		<img border="0" src="<ssf:url 
		    webPath="viewFile"
		    folderId="${photo_folder}"
		    entryId="${photo_entry}" >
		    <ssf:param name="fileId" value="${thumbnail.id}"/>
		    <ssf:param name="viewType" value="thumbnail"/>
		    </ssf:url>" onerror="ss_buddyPhotoLoadError(this, '<ssf:nlt tag="photo.none"/>');" />
	</c:if>
	<c:if test="${empty thumbnail}">
		<ssf:nlt tag="photo.none"/>
	</c:if>
</div></div>