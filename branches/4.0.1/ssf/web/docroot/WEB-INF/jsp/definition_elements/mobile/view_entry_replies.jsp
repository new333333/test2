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
<% // View replies %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<% // Process the replies only if this is the top level entry being displayed %>
<c:if test="${ssEntry == ssDefinitionEntry}" >
  <c:if test="${!empty ssFolderEntryDescendants}">

<div class="folder-head">
  <c:if test="${!empty property_caption}">
    <span>${property_caption}</span>
  </c:if>
  <c:if test="${empty property_caption}">
    <ssf:nlt tag="mobile.comments"/>
  </c:if>
  (<span>${ssDefinitionEntry.totalReplyCount}</span>)
</div>


<c:forEach var="reply" items="${ssFolderEntryDescendants}">
  <jsp:useBean id="reply" type="org.kablink.teaming.domain.Entry" />
  <c:if test="${ssEntry == reply.parentEntry}">
    <c:set var="commentClass" value="comment entry"/>
    <c:set var="commentImg" value="comments_25.png"/>
  </c:if>
  <c:if test="${ssEntry != reply.parentEntry}">
    <c:set var="commentClass" value="comment2 entry"/>
    <c:set var="commentImg" value="comments_16.png"/>
  </c:if>
  <div class="${commentClass}">
    <img class="comment-img" src="<html:rootPath/>images/mobile/${commentImg}"/>

	<c:set var="ss_showSignatureAfterTitle" value="false" scope="request"/>
	<c:set var="ss_signatureShown" value="false" scope="request"/>
    <c:if test="${!empty reply.entryDefId}">
 	  <ssf:displayConfiguration configDefinition='<%= (Document) reply.getEntryDefDoc() %>' 
		configElement='<%= (Element) reply.getEntryDefDoc().getRootElement().selectSingleNode("//item[@name=\'entryView\' or @name=\'profileEntryView\' or @name=\'fileEntryView\']") %>' 
		configJspStyle="${ssConfigJspStyle}" 
		processThisItem="false" 
		entry="<%= reply %>" />
    </c:if>
    <c:if test="${empty reply.entryDefId}">
 	  <ssf:displayConfiguration configDefinition='<%= (Document) reply.getEntryDefDoc() %>' 
		configElement="${ssConfigElement}" 
		configJspStyle="${ssConfigJspStyle}" 
		processThisItem="false" 
		entry="<%= reply %>" />
    </c:if>
  </div>

</c:forEach>

</div>
  </c:if>
</c:if>
