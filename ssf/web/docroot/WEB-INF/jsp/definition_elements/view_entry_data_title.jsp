<% //Title view %>
<jsp:useBean id="definitionEntry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />
<jsp:useBean id="seenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<h1 class="entryTitle">
<%
	if (!seenMap.checkIfSeen(definitionEntry)) {
		seenMap.setSeen(definitionEntry);
		%><img border="0" src="<%= contextPath %>/html/pics/sym_s_unseen.gif"><%
	}
%>
<c:out value="${definitionEntry.docNumber}"/>. 
<a href="<ssf:url 
    folderId="<%= definitionEntry.getParentFolder().getStringId() %>" 
    operation="view_entry"
    entryId="<%= definitionEntry.getId().toString() %>"
    />" onClick="return(ss_openUrlInPortlet(this.href));"><c:if test="${empty definitionEntry.title}">
    <span class="contentbold"><i>(no title)</i></span>
    </c:if><c:out value="${definitionEntry.title}"/></a></h1>
