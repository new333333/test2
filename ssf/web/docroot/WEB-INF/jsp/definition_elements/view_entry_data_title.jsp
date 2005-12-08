<% //Title view %>
<jsp:useBean id="ssDefinitionEntry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />

<%
	if (!ssSeenMap.checkIfSeen(ssDefinitionEntry)) {
		ssSeenMap.setSeen(ssDefinitionEntry);
		%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
%>
<h1 class="ss_entryTitle">
<c:out value="${ssDefinitionEntry.docNumber}"/>. 
 <a href="<ssf:url 
    folderId="<%= ssDefinitionEntry.getParentFolder().getId().toString() %>" 
    action="view_entry"
    entryId="<%= ssDefinitionEntry.getId().toString() %>"
    />"><c:if test="${empty ssDefinitionEntry.title}">
    <span class="ss_contentbold"><i>(no title)</i></span>
    </c:if><c:out value="${ssDefinitionEntry.title}"/></a></h1>