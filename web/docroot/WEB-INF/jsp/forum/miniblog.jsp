<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<c:set var="ss_windowTitle" value='<%= NLT.get("miniblog") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body class="tundra">
</ssf:ifadapter>
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
function ss_showMiniblog${renderResponse.namespace}(id, obj) {
	ss_viewMiniBlog(id, '0', false);
}
function ss_showMiniblogPage${renderResponse.namespace}(id, currentPage, direction) {
	var page = parseInt(currentPage);
	if (direction == 'next') page = parseInt(page + 1);
	if (direction == 'previous') {
		page = parseInt(page - 1);
		if (page < 0) page = 0;
	}
	ss_viewMiniBlog(id, page, false);
}
</script>

<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />

<div class="ss_style ss_portlet">
  <ssf:form title='<%= NLT.get("miniblog") %>'>
  <div>
    <div>
	      <span class="ss_labelminiBlog"><ssf:nlt tag="navigation.findPerson"/></span>
	      <ssf:find type="user"
		    clickRoutine="ss_showMiniblog${renderResponse.namespace}"
		    leaveResultsVisible="false"
		    width="100px" singleItem="true"/> 
	</div>
    
  </div>
  
  <table class="margintop3">
  <tr>
  	<td rowspan="2" valign="top">
	  <ssf:buddyPhoto style="ss_thumbnail_standalone ss_thumbnail_standalone_small" 
					user="${ss_miniblog_user}" 
					folderId="${ss_miniblog_user.parentBinder.id}" entryId="${ss_miniblog_user.id}" />
  	</td>
	<td>
	    <div class="ss_form_subhead ss_nowrap margintop3"><span class="ss_size_14px"><ssf:showUser user="${ss_miniblog_user}" target="_blank" close="true" /></span>
			<span id="ss_nextPage">
				<c:if test="${ss_miniblogPage > '0'}">
				<a href="javascript: ;" 
				  onClick="ss_showMiniblogPage${renderResponse.namespace}('${ss_miniblog_user.id}', '${ss_miniblogPage}', 'previous');return false;">
				<img src="<html:imagesPath/>pics/sym_arrow_left_.png" 
				  align="absmiddle" title="<ssf:nlt tag="general.previousPage"/>"/>
				</a>
				</c:if>
				<c:if test="${empty ss_miniblogPage || ss_miniblogPage <= '0'}">
				<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_left_g.png"/>
				</c:if>
				<c:if test="${!empty ss_miniblog_statuses}">
				<a href="javascript: ;" 
				  onClick="ss_showMiniblogPage${renderResponse.namespace}('${ss_miniblog_user.id}', '${ss_miniblogPage}', 'next');return false;">
				<img src="<html:imagesPath/>pics/sym_arrow_right_.png"
				  align="absmiddle" title="<ssf:nlt tag="general.nextPage"/>"/>
				</a>
				</c:if>
				<c:if test="${empty ss_miniblog_statuses}">
				<img align="absmiddle" src="<html:imagesPath/>pics/sym_arrow_right_g.png"/>
				</c:if>
			</span>	
		</div>
	</td>
  </tr>
  <tr>
	<td valign="top">
	  <ul style="padding-left: 3px; margin-top: 0px;">
	  <c:forEach var="status" items="${ss_miniblog_statuses}">
	    <li class="ss_list-style-image_miniblog">
		  <span class="ss_miniblog_subhead ss_bold" style="font-size: 11px;"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				value="${status.date}" type="both" 
				timeStyle="short" dateStyle="short" />
		  </span>
		  <div class="ss_size_13px">${status.description}</div>
	    </li>
	  </c:forEach>
	  </ul>
  </td>
  </tr>
  </table>
  <div align="right">
    <input type="button" class="ss_submit" value='<ssf:nlt tag="button.close"/>' 
      onClick="ss_cancelButtonCloseWindow();return false;"/>
  </div>
</ssf:form>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
