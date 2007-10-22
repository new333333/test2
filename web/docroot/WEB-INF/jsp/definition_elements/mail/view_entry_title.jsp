<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% //Title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_entryContent">
<span class="ss_entryTitle">
<table border="0" width="100%">
<tr><td align="left">
<c:if test="${!empty ssDefinitionEntry.docNumber}">
  <c:out value="${ssDefinitionEntry.docNumber}"/>.
</c:if>
  <a href="<ssf:url 
  		adapter="true" 
    	portletName="ss_forum" 
   		action="view_permalink"
		binderId="${ssDefinitionEntry.parentBinder.id}"
		entryId="${ssDefinitionEntry.id}">
		<ssf:param name="entityType" value="${ssDefinitionEntry.entityType}" />
    	<ssf:param name="newTab" value="1"/>
 	  	</ssf:url>">
<c:if test="${empty ssDefinitionEntry.title}">
  <span class="ss_light">--<ssf:nlt tag="entry.noTitle"/>--</span>
</c:if><c:out value="${ssDefinitionEntry.title}"/></a></span>
</td><td align="right">
<c:if test="${!empty ssDefinitionEntry.parentBinder.posting.emailAddress}">
&nbsp;&nbsp;<a href="mailto:${ssDefinitionEntry.parentBinder.posting.emailAddress}?subject=RE: DocId:${ssDefinitionEntry.parentBinder.id}:${ssDefinitionEntry.id}"
		style="font-size: 11px; color: #3366cc; font-weight:bold;
border-top: 1px solid #d5d5d5; border-left:
  1px solid #d5d5d5;
border-right: 1px solid #666666;
  border-bottom: 1px solid #666666;
background-color:
  #e5e5e5; padding: 3px;
font-family: arial, helvetica,
  sans-serif;
margin-left: 0px; margin-right: 6px; margin-bottom,
  margin-top: 2px;
line-height: 200%; text-decoration:
  none;" ><ssf:nlt tag="mail.reply"/></a>
</c:if>
 	  	
</td>
</tr>
</table>

</div>