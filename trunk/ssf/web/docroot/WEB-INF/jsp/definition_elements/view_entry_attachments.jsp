<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_caption" type="String" scope="request" />
<jsp:useBean id="folderEntry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />

<br><b><c:out value="${property_caption}"/></b><br>

<c:forEach var="selection" items="${folderEntry.fileAttachments}" >
<c:set var="selectionId" value="${selection.id}" />
<jsp:useBean id="selectionId" type="java.lang.String" />
<%
	String url = "forum/view_file?forumId=";
	url += folderEntry.getParentFolder().getId();
	url += "&entryId=";
	url += folderEntry.getId();
	url += "&op=view_file&file=";
	url += selectionId;
%>
<a class="bg" target="_blank" href="<ssf:url url="<%= url %>" />"><c:out value="${selection.fileItem.name}"/></a><br>
 </c:forEach>
 
<br>
