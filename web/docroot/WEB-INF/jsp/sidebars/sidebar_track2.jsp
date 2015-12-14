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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${empty ss_trackThisLink}">
  <c:set var="ss_trackThisLink" value="0" scope="request"/>
</c:if>
<c:set var="ss_trackThisLink" value="${ss_trackThisLink + 1}" scope="request"/>
<c:if test="${!ss_searchResultsPage}">
	<ssf:ifLoggedIn>
		<c:if test="${(empty ssEntry || ssEntry.entityType != 'folderEntry') && 
			!empty ssBinder && ssBinder.entityType != 'profiles'}">
				   <div id="ss_track_this_anchor_div${renderResponse.namespace}${ss_trackThisLink}">
				   <a href="javascript: ;"   
	  				onclick="ss_trackThisBinder('${ssBinder.id}', '${renderResponse.namespace}${ss_trackThisLink}');return false;"
				 	<c:if test="${ssBinder.entityType == 'workspace'}">
	  			 		<c:if test="${ssBinder.definitionType != 12}">
	  						title="<%= NLT.get("relevance.trackedItems").replaceAll("\"", "&QUOT;") %>" >
	  						<span><ssf:nlt tag="relevance.trackThisWorkspace"/></span>
	  					</c:if>
	  			 		<c:if test="${ssBinder.definitionType == 12}">
	  						title="<%= NLT.get("relevance.trackedItems").replaceAll("\"", "&QUOT;") %>" >
	  						<span><ssf:nlt tag="relevance.trackThisPerson"/></span>
	  					</c:if>
				 	</c:if>
				 	<c:if test="${ssBinder.entityType == 'folder'}">
	  			 		<c:if test="${ssDefinitionFamily != 'calendar'}">
	  						title="<%= NLT.get("relevance.trackedItems").replaceAll("\"", "&QUOT;") %>" >
	  						<span><ssf:nlt tag="relevance.trackThisFolder"/></span>
	  					</c:if>
	  			 		<c:if test="${ssDefinitionFamily == 'calendar'}">
	  						title="<%= NLT.get("relevance.trackedItems").replaceAll("\"", "&QUOT;") %>" >
	  						<span><ssf:nlt tag="relevance.trackThisCalendar"/></span>
	  					</c:if>
				 	</c:if>
				  </a>
				  </div>
				
				  <div id="ss_track_this_ok${renderResponse.namespace}${ss_trackThisLink}" 
	  				style="position:absolute; display:none; visibility:hidden; z-index:500;
	         		border:1px solid black; padding:6px; background-color:#FFFFFF; white-space:nowrap;">
				  </div>
		</c:if>
	</ssf:ifLoggedIn>
</c:if>
