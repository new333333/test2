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
<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>

<%@ page import="org.dom4j.Document" %>
<%@ page import="org.dom4j.DocumentHelper" %>
<%@ page import="org.dom4j.Element" %>
<%@ page import="com.sitescape.team.domain.DefinitionInvalidOperation" %>
<%@ page import="com.sitescape.team.util.NLT" %>

<jsp:useBean id="definitionTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="data" type="java.util.Map" scope="request" />
<%@ page import="com.sitescape.team.domain.Entry" %>
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
<%
	if (1 == 0) {
%>
		<ssf:buildDefinitionDivs title="<%= NLT.get("definition.select_item") %>"
		  sourceDocument="<%= (Document) data.get("sourceDefinition") %>" 
		  configDocument="${ssConfigDefinition}"
		  option="<%= (String) data.get("option") %>" 
		  itemId="<%= (String) data.get("itemId") %>" 
		  itemName="<%= (String) data.get("itemName") %>" 
		  refItemId="<%= (String) data.get("refItemId") %>" 
		/>
<%
	} else {
%>
<taconite-root xml:space="preserve">
<%
  if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message" 
		 style="visibility:hidden; display:none;">error</div>
	</taconite-replace>
<%
  } else {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>

	<taconite-replace contextNodeID="displaydiv" parseInBrowser="true">
	  <div id="displaydiv" style="margin:0px; padding:4px;"> 
		<ssf:buildDefinitionDivs title="<%= NLT.get("definition.select_item") %>"
		  sourceDocument="<%= (Document) data.get("sourceDefinition") %>" 
		  configDocument="${ssConfigDefinition}"
		  option="<%= (String) data.get("option") %>" 
		  itemId="<%= (String) data.get("itemId") %>" 
		  itemName="<%= (String) data.get("itemName") %>" 
		  refItemId="<%= (String) data.get("refItemId") %>" 
		/>
	  </div>
	</taconite-replace>
<%
  }
%>	
</taconite-root>
<%
	}
%>
