<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% // The default entry view if no definition exists for an entry %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssDefinitionEntry" type="java.lang.Object" scope="request" />

<div class="ss_style ss_portlet" width="100%">
<%
	if (ssDefinitionEntry instanceof com.sitescape.team.domain.Principal) {
%>	
<%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_title.jsp" %>
<% } else {
%>
<%@ include file="/WEB-INF/jsp/definition_elements/view_entry_data_title.jsp" %>
<% } %>

<div class="formBreak">
<div class="ss_entryContent">
<ssf:markup type="view" entity="${ssDefinitionEntry}"><c:out 
  value="${ssDefinitionEntry.description.text}" escapeXml="false"/></ssf:markup>
</div>
</div>
<c:forEach var="descendant" items="${ssFolderEntryDescendants}">
<div class="formBreak">
<c:out value="${descendant}"/>
</div>
</c:forEach>
BBB
</div>
