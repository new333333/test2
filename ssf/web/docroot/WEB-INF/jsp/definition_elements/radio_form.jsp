<% // The radio form element %>
<%@ include file="/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_forum" type="com.sitescape.ef.domain.Binder" scope="request" />
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ss_forum_configJspStyle" type="String" scope="request" />
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	String elementName = (String) request.getAttribute("property_name");
	String orgRadioGroupName = (String) request.getAttribute("radioGroupName");
	request.setAttribute("radioGroupName", elementName);
	String caption = (String) request.getAttribute("property_caption");
	if (caption != null && !caption.equals("")) {
		caption = "<span class='content'>" + caption + "</span>\n<br/>\n";
	}
	String checked = "";
%>
<div class="formBreak">
<div>
<%= caption %>
<sitescape:displayConfiguration configDefinition="<%= ss_forum_config_definition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= ss_forum_configJspStyle %>" />
<%
	request.setAttribute("radioGroupName", orgRadioGroupName);
%>
</div>
</div>
