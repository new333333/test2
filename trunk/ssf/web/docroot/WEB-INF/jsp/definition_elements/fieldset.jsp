<% //fieldset %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
%>
<script type="text/javascript">
function ss_roundFieldset() {
	if(!NiftyCheck()) return;
	Rounded("fieldset.ss_fieldset_rounded","#FFFFFF","#BBD8FF");
}
createOnLoadObj('ss_roundFieldset', ss_roundFieldset);
</script>
<div class="formBreak">
<fieldset class="ss_fieldset_rounded">
<c:if test="${!empty property_legend}">
<legend class="ss_legend"><c:out value="${property_legend}"/></legend>
</c:if>
<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= ssConfigJspStyle %>" />
</fieldset>
</div>
