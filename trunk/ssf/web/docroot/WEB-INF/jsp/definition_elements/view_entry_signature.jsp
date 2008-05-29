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
%><%--
--%><% //Entry signature view %><%--
--%><%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<table style="padding-left: 30px;" cellspacing="0" cellpadding="0">
 <tr>
  <td valign="top" style="padding-left:10px;">
    <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_creator.jsp" %>
  </td>
  <td valign="top" style="padding-left:15px;">
  <c:set var="property_caption" value=""/>
    <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_date.jsp" %>
  </td>
 </tr>
</table>

<c:if test="${!empty ssDefinitionEntry.modification.principal && 
  ssDefinitionEntry.modification.date > ssDefinitionEntry.creation.date}">
<table style="padding-left: 30px;" cellspacing="0" cellpadding="0">
 <tr>
  <td valign="top" style="padding-left:30px;">
	<div class="ss_entryContent ss_entrySignature">
	  <span style="padding-right:8px;"><ssf:nlt tag="entry.modifiedBy"/></span>
<c:if test="${ssConfigJspStyle != 'mail'}">
	  <ssf:showUser user="${ssDefinitionEntry.modification.principal}"/>
</c:if>
<c:if test="${ssConfigJspStyle == 'mail'}">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    action="view_permalink"
	    binderId="${ssDefinitionEntry.modification.principal.parentBinder.id}"
	    entryId="${ssDefinitionEntry.modification.principal.id}">
	    <ssf:param name="entityType" value="workspace" />
	    <ssf:param name="newTab" value="1" />
		</ssf:url>"><c:out value="${ssDefinitionEntry.modification.principal.title}"/></a>
</c:if>
	</div>
  </td>
  <td valign="top" style="padding-left:15px;">
	<div class="ss_entryContent ss_entrySignature">
	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
	     value="${ssDefinitionEntry.modification.date}" type="both" 
		 timeStyle="medium" dateStyle="medium" />
	</div>
  </td>
 </tr>
</table>
</c:if>

<c:if test="${!empty ssDefinitionEntry.reservation.principal}">
	<table style="padding-left: 30px;" cellspacing="0" cellpadding="0">
	 <tr>
	  <td valign="top" style="padding-left:30px;">
		<div class="ss_entryContent ss_entrySignature">
		  <span style="padding-right:8px;">
		  <ssf:nlt tag="entry.reservedBy"/>&nbsp;<img <ssf:alt tag="alt.locked"/> 
		    src="<html:imagesPath/>pics/sym_s_caution.gif"/>
		  </span>
		  
		  <ssf:showUser user="${ssDefinitionEntry.reservation.principal}"/>
		</div>
	  </td>
	 </tr>
	</table>
</c:if>