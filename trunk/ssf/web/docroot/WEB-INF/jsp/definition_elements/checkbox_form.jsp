<% //Checkbox form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String inline = (String) request.getAttribute("property_inline");
	if (inline == null) {inline = "block";}
	if (inline.equals("true")) {
		inline = "inline";
	} else {
		inline = "block";
	}
	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>
<c:set var="cb_checked" value=""/>
<c:if test="${ssEntry.customAttributes[property_name].value}" >
<c:set var="cb_checked" value="checked"/>
</c:if>
<div style="display:<%= inline %>;">
<input type="checkbox" name="<%= elementName %>" <c:out value="${cb_checked}"/>>&nbsp;<span class="ss_labelRight"><%= caption %><%= required %></span></checkbox>
</div>
