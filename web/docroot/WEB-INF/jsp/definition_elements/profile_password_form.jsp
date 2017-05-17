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
<% //Password form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%@ page import="org.kablink.teaming.domain.Principal" %>
<%
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null) {caption = "";}
	Principal targetUser = (Principal) request.getAttribute("ssEntry");
	boolean isSuper = false;
	if (targetUser instanceof User) isSuper = ((User)targetUser).isSuper();
%>
<c:if test="${empty ss_profile_entry_form || not ss_profile_entry_form}">
<c:set var="isSuper" value="<%= isSuper %>"/>
<div class="ss_entryContent">
  <c:if test="${empty ssReadOnlyFields['password']}">
	<div class="ss_labelAbove"><%= caption %></div>
	<c:if test="${!empty ssDefinitionEntry.password && (!ss_isBinderAdmin || 
			ssDefinitionEntry.name == ssUser.name || isSuper)}">
		<div class="ss_labelAbove"><ssf:nlt tag="__profile_password_original"/></div>
		<input type="password" size="40" name="password_original" class="ss_text" autocomplete="off" />
	</c:if>
	<div class="ss_labelAbove"><ssf:nlt tag="__profile_password_new"/></div>
	<input type="password" size="40" name="password" class="ss_text" autocomplete="off" 
	  <c:if test="${!empty ssDefinitionEntry.password}"> value="*****"</c:if> />
	<div class="ss_labelAbove"><ssf:nlt tag="__profile_password_again"/></div>
	<input type="password" size="40" name="password2" class="ss_text" autocomplete="off" 
	  <c:if test="${!empty ssDefinitionEntry.password}"> value="*****"</c:if> />
	<c:if test="${!empty ssDefinitionEntry.password}">
	  <input type="hidden" name="password3" value="*****" />
	</c:if>
  </c:if>
</div>

<!-- If needed, show the Text Verification controls. -->
<%@ include file="/WEB-INF/jsp/definition_elements/textVerification.jsp" %>

</c:if>