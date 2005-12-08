<% // 2 column table %>
<%@ page import="org.dom4j.Element" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />
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
<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= tdItem1 %>" configJspStyle="<%= ssConfigJspStyle %>" 
  processThisItem="true" />
</td>
<%
				//Output the second <td>
				if (itItems.hasNext()) {
					Element tdItem2 = (Element) itItems.next();
%>
<td>
<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= tdItem2 %>" configJspStyle="<%= ssConfigJspStyle %>" 
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

