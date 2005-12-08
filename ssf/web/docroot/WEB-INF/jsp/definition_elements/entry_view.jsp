<% //View an entry %>
<%@ include file="/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_forum" type="com.sitescape.ef.domain.Binder" scope="request" />
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_configJspStyle" type="String" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ss_definition_folder_entry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />
<jsp:useBean id="ss_forum_entry_toolbar" type="java.util.SortedMap" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	
	int boxWidth = (int)ParamUtil.get(request, "box_width", (double)RES_TOTAL) - 6;
%>
<div class="ss_portlet">
<c:set var="toolbar" value="${ss_forum_entry_toolbar}" scope="request" />
<%@ include file="/jsp/definition_elements/toolbar_view.jsp" %>
<sitescape:displayConfiguration configDefinition="<%= ss_forum_config_definition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= ss_forum_configJspStyle %>" 
  folderEntry="<%= ss_definition_folder_entry %>" />
</div>
