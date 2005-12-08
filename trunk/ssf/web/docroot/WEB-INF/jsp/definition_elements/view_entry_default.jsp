<% // The default entry view if no definition exists for an entry %>
<jsp:useBean id="folderEntryDescendants" type="java.util.List" scope="request" />

<div class="ss_portlet" width="100%">

<%@ include file="/jsp/definition_elements/title_view.jsp" %>

<div class="formBreak">
<div class="entryContent">
<c:out value="${folderEntry.description.text}"/>
</div>
</div>
<%
	Iterator itEntryData = folderEntryDescendants.iterator();
	while (itEntryData.hasNext()) {
%>
<div class="formBreak">
<%
		itEntryData.next().toString();
%>
</div>
<%
	}
%>

</div>
