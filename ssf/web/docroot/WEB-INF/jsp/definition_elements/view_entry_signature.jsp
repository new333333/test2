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
<% //Entry signature view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<table cellspacing="0" cellpadding="0">
 <tr>
  <td valign="top" style="padding-left:10px;">
    <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_creator.jsp" %>
  </td>
  <td valign="top" style="padding-left:15px;">
    <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_date.jsp" %>
  </td>
 </tr>
</table>

<c:if test="${!empty ssDefinitionEntry.modification.principal && 
  ssDefinitionEntry.modification.date > ssDefinitionEntry.creation.date}">
<table cellspacing="0" cellpadding="0">
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
	    binderId="${ssDefinitionEntry.creation.principal.parentBinder.id}"
	    entryId="${ssDefinitionEntry.creation.principal.id}">
	    <ssf:param name="entityType" value="workspace" />
	    <ssf:param name="newTab" value="1" />
		</ssf:url>"><c:out value="${ssDefinitionEntry.creation.principal.title}"/></a>
</c:if>
	</div>
  </td>
  <td valign="top" style="padding-left:15px;">
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
	<table cellspacing="0" cellpadding="0">
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