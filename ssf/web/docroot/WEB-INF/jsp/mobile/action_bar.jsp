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
<%@ page import="org.kablink.teaming.ObjectKeys" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<div id="actions">
 <table cellspacing="0" cellpadding="0" width="100%">
 <tr>
 <td>
  
<c:if test="${!ss_hideMiniBlog}">
  <a href="javascript: ;" 
    onClick="ss_toggleDivVisibility('micro-blog-edit');ss_selectElement('micro-blog-text');return false;"><ssf:nlt tag="miniblog"/></a>
</c:if>

  <c:if test="${ssDefinitionFamily == 'calendar'}">
    <%@ include file="/WEB-INF/jsp/mobile/calendar_today_button.jsp" %>
  </c:if>  
  
  <c:if test="${!empty ss_new_actions}">
    <c:if test="${fn:length(ss_new_actions) == 1}">
		<c:choose>
			<c:when test="${!empty ssAddReplyTitle}">
				<c:forEach var="action" items="${ss_new_actions}">
				<a class="actions-a" href="${action.url}" 
				  title="${action.title}"><img class="actionbar-img" src="<html:rootPath/>css/images/mobile/reply_50.png" border="0" align="absmiddle"/></a>
				</c:forEach>
			</c:when>
			<c:otherwise>
				<c:forEach var="action" items="${ss_new_actions}">
				<a class="actions-a" href="${action.url}" 
				  title="${action.title}"><img class="actionbar-img" src="<html:rootPath/>css/images/mobile/new_50.png" border="0" align="absmiddle"/></a>
				</c:forEach>
			</c:otherwise>
		</c:choose>	
    </c:if>
    <c:if test="${fn:length(ss_new_actions) > 1}">
      <a class="actions-a" href="javascript: ;" 
        onClick="ss_toggleDivVisibility('new-actions-menu');return false;"><img class="actionbar-img" src="<html:rootPath/>css/images/mobile/new_50.png" border="0" align="absmiddle"/></a>
    </c:if>
  </c:if>  

  <c:if test="${!empty ss_modify_actions}">
     <c:if test="${fn:length(ss_modify_actions) == 1}">
      <c:forEach var="action" items="${ss_modify_actions}">	  
		  <a class="action-a actionimg" href="${action.url}" title="${action.title}">
			<img class="actionbar-img" src="<html:rootPath/>css/images/mobile/edit_50.png" border="0" align="absmiddle"/>
		  </a>
      </c:forEach>
    </c:if>
  </c:if>  

  <c:if test="${!empty ss_delete_actions}">
     <c:if test="${fn:length(ss_delete_actions) == 1}">
      <c:forEach var="action" items="${ss_delete_actions}">
		  <a class="action-a actionimg" href="${action.url}" title="${action.title}">
			<img class="actionbar-img" src="<html:rootPath/>css/images/mobile/delete_50.png" border="0" align="absmiddle"/>
		  </a>
      </c:forEach>
    </c:if>
  </c:if>  

  <a class="hierarchy-a actionimg" href="javascript: ;" onClick="ss_toggleDivVisibility('hierarchy-dialog');return false;">
    <img class="actionbar-img" src="<html:rootPath/>css/images/mobile/hierarchy_50.png" border="0" align="absmiddle"/>
  </a>

  <c:if test="${!empty ss_actions}">
    <a class="actions-a actionimg" href="javascript: ;" 
      onClick="ss_toggleDivVisibility('actions-menu');return false;"><img class="actionbar-img" src="<html:rootPath/>css/images/mobile/more_menu_50.png" border="0" align="absmiddle"/></a>
  </c:if>  
 </td>
 <td align="right">
   <c:if test="${ss_showFolderNextPrev}">
     <%@ include file="/WEB-INF/jsp/mobile/folder_next_prev.jsp" %>
   </c:if>
   <c:if test="${ss_showEntryNextPrev}">
     <%@ include file="/WEB-INF/jsp/mobile/entry_next_prev.jsp" %>
   </c:if>
   <c:if test="${ss_showSearchResultsNextPrev}">
     <%@ include file="/WEB-INF/jsp/mobile/search_results_next_prev.jsp" %>
   </c:if>
   <c:if test="${ss_showFindPlacesResultsNextPrev}">
     <%@ include file="/WEB-INF/jsp/mobile/find_places_results_next_prev.jsp" %>
   </c:if>
   <c:if test="${ss_showFindPeopleResultsNextPrev}">
     <%@ include file="/WEB-INF/jsp/mobile/find_people_results_next_prev.jsp" %>
   </c:if>
   <c:if test="${ss_showWhatsNewNextPrev}">
     <%@ include file="/WEB-INF/jsp/mobile/whats_new_next_prev.jsp" %>
   </c:if>
 </td>
 </tr>
 </table>
</div>

<%@ include file="/WEB-INF/jsp/mobile/search.jsp" %>

<%@ include file="/WEB-INF/jsp/mobile/navigation.jsp" %>

<c:if test="${!ss_hideMiniBlog}">
<%@ include file="/WEB-INF/jsp/mobile/miniblog.jsp" %>
</c:if>

<%@ include file="/WEB-INF/jsp/mobile/new_menu.jsp" %>
<%@ include file="/WEB-INF/jsp/mobile/actions_menu.jsp" %>
