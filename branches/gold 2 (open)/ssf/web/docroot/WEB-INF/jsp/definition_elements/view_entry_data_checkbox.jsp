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
<% //checkbox view %>
<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
<c:if test="${ssConfigJspStyle != 'mail'}">
<c:if test="${ssDefinitionEntry.customAttributes[property_name].value}" >
<input type="checkbox" checked DISABLED>
</c:if>
<c:if test="${!ssDefinitionEntry.customAttributes[property_name].value}" >
<input type="checkbox" DISABLED>
</c:if>
<span class="ss_labelRight"><c:out value="${property_caption}" /></span>
</c:if>

<c:if test="${ssConfigJspStyle == 'mail'}">
<c:if test="${ssDefinitionEntry.customAttributes[property_name].value}" >
<ssf:nlt tag="(checked)"/>
</c:if>
<c:if test="${!ssDefinitionEntry.customAttributes[property_name].value}" >
<ssf:nlt tag="(notchecked)"/>
</c:if>
<span class="ss_labelRight"><c:out value="${property_caption}" /></span>
</c:if>
</div>
</c:if>

<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
    <c:out value="${property_caption}" />
  </td>
  <td valign="top">
<c:if test="${ssConfigJspStyle != 'mail'}">
	<c:if test="${ssDefinitionEntry.customAttributes[property_name].value}" >
	<input type="checkbox" checked DISABLED>
	</c:if>
	<c:if test="${!ssDefinitionEntry.customAttributes[property_name].value}" >
	<input type="checkbox" DISABLED>
	</c:if>
</c:if>
<c:if test="${ssConfigJspStyle == 'mail'}">
	<c:if test="${ssDefinitionEntry.customAttributes[property_name].value}" >
	<ssf:nlt tag="(checked)"/>
	</c:if>
	<c:if test="${!ssDefinitionEntry.customAttributes[property_name].value}" >
	<ssf:nlt tag="(notchecked)"/>
	</c:if>
</c:if>
  </td>
</tr>
</c:if>
