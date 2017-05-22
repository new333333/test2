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
<%@ page import="java.util.Stack" %>
<%@ page import="org.kablink.teaming.domain.Binder" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

  <div id="hierarchy-dialog" class="action-dialog" style="display:none; z-index:2;">
	<div class="close-menu">
	  <input id="hierarchy-cancel" type="image" src="<html:rootPath/>images/icons/close_menu.png" 
	    name="hierarchyhCancel" onClick="ss_hideMenu('hierarchy-dialog');return false;" />
	</div>
    <div class="dialog-head">
      <span><ssf:nlt tag="title.goto"/></span>
    </div>
    <div class="dialog-content">
    <c:set var="navPadding" value="0"/>
	<c:if test="${!empty ssBinder.parentBinder}">
	  <c:set var="parentBinder" value="${ssBinder.parentBinder}"/>
	  <jsp:useBean id="parentBinder" type="java.lang.Object" />
	  <%
		Stack parentTree = new Stack();
		while (parentBinder != null) {
			parentTree.push(parentBinder);
			parentBinder = ((Binder)parentBinder).getParentBinder();
		}
		while (!parentTree.empty()) {
			Binder nextBinder = (Binder) parentTree.pop();
	  %>
	 <c:set var="nextBinder" value="<%= nextBinder %>"/>
	   <div class="menu-item" style="padding-left: ${navPadding}px;">
	  	<a 
		  <c:if test="${nextBinder.entityType == 'folder'}">
  			href="<ssf:url adapter="true" portletName="ss_forum" 
				folderId="${nextBinder.id}" 
				action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />"
		  </c:if>
		  <c:if test="${nextBinder.entityType == 'workspace'}">
  		  	href="<ssf:url adapter="true" portletName="ss_forum" 
				folderId="${nextBinder.id}" 
				action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />"
		  </c:if>
		  <c:if test="${nextBinder.entityType == 'profiles'}">
  	  	  	href="<ssf:url adapter="true" portletName="ss_forum" 
				folderId="${nextBinder.id}" 
				action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />"
		  </c:if>
	  	  >
	      <c:if test="${empty nextBinder.title}" >
			--<ssf:nlt tag="entry.noTitle" />--
	  	  </c:if>
	  	  <c:out value="${nextBinder.title}" />
		</a>
	  </div>
	  <c:set var="navPadding" value="${navPadding + 10}"/>
  	 <%
	   }
  	 %>
	</c:if>

	<c:if test="${!empty ssBinder}">
	   <div class="menu-item menu-curlevel" style="padding-left: ${navPadding}px;">
	  	<a 
		  <c:if test="${ssBinder.entityType == 'folder'}">
  			href="<ssf:url adapter="true" portletName="ss_forum" 
				folderId="${ssBinder.id}" 
				action="__ajax_mobile" operation="mobile_show_folder" actionUrl="false" />"
		  </c:if>
		  <c:if test="${ssBinder.entityType == 'workspace'}">
  		  	href="<ssf:url adapter="true" portletName="ss_forum" 
				folderId="${ssBinder.id}" 
				action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />"
		  </c:if>
		  <c:if test="${ssBinder.entityType == 'profiles'}">
  	  	  	href="<ssf:url adapter="true" portletName="ss_forum" 
				folderId="${ssBinder.id}" 
				action="__ajax_mobile" operation="mobile_show_workspace" actionUrl="false" />"
		  </c:if>
	  	  >
	      <c:if test="${empty ssBinder.title}" >
			--<ssf:nlt tag="entry.noTitle" />--
	  	  </c:if>
	  	  <c:out value="${ssBinder.title}" />
		</a>
	  </div>
	</c:if>
  </div>
  </div>
