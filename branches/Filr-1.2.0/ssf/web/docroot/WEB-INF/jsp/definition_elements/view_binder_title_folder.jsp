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
<% //Folder title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${empty ss_breadcrumbsShowIdRoutine}">
  <c:set var="ss_breadcrumbsShowIdRoutine" value="ss_treeShowIdNoWS" scope="request" />
</c:if>
<div id="ss_profile_box_h1">
  <ul class="ss_horizontal ss_nobullet">
  <c:set var="parentBinder2" value="${ssDefinitionEntry}"/>
  <jsp:useBean id="parentBinder2" type="java.lang.Object" />
  <c:set var="action" value="view_permalink"/>
  <c:if test="${ssConfigJspStyle == 'template'}">
    <c:set var="action" value="configure_configuration"/>
  </c:if>
  <%
	Stack parentTree2 = new Stack();
	while (parentBinder2 != null) {
		parentTree2.push(parentBinder2);
		if (((Binder)parentBinder2).getEntityType().equals(org.kablink.teaming.domain.EntityIdentifier.EntityType.workspace)) break;
		parentBinder2 = ((Binder)parentBinder2).getParentBinder();
	}
	while (!parentTree2.empty()) {
		Binder nextBinder2 = (Binder) parentTree2.pop();
  %>
  <c:set var="nextBinder" value="<%= nextBinder2 %>"/>
  <li>
 	  <c:if test="${nextBinder.entityType == 'folder' && !empty ssNavigationLinkTree[nextBinder.id]}">
	 <table cellpadding="0" cellspacing="0">
	 <tr>
	   <td valign="top">
	    <div class="ss_treeWidget">
	     <a 
	     <c:if test="${ssConfigJspStyle != 'template'}">
	       href="javascript: ;" 
	         onclick="return ss_gotoPermalink('${nextBinder.id}', '${nextBinder.id}', '${nextBinder.entityType}', '_ss_forum_', 'yes');"
	     </c:if>
	     <c:if test="${ssConfigJspStyle == 'template'}">
	       href='<ssf:url adapter="true" portletName="ss_forum" 
			    action="${action}"
           		folderId="${nextBinder.id}" />'
          </c:if>
          >
	         <c:if test="${empty nextBinder.title}">
               <span class="ss_light">--<ssf:nlt tag="entry.noTitle" />--</span>
             </c:if>
             <span>${nextBinder.title}</span>
          </a> 
         </div>
        </td>
 		   <%  if (!parentTree2.empty()) {  %>
 		     <td><div class="ss_profile_box_h1 ss_treeWidget"><img src="<html:rootPath/>images/pics/breadspace.gif" border="0" align="absmiddle"></div>
 		     </td>
 		   <%  }  %>
 		 </tr>
 		 </table>
  </c:if>
  <c:if test="${nextBinder.entityType != 'folder' || empty ssNavigationLinkTree[nextBinder.id]}">
	  <div class="ss_treeWidget">
	     <a
	     <c:if test="${ssConfigJspStyle != 'template'}">
	       href="javascript: ;" 
	         onclick="return ss_gotoPermalink('${nextBinder.id}', '${nextBinder.id}', '${nextBinder.entityType}', '_ss_forum_', 'yes');"
	     </c:if>
	     <c:if test="${ssConfigJspStyle == 'template'}">
		  href='<ssf:url crawlable="true"
	           adapter="true" portletName="ss_forum"
	           folderId="${nextBinder.id}" 
	           action="${action}"/>'
	     </c:if>
	     >
		    <c:if test="${empty nextBinder.title}">
	          <span class="ss_light">--<ssf:nlt tag="entry.noTitle" />--</span>
	        </c:if>
	         	<span>${nextBinder.title}</span>
      	 </a>
      <%  if (!parentTree2.empty()) {  %><img src="<html:rootPath/>images/pics/breadspace.gif" border="0" align="absmiddle"><%  }  %>
      </div>
  </c:if>
  </li>
 	 <%
   }
 	 %>
  </ul>
</div>
