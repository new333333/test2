<% //Checkbox form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ss_forum_configJspStyle" type="String" scope="request" />
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
<c:if test="${ss_forum_entry.customAttributes[property_name].value}" >
<c:set var="cb_checked" value="checked"/>
</c:if>
<div class="formBreak">
<div style="display:<%= inline %>;">
<input type="checkbox" name="<%= elementName %>" <c:out value="${cb_checked}"/>>&nbsp;<%= caption %></checkbox>
</div>
</div>
