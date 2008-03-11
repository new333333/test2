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
		<div id="ss_title" class="ss_pt_title"><ssf:nlt tag="relevance.documents"/></div>
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
		<div id="ss_title" class="ss_pt_title"> <ssf:nlt tag="relevance.email"/> </div>
	</ssf:param>
		
      
        <div id="ss_today">
       <div id="ss_hints"><em>This is my email for today </em>  </div>
         <img src="<html:rootPath/>images/pics/mailicon.png" alt="email" width="16" height="12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Andy Fox</a> RE: <a href="#" class="ss_link_2">My Itinerary</a> 9:35 AM<br>
         <div style="#ss_email">Well, first I'd plug in the info from underneath the laptop into the warranty lookup on this page...</div> 
        <img src="<html:rootPath/>images/pics/mailicon.png" alt="email" width="16" height="12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Bill Bliss</a> RE: <a href="#" class="ss_link_2">Quality Control Guidelines</a> 7:20 AM<br>
        <div> The project is going great so far...</div>
        <img src="<html:rootPath/>images/pics/mailicon.png" alt="email" width="16" height="12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Gerry Kimball</a> RE: <a href="#" class="ss_link_2">Rental space</a> Fri Mar 7, 5:20 AM<br>
        <div> Well, first I'd plug in the info from underneath the laptop into the warranty lookup on this page: </div>
        <img src="<html:rootPath/>images/pics/mailicon.png" alt="email" width="16" height="12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Bill Bliss</a> RE: <a href="#" class="ss_link_2">Ensemble Practice</a> Thu Mar 6, 12:10 PM<br>
        <div>Well, first I'd plug in the info from underneath the laptop into the warranty lookup on this page: </div>
          </div><!-- end of today -->
         
          
		
	</ssf:canvas>
	
	<ssf:canvas id="relevanceEmail" type="inline" styleId="ss_bookmarks">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title" class="ss_pt_title"> <ssf:nlt tag="relevance.bookmarks"/> </div>
	</ssf:param>
		
      
        <div id="ss_today">
       <div id="ss_hints"><em>These are my bookmarks. </em>  </div>
        <a href="#" class="ss_link_2">My Itinerary</a> <br>
        <a href="#" class="ss_link_1">Peter Hurley</a> <a href="#" class="ss_link_2">Engineering//New Version Outline</a> <br>
        <a href="#" class="ss_link_1">Gerry Kimball</a> <a href="#" class="ss_link_2">Marketing//Agenda</a> <br>
        <a href="#" class="ss_link_1">Bill Bliss</a> <a href="#" class="ss_link_2">Blog Folder//Ensemble Music</a> <br>
          </div><!-- end of today -->
         
          
		
	</ssf:canvas>
	
		<ssf:canvas id="relevanceEmail" type="inline" styleId="ss_notes">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title" class="ss_pt_title"> <ssf:nlt tag="relevance.notes"/> </div>
	</ssf:param>
		
      
        <div id="ss_today">
       <div id="ss_hints"><em>These are my notes. </em>  </div>
        Need to bring foils to lunch meeting to finalize Keynot Address.  Have UPS ship demo brochures to arrive by Friday.  Make sure to add Tom, Dick, and Harry to talking points re:  design phase<br>
          </div><!-- end of today -->
         
          
		
	</ssf:canvas>

        </div><!-- end of ss_col 1 -->
        
      <div id="ss_col2" class="ss_col2">
	<ssf:canvas id="relevanceTasks" type="inline" styleId="ss_tasks">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title" class="ss_pt_title"><ssf:nlt tag="relevance.tasks"/></div>
	</ssf:param>
	
		<jsp:include page="/WEB-INF/jsp/forum/relevance_dashboard/my_tasks.jsp" />
	</ssf:canvas>
	
	<ssf:canvas id="relevanceEmail" type="inline" styleId="ss_calendar">
	<ssf:param name="title" useBody="true" >
		<div id="ss_title" class="ss_pt_title"> <ssf:nlt tag="relevance.calendar"/> </div>
	</ssf:param>
		
      
        <div id="ss_today">
       <div id="ss_hints"><em>This is my calendar. </em>  </div>
       <div id="#ss_calendar" 
        <li>Thu March 6, 9:00-10:00 am<br/>
        Marketing//Development<br/>
        <a href="#" class="ss_link_2">All Hands Meeting</a> </li><br>
        <li>Mon March 10, 1:00-3:00 pm<br/>
        Engineering//Wiki<br/>
        <a href="#" class="ss_link_2">Software Development</a> </li><br>
        <li>Tue March 11, 10:00-11:00 am<br/>
        Marketing//Blog<br/>
        <a href="#" class="ss_link_2">Coffee with Vendors</a> </li><br>
        <li>Tue March 11, 4:00-5:00 pm<br/>
        Finance<br/>
        <a href="#" class="ss_link_2">All Hands Meeting</a> </li><br>
        <li>Fri March 14, 6:00-10:00 am<br/>
        Software//Expo<br/>
        <a href="#" class="ss_link_2">Flight to Salt Lake</a> </li><br>
        </div><!-- end of para -->
          </div><!-- end of today -->
         
          
		
	</ssf:canvas>
      </div><!-- end of col2 -->
      
      <div id="ss_col3" class="ss_col3">
      
<ssf:canvas id="relevanceWorkspaces" type="inline" styleId="ss_trackedItems">
	
	<ssf:param name="title" useBody="true" >
		<div id="ss_title" class="ss_pt_title"><ssf:nlt tag="relevance.ping"/></div>
	</ssf:param>
	<div id="ss_hints"><em>The cool dude is here and I am happy with this are you?</em></div>
	<img src="<html:rootPath/>images/pics/sym_s_gray_dude.gif" alt="email" width="12" height="12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Robbin Smart</a><br/>
	<img src="<html:rootPath/>images/pics/sym_s_green_dude.gif" alt="email" width="12" height=12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Lisa Fadden</a><br/>
	<img src="<html:rootPath/>images/pics/sym_s_gray_dude.gif" alt="email" width="12" height="12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Bill Bliss</a><br/>
	<img src="<html:rootPath/>images/pics/sym_s_gray_dude.gif" alt="email" width="12" height="12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Peter Hurley</a><br/>
	<img src="<html:rootPath/>images/pics/sym_s_green_dude.gif" alt="email" width="12" height="12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Andy Fox</a><br/>
	<img src="<html:rootPath/>images/pics/sym_s_yellow_dude.gif" alt="email" width="12" height="12" hspace="2" border="0" />&nbsp;<a href="#" class="ss_link_1">Brent McConnell</a><br/>
	</div>

	</ssf:canvas>
      </div><!-- end of col3 -->
    </div><!-- end of col left -->
  </div><!-- end of col mid -->
</div><!-- end of content -->
<div class="ss_clear_float"></div>
