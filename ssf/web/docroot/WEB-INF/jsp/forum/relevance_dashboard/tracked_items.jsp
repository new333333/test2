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
      
	<ssf:canvas id="relevanceFolders" type="inline" styleId="ss_trackedItems">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title" class="ss_pt_title ss_blue ss_tracked_img"> <ssf:nlt tag="relevance.trackedFolders"/> 
	</div>
	</ssf:param>
	
<div id="ss_para" "ss_link_5">
<div id="ss_hints"><em>These are my folders I have chosen to track. To track a folder  you must click on the "TRACK:" button above. </em>  </div>
<c:forEach var="binder" items="${ss_trackedBinders}">
  <c:if test="${binder.entityType == 'folder'}">
	
	    <table cellpadding="0" cellspacing="0"><tbody><tr><td>
	    <a class="ss_link_5"
	      href="<ssf:url adapter="true" portletName="ss_forum" 
	    	action="view_permalink"
	    	binderId="${binder.id}">
	    	<ssf:param name="entityType" value="folder" />
	    	<ssf:param name="newTab" value="1" />
			</ssf:url>"
		  onClick="return ss_gotoPermalink('${binder.id}', '${binder.id}', 'folder', '${ss_namespace}', 'yes');"
	    ><span>${binder.title} (${binder.parentBinder.title})</span> </a>
	    <c:forEach var="calendar" items="${ss_trackedCalendars}">
	      <c:if test="${calendar.id == binder.id}"> <span class="ss_fineprint">(<ssf:nlt tag="relevance.trackedCalendar"/>)</span></c:if>
	    </c:forEach>
	    &nbsp;<img src="<html:rootPath/>images/icons/folder_green_sm.png" alt="folder" width="11" height="10" hspace="2" border="0" align="absmiddle" />
	    <img style="padding:4px 0px 0px 2px;" align="texttop"
      src="<html:rootPath/>images/pics/delete.gif"
      onClick="ss_trackedItemsDelete(this, '${binder.id}');"/>
	      </td></tr></tbody></table>
	
  </c:if>
</c:forEach>
</div> <!-- end of ss_para -->
	</ssf:canvas>

        </div><!-- end of ss_col 1 -->
      <div id="ss_col2" class="ss_col2">

	<ssf:canvas id="relevanceWorkspaces" type="inline" styleId="ss_trackedItems">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title" class="ss_pt_title ss_blue ss_tracked_img"> <ssf:nlt tag="relevance.trackedWorkspaces"/> </div>
	</ssf:param>
	
<div id="ss_para" class="ss_paraC">
<div id="ss_hints"><em>These are my Global and Team Workspaces I have chosen to track. To track a workspace you must click on the "TRACK:" button above while visiting your favorite workspaces. </em>  </div>
<c:forEach var="binder" items="${ss_trackedBinders}">
  <c:if test="${binder.entityType == 'workspace' && binder.definitionType == 8}">
	    <table cellpadding="0" cellspacing="0"><tbody><tr><td>
	    <a class="ss_link_5"
	      href="<ssf:url adapter="true" portletName="ss_forum" 
	    	action="view_permalink"
	    	binderId="${binder.id}">
	    	<ssf:param name="entityType" value="workspace" />
	    	<ssf:param name="newTab" value="1" />
			</ssf:url>"
		  onClick="return ss_gotoPermalink('${binder.id}', '${binder.id}', 'workspace', '${ss_namespace}', 'yes');"
	    ><span>${binder.title} (${binder.parentBinder.title})</span> </a><img src="<html:rootPath/>images/icons/folder_cyan_sm.png" alt="folder" width="11" height="10" hspace="2" border="0" align="absmiddle" />&nbsp;
	    <img style="padding:4px 0px 0px 2px;" align="texttop" src="<html:rootPath/>images/pics/delete.gif"
      onClick="ss_trackedItemsDelete(this, '${binder.id}');"/>
      </td></tr></tbody></table>
  </c:if>
</c:forEach>
	</div> <!-- end of ss_para -->

	</ssf:canvas>

      </div><!-- end of col2 -->
      <div id="ss_col3" class="ss_col3">

	<ssf:canvas id="relevancePeople" type="inline" styleId="ss_trackedPeople">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title" class="ss_pt_title ss_blue ss_tracked_img"> <ssf:nlt tag="relevance.trackedPeople"/> 
		</div>
	</ssf:param>
	
	  <div id="ss_today">
	  <div id="trackedPeople">
	  <div id="ss_hints"><em>These are my Peeps I have chosen to track. To track a person you must click on the "TRACK:" button above while visitng their profile page. </em>  </div>
	  <c:set var="ss_show_tracked_item_delete_button" value="true" scope="request"/>
	  <jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/my_buddies.jsp" />
	  </div><!-- end of trackedPeople -->
	  </div><!-- end of ss_today -->
	 
	</ssf:canvas>

      </div><!-- end of col3 -->
    </div><!-- end of col left -->
  </div><!-- end of col mid -->
</div><!-- end of content -->
<div class="ss_clear_float"></div>
