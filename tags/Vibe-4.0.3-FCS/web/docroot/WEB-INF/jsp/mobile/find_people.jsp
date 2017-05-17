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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("mobile.searchPeople") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>

<c:set var="ss_pageTitle" value='<%= NLT.get("mobile.searchPeople") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/mobile/masthead.jsp" %>

<div class="content">

<c:set var="ss_hideMiniBlog" value="true" scope="request" />
<c:set var="ss_showFindPeopleResultsNextPrev" value="true" scope="request" />
<%@ include file="/WEB-INF/jsp/mobile/action_bar.jsp" %>

  <div class="folders">
    <div class="folder-content">
      <div class="entry-actions" style="text-align: left;">
		  <form method="post" action="<ssf:url adapter="true" portletName="ss_forum" 
					action="__ajax_mobile" actionUrl="true" 
					operation="mobile_find_people" />">
		  <label for="searchText"><div class="ss_bold" style="padding-left: 5px;"><ssf:nlt tag="navigation.findUser"/></div></label>
		  <input type="text" size="25" name="searchText" id="searchText" autocomplete="off"
		    value="<ssf:escapeQuotes>${ss_searchText}</ssf:escapeQuotes>"/><input 
		    type="submit" name="okBtn" value="<ssf:nlt tag="button.search"/>"/>
				<sec:csrfInput />
			</form>
      </div>
	
	  <c:forEach var="user" items="${ssUsers}" >
	    <div class="folder-item">
		  <a href="<ssf:url adapter="true" portletName="ss_forum" 
		    entryId="${user._docId}"
		    action="__ajax_mobile" operation="mobile_show_user" actionUrl="false" />">
		    <ssf:userTitle user="${user._principal}"/>
		  </a>
		</div>
	  </c:forEach>
		
      <div class="entry-actions">
        <%@ include file="/WEB-INF/jsp/mobile/find_people_results_next_prev.jsp" %>
      </div>

	</div>
  </div>
</div>

</body>
</html>
