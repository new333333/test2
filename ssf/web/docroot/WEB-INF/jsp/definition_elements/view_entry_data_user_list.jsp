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
<% //User_list view %>
<c:set var="userlist_entry" value="${ssDefinitionEntry}"/>
<jsp:useBean id="userlist_entry" type="com.sitescape.team.domain.Entry" />

<c:if test="${empty ss_element_display_style}">
<div class="ss_entryContent">
<span class="ss_labelLeft"><c:out value="${property_caption}" /></span>
<ul class="ss_nobullet">
<c:forEach var="selection" items="<%= com.sitescape.team.util.ResolveIds.getPrincipals(userlist_entry.getCustomAttribute(property_name)) %>" >
<li><ssf:showUser user="${selection}" /></li>
</c:forEach>
</ul>
</div>
</c:if>

<c:if test="${!empty ss_element_display_style && 
    ss_element_display_style == 'tableAlignLeft'}">
<tr>
  <td class="ss_table_spacer_right" valign="top" align="right">
    <span class="ss_light"><c:out value="${property_caption}" /></span>
  </td>
  <td valign="top" align="left">
	<ul class="ss_nobullet">
	<c:forEach var="selection" items="<%= com.sitescape.team.util.ResolveIds.getPrincipals(userlist_entry.getCustomAttribute(property_name)) %>" >
 	 <li><ssf:showUser user="${selection}" /></li>
	</c:forEach>
  </td>
</tr>
</c:if>








