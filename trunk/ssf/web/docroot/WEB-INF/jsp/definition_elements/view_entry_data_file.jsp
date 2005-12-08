<% //File view %>
<jsp:useBean id="ssFolderEntry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />

<c:if test="${!empty ssFolderEntry.customAttributes[property_name]}">
<div class="entryContent">
<span class="contentbold"><c:out value="${property_caption}" /></span>
<c:set var="selection" value="${ssFolderEntry.customAttributes[property_name].value}" />
<%
	String url = "forum/view_file?forumId=";
	url += ssFolderEntry.getParentFolder().getId();
	url += "&entryId=";
	url += ssFolderEntry.getId();
	url += "&op=view_file&attr=";
	url += property_name;
%>
<a class="bg" target="_blank" href="<ssf:url url="<%= url %>" />"><c:out value="${selection.fileItem.name}"/></a><br>
</div>
</c:if>
