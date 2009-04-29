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
<% //Workspace title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="actionVar" value="view_ws_listing"/>
<c:set var="actionVar2" value="view_folder_listing"/>
<c:if test="${ssConfigJspStyle != 'template'}">
	<c:if test="${ssDefinitionEntry.parentBinder.entityType == 'folder'}">
	  <c:set var="actionVar" value="view_folder_listing"/>
	</c:if>
	<c:if test="${ssDefinitionEntry.parentBinder.entityType == 'profiles'}">
	  <c:set var="actionVar" value="view_profile_listing"/>
	</c:if>
</c:if>
<c:if test="${ssConfigJspStyle == 'template'}">
	<c:set var="actionVar" value="configure_configuration"/>
	<c:set var="actionVar2" value="configure_configuration"/>
</c:if>

  <div id="ss_topic_box">
	<div id="ss_topic_box_h1">
	
	<c:if test="${ssDefinitionEntry.entityType == 'workspace'}">
	  <ul class="ss_horizontal ss_nobullet">
	  <li>
	  <a href="<ssf:url crawlable="true"
           adapter="true" portletName="ss_forum"
           folderId="${ssDefinitionEntry.id}" 
           action="view_ws_listing"/>">
	    <c:if test="${empty ssDefinitionEntry.title}">
          <span class="ss_light">--<ssf:nlt tag="entry.noTitle" />--</span>
        </c:if>
        <span>${ssDefinitionEntry.title}</span>
      </a>
      </li>
      </ul>
    </c:if>
    
	<c:if test="${ssDefinitionEntry.entityType == 'profiles'}">
	  <ul class="ss_horizontal ss_nobullet">
	  <li>
	  <a href="<ssf:url crawlable="true"
           adapter="true" portletName="ss_forum"
           folderId="${ssDefinitionEntry.id}" 
           action="view_profile_listing"/>">
	    <c:if test="${empty ssDefinitionEntry.title}">
          <span class="ss_light">--<ssf:nlt tag="entry.noTitle" />--</span>
        </c:if>
        <span>${ssDefinitionEntry.title}</span>
      </a>
      </li>
      </ul>
    </c:if>
    
	<c:if test="${ssDefinitionEntry.entityType == 'folder'}">
	  <ul class="ss_horizontal ss_nobullet">
	  <c:set var="parentBinder2" value="${ssDefinitionEntry}"/>
	  <c:set var="action" value="view_ws_listing"/>
	  <jsp:useBean id="parentBinder2" type="java.lang.Object" />
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
	  <div style="display:inline;">
	  <a href="<ssf:url crawlable="true"
           adapter="true" portletName="ss_forum"
           folderId="${nextBinder.id}" 
           action="${action}"/>" style="vertical-align:middle;">
	    <c:if test="${empty nextBinder.title}">
          <span class="ss_light">--<ssf:nlt tag="entry.noTitle" />--</span>
        </c:if>
        <span>${nextBinder.title}</span>
      </a><%  if (!parentTree2.empty()) {  %>&nbsp;&gt;&gt;<%  }  %>
      </div>
	  </li>
	  <c:set var="action" value="view_folder_listing"/>
  	 <%
	   }
  	 %>
	  </ul>
    </c:if>

	<div class="ss_clear"></div>
    </div>
    
	<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
	  configElement="${item}" 
	  configJspStyle="${ssConfigJspStyle}"
	  entry="${ssDefinitionEntry}" />
    
  </div><!-- end of box -->
