<% //File view %>
<jsp:useBean id="ss_forum_entry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />

<c:if test="${!empty ss_forum_entry.customAttributes[property_name]}">
<div class="entryContent">
<span class="contentbold"><c:out value="${property_caption}" /></span>
<c:set var="selection" value="${ss_forum_entry.customAttributes[property_name].value}" />
<%
	String url = "forum/view_file?forumId=";
	url += ss_forum_entry.getParentFolder().getId();
	url += "&entryId=";
	url += ss_forum_entry.getId();
	url += "&op=view_file&attr=";
	url += property_name;
%>
<a class="bg" target="_blank" href="<ssf:url url="<%= url %>" />"><c:out value="${selection.fileItem.name}"/></a><br>
</div>
</c:if>
