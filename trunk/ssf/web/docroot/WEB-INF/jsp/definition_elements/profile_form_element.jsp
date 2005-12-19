<% // Show a profile form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="property_name" type="String" scope="request" />
<jsp:useBean id="property_caption" type="String" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	String itemType = (String) item.attributeValue("name", "");
	if (itemType.equals("name")) {
		%><%@ include file="/WEB-INF/jsp/definition_elements/view_profile_data_name.jsp" %><%
	
	} else if (itemType.equals("profileElements")) {
		Element profileElementNameProperty = (Element) item.selectSingleNode("./properties/property[@name='name']");
		String profileElementType = profileElementNameProperty.attributeValue("value", "");

		//Get the item being displayed
		Element formItem = (Element) request.getAttribute("item");
		String formItemType = (String) formItem.attributeValue("name", "");
		if (formItemType.equals("profileElements")) {
			Element formProfileElementNameProperty = (Element) formItem.selectSingleNode("./properties/property[@name='name']");
			String formProfileElementType = formProfileElementNameProperty.attributeValue("value", "");
%>
<div >
<span class="ss_labelAbove"><%= formProfileElementType %></span>
<input type="text" name="<%= formProfileElementType %>" 
 value="<c:out value="${ssEntry[formProfileElementType].value}"/>">
</div>
<%
		}
	}
%>
