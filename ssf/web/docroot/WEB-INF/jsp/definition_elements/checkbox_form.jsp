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
%>
<c:set var="cb_checked" value=""/>
<c:if test="${ssFolderEntry.customAttributes[property_name].value}" >
<c:set var="cb_checked" value="checked"/>
</c:if>
<div class="formBreak">
<div style="display:<%= inline %>;">
<input type="checkbox" name="<%= elementName %>" <c:out value="${cb_checked}"/>>&nbsp;<span class="ss_content"><%= caption %></span></checkbox>
</div>
</div>
