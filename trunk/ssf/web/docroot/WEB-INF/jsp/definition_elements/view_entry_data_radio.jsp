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
<%@ page import="com.sitescape.team.web.util.DefinitionHelper" %>
<%
	String caption = "";
	if(ssDefinitionEntry.getCustomAttributes().get(property_name) != null) {
		caption = DefinitionHelper.findCaptionForValue(ssConfigDefinition, item,
					(String) ((CustomAttribute) ssDefinitionEntry.getCustomAttributes().get(property_name)).getValue());
	}
%>
<c:set var="caption" value="<%= caption %>"/>

<% //Radio view %>
<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
<span class="ss_labelRight"><c:out value="${property_caption}" /></span>
<c:out value="${caption}" escapeXml="false"/>
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
	  <c:out value="${caption}" escapeXml="false"/>
	</span>
  </td>
  </tr>
</c:if>
