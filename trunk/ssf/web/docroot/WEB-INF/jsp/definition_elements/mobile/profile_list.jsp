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
<% // Profile listing %>
<%@ page import="org.kablink.teaming.domain.Principal" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

  <div class="folders">
	<div class="folder-content" width="100%">
	  <div class="entry" align="right">
		<table cellspacing="0" cellpadding="0">
		<tr>
		<td>
		<c:if test="${!empty ss_prevPage}">
	    <a href="<ssf:url adapter="true" portletName="ss_forum" 
		  folderId="${ssBinder.id}" 
		  action="__ajax_mobile" 
		  operation="mobile_show_folder" 
		  actionUrl="false" ><ssf:param name="pageNumber" value="${ss_prevPage}"/></ssf:url>"
	    ><img border="0" src="<html:rootPath/>images/pics/sym_arrow_left_.gif"/></a>
		</c:if>
		<c:if test="${empty ss_prevPage}">
	  	<img border="0" src="<html:rootPath/>images/pics/sym_arrow_left_g.gif"
	  		<ssf:alt tag=""/> />
		</c:if>
		</td><td style="padding-left:20px;">
		<c:if test="${!empty ss_nextPage}">
	  	<a href="<ssf:url adapter="true" portletName="ss_forum" 
			folderId="${ssBinder.id}" 
			action="__ajax_mobile" 
			operation="mobile_show_folder" 
			actionUrl="false" ><ssf:param name="pageNumber" value="${ss_nextPage}"/></ssf:url>"
	  	><img border="0" src="<html:rootPath/>images/pics/sym_arrow_right_.gif"/></a>
		</c:if>
		<c:if test="${empty ss_nextPage}">
	  		<img border="0" src="<html:rootPath/>images/pics/sym_arrow_right_g.gif"
	  		<ssf:alt tag=""/> />
		</c:if>
		</tr>
		</table>
	  </div>

<c:forEach var="entry" items="${ssWorkspaces}" >
	<div class="entry">
	  <div class="entry-title">
	    <a href="<ssf:url adapter="true" portletName="ss_forum" 
				  binderId="${entry.id}"  
				  action="__ajax_mobile" 
				  operation="mobile_show_workspace" actionUrl="false" />"
		>${entry.title}</a>
	  </div>
	  
	  <div>
	    <span class="entry-author">${entry.owner.title}</span>
	  </div>
	  
	  <c:if test="${!empty entry.owner.emailAddress}">
	  <div>
		<span class="entry-type"><ssf:mailto email="${entry.owner.emailAddress}"/></span>
	  </div>
	  </c:if>
	</div>
</c:forEach>
</div>
</div>
<c:set var="ss_mobileBinderListShown" value="true" scope="request"/>
