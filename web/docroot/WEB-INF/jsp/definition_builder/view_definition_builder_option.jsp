<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
