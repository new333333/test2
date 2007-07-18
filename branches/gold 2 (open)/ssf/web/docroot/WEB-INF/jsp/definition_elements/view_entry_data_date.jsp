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
<% //Date view %>
<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
 <span class="ss_labelLeft"><c:out value="${property_caption}" /></span>
 <span>
 <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				      value="${ssDefinitionEntry.customAttributes[property_name].value}" type="date" 
					  pattern="dd MMM yyyy" />

</span>
</div>
</c:if>

<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
    <c:out value="${property_caption}" />
  </td>
  <td valign="top">
	<span class="ss_bold">
	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				      value="${ssDefinitionEntry.customAttributes[property_name].value}" type="date" 
					  pattern="dd MMM yyyy" />
	</span>
  </td>
</tr>
</c:if>
