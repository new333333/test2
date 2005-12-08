<% //File view %>
<jsp:useBean id="ssFolderEntry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />

<c:if test="${!empty ssFolderEntry.customAttributes[property_name]}">
<div class="ss_entryContent">
<span class="ss_contentbold"><c:out value="${property_caption}" /></span>
<%
	String url = "forum/view_file?forumId=";
	url += ssFolderEntry.getParentFolder().getId();
	url += "&entryId=";
	url += ssFolderEntry.getId();
	url += "&op=view_file&attr=";
	url += property_name;
%>

<span class="ss_content">
<c:set var="selections" value="${ssFolderEntry.customAttributes[property_name].value}" />
<c:forEach var="selection" items="${selections}">
<a target="_blank" 
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssFolderEntry.parentFolder.id}"
    entryId="${ssFolderEntry.id}" >
    <ssf:param name="fileId" value="${selection.id}"/>
    </ssf:url>"><c:out value="${selection.fileItem.name}"/></a><br>
</c:forEach>
</span>
</div>
</c:if>
