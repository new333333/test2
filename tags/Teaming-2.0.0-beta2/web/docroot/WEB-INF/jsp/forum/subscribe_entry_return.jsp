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
<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<div class="ss_style" align="left" style="width: 50%;">

<form method="post" id="${ss_namespace}subscription_entry_form${ssEntry.id}" 
    style="text-align:center;width: 300px;">
  <span class="ss_bold"><ssf:nlt tag="subscribe.select.type"/></span><br/><br/>
  <%@ include file="/WEB-INF/jsp/entry/subscribe_entry.jsp" %>

  <br/>
  <br/>
  <input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>"
    onClick="ss_post('<ssf:url adapter="true" action="__ajax_request" actionUrl="true" portletName="ss_forum" binderId="${ssEntry.parentBinder.id}" entryId="${ssEntry.id}">
    <ssf:param name="namespace" value="${ss_namespace}"/>
    <ssf:param name="operation" value="subscribe"/>
     <ssf:param name="okBtn" value="1"/>
    </ssf:url>', '${ss_namespace}subscription_entry_form${ssEntry.id}');ss_cancelPopupDiv('${ss_namespace}ss_subscription_entry${ssEntry.id}');return false;">
 &nbsp;&nbsp;&nbsp;
  <input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"
  onClick="ss_cancelPopupDiv('${ss_namespace}ss_subscription_entry${ssEntry.id}');return false;">
</form>
</div>
