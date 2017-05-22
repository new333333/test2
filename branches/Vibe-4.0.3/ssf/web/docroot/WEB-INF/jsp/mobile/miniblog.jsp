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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${empty ss_microblog_post_url}">
  <c:set var="ss_microblog_post_url" scope="request">
    <ssf:url adapter="true" portletName="ss_forum" 
			folderId="${ssBinder.id}"
			action="__ajax_mobile" 
			operation="mobile_show_front_page" 
			actionUrl="true" />
  </c:set>
</c:if>
<%@ page import="org.kablink.teaming.ObjectKeys" %>

  <div id="micro-blog-edit" class="action-dialog" style="display: none;z-index:2;">
	<div class="close-menu">
        <input id="micro-blog-cancel" type="image" src="<html:rootPath/>images/icons/close_menu.png" 
          name="PostBlogCancel" onClick="ss_hideMenu('micro-blog-edit');return false;"/>
	</div>
	<div class="dialog-head">
	  <span><label for="miniblogText"><ssf:nlt tag="miniblog"/></label></span>
	  <span id="micro-blog-date">
		<c:if test="${!empty ssUser.status && !empty ssUser.statusDate}">
		  <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				value="${ssUser.statusDate}" type="both" 
				timeStyle="short" dateStyle="short" />
		</c:if>
	  </span>
	</div>
	<form id="microblogForm" method="post" action="${ss_microblog_post_url}">
			
	  <!-- necessary "cols" attribute is set to 20 for Blackberry and is overridden by CSS -->
			
      <textarea id="micro-blog-text" rows="5" cols="20" name="miniblogText"
      ><c:if test="${!empty ssUser.status && !empty ssUser.statusDate}">${ssUser.status}</c:if></textarea>
      <div id="micro-blog-buttons">
        <input id="micro-blog-post" type="submit" value="<ssf:nlt tag="button.post"/>" 
          name="miniblogBtn" onClick="ss_hideMenu('micro-blog-edit');return true;" />
        <input id="micro-blog-clear" type="reset" value="<ssf:nlt tag="button.clear"/>" name="ClearBlog"
          onclick="ss_clearStatusMobile('micro-blog-text');return false;"/>
      </div>
		<sec:csrfInput />
	</form>
  </div>


