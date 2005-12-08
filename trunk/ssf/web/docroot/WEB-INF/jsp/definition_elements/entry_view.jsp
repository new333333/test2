<% //View an entry %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="configDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="configJspStyle" type="String" scope="request" />
<jsp:useBean id="configElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="definitionEntry" type="com.sitescape.ef.domain.FolderEntry" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	
	int boxWidth = (int)ParamUtil.get(request, "box_width", (double)RES_TOTAL) - 6;
%>
<div class="ss_portlet">
<c:set var="toolbar" value="${folderEntryToolbar}" scope="request" />
<%@ include file="/jsp/definition_elements/toolbar_view.jsp" %>
<ssf:displayConfiguration configDefinition="<%= configDefinition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= configJspStyle %>" 
  folderEntry="<%= definitionEntry %>" />
</div>
