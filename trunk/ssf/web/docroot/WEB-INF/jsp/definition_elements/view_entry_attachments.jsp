<% // View entry attachments %>
<%@ include file="/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_caption" type="String" scope="request" />
<jsp:useBean id="ss_forum_entry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />

<br><b><c:out value="${property_caption}"/></b><br>

<c:forEach var="selection" items="${ss_forum_entry.fileAttachments}" >
<c:set var="selectionId" value="${selection.id}" />
<jsp:useBean id="selectionId" type="java.lang.String" />
<%
	String url = "forum/view_file?forumId=";
	url += ss_forum_entry.getParentFolder().getId();
	url += "&entryId=";
	url += ss_forum_entry.getId();
	url += "&op=view_file&file=";
	url += selectionId;
%>
<a class="bg" target="_blank" href="<sitescape:url url="<%= url %>" />"><c:out value="${selection.fileItem.name}"/></a><br>
 </c:forEach>
 
<br>
