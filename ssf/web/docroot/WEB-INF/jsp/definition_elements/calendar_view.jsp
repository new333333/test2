<% // Calendar view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssFolder" type="com.sitescape.ef.domain.Folder" scope="request" />
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="ssFolderDomTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssEventDates" type="java.util.HashMap" scope="request" />
<jsp:useBean id="ssCalendarViewMode" type="java.lang.String" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="java.lang.String" scope="request" />
<%
	String folderId = ssFolder.getId().toString();
	String parentFolderId = "";
	if (ssFolder instanceof Folder) {
		Folder parentFolder = ((Folder) ssFolder).getParentFolder();
		if (parentFolder != null) parentFolderId = parentFolder.getId().toString();
	}
%>

<div class="ss_folder">
<h1 class="ss_folderTitle">Folders</h1>
<table width="100%" border="0" cellpadding="2" cellspacing="0">
 <tr>
  <td>
	  <ssf:tree treeName="folderTree" treeDocument="<%= ssFolderDomTree %>" 
	    rootOpen="false" 
	    nodeOpen="<%= parentFolderId %>" highlightNode="<%= folderId %>" /></td>
 </tr>
</table>

</div>

<hr>
Calendar view<br>
Viewing: ${ssCalendarViewMode} 
<br>Starting: ${ssCalStartDate} 
<br>Ending: ${ssCalEndDate}
<hr>
<c:forEach var="pair" items="${ssEventDates}" >
${pair.key}<br>
${pair.value}<br>
</c:forEach>




