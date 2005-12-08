<% //Event widget form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<%
	Event initEvent = new Event();

	//Get the formName of event being displayed
	Element item = (Element) request.getAttribute("item");
	Element nameProperty = (Element) item.selectSingleNode("properties/property[@name='name']");
	String elementName = "event";
	if (nameProperty != null) {
		elementName = nameProperty.attributeValue("value", "event");
	}
	String formName = (String) request.getAttribute("formName");
	String caption = (String) request.getAttribute("property_caption");
	String hD = (String) request.getAttribute("property_hasDuration");
	String hR = (String) request.getAttribute("property_hasRecurrence");
	Boolean hasDur = new Boolean(hD);
	Boolean hasRecur = new Boolean(hR);
	if (caption == null) {caption = "";}
	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>
<span class="ss_labelAbove"><%= caption %><%= required %></span>

<c:if test="${!empty ssFolderEntry.customAttributes[property_name]}" >
<c:set var="ev" value="${ssFolderEntry.customAttributes[property_name].value}" />
</c:if>
<jsp:useBean id="ev" type="com.sitescape.ef.domain.Event" class="com.sitescape.ef.domain.Event" />

<ssf:eventeditor id="<%= elementName %>" 
         formName="<%= formName %>" 
         initEvent="<%= ev %>"
         hasDuration="<%= hasDur %>"
         hasRecurrence="<%= hasRecur %>" />
</div>
<div class="ss_divider"></div>
