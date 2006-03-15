<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>

<%@ page import="org.dom4j.Document" %>
<%@ page import="org.dom4j.DocumentHelper" %>
<%@ page import="org.dom4j.Element" %>
<%@ page import="com.sitescape.ef.domain.DefinitionInvalidOperation" %>
<%@ page import="com.sitescape.ef.util.NLT" %>

<jsp:useBean id="definitionTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="data" type="java.util.Map" scope="request" />
<jsp:useBean id="ssPublicEntryDefinitions" type="java.util.Map" scope="request" />
<%@ page import="com.sitescape.ef.domain.Entry" %>
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
		<ssf:buildDefinitionDivs title="<%= NLT.get("definition.select_item") %>"
		  sourceDocument="<%= (Document) data.get("sourceDefinition") %>" 
		  configDocument="${ssConfigDefinition}"
		  entryDefinitions="<%= ssPublicEntryDefinitions %>"
		  option="<%= (String) data.get("option") %>" 
		  itemId="<%= (String) data.get("itemId") %>" 
		  itemName="<%= (String) data.get("itemName") %>" 
		/>
<%
	if (1 == 0) {
%>
<taconite-root xml:space="preserve">
<%
  if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="ss_load_div_status_message" parseInBrowser="true">
		<div id="ss_load_div_status_message" 
		 style="visibility:hidden; display:none;">error</div>
	</taconite-replace>
<%
  } else {
%>
	<taconite-replace contextNodeID="ss_load_div_status_message" parseInBrowser="true">
		<div id="ss_load_div_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>

	<taconite-replace contextNodeID="displaydiv" parseInBrowser="true">
	  <div id="displaydiv" style="margin:4px;"> 
		<ssf:buildDefinitionDivs title="<%= NLT.get("definition.select_item") %>"
		  sourceDocument="<%= (Document) data.get("sourceDefinition") %>" 
		  configDocument="${ssConfigDefinition}"
		  entryDefinitions="<%= ssPublicEntryDefinitions %>"
		  option="<%= (String) data.get("option") %>" 
		  itemId="<%= (String) data.get("itemId") %>" 
		  itemName="<%= (String) data.get("itemName") %>" 
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
