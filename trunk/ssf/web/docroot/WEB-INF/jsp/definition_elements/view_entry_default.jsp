<% // The default entry view if no definition exists for an entry %>
<jsp:useBean id="ss_forum_entry_descendants" type="java.util.List" scope="request" />

<div class="ss_portlet" width="100%">

<%@ include file="/jsp/definition_elements/title_view.jsp" %>

<div class="formBreak">
<div class="entryContent">
<c:out value="${ss_forum_entry.description.text}"/>
</div>
</div>
<%
	Iterator itEntryData = ss_forum_entry_descendants.iterator();
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
