<% //Title view %>
<jsp:useBean id="ss_definition_folder_entry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />
<jsp:useBean id="ss_folder_seenmap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<h1 class="entryTitle">
<%
	if (!ss_folder_seenmap.checkIfSeen(ss_definition_folder_entry)) {
		ss_folder_seenmap.setSeen(ss_definition_folder_entry);
		%><img border="0" src="<%= contextPath %>/html/pics/sym_s_unseen.gif"><%
	}
%>
<c:out value="${ss_definition_folder_entry.docNumber}"/>. 
<a href="<ssf:url 
    folderId="<%= ss_definition_folder_entry.getParentFolder().getStringId() %>" 
    operation="view_entry"
    entryId="<%= ss_definition_folder_entry.getId().toString() %>"
    />" onClick="return(ss_openUrlInPortlet(this.href));"><c:if test="${empty ss_definition_folder_entry.title}">
    <span class="contentbold"><i>(no title)</i></span>
    </c:if><c:out value="${ss_definition_folder_entry.title}"/></a></h1>
