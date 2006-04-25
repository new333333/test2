<% // The html editor widget %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String formName = (String) request.getAttribute("formName");
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String height = (String) request.getAttribute("property_height");
	if (height == null || height.equals("")) {
		height = "200";
	}
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<b>"+caption+"</b><br>";
	}

	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
	
%>
<c:set var="textValue" value=""/>
<c:if test="${!empty ssDefinitionEntry}">
  <c:if test="${property_name == 'description'}" >
    <c:set var="textValue" value="${ssDefinitionEntry.description.text}"/>
  </c:if>
  <c:if test="${property_name != 'description'}" >
    <c:set var="textValue" value="${ssDefinitionEntry.customAttributes[property_name].value.text}"/>
  </c:if>
</c:if>
<div class="ss_entryContent">
  <span class="ss_labelLeft"><%= caption %><%= required %></span>
    <ssf:htmleditor id="<%= elementName %>" 
      formName="${formName}" height="<%= height %>" color="${ss_form_element_header_color}"
      initText="${textValue}" />
<script type="text/javascript">
ss_createEventObj('ss_htmlareaUnload_${formName}_${property_name}', "UNLOAD")
function ss_htmlareaUnload_${formName}_${property_name}() {
	alert('ss_htmlareaUnload_${formName}_${property_name}' + document.forms.${formName}.${property_name}.value)
}
</script>
</div>
