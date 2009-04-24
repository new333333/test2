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
<% //Title view for folders %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="actionVar" value="view_ws_listing"/>
<c:set var="actionVar2" value="view_folder_listing"/>
<c:if test="${ssConfigJspStyle != 'template'}">
<c:if test="${ssDefinitionEntry.parentBinder.entityType == 'folder'}">
  <c:set var="actionVar" value="view_folder_listing"/>
</c:if>
</c:if>
<c:if test="${ssConfigJspStyle == 'template'}">
<c:set var="actionVar" value="configure_configuration"/>
<c:set var="actionVar2" value="configure_configuration"/>
</c:if>
<div class="ss_entryContent" style="text-align:left; padding-bottom:16px;">
  <ul class="ss_horizontal ss_nobullet">
  <c:if test="${!empty ssDefinitionEntry.parentBinder.title}">
	<li>
	<span class="ss_link_7">
  	  <a class="ss_link_7" 
  	    href="<ssf:url crawlable="true" adapter="true" portletName="ss_forum"
  	    action="${actionVar}" 
		binderId="${ssDefinitionEntry.parentBinder.id}"/>"
	  >${ssDefinitionEntry.parentBinder.title}</a>&nbsp;&gt;&gt;&nbsp;&nbsp;
	</span>
	</li>
  </c:if>
  <li>
  <span class="ss_link_8">
	  <img src="<html:rootPath/>images/pics/discussion/folder_orange.png"
	  	<ssf:alt tag=""/>>&nbsp;<a
	    href="<ssf:url crawlable="true" adapter="true" portletName="ss_forum" 
	    action="${actionVar2}" binderId="${ssDefinitionEntry.id}"/>"
	  ><c:if test="${empty ssDefinitionEntry.title}" >--<ssf:nlt tag="entry.noTitle" />--</c:if
	  >${ssDefinitionEntry.title}</a>
  </span>
  </li>
  </ul>
</div>
