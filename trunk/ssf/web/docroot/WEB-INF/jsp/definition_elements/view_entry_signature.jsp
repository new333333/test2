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
%><%--
--%><% //Entry signature view %><%--
--%><%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${empty propertyValues_displayType || propertyValues_displayType[0] == 'inline'}">
<table style="padding-left: 30px;" cellspacing="0" cellpadding="0">
 <tr>
  <td valign="bottom" style="padding-left:10px;">
    <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_creator.jsp" %>
  </td>
  <td valign="bottom" style="padding-left:15px;">
  <c:set var="property_caption" value=""/>
    <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_date.jsp" %>
  </td>
 </tr>
</table>

  <c:if test="${!empty ssDefinitionEntry.modification.principal && 
    ssDefinitionEntry.modification.date > ssDefinitionEntry.creation.date}">
	<table style="padding-left: 30px;" cellspacing="0" cellpadding="0">
	 <tr>
	  <td valign="bottom" style="padding-left:30px;">
		<div class="ss_entryContent ss_entrySignature">
		  <span style="padding-right:8px;"><ssf:nlt tag="entry.modifiedBy"/></span>
	
		  <ssf:showUser user="${ssDefinitionEntry.modification.principal}"/>
	
		</div>
	  </td>
	  <td valign="bottom" style="padding-left:15px;">
		<div class="ss_entryContent ss_entrySignature">
		<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
		     value="${ssDefinitionEntry.modification.date}" type="both" 
			 timeStyle="short" dateStyle="medium" />
		</div>
	  </td>
	 </tr>
	</table>
  </c:if>

  <c:if test="${!empty ssDefinitionEntry.reservation.principal}">
	<table style="padding-left: 30px;" cellspacing="0" cellpadding="0">
	 <tr>
	  <td valign="bottom" style="padding-left:30px;">
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
  <c:if test="${!empty ssConfigDefinition}">
  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
    configElement="${item}" 
    configJspStyle="${ssConfigJspStyle}" />
  </c:if>
</c:if>
<c:if test="${propertyValues_displayType[0] == 'leftAligned'}">
<table>
<tr>
<td valign="bottom">
  <c:if test="${property_showPicture && !empty ssDefinitionEntry.top}">
    <div 
      <c:if test="${!ssDefinitionEntry.top}">class="ss_replies_indent_picture" </c:if>
    >
  </c:if>
  <c:out value="${property_caption}" />
  <c:if test="${property_showPicture}">
	<a href="<ssf:url action="view_ws_listing"><ssf:param name="binderId" 
		value="${ssDefinitionEntry.creation.principal.parentBinder.id}"/><ssf:param name="entryId" 
		value="${ssDefinitionEntry.creation.principal.id}"/>
    	<ssf:param name="newTab" value="1" />
		</ssf:url>" <ssf:title tag="title.goto.profile.page" />
	  onClick="ss_openUrlInPortlet(this.href);return false;">
		<ssf:buddyPhoto style="ss_thumbnail_standalone ss_thumbnail_standalone_signature" 
			photos="${ssDefinitionEntry.creation.principal.customAttributes['picture'].value}" 
			folderId="${ssDefinitionEntry.creation.principal.parentBinder.id}" entryId="${ssDefinitionEntry.creation.principal.id}" />
	</a><br/>
  </c:if>
<table cellspacing="0" cellpadding="0">
 <tr>
  <td valign="bottom">
    <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_creator.jsp" %>
  </td>
 </tr>
 <tr>
  <td valign="bottom">
  <c:set var="property_caption" value=""/>
    <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_date.jsp" %>
  </td>
 </tr>
  <c:if test="${!empty ssDefinitionEntry.modification.principal && 
    ssDefinitionEntry.modification.date > ssDefinitionEntry.creation.date}">
   <tr>
    <td>
		<div class="ss_entryContent ss_entrySignature">
		  <span style="padding-right:8px;"><ssf:nlt tag="entry.modifiedBy"/></span><br/>
		  <ssf:showUser user="${ssDefinitionEntry.modification.principal}"/>
		</div>
		<div class="ss_entryContent ss_entrySignature">
		<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
		     value="${ssDefinitionEntry.modification.date}" type="both" 
			 timeStyle="short" dateStyle="medium" />
		</div>
	</td>
   </tr>
  </c:if>

  <c:if test="${!empty ssDefinitionEntry.reservation.principal}">
   <tr>
    <td>
		<div class="ss_entryContent ss_entrySignature">
		  <span style="padding-right:8px;">
		  <ssf:nlt tag="entry.reservedBy"/>&nbsp;<img <ssf:alt tag="alt.locked"/> 
		    src="<html:imagesPath/>pics/sym_s_caution.gif"/>
		  </span><br/>
		  <ssf:showUser user="${ssDefinitionEntry.reservation.principal}"/>
		</div>
	</td>
   </tr>
  </c:if>
</table>

  <c:if test="${property_showPicture}">
    </div>
  </c:if>
</td>
<td valign="bottom" style="padding-left:10px;">
<c:if test="${!empty ssConfigDefinition}">
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" />
</c:if>
</td>
</tr>
</table>
</c:if>

<c:if test="${propertyValues_displayType[0] == 'inlineWithRating'}">
<table cellspacing="0" cellpadding="0" width="100%">
<tr><td valign="top">
<table cellspacing="0" cellpadding="0">
<tr>
  <td valign="bottom">
    <div class="ss_entrySignature">
      <span><c:out value="${property_caption}" /></span>
    </div>
    <c:set var="property_caption" value=""/>
  </td>
  <td valign="bottom">
      <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_creator.jsp" %>
  </td>
  <td valign="bottom">
      <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_date.jsp" %>
  </td>
 </tr>
  <c:if test="${!empty ssDefinitionEntry.modification.principal && 
    ssDefinitionEntry.modification.date > ssDefinitionEntry.creation.date}">
   <tr>
    <td valign="bottom">
	  <div class="ss_entrySignature">
	    <span><ssf:nlt tag="entry.modifiedBy"/></span>
	  </div>
	</td>
	<td valign="bottom">
	  <div class="ss_entrySignature">
	    <ssf:showUser user="${ssDefinitionEntry.modification.principal}"/>
	  </div>
	</td>
	<td valign="bottom">
	  <div class="ss_entrySignature">
	    <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
		value="${ssDefinitionEntry.modification.date}" type="both" 
		timeStyle="short" dateStyle="medium" />
	  </div>
	</td>
   </tr>
  </c:if>

  <c:if test="${!empty ssDefinitionEntry.reservation.principal}">
   <tr>
    <td valign="bottom">
	  <div class="ss_entrySignature">
	    <ssf:nlt tag="entry.reservedBy"/>
	  </div>
	</td>
	<td valign="bottom">
	  <div class="ss_entrySignature">
	    <img <ssf:alt tag="alt.locked"/> 
	      src="<html:imagesPath/>pics/sym_s_caution.gif"/>
	    <ssf:showUser user="${ssDefinitionEntry.reservation.principal}"/>
	  </div>
	</td>
	<td valign="bottom">
	</td>
   </tr>
  </c:if>

</table>
</td>
<td valign="top" align="right">
  <div style="padding-top:8px;">
    <%@ include file="/WEB-INF/jsp/definition_elements/popular_view.jsp" %>
  </div>
</td>
</tr>
</table>

<c:if test="${!empty ssConfigDefinition}">
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" />
</c:if>
</c:if>

