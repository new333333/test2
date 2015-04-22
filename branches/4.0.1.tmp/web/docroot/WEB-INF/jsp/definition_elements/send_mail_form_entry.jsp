<%
/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
<% // Send mail on submit %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_entryContent">
<div style="padding:15px 0px 15px 0px;">
<ssf:expandableArea title="${property_caption}">

<c:if test="${empty ssUser.emailAddresses}">
  <div class="ss_entryContent">
  <span><ssf:nlt tag="sendMail.noUserEmailAddress"/></span>
  </div>
</c:if>
<c:if test="${!empty ssUser.emailAddresses}">

<% /* User selection. */ %>
<div class="ss_entryContent">
	<span class="ss_labelAbove"><ssf:nlt tag="entry.sendMail.toList" /></span>
	<ssf:find formName="${formName}" formElement="_sendMail_toList" type="user" 
	userList="<%= new java.util.HashSet() %>" sendingEmail="true" width="150px" />
	<ssf:clipboard type="user" formElement="_sendMail_toList" />
</div>

<% /* Group selection. */ %>
<div class="ss_entryContent">
 	<span class="ss_labelAbove"><ssf:nlt tag="entry.sendMail.toList.groups" /></span>
	<ssf:find formName="${formName}" formElement="_sendMail_toList_groups" 
	type="group" sendingEmail="true" width="150px" />
</div>

<% /* Named team selection. */ %>
<div class="ss_entryContent">
	<span class="ss_labelAbove"><ssf:nlt tag="entry.sendMail.toList.teams" /></span>
	<ssf:find formName="${formName}" formElement="_sendMail_toList_teams" type="teams" 
	sendingEmail="true" width="150px" />
</div>

<% /* Local team selection. */ %>
<c:if test="${ssBinderHasTeamMembers}">  
  <div class="ss_entryContent">
  <input type="checkbox" name="_sendMail_toTeam" />
  <span class="ss_labelAfter"><label for="_sendMail_toTeam">
    <ssf:nlt tag="entry.sendMail.toTeam"/>
  </label></span>
  <br/>
  </div>
</c:if>

<div class="ss_entryContent">
  <br/>

  <span class="ss_labelAbove"><label for="_sendMail_subject">
    <ssf:nlt tag="entry.sendMail.subject"/>
  </label></span>
  <c:if test="${!empty ss_pageType && ss_pageType == 'modify'}">
  	<input type="hidden"         name="_sendMail_subject_default" id="_sendMail_subject_default" value="<ssf:nlt tag='entry.sendMail.modifiedEntry'><ssf:param name="value" value="${ssBinder.title}"/></ssf:nlt>"/>
  	<input type="text" size="80" name="_sendMail_subject"         id="_sendMail_subject"         value="<ssf:nlt tag='entry.sendMail.modifiedEntry'><ssf:param name="value" value="${ssBinder.title}"/></ssf:nlt>"/>
  </c:if>
  <c:if test="${empty ss_pageType || ss_pageType != 'modify'}">
  	<input type="hidden"         name="_sendMail_subject_default" id="_sendMail_subject_default" value="<ssf:nlt tag='entry.sendMail.newEntry'><ssf:param name="value" value="${ssBinder.title}"/></ssf:nlt>"/>
  	<input type="text" size="80" name="_sendMail_subject"         id="_sendMail_subject"         value="<ssf:nlt tag='entry.sendMail.newEntry'><ssf:param name="value" value="${ssBinder.title}"/></ssf:nlt>"/>
  </c:if>
  <br/>

  <span class="ss_labelAbove"><label for="_sendMail_body">
    <ssf:nlt tag="entry.sendMail.body"/>
  </label></span>
  <div>
    <ssf:htmleditor name="_sendMail_body" height="200" toolbar="minimal" />
  </div>

  <br/>
  <input type="checkbox" name="_sendMail_includeAttachments" />
  <span class="ss_labelAfter"><label for="_sendMail_includeAttachments">
    <ssf:nlt tag="entry.sendMail.includeAttachments"/>
  </label></span>
</div>
</c:if>
</ssf:expandableArea>
</div>
</div>

