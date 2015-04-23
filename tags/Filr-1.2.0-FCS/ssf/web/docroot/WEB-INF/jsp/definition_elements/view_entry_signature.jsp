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
<c:choose>
  <c:when test="${ss_parentFolderViewStyle == 'wiki' && ssDefinitionEntry.parentEntry == null}">
    <c:if test="${!empty ssConfigDefinition}">
	  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	    configElement="${item}" 
	    configJspStyle="${ssConfigJspStyle}" />
	</c:if>
  </c:when>
  
  <c:otherwise>
<div class="ss_clipped_signature">
<c:if test="${empty propertyValues_displayType || propertyValues_displayType[0] == 'inline'}">
  <c:if test="${!ss_hideEntrySignature}">
	<table cellspacing="0" cellpadding="0" class="margintop1" width="100%">
		<tr>
			<td>
				<span class="ss_entryContent ss_entrySignature"><ssf:nlt tag="entry.createdBy"/></span>
	  		</td>
			<td class="ss_non_clipped_signature">
				<%@ include file="/WEB-INF/jsp/definition_elements/view_entry_creator.jsp" %>
			</td>
			<td width="100%">
			  <c:set var="property_caption" value=""/>
				<%@ include file="/WEB-INF/jsp/definition_elements/view_entry_date.jsp" %>
			</td>
		</tr>

    <c:if test="${!empty ssDefinitionEntry.modification.principal && 
      ssDefinitionEntry.modification.date > ssDefinitionEntry.creation.date}">
	   <tr>
	    <td>
		  <div class="ss_entryContent ss_entrySignature" ><ssf:nlt tag="entry.modified"/></div>
	    </td>
	    <td class="ss_non_clipped_signature">
		  <div class="ss_entryContent ss_entrySignatureUser ss_wrap"><ssf:showUser user="${ssDefinitionEntry.modification.principal}" showHint="true"/></div>
	    </td>
	    <td>
		  <div class="ss_entryContent ss_entryDate ss_wrap">
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
		  <div class="ss_entryContent ss_entrySignature ss_wrap margintop3">
		    <span style="padding-right:5px;">
		  	  <img style="margin-right: 5px;" <ssf:alt tag="alt.locked"/> align="absmiddle" 
		  	    src="<html:imagesPath/>pics/sym_s_caution.gif"/><ssf:nlt tag="entry.reservedBy"/></span>
		  </div>
	    </td>
	    <td class="ss_non_clipped_signature">
		  <div class="ss_entryContent margintop3">
		    <div class="ss_entrySignatureUser ss_wrap">
		      <ssf:showUser user="${ssDefinitionEntry.reservation.principal}" showHint="true"/>
		    </div>
		  </div>
	    </td>
	    <td>
	    </td>
	   </tr>
    </c:if>
    </table>
  </c:if>

  <c:if test="${!empty ssConfigDefinition}">
  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
    configElement="${item}" 
    configJspStyle="${ssConfigJspStyle}" />
  </c:if>
</c:if>

<c:if test="${propertyValues_displayType[0] == 'leftAligned'}">
<table cellspacing="0" cellpadding="0" width="100%">
<tr>
<td class="ss_non_clipped_signature <c:if test="${!ssDefinitionEntry.top}">ss_replies_indent_picture" </c:if>" >
<c:if test="${!ss_hideEntrySignature}">
  <div>
  <c:out value="${property_caption}" />
  <c:if test="${property_showPicture}">
	<a href="<ssf:url action="view_ws_listing"><ssf:param name="binderId" 
		value="${ssDefinitionEntry.creation.principal.parentBinder.id}"/><ssf:param name="entryId" 
		value="${ssDefinitionEntry.creation.principal.id}"/>
    	<ssf:param name="newTab" value="1" />
		</ssf:url>" <ssf:title tag="title.goto.profile.page" />
	  onClick="ss_openUrlInPortlet(this.href);return false;">
		<ssf:buddyPhoto style="ss_thumbnail_standalone ss_thumbnail_standalone_signature" 
			user="${ssDefinitionEntry.creation.principal}" 
			folderId="${ssDefinitionEntry.creation.principal.parentBinder.id}" 
			entryId="${ssDefinitionEntry.creation.principal.id}" />
	</a>
  </c:if>
		<table cellspacing="0" cellpadding="0" class="margintop1" width="100%">
		 <tr>
		  <td class="ss_non_clipped_signature">
			<%@ include file="/WEB-INF/jsp/definition_elements/view_entry_creator.jsp" %>
		  </td>
		 </tr>
		 <tr>
		  <td style="padding-left: 10px;">
		  <c:set var="property_caption" value=""/>
			<%@ include file="/WEB-INF/jsp/definition_elements/view_entry_date.jsp" %>
		  </td>
		 </tr>
		  <c:if test="${!empty ssDefinitionEntry.modification.principal && 
			ssDefinitionEntry.modification.date > ssDefinitionEntry.creation.date}">
		   <tr>
			<td class="ss_non_clipped_signature">
				<div class="ss_entryContent margintop2">
				  <div style="padding-right:8px; font-weight: bold; font-size: 11px;"><ssf:nlt tag="entry.modified"/></div>
				  <div class="ss_entrySignatureUser ss_wrap">
				    <ssf:showUser user="${ssDefinitionEntry.modification.principal}" showHint="true"/>
				  </div>
				</div>
				<div class="ss_entryContent ss_entryDate ss_wrap" style="padding-left: 19px;">
				<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
					 value="${ssDefinitionEntry.modification.date}" type="both" 
					 timeStyle="short" dateStyle="medium" />
				</div>
			</td>
		   </tr>
		  </c:if>
		
		  <c:if test="${!empty ssDefinitionEntry.reservation.principal}">
		   <tr>
			<td class="ss_non_clipped_signature">
				<div class="ss_entryContent margintop3">
				  <span style="padding-right:5px;">
				  	<img style="margin-right: 5px;" <ssf:alt tag="alt.locked"/> align="absmiddle" 
				  	  src="<html:imagesPath/>pics/sym_s_caution.gif"/><ssf:nlt tag="entry.reservedBy"/>
				  </span>
				  <div class="ss_entrySignatureUser ss_wrap">
				    <ssf:showUser user="${ssDefinitionEntry.reservation.principal}" showHint="true"/>
				  </div>
				</div>
			</td>
		   </tr>
		  </c:if>
		</table>

  </div>
</c:if>
</td>
<td style="padding-left:10px;">
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
<c:if test="${!ss_hideEntrySignature}">
<table cellspacing="0" cellpadding="0">
<tr>
	<td>
		<table cellspacing="0" cellpadding="0">
			<tr>
			  <td>
				<div class="ss_entrySignature">
				  <span><c:out value="${property_caption}" /></span>
				</div>
				<c:set var="property_caption" value=""/>
			  </td>
			  <td>
				  <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_creator.jsp" %>
			  </td>
			  <td nowrap>
				  <%@ include file="/WEB-INF/jsp/definition_elements/view_entry_date.jsp" %>
			  </td>
			 </tr>
			  <c:if test="${!empty ssDefinitionEntry.modification.principal && 
				ssDefinitionEntry.modification.date > ssDefinitionEntry.creation.date}">
			   <tr>
				<td>
				  <div class="ss_entrySignature">
					<span><ssf:nlt tag="entry.modified"/></span>
				  </div>
				</td>
				<td>
				  <div class="ss_entrySignatureUser ss_wrap">
					<ssf:showUser user="${ssDefinitionEntry.modification.principal}" showHint="true"/>
				  </div>
				</td>
				<td nowrap>
				  <div class="ss_entryDate ss_wrap">
					<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
					value="${ssDefinitionEntry.modification.date}" type="both" 
					timeStyle="short" dateStyle="medium" />
				  </div>
				</td>
			   </tr>
			  </c:if>

			  <c:if test="${!empty ssDefinitionEntry.reservation.principal}">
			   <tr>
				<td colspan="3">
				  <div class="ss_entrySignature ss_wrap margintop3">
					<img style="margin-right: 5px;" <ssf:alt tag="alt.locked"/> align="absmiddle" 
					  src="<html:imagesPath/>pics/sym_s_caution.gif"/><ssf:nlt tag="entry.reservedBy"/>
					<div class="ss_entrySignatureUser ss_wrap">
					  <ssf:showUser user="${ssDefinitionEntry.reservation.principal}" showHint="true"/>
					</div>
				  </div>
				</td>
			  </tr>
			</c:if>
		</table>
	</td>
	<td align="right" width="100%">
		  <div>
			<%@ include file="/WEB-INF/jsp/definition_elements/popular_view.jsp" %>
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
</div>
  </c:otherwise>
</c:choose>
