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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!ss_searchResultsPage}">
	<ssf:ifLoggedIn>
		<c:if test="${empty ssEntry && !empty ssBinder && ssBinder.entityType != 'profiles'}">
			<div id="ss_leftNav">
			  <ul>
				<li>
				   <a href="javascript: ;" 
	  				onClick="ss_trackThisBinder('${ssBinder.id}', '${renderResponse.namespace}');return false;"
				 	<c:if test="${ssBinder.entityType == 'workspace'}">
	  			 		<c:if test="${ssBinder.definitionType != 12}">
	  						title="<ssf:nlt tag="relevance.trackThisWorkspace"/>" >
	  						<span><ssf:nlt tag="relevance.justTrack"/></span>
	  					</c:if>
	  			 		<c:if test="${ssBinder.definitionType == 12}">
	  						title="<ssf:nlt tag="relevance.trackThisPerson"/>" >
	  						<span><ssf:nlt tag="relevance.justTrack"/></span>
	  					</c:if>
				 	</c:if>
				 	<c:if test="${ssBinder.entityType == 'folder'}">
	  			 		<c:if test="${ssDefinitionFamily != 'calendar'}">
	  						title="<ssf:nlt tag="relevance.trackThisFolder"/>" >
	  						<span><ssf:nlt tag="relevance.justTrack"/></span>
	  					</c:if>
	  			 		<c:if test="${ssDefinitionFamily == 'calendar'}">
	  						title="<ssf:nlt tag="relevance.trackThisCalendar"/>" >
	  						<span><ssf:nlt tag="relevance.justTrack"/></span>
	  					</c:if>
				 	</c:if>
				  </a>
				
				  <div id="ss_track_this_ok${renderResponse.namespace}" 
	  				style="position:relative; display:none; visibility:hidden; top:5px; left:10px; z-index:500;
	         		border:1px solid black; padding-top:10px; padding-left: 10px; padding-bottom: 10px; padding-right: 10px; background-color:#ffffff; white-space:nowrap; margin-bottom:10px;">
				  </div>
				</li>
			  </ul>
			</div>
		</c:if>
	</ssf:ifLoggedIn>
</c:if>
