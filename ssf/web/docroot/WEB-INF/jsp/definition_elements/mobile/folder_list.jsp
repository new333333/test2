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
<% //Folder list.  %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
  <c:if test="${!empty ssFolders}">
  <div class="folders">
    <div class="folder-head">
      <ssf:nlt tag="mobile.folders"/>
    </div>
	<c:forEach var="folder" items="${ssFolders}" >
      <div class="folder-item">
		<a href="<ssf:url adapter="true" portletName="ss_forum" 
				folderId="${folder.id}" 
				action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />">
		  <c:if test="${empty folder.title}">
		    (<ssf:nlt tag="folder.noTitle"/>)
		  </c:if>
		  <c:out value="${folder.title}" escapeXml="true"/>
		</a>
	  </div>
	</c:forEach>
	
  </div>
  </c:if>

  <div class="folders">
    <div class="folder-head">
       <ssf:nlt tag="mobile.entries"/>
    </div>
    

	<div class="folder-content" width="100%">
	  <div class="entry">
		<table cellspacing="0" cellpadding="0" width="95%">
		<tr>
		<td valign="middle" width="10%"> </td>
		<td valign="middle" width="80%" align="center" nowrap>
	 	<c:if test="${!empty ss_mobileBinderDefUrlList}">
	  	<div align="center">
	  	  <form name="addEntryForm" 
	  		action="<ssf:url adapter="true" portletName="ss_forum" 
				binderId="${ssBinder.id}" 
				action="__ajax_mobile" 
				operation="mobile_add_entry" 
				actionUrl="true" />" 
			method="post">
	  		<label for="url"><span style="display:none;"><ssf:nlt tag="label.selectOption"/></span></label>
	  		<select name="url" id="url" size="1">
	    	  <c:if test="${fn:length(ss_mobileBinderDefUrlList) == 1}">
		  	    <c:forEach var="def" items="${ss_mobileBinderDefUrlList}">
		    	  <option value="${def.url}"><ssf:nlt tag="button.add"/>: ${def.title}</option>
		  	    </c:forEach>
	    	  </c:if>
	    	  <c:if test="${fn:length(ss_mobileBinderDefUrlList) > 1}">
	      		<option value="">--<ssf:nlt tag="mobile.add"/>--</option>
		  		<c:forEach var="def" items="${ss_mobileBinderDefUrlList}">
		    	  <option value="${def.url}">${def.title}</option>
		  		</c:forEach>
			  </c:if>
	  		</select>
	  		<input type="submit" name="goBtn" value="<ssf:nlt tag="button.ok"/>"/>
	  	  </form>
	  	</div>
	 	</c:if>
		</td>
		
		<td valign="middle" width="10%" nowrap>
	<c:if test="${ssDefinitionFamily != 'calendar'}">
		<table cellspacing="0" cellpadding="0"><tr>
		<td>
		<c:if test="${!empty ss_prevPage}">
	    <a href="<ssf:url adapter="true" portletName="ss_forum" 
		  folderId="${ssBinder.id}" 
		  action="__ajax_mobile" 
		  operation="mobile_show_folder" 
		  actionUrl="false" ><ssf:param name="pageNumber" value="${ss_prevPage}"/></ssf:url>"
	    ><img border="0" src="<html:rootPath/>images/mobile/nl_left_16.gif"/></a>
		</c:if>
		<c:if test="${empty ss_prevPage}">
	  	<img border="0" src="<html:rootPath/>images/mobile/nl_left_dis_16.gif"
	  		<ssf:alt tag=""/> />
		</c:if>
		</td><td style="padding-left:20px;">
		<c:if test="${!empty ss_nextPage}">
	  	<a href="<ssf:url adapter="true" portletName="ss_forum" 
			folderId="${ssBinder.id}" 
			action="__ajax_mobile" 
			operation="mobile_show_folder" 
			actionUrl="false" ><ssf:param name="pageNumber" value="${ss_nextPage}"/></ssf:url>"
	  	><img border="0" src="<html:rootPath/>images/mobile/nl_right_16.gif"/></a>
		</c:if>
		<c:if test="${empty ss_nextPage}">
	  		<img border="0" src="<html:rootPath/>images/mobile/nl_right_dis_16.gif"
	  		<ssf:alt tag=""/> />
		</c:if>
		</tr></table>
	</c:if>

		</td></tr>
		</table>
	  </div>

	<c:if test="${ssDefinitionFamily != 'calendar'}">
	    <c:forEach var="entryFol" items="${ssFolderEntries}">
	    	<jsp:useBean id="entryFol" type="java.util.Map" />
			<div class="entry">
			  <div class="entry-title">
			    <a href="<ssf:url adapter="true" portletName="ss_forum" 
				  folderId="${entryFol._binderId}"  entryId="${entryFol._docId}"
				  action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />"
			    >
			  	  <span><c:if test="${empty entryFol.title}">--<ssf:nlt tag="entry.noTitle"/>--</c:if>
			  	    <ssf:makeWrapable><c:out value="${entryFol.title}" escapeXml="true"/></ssf:makeWrapable>
			  	  </span>
			    </a>
			  </div>
			  <c:if test="${!empty entryFol._totalReplyCount}">
			    <div class="entry-comment-label">${entryFol._totalReplyCount}</div>
			  </c:if>
			  
			  <div class="entry-signature">
				<span class="entry-author"><img src="<html:rootPath/>images/pics/sym_s_gray_dude.gif" 
			  	    width="11" height="10" hspace="2" border="0" style="vertical-align:middle" 
			  	    <ssf:alt tag=""/> /><a href="<ssf:url adapter="true" portletName="ss_forum" 
				    action="__ajax_mobile"
				    operation="mobile_show_workspace"
				    binderId="${entryFol._principal.workspaceId}" />"
				  ><c:out value="${entryFol._principal.title}" escapeXml="true"/></a></span>
				<span class="entry-date"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
		          	value="${entryFol._modificationDate}" type="both" 
			      	timeStyle="short" dateStyle="medium" />
			    </span>
			  </div>
		 
		  	  
			  <c:if test="${!empty entryFol._desc}">
			    <div class="entry-content">
			    	<ssf:textFormat 
			      	  formatAction="limitedDescription" 
			          textMaxWords="10"><ssf:markup search="${entryFol}">${entryFol._desc}</ssf:markup></ssf:textFormat>
		  	    <div class="ss_clear"></div>
		  	    </div>
			  </c:if>
		
	    	</div>
		</c:forEach>
	</c:if>
	<c:if test="${ssDefinitionFamily == 'calendar'}">
		<%@ include file="/WEB-INF/jsp/mobile/show_calendar.jsp" %>
	</c:if>
		
	</div>
<c:set var="ss_mobileBinderListShown" value="true" scope="request"/>
