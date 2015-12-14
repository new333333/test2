<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% // 2 column table %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
		
		//Iterate through the child items, putting them into a 2 column table
		Iterator itItems = item.elementIterator("item");
		if (itItems.hasNext()) {
%>
<c:set var="tableWidth" value="100%"/>
<c:if test="${!empty property_tableWidth}"><c:set var="tableWidth" value="${property_tableWidth}"/></c:if>
<c:if test="${tableWidth == '-'}"><c:set var="tableWidth" value=""/></c:if>
<table cellspacing="0" cellpadding="0" width="${tableWidth}">
<%
			while (itItems.hasNext()) {
%>
<tr>
<%
				//Output the first <td>
				Element tdItem1 = (Element) itItems.next();
%>
<c:set var="align" value="left"/>
<c:if test="${!empty propertyValues_align1[0]}"><c:set var="align" value="${propertyValues_align1[0]}"/></c:if>
<c:set var="valign" value="top"/>
<c:if test="${!empty propertyValues_valign1[0]}"><c:set var="valign" value="${propertyValues_valign1[0]}"/></c:if>
<td align="${align}" valign="${valign}" <c:if test="${!empty property_width1}"> width="${property_width1}" </c:if> >
<ssf:displayConfiguration 
  configDefinition="${ssConfigDefinition}" 
  configElement="<%= tdItem1 %>" 
  configJspStyle="${ssConfigJspStyle}" 
  entry="${ssDefinitionEntry}"
  processThisItem="true" />
</td>
<%
				//Output the second <td>
				if (itItems.hasNext()) {
					Element tdItem2 = (Element) itItems.next();
%>
<c:set var="align" value="left"/>
<c:if test="${!empty propertyValues_align2[0]}"><c:set var="align" value="${propertyValues_align2[0]}"/></c:if>
<c:set var="valign" value="top"/>
<c:if test="${!empty propertyValues_valign2[0]}"><c:set var="valign" value="${propertyValues_valign2[0]}"/></c:if>
<td align="${align}" valign="${valign}" <c:if test="${!empty property_width2}"> width="${property_width2}" </c:if> >
<ssf:displayConfiguration 
  configDefinition="${ssConfigDefinition}" 
  configElement="<%= tdItem2 %>" 
  configJspStyle="${ssConfigJspStyle}" 
  entry="${ssDefinitionEntry}"
  processThisItem="true" />
</td>
<%
				} else {
%>
<c:set var="align" value="left"/>
<c:if test="${!empty propertyValues_align2[0]}"><c:set var="align" value="${propertyValues_align2[0]}"/></c:if>
<c:set var="valign" value="top"/>
<c:if test="${!empty propertyValues_valign2[0]}"><c:set var="valign" value="${propertyValues_valign2[0]}"/></c:if>
<td align="${align}" valign="${valign}" <c:if test="${!empty property_width2}"> width="${property_width2}" </c:if> >&nbsp;</td>
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

