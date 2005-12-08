<% // The selectbox form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ss_forum_configJspStyle" type="String" scope="request" />
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	String elementName = (String) request.getAttribute("property_name");
	request.setAttribute("selectboxName", elementName);
	String caption = (String) request.getAttribute("property_caption");
	String multiple = (String) request.getAttribute("property_multipleAllowed");
	if (multiple != null && multiple.equals("true")) {
		multiple = "multiple";
	} else {
		multiple = "";
	}
	String size = (String)request.getAttribute("property_size");
	if (size == null || size.equals("")) {
		size = "";
	} else {
		size = "size='" + size + "'";
	}
	if (caption != null && !caption.equals("")) {
		caption += "<br>\n";
	}
%>
<div class="formBreak">
<div>
<%= caption %><select name="<%= elementName %>" <%= multiple %> <%= size %>>
<ssf:displayConfiguration configDefinition="<%= ss_forum_config_definition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= ss_forum_configJspStyle %>" />
</select>
</div>
</div>
