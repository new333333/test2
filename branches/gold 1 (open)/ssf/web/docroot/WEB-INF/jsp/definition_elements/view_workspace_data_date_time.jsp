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
<tr>
  <td class="ss_table_spacer_right"><c:out value="${property_caption}" />:</td>
  <td valign="top">
	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				      value="${ssDefinitionEntry.customAttributes[property_name].value}" 
				      type="both" dateStyle="medium" timeStyle="short" />
  </td>
</tr>
