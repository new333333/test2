<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% // 2 column table %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
		String alignment = (String) request.getAttribute("property_alignment");
		String divClass = "ss_buttonBarLeft";
		if (alignment.equals("right")) {
			divClass = "ss_buttonBarRight";
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
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= btnItem %>" configJspStyle="${ssConfigJspStyle}" 
  processThisItem="true" />
<%
			}
		}
%>
</div>
