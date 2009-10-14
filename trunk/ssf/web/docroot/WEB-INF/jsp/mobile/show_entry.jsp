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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${!empty ssEntry.title}">
  <c:set var="ss_windowTitle" value="${ssEntry.title}" scope="request"/>
</c:if>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>

<%@ include file="/WEB-INF/jsp/mobile/masthead.jsp" %>

<div class="content">

<%@ include file="/WEB-INF/jsp/mobile/action_bar.jsp" %>

  <div class="folders">
    <div class="folder-content">

	  <div align="right" style="padding-right:8px;">
	    <a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${ssBinder.id}" 
						entryId="${ssEntry.id}"
						action="__ajax_mobile" 
						operation="mobile_show_prev_entry" 
						actionUrl="false" />">
	      <span class="ss_mobile_small"><ssf:nlt tag="nav.prevEntry"/></span>
	    </a>&nbsp;&nbsp;&nbsp;<a href="<ssf:url adapter="true" portletName="ss_forum" 
						folderId="${ssBinder.id}" 
						entryId="${ssEntry.id}"
						action="__ajax_mobile" 
						operation="mobile_show_next_entry" 
						actionUrl="false" />">
	      <span class="ss_mobile_small"><ssf:nlt tag="nav.nextEntry"/></span>
	    </a>
	  </div>
	
	<c:if test="${!empty ssEntry}">
			<div align="center">
			  <table>
			  <tr>
			  <c:if test="${!empty ss_mobileBinderDefUrlList && fn:length(ss_mobileBinderDefUrlList) > 1}">
				<td valign="top">
				  <form name="addEntryForm" 
				  		action="<ssf:url adapter="true" portletName="ss_forum" 
							binderId="${ssBinder.id}" 
							entryId="${ssEntry.id}" 
							action="__ajax_mobile" 
							operation="mobile_add_reply" 
							actionUrl="true" />" 
				  		method="post">
				  <table>
				  <tr>
				  <td valign="top">
				  <select name="url" size="1">
			      <option value="">--<ssf:nlt tag="mobile.addReply"/>--</option>
				  <c:forEach var="def" items="${ss_mobileBinderDefUrlList}">
				    <option value="${def.url}">${def.title}</option>
				  </c:forEach>
				  </select>
				  </td>
				  <td valign="top">
				  <input type="submit" name="goBtn" value="<ssf:nlt tag="button.ok"/>">
				  </td>
				  </tr>
				  </table>  
				  </form>
				</td>
			  </c:if>
			  <c:if test="${!empty ss_mobileBinderDefUrlList && fn:length(ss_mobileBinderDefUrlList) == 1}">
			    <td valign="top">
				  <c:forEach var="def" items="${ss_mobileBinderDefUrlList}">
				    <a href="${def.url}"><ssf:nlt tag="button.add"/>: ${def.title}</a>
				  </c:forEach>
				</td>
			  </c:if>
			  
			  <c:if test="${!empty ss_mobileEntryModifyUrl}">
			    <td 
			      <c:if test="${!empty ss_mobileBinderDefUrlList}">
			        style="padding-left:10px;"
			      </c:if>
			      valign="top">
			  	  <a href="${ss_mobileEntryModifyUrl}"><ssf:nlt tag="mobile.modifyEntry"/></a>
			  	</td>
			  </c:if>
			</tr>
			</table>
			</div>
			
		<div class="entry">
		  <div class="entry-comment-label">${ssEntry.totalReplyCount}</div>

	  		<div style="padding: 4px 6px;">
			  <c:set var="ss_tagObject" value="${ssDefinitionEntry}" scope="request"/>
			  <ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
				configElement="${ssConfigElement}" 
				configJspStyle="mobile" 
				entry="${ssEntry}" />
			</div>
		</div>
	</c:if> 
  </div>	
</div>

</body>
</html>
