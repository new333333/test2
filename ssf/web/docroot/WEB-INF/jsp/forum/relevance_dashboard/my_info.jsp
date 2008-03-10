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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div id="ss_dashboard_content" class="ss_tricolumn">
  <div class="ss_colmid">
    <div class="ss_colleft">
      <div id="ss_col1" class="ss_col1">
      
	<ssf:canvas id="relevanceDocuments" type="inline" styleId="ss_documents">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title"><ssf:nlt tag="relevance.documents"/></div>
	</ssf:param>
		<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/my_docs.jsp" />
	</ssf:canvas>
	
	<ssf:canvas id="relevanceVisitedEntries" type="inline" styleId="ss_documents">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title"><ssf:nlt tag="relevance.visitedEntries"/></div>
	</ssf:param>
		<c:set var="ss_showRecentlyVisitedEntities" value="view" scope="request"/>
		<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/my_visited_entries.jsp" />
	</ssf:canvas>
	
	<ssf:canvas id="relevanceVisitedFiles" type="inline" styleId="ss_documents">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title"><ssf:nlt tag="relevance.visitedFiles"/></div>
	</ssf:param>
		<c:set var="ss_showRecentlyVisitedEntities" value="download" scope="request"/>
		<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/my_visited_entries.jsp" />
	</ssf:canvas>
	
	<ssf:canvas id="relevanceEmail" type="inline" styleId="ss_email">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title"> <ssf:nlt tag="relevance.email"/> </div>
	</ssf:param>
		
      
        <div id="ss_today">
       <div id="ss_hints"><em>This is my email for today </em>  </div>
         <img src="<html:rootPath/>images/pics/mailicon.png" alt="email" width="16" height="12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Andy Fox</a> RE: <a href="#" class="ss_link_2">My Itinerary</a><br>
        <img src="<html:rootPath/>images/pics/mailicon.png" alt="email" width="16" height="12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Bill Bliss</a> RE: <a href="#" class="ss_link_2">Quality Control Guidelines</a><br>
        <img src="<html:rootPath/>images/pics/mailicon.png" alt="email" width="16" height="12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Gerry Kimball</a> RE: <a href="#" class="ss_link_2">Rental space</a><br>
        <img src="<html:rootPath/>images/pics/mailicon.png" alt="email" width="16" height="12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Bill Bliss</a> RE: <a href="#" class="ss_link_2">Ensemble Practice</a><br>
          </div><!-- end of today -->
         
          
		
	</ssf:canvas>
	

        </div><!-- end of ss_col 1 -->
      <div id="ss_col2" class="ss_col2">
	<ssf:canvas id="relevanceTasks" type="inline" styleId="ss_tasks">
	<ssf:param name="title" value="<%= NLT.get("relevance.tasks") %>"/>
		<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/my_tasks.jsp" />
	</ssf:canvas>
      </div><!-- end of col2 -->
      <div id="ss_col3" class="ss_col3">

      </div><!-- end of col3 -->
    </div><!-- end of col left -->
  </div><!-- end of col mid -->
</div><!-- end of content -->
<div class="ss_clear_float"></div>
