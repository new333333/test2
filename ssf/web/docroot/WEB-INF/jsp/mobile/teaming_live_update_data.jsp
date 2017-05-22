<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<jsp:useBean id="ssSeenMap" type="org.kablink.teaming.domain.SeenMap" scope="request" />
<c:set var="gwtUIActive" value="false" />
<% if (GwtUIHelper.isGwtUIActive(request)) { %>
	<c:set var="gwtUIActive" value="true" />
<% } %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="newTeamingEntries" value="0" />
	    <c:if test="${ss_whatsNewType != 'microblog'}">
	      <c:forEach var="entryWn" items="${ss_whatsNewBinder}">
	    	<jsp:useBean id="entryWn" type="java.util.Map" />
	    	<c:if test="${entryWn._modificationDate.time > ss_teaming_live_previous_update_date.time}">
	    	  <c:set var="newTeamingEntries" value="${newTeamingEntries + 1}" />
	    	</c:if>
	    	<div class="entry">
	    	  <div class="entry-title">
			    <% if (!ssSeenMap.checkIfSeen(entryWn)) { %>
					<span><img src="<html:rootPath/>images/pics/discussion/sunburst.png" 
					  	align="absmiddle" border="0" <ssf:alt tag="alt.unseen"/> /></span>
  				<% } %>
			    <a
			    	target="_blank"
			    	<c:if test="${gwtUIActive == 'true'}">
				    	href="<ssf:permalink entityId="${entryWn._docId}" entityType="folderEntry" />"
			    	</c:if>
			    	<c:if test="${gwtUIActive != 'true'}">
				    	href="<ssf:url adapter="true" portletName="ss_forum" 
					  		folderId="${entryWn._binderId}"  entryId="${entryWn._docId}"
					  		action="view_folder_listing" actionUrl="false" />"
				  	</c:if>
			    >
			  	  <span><c:if test="${empty entryWn.title}">--<ssf:nlt tag="entry.noTitle"/>--</c:if>
			  	    <ssf:makeWrapable><c:out value="${entryWn.title}" escapeXml="true"/></ssf:makeWrapable>
			  	  </span>
			    </a>
			  </div>
		 
		  	  <div class="entry-signature">
		  	    <span class="entry-author">
				  <a
				  		target="_blank"
				    	<c:if test="${gwtUIActive == 'true'}">
					  		href="<ssf:permalink entityId="${entryWn._principal.workspaceId}" entityType="workspace" />"
				    	</c:if>
				    	<c:if test="${gwtUIActive != 'true'}">
					  		href="<ssf:url adapter="true" portletName="ss_forum" 
					    		action="view_ws_listing"
					    		binderId="${entryWn._principal.workspaceId}" />"
				    	</c:if>
				  ><ssf:userTitle user="${entryWn._principal}"/></a>
				</span>
	
			  	<span class="entry-date">
			    	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
		          	value="${entryWn._modificationDate}" type="both" 
			      	timeStyle="short" dateStyle="medium" />
			  	</span>
		  	  </div>
		  
		  	  <div class="entry-type">
		  	    <span>
				<c:set var="path" value=""/>
				<c:if test="${!empty ss_whatsNewBinderFolders[entryWn._binderId]}">
				  <c:set var="path" value="${ss_whatsNewBinderFolders[entryWn._binderId]}"/>
				  <c:set var="title" value="${ss_whatsNewBinderFolders[entryWn._binderId].parentBinder.title} // ${ss_whatsNewBinderFolders[entryWn._binderId].title}"/>
				</c:if>
				<c:set var="isDashboard" value="yes"/>
				<c:if test="${!empty path}">
	     			<a
	     				target="_blank"
				    	<c:if test="${gwtUIActive == 'true'}">
		     				href="<ssf:permalink entityId="${entryWn._binderId}" entityType="folder" />"
				    	</c:if>
				    	<c:if test="${gwtUIActive != 'true'}">
		     				href="<ssf:url adapter="true" portletName="ss_forum" 
								folderId="${entryWn._binderId}" 
								action="view_folder_listing" actionUrl="false" />"
						</c:if>
					 >${title}</a>
				</c:if>
				</span>
			  </div>
			
			  <c:if test="${!empty entryWn._desc}">
			    <div class="entry-content">
			    	<ssf:textFormat 
			      	  formatAction="limitedDescription" 
			          textMaxWords="50"><ssf:markup search="${entryWn}">${entryWn._desc}</ssf:markup></ssf:textFormat>
		  	      <div class="ss_clear"></div>
		  	    </div>
			  </c:if>
		
	    	</div>
          </c:forEach>
        </c:if>
        <c:if test="${ss_whatsNewType == 'microblog'}">
	  		<c:forEach var="activity" items="${ss_activities}">
	    	  <c:if test="${activity.date.time > ss_teaming_live_previous_update_date.time}">
	    	    <c:set var="newTeamingEntries" value="${newTeamingEntries + 1}" />
	    	  </c:if>
	    	<div class="entry">
	    	  <div class="entry-title">
		    	<a
		    		target="_blank"
			    	<c:if test="${gwtUIActive == 'true'}">
			    		href="<ssf:permalink entityId="${activity.user.workspaceId}" entityType="workspace" />"
			    	</c:if>
			    	<c:if test="${gwtUIActive != 'true'}">
			    		href="<ssf:url adapter="true" portletName="ss_forum" 
							binderId="${activity.user.workspaceId}"
							action="view_ws_listing" />"
					</c:if>
				><ssf:userTitle user="${activity.user}"/></a>
		      </div>
		  	  <div>
			  	<span class="entry-date">
			    	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
					      value="${activity.date}" type="both" 
						  timeStyle="short" dateStyle="short" />
			  	</span>
		  	  </div>
		  
			  <c:if test="${!empty activity.description}">
			    <div class="entry-content">
			    	<span>${activity.description}</span>
		  	    </div>
			  </c:if>
			  </div>
	  		</c:forEach>
        </c:if>
<script type="text/javascript">
newItemsCount = ${newTeamingEntries};
</script>