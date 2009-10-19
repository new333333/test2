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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("mobile.whatsNew") %>' scope="request"/>
<c:set var="ss_pageTitle2" value="mobile.whatsNewIn" />
<c:if test="${ss_whatsNewSite}">
  <c:set var="ss_pageTitle2" value="mobile.whatsNewSiteWide" />
</c:if>
<c:if test="${ss_type == 'unseen'}">
  <c:set var="ss_windowTitle" value='<%= NLT.get("mobile.whatsUnread") %>' scope="request"/>
  <c:set var="ss_pageTitle2" value="mobile.whatsUnreadIn" />
  <c:if test="${ss_whatsNewSite}">
    <c:set var="ss_pageTitle2" value="mobile.whatsUnreadSiteWide" />
  </c:if>
</c:if>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>

<c:set var="ss_pageTitle" value="${ss_windowTitle}" scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/masthead.jsp" %>

<div class="content">

<%@ include file="/WEB-INF/jsp/mobile/action_bar.jsp" %>

  <div class="folders">
    <div class="folder-content">
      <div class="folder-head">
	   <span>
	    <ssf:nlt tag="${ss_pageTitle2}">
	      <ssf:param name="value" useBody="true">
	        <a href="<ssf:url adapter="true" 
	        		portletName="ss_forum" 
					binderId="${ssBinder.id}" 
					action="__ajax_mobile" 
					operation="mobile_show_folder" 
					actionUrl="false" />"
			><c:out value="${ssBinder.title}" escapeXml="true"/></a>
	      </ssf:param>
	    </ssf:nlt>
	   </span>      
	  </div>


	  <div style="padding:10px 20px 14px 0px;" align="right">
		<c:if test="${ss_pageNumber > '0'}">
			<a href="<ssf:url 
			  action="__ajax_mobile" 
			  binderId="${ssBinder.id}"
			  operation="mobile_whats_new"><ssf:param
			  name="type" value="${ss_type}"/><ssf:param
			  name="pageNumber" value="${ss_pageNumber - 1}"/><ssf:param
			  name="namespace" value="${ss_namespace}"/></ssf:url>" 
			><img border="0" src="<html:rootPath/>images/pics/sym_arrow_left_.gif" 
			    title="<ssf:nlt tag="general.previousPage"/>"
			    <ssf:alt tag=""/> /></a>
		</c:if>
		<c:if test="${empty ss_pageNumber || ss_pageNumber <= '0'}">
		  <img src="<html:rootPath/>images/pics/sym_arrow_left_g.gif"
		  	<ssf:alt tag=""/> />
		</c:if>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<c:if test="${!empty ss_whatsNewBinder}">
		  <a href="<ssf:url 
			action="__ajax_mobile" 
			binderId="${ssBinder.id}"
			operation="mobile_whats_new"><ssf:param
			name="type" value="${ss_type}"/><ssf:param
			name="pageNumber" value="${ss_pageNumber + 1}"/><ssf:param
			name="namespace" value="${ss_namespace}"/></ssf:url>" 
		  ><img border="0" src="<html:rootPath/>images/pics/sym_arrow_right_.gif" 
			  title="<ssf:nlt tag="general.nextPage"/>"
			  <ssf:alt tag=""/> /></a>
		</c:if>
		<c:if test="${empty ss_whatsNewBinder}">
			<img src="<html:rootPath/>images/pics/sym_arrow_right_g.gif"
			<ssf:alt tag=""/> />
		</c:if>
	  </div>
  
		<c:if test="${empty ss_whatsNewBinder && ss_pageNumber > '0'}">
		  <span style="padding:10px;" class="ss_italic"><ssf:nlt tag="whatsnew.noMoreEntriesFound"/></span>
		</c:if>
		<c:if test="${empty ss_whatsNewBinder && (empty ss_pageNumber || ss_pageNumber <= '0')}">
		  <span style="padding:10px;" class="ss_italic"><ssf:nlt tag="whatsnew.noEntriesFound"/></span>
		</c:if>

        <c:forEach var="entryWn" items="${ss_whatsNewBinder}">
	      <jsp:useBean id="entryWn" type="java.util.Map" />
	      <div class="entry">
			<div class="entry-title">
			  <a href="<ssf:url adapter="true" portletName="ss_forum" 
				folderId="${entryWn._binderId}"  entryId="${entryWn._docId}"
				action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />"
			  >
			  	<span><c:if test="${empty entryWn.title}">--<ssf:nlt tag="entry.noTitle"/>--</c:if>
			  	  <ssf:makeWrapable><c:out value="${entryWn.title}" escapeXml="true"/></ssf:makeWrapable>
			  	</span>
			  </a>
			</div>
			
			<div class="entry-signature">
				 <span class="entry-author"><a href="<ssf:url adapter="true" portletName="ss_forum" 
				    action="__ajax_mobile"
				    operation="mobile_show_workspace"
				    binderId="${entryWn._principal.workspaceId}" />"
				 ><c:out value="${entryWn._principal.title}" escapeXml="true"/></a></span>
	
			  	 <span class="entry-date">
			    	<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
		          	value="${entryWn._modificationDate}" type="both" 
			      	timeStyle="short" dateStyle="medium" />
			  	 </span>
		   
		  	</div>
		  
		  	<div class="entry-type">
				<c:set var="path" value=""/>
				<c:if test="${!empty ss_whatsNewBinderFolders[entryWn._binderId]}">
				  <c:set var="path" value="${ss_whatsNewBinderFolders[entryWn._binderId]}"/>
				  <c:set var="title" value="${ss_whatsNewBinderFolders[entryWn._binderId].title} (${ss_whatsNewBinderFolders[entryWn._binderId].parentBinder.title})"/>
				</c:if>
				<c:set var="isDashboard" value="yes"/>
				<c:if test="${!empty path}">
	     			<a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${entryWn._binderId}" 
						action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />"
					 >
			  		 <span>${title}</span></a>
				</c:if>
			</div>
			
			  <c:if test="${!empty entryWn._desc}">
			    <div class="entry-content">
			    	<span style="border:#cecece solid 1px;"><ssf:textFormat 
			      	  formatAction="limitedDescription" 
			          textMaxWords="20"><ssf:markup search="${entryWn}">${entryWn._desc}</ssf:markup></ssf:textFormat>
			        </span>
			        <div class="ss_clear"></div>
		  	    </div>
			  </c:if>
		
	      </div>
        </c:forEach>
  
	  <div style="padding:14px 0px 10px 20px;">
		<c:if test="${ss_pageNumber > '0'}">
			<a href="<ssf:url 
			  action="__ajax_mobile" 
			  binderId="${ssBinder.id}"
			  operation="mobile_whats_new"><ssf:param
			  name="type" value="${ss_type}"/><ssf:param
			  name="pageNumber" value="${ss_pageNumber - 1}"/><ssf:param
			  name="namespace" value="${ss_namespace}"/></ssf:url>" 
			><img border="0" src="<html:imagesPath/>pics/sym_arrow_left_.gif" 
			    title="<ssf:nlt tag="general.previousPage"/>"
			    <ssf:alt tag=""/> /></a>
		</c:if>
		<c:if test="${empty ss_pageNumber || ss_pageNumber <= '0'}">
		  <img src="<html:imagesPath/>pics/sym_arrow_left_g.gif"
		  	<ssf:alt tag=""/> />
		</c:if>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<c:if test="${!empty ss_whatsNewBinder}">
		  <a href="<ssf:url 
			action="__ajax_mobile" 
			binderId="${ssBinder.id}"
			operation="mobile_whats_new"><ssf:param
			name="type" value="${ss_type}"/><ssf:param
			name="pageNumber" value="${ss_pageNumber + 1}"/><ssf:param
			name="namespace" value="${ss_namespace}"/></ssf:url>" 
		  ><img border="0" src="<html:imagesPath/>pics/sym_arrow_right_.gif" 
			  title="<ssf:nlt tag="general.nextPage"/>"
			  <ssf:alt tag=""/> /></a>
		</c:if>
		<c:if test="${empty ss_whatsNewBinder}">
			<img src="<html:imagesPath/>pics/sym_arrow_right_g.gif"
				<ssf:alt tag=""/> />
		</c:if>
	  </div>
    </div>
  </div>  

</div>
</body>
</html>
