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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${!empty ss_toolbar_meeting_url}">
		<div class="ss_leftNav ss_sideMeet">
		  <ul>
			<li>
			  <c:if test="${!ss_toolbar_meeting_post}">
				  <a href="${ss_toolbar_meeting_url}"
				    onclick="ss_toolbarPopupUrl(this.href);return false;"
				    title="<%= NLT.get("sidebar.addMeeting.info").replaceAll("\"", "&QUOT;") %>"
				  >
				    <span><ssf:nlt tag="toolbar.menu.addMeeting"/></span>
				  </a>
			  </c:if>
			
			  <c:if test="${ss_toolbar_meeting_post}">
				<c:set var="contributorIdList" value=""/>
				<c:forEach var="contributorId" items="${ss_toolbar_meeting_ids}">
				  <c:if test="${!empty contributorIdList}"><c:set var="contributorIdList" value="${contributorIdList}, "/></c:if>
				  <c:set var="contributorIdList" value="${contributorIdList}${contributorId}"/>
				</c:forEach>
					<form class="inline" action="${ss_toolbar_meeting_url}" method="post" 
					  target="footerToolbarOptionWnd"
					>
					  <input type="hidden" name="ssUsersIdsToAdd" value="${contributorIdList}"/>
					  <a href="javascript: ;" 
					    onclick="ss_toolbarPopupUrl('', 'footerToolbarOptionWnd'); ss_submitParentForm(this);return false; "
					  ><span><ssf:nlt tag="toolbar.menu.addMeeting"/></span></a>
						<sec:csrfInput />
					</form>
			  </c:if>
			</li>
		  </ul>
		</div>
</c:if>
