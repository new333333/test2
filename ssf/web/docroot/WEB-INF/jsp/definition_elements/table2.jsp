<% // 2 column table %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
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
<td valign="top">
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= tdItem1 %>" configJspStyle="${ssConfigJspStyle}" 
  processThisItem="true" />
</td>
<%
				//Output the second <td>
				if (itItems.hasNext()) {
					Element tdItem2 = (Element) itItems.next();
%>
<td valign="top">
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= tdItem2 %>" configJspStyle="${ssConfigJspStyle}" 
  processThisItem="true" />
</td>
<%
				} else {
%>
<td valign="top">&nbsp;</td>
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

