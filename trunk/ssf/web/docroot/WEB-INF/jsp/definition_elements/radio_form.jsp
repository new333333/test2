<% // The radio form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="configDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="configElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="configJspStyle" type="String" scope="request" />
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
<ssf:displayConfiguration configDefinition="<%= configDefinition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= configJspStyle %>" />
<%
	request.setAttribute("radioGroupName", orgRadioGroupName);
%>
</div>
</div>
