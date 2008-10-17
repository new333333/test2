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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value="<%= NLT.get("toolbar.menu.whatsNew") %>" scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>
<div id="ss_para">
  <div style="padding-left:20px;padding-bottom:16px;">
	<c:if test="${ss_pageNumber > '0'}">
		<a href="<ssf:url 
		  action="__ajax_mobile" 
		  binderId="${ssBinder.id}"
		  operation="mobile_whats_new"><ssf:param
		  name="type" value="${ss_type}"/><ssf:param
		  name="pageNumber" value="${ss_pageNumber - 1}"/><ssf:param
		  name="namespace" value="${ss_namespace}"/></ssf:url>" 
		><img border="0" src="<html:imagesPath/>pics/sym_arrow_left_.gif" 
		    title="<ssf:nlt tag="general.previousPage"/>"/></a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${empty ss_pageNumber || ss_pageNumber <= '0'}">
	  <img src="<html:imagesPath/>pics/sym_arrow_left_g.gif"/>
	</c:if>
	<c:if test="${!empty ss_whatsNewBinder}">
	  <a href="<ssf:url 
		action="__ajax_mobile" 
		binderId="${ssBinder.id}"
		operation="mobile_whats_new"><ssf:param
		name="type" value="${ss_type}"/><ssf:param
		name="pageNumber" value="${ss_pageNumber + 1}"/><ssf:param
		name="namespace" value="${ss_namespace}"/></ssf:url>" 
	  ><img border="0" src="<html:imagesPath/>pics/sym_arrow_right_.gif" 
		  title="<ssf:nlt tag="general.nextPage"/>"/></a>
	</c:if>
	<c:if test="${empty ss_whatsNewBinder}">
		<img src="<html:imagesPath/>pics/sym_arrow_right_g.gif"/>
	</c:if>
  </div>
  
  <div >
		<c:if test="${empty ss_whatsNewBinder && ss_pageNumber > '0'}">
		  <span class="ss_italic"><ssf:nlt tag="whatsnew.noMoreEntriesFound"/></span>
		</c:if>
		<c:if test="${empty ss_whatsNewBinder && (empty ss_pageNumber || ss_pageNumber <= '0')}">
		  <span class="ss_italic"><ssf:nlt tag="whatsnew.noEntriesFound"/></span>
		</c:if>
	    <c:forEach var="entryWn" items="${ss_whatsNewBinder}">
    	<jsp:useBean id="entryWn" type="java.util.Map" />
    	<li>
		  <a href="<ssf:url adapter="true" portletName="ss_forum" 
			folderId="${entryWn._binderId}"  entryId="${entryWn._docId}"
			action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />"
		  >
		  	<span><c:if test="${empty entryWn.title}">--<ssf:nlt tag="entry.noTitle"/>--</c:if>${entryWn.title}</span>
		  </a>
	 
	  	 <br/>
	  	 <div style="padding-left:14px;">
		  	  <img src="<html:rootPath/>images/pics/sym_s_gray_dude.gif" 
		  	    width="11" height="10" hspace="2" border="0" align="absmiddle" />
			  <a href="<ssf:url adapter="true" portletName="ss_forum" 
			    action="__ajax_mobile"
			    operation="mobile_show_workspace"
			    binderId="${entryWn._principal.workspaceId}" />"
			  ><span class="ss_mobile_light ss_mobile_small"
			  >${entryWn._principal.title}</span></a>

		  	 <span>
		    	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
	          	value="${entryWn._modificationDate}" type="both" 
		      	timeStyle="short" dateStyle="medium" />
		  	 </span>
	   
	  	 </div>
	  
	  	 <div style="padding-left:14px;">
			<c:set var="path" value=""/>
			<c:if test="${!empty ss_whatsNewBinderFolders[entryWn._binderId]}">
			  <c:set var="path" value="${ss_whatsNewBinderFolders[entryWn._binderId]}"/>
			  <c:set var="title" value="${ss_whatsNewBinderFolders[entryWn._binderId].title} (${ss_whatsNewBinderFolders[entryWn._binderId].parentBinder.title})"/>
			</c:if>
			<c:set var="isDashboard" value="yes"/>
			<c:if test="${!empty path}">
		  	    <img src="<html:rootPath/>images/icons/folder_cyan_sm.png" 
		  	      width="11" height="10" hspace="2" border="0" align="absmiddle" />
     			<a href="<ssf:url adapter="true" portletName="ss_forum" 
					folderId="${entryWn._binderId}" 
					action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />"
				 >
		  		 <span>${title}</span></a>
			</c:if>
		</div>
		
		<c:if test="${!empty entryWn._desc}">
		  <div style="padding-left:14px;">
		    	<span style="border:#cecece solid 1px;"><ssf:textFormat 
		      	  formatAction="limitedDescription" 
		          textMaxWords="10"><ssf:markup search="${entryWn}">${entryWn._desc}</ssf:markup></ssf:textFormat>
		        </span>
	  	   </div>
		</c:if>
	
    	</li>

    	<br/>
   </c:forEach>
  </div>
  
  <div style="padding-left:20px;">
	<c:if test="${ss_pageNumber > '0'}">
		<a href="<ssf:url 
		  action="__ajax_mobile" 
		  binderId="${ssBinder.id}"
		  operation="mobile_whats_new"><ssf:param
		  name="type" value="${ss_type}"/><ssf:param
		  name="pageNumber" value="${ss_pageNumber - 1}"/><ssf:param
		  name="namespace" value="${ss_namespace}"/></ssf:url>" 
		><img border="0" src="<html:imagesPath/>pics/sym_arrow_left_.gif" 
		    title="<ssf:nlt tag="general.previousPage"/>"/></a>&nbsp;&nbsp;&nbsp;
	</c:if>
	<c:if test="${empty ss_pageNumber || ss_pageNumber <= '0'}">
	  <img src="<html:imagesPath/>pics/sym_arrow_left_g.gif"/>
	</c:if>
	<c:if test="${!empty ss_whatsNewBinder}">
	  <a href="<ssf:url 
		action="__ajax_mobile" 
		binderId="${ssBinder.id}"
		operation="mobile_whats_new"><ssf:param
		name="type" value="${ss_type}"/><ssf:param
		name="pageNumber" value="${ss_pageNumber + 1}"/><ssf:param
		name="namespace" value="${ss_namespace}"/></ssf:url>" 
	  ><img border="0" src="<html:imagesPath/>pics/sym_arrow_right_.gif" 
		  title="<ssf:nlt tag="general.nextPage"/>"/></a>
	</c:if>
	<c:if test="${empty ss_whatsNewBinder}">
		<img src="<html:imagesPath/>pics/sym_arrow_right_g.gif"/>
	</c:if>
  </div>

  <br/>
  <div class="ss_mobile_breadcrumbs ss_mobile_small">
  <c:if test="${ssBinder.entityIdentifier.entityType == 'folder'}">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
		action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false"
		binderId="${ssBinder.id}" />"
		><ssf:nlt tag="mobile.currentFolder"/></a>
  </c:if>
  <c:if test="${ssBinder.entityIdentifier.entityType == 'workspace'}">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
		action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false"
		binderId="${ssBinder.id}" />"
		><ssf:nlt tag="mobile.currentWorkspace"/></a>
  </c:if>
  <br/>
  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	action="__ajax_mobile" operation="mobile_show_front_page" actionUrl="false" />"
	><ssf:nlt tag="mobile.returnToTop"/></a>
  </div>
  
</div><!-- end of ss_para -->

