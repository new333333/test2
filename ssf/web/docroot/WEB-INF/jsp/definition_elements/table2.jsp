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
		
		//Iterate through the child items, putting them into a 2 column table
		Iterator itItems = item.elementIterator("item");
		if (itItems.hasNext()) {
%>
<table cellspacing="0" cellpadding="0" width="100%">
<%
			while (itItems.hasNext()) {
%>
<tr>
<%
				//Output the first <td>
				Element tdItem1 = (Element) itItems.next();
%>
<td>
<sitescape:displayConfiguration configDefinition="<%= ss_forum_config_definition %>" 
  configElement="<%= tdItem1 %>" configJspStyle="<%= ss_forum_configJspStyle %>" 
  processThisItem="true" />
</td>
<%
				//Output the second <td>
				if (itItems.hasNext()) {
					Element tdItem2 = (Element) itItems.next();
%>
<td>
<sitescape:displayConfiguration configDefinition="<%= ss_forum_config_definition %>" 
  configElement="<%= tdItem2 %>" configJspStyle="<%= ss_forum_configJspStyle %>" 
  processThisItem="true" />
</td>
<%
				} else {
%>
<td>&nbsp;</td>
<%
				}
%>
</tr>
<%
			}
%>
</table>
<%
		}
%>

