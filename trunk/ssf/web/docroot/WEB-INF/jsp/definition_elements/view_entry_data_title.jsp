<% //Title view %>
<jsp:useBean id="ssDefinitionEntry" type="com.sitescape.ef.domain.Entry" scope="request" />
<div class="ss_entryContent">
<h1 class="ss_entryTitle">
<% if (ssDefinitionEntry instanceof FolderEntry) { %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<%
	if (!ssSeenMap.checkIfSeen(ssDefinitionEntry)) {
		ssSeenMap.setSeen(ssDefinitionEntry);
		%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
%>
<c:out value="${ssDefinitionEntry.docNumber}"/>.
 <a class="ss_link_nodec" href="<ssf:url 
    folderId="${ssDefinitionEntry.parentFolder.id}" 
    action="view_entry"
    entryId="${ssDefinitionEntry.id}"/>">
<% } %>
<c:if test="${empty ssDefinitionEntry.title}">
    <span class="ss_gray">--no title--</span>
    </c:if><c:out value="${ssDefinitionEntry.title}"/></a></h1>
</div>