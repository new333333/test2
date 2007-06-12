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
<tr>
	<td><c:out value="${property_caption}" />:</td>
	<td>
		<ul class="ss_nobullet">
		<c:forEach var="principal" items="<%= com.sitescape.team.util.ResolveIds.getPrincipals((CustomAttribute) ssDefinitionEntry.getCustomAttribute(property_name)) %>" >
			<li><ssf:showUser user="${principal}" /></li>
		</c:forEach>
		</ul>
	</td>
</tr>