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
<% //Entry creator view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<div class="ss_entryContent ss_entrySignature">
  <c:out value="${property_caption}" />
<c:if test="${!empty ssDefinitionEntry.creation.principal}">
    <ssf:showUser user="${ssDefinitionEntry.creation.principal}"/>

  <c:if test="${!empty ssDefinitionEntry.postedBy}">
    <br/>
    <span>(<ssf:nlt tag="entry.postedBy"/>&nbsp;<c:out value="${ssDefinitionEntry.postedBy}"/>)</span>
  </c:if>
  <c:if test="${!empty ssDefinitionEntry.customAttributes['_guestName'].valueSet}">
    <c:forEach var="guestName" items="${ssDefinitionEntry.customAttributes['_guestName'].valueSet}" varStatus="status">
    	<c:if test="${status.count == 1}"><c:set var="guestNameEntered" value="${guestName}"/></c:if>
    	<c:if test="${status.count == 2}"><c:set var="guestEmailEntered" value="${guestName}"/></c:if>
    </c:forEach>
    <br/>
    <span>(<c:if test="${!empty guestNameEntered}">${guestNameEntered}<c:if test="${!empty guestEmailEntered}">, </c:if></c:if>${guestEmailEntered})</span>
  </c:if>
</c:if>
</div>
