<% // 2 column table %>
<%@ include file="/html/common/init.jsp" %>
<%@ page import="org.dom4j.Element" %>
<jsp:useBean id="ss_forum_forum" type="com.sitescape.ef.domain.Binder" scope="request" />
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ss_forum_configJspStyle" type="String" scope="request" />
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
		String alignment = (String) request.getAttribute("property_alignment");
		String divClass = "buttonBarLeft";
		if (alignment.equals("right")) {
			divClass = "buttonBarRight";
		}
		
%>
<div class="<%= divClass %>">
<%
		//Iterate through the child button items, putting them into a set of divs
		Iterator itItems = item.elementIterator("item");
		if (itItems.hasNext()) {
			while (itItems.hasNext()) {
				//Output the button
				Element btnItem = (Element) itItems.next();
%>
<sitescape:displayConfiguration configDefinition="<%= ss_forum_config_definition %>" 
  configElement="<%= btnItem %>" configJspStyle="<%= ss_forum_configJspStyle %>" 
  processThisItem="true" />
<%
			}
		}
%>
</div>
