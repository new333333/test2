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

<div id="new-actions-menu" class="action-dialog" style="display:none; z-index:2;">
	<div id="new-actions-button" class="close-menu">
	  <input id="new-actions-menu-close" type="image" src="<html:rootPath/>images/icons/close_menu.png" 
	    name="newactionsmenuclose" onClick="ss_hideMenu('new-actions-menu');return false;" />
	</div>
    <div class="dialog-head">
      <span><ssf:nlt tag="mobile.newEntry"/></span>
    </div>
    <div class="dialog-content">
      <c:forEach var="action" items="${ss_new_actions}">
        <c:if test="${empty action.spacer}">
		  <div class="menu-item">
		    <a href="${action.url}" <c:if test="${!empty action.onclick}">onClick="${action.onclick}" </c:if>
		    >${action.title}</a>
		  </div>
		</c:if>
		<c:if test="${!empty action.spacer}">
		  <div class="menu-spacer">&nbsp;</div>
		</c:if>
      </c:forEach>
    </div>
</div>

