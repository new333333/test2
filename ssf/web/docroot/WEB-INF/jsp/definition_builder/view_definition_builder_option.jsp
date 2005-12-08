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
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />
<%@ page import="com.sitescape.ef.domain.FolderEntry" %>

<ssf:buildDefinitionDivs title="<%= NLT.get("definition.select_item") %>"
  sourceDocument="<%= (Document) data.get("sourceDefinition") %>" 
  configDocument="<%= ssConfigDefinition %>"
  entryDefinitions="<%= ssPublicEntryDefinitions %>"
  option="<%= (String) data.get("option") %>" 
  itemId="<%= (String) data.get("itemId") %>" 
  itemName="<%= (String) data.get("itemName") %>" 
  />
