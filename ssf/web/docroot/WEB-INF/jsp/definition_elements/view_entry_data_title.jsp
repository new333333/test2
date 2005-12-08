<% //Title view %>
<jsp:useBean id="ssDefinitionEntry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<h1 class="entryTitle">
<%
	if (!ssSeenMap.checkIfSeen(ssDefinitionEntry)) {
		ssSeenMap.setSeen(ssDefinitionEntry);
		%><img border="0" src="<%= contextPath %>/html/pics/sym_s_unseen.gif"><%
	}
%>
<c:out value="${ssDefinitionEntry.docNumber}"/>. 
<a href="<ssf:url 
    folderId="<%= ssDefinitionEntry.getParentFolder().getId().toString() %>" 
    operation="view_entry"
    entryId="<%= ssDefinitionEntry.getId().toString() %>"
    />" onClick="return(ss_openUrlInPortlet(this.href));"><c:if test="${empty ssDefinitionEntry.title}">
    <span class="contentbold"><i>(no title)</i></span>
    </c:if><c:out value="${ssDefinitionEntry.title}"/></a></h1>
