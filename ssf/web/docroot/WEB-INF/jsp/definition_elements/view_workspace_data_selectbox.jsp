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
<% //Selectbox view %>
<%@ page import="com.sitescape.team.web.util.DefinitionHelper" %>
<%@ page import="com.sitescape.team.util.NLT" %>

<tr>
  <td><c:out value="${property_caption}" />:</td>
  <td class="ss_bold" valign="top">
	<ul class="ss_nobullet">
	<c:forEach var="selection" items="${ssDefinitionEntry.customAttributes[property_name].valueSet}" >
<%
	String caption = DefinitionHelper.findCaptionForValue(ssConfigDefinition, item,
											(String) pageContext.getAttribute("selection"));
	caption = NLT.getDef(caption);
%>
<c:set var="caption" value="<%= caption %>"/>
	<li><c:out value="${caption}" escapeXml="false"/></span></li>
	</c:forEach>
	</ul>
  </td>
</tr>
