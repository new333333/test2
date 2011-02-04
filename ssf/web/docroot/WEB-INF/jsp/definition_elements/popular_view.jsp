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
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${!ss_pseudoEntity}">
<c:if test="${!ss_popular_view_seen}">
<c:set var="ss_popular_view_seen" value="true" scope="request"/>
<c:if test="${empty ss_ratingSupportLoaded}">
<div id="ss_rating_info_div${renderResponse.namespace}" 
  style="position:absolute; display:none; visibility:hidden;
  border:1px dotted #666; padding:4px; background-color: #CCDFDE; font-family: Arial; font-size: 12px;">
<span><ssf:nlt tag="popularity.rating.register"/></span>:
<span id="ss_rating_info${renderResponse.namespace}"></span>
</div>
<c:set var="ss_ratingSupportLoaded" value="1" scope="request"/>
</c:if>
<c:set var="ss_ratingDivId" value="ss_rating_div_${renderResponse.namespace}${ssDefinitionEntry.id}" 
  scope="request"/>
<div align="right">
<table cellspacing="0" cellpadding="0">
<tr>
<c:choose>
  <c:when test="${ss_defFam == 'entry'}">
		<td class="ss_nowrap" valign="middle" colspan="2" align="right">  
		  <ssHelpSpot helpId="workspaces_folders/entries/rating" offsetX="-20" offsetY="-3" 
  			title="<ssf:nlt tag="helpSpot.rating"/>"></ssHelpSpot>
			<%@ include file="/WEB-INF/jsp/forum/rating.jsp" %>
		</td>
  </c:when>
  <c:otherwise>
		<td class="ss_nowrap" valign="middle" align="right">  
		 <ssHelpSpot helpId="workspaces_folders/entries/rating" offsetX="-20" offsetY="-3" 
  			title="<ssf:nlt tag="helpSpot.rating"/>"></ssHelpSpot>
			<%@ include file="/WEB-INF/jsp/forum/rating.jsp" %>
		</td>
  </c:otherwise>
</c:choose> 
 		<td class="ss_nowrap" valign="middle" align="right" > 
			<span style="padding-left: 10px;">
			<c:if test="${ssDefinitionEntry.top}">
			
			   <c:if test="${!empty ssDefinitionEntry.popularity}">
				<span class="ss_muted_label_small">
				  <c:if test="${ssDefinitionEntry.popularity == 1}">
					<ssf:nlt tag="popularity.visit1"/>
				  </c:if>
				  <c:if test="${ssDefinitionEntry.popularity > 1}">
					<b><ssf:nlt tag="popularity.visits"><ssf:param 
					  name="value" value="${ssDefinitionEntry.popularity}"/></ssf:nlt>
					 </b> 
				  </c:if>
				</span>
			  </c:if>
			  <c:if test="${empty ssDefinitionEntry.popularity}">
				<span class="ss_muted_label_small"><ssf:nlt tag="popularity.visits.none" /></span>
			  </c:if>
			
			</c:if>
			</span>	
			<span style="padding-left: 5px;">
				<c:if test="${!empty ssDefinitionEntry.totalReplyCount}">
				<span class="ss_muted_label_small">
				  <c:if test="${ssDefinitionEntry.totalReplyCount == 1}">
					<c:if test="${ssDefinitionFamily == 'discussion'}"><ssf:nlt tag="popularity.reply1"/></c:if>
					<c:if test="${ssDefinitionFamily != 'discussion'}"><ssf:nlt tag="popularity.comment1"/></c:if>
				  </c:if>
				  <c:if test="${ssDefinitionEntry.totalReplyCount > 1}">
					<c:if test="${ssDefinitionFamily == 'discussion'}"><ssf:nlt tag="popularity.replies"><ssf:param 
					  name="value" value="${ssDefinitionEntry.totalReplyCount}"/></ssf:nlt></c:if>
					<c:if test="${ssDefinitionFamily != 'discussion'}"><ssf:nlt tag="popularity.comments"><ssf:param 
					  name="value" value="${ssDefinitionEntry.totalReplyCount}"/></ssf:nlt></c:if>
				  </c:if>
				</span>
				</c:if>
				<c:if test="${ssDefinitionEntry.totalReplyCount == 0}">
				<c:if test="${ssDefinitionFamily == 'discussion'}"><span class="ss_muted_label_small"><ssf:nlt tag="popularity.replies.none" /></span></c:if>
				<c:if test="${ssDefinitionFamily != 'discussion'}"><span class="ss_muted_label_small"><ssf:nlt tag="popularity.comments.none" /></span></c:if>
				</c:if>
			</span>
		</td>
	</tr>
</table>
</div><!--end of alignment-->
</c:if>
</c:if>
