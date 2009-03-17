<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
    <div align="right">
	      <span class="ss_labelAbove ss_labelminiBlog"><ssf:nlt tag="navigation.findPerson"/></span>
	      <ssf:find type="user"
		    clickRoutine="ss_showMiniblog${renderResponse.namespace}"
		    leaveResultsVisible="false"
		    width="100px" singleItem="true"/> 
	</div>
    <div class="ss_form_subhead"><ssf:showUser user="${ss_miniblog_user}" target="_blank" close="true" /></div>
    
  </div>
  <div id="ss_nextPage" align="right">
	<c:if test="${ss_miniblogPage > '0'}">
	<a href="javascript: ;" 
	  onClick="ss_showMiniblogPage${renderResponse.namespace}('${ss_miniblog_user.id}', '${ss_miniblogPage}', 'previous');return false;">
	<img src="<html:imagesPath/>pics/sym_arrow_left_.gif" 
	  title="<ssf:nlt tag="general.previousPage"/>"/>
	</a>
	</c:if>
	<c:if test="${empty ss_miniblogPage || ss_miniblogPage <= '0'}">
	<img src="<html:imagesPath/>pics/sym_arrow_left_g.gif"/>
	</c:if>
	<c:if test="${!empty ss_miniblog_statuses}">
	<a href="javascript: ;" 
	  onClick="ss_showMiniblogPage${renderResponse.namespace}('${ss_miniblog_user.id}', '${ss_miniblogPage}', 'next');return false;">
	<img src="<html:imagesPath/>pics/sym_arrow_right_.gif"
	  title="<ssf:nlt tag="general.nextPage"/>"/>
	</a>
	</c:if>
	<c:if test="${empty ss_miniblog_statuses}">
	<img src="<html:imagesPath/>pics/sym_arrow_right_g.gif"/>
	</c:if>
  </div>
  
  <table>
  <tr>
  <td valign="top" align="center">
	  <ssf:buddyPhoto style="ss_thumbnail_standalone ss_thumbnail_standalone_small" 
					photos="${ss_miniblog_user.customAttributes['picture'].value}" 
					folderId="${ss_miniblog_user.parentBinder.id}" entryId="${ss_miniblog_user.id}" />
  </td>
  <td valign="top" style="padding-left:10px;">
	  <ul>
	  <c:forEach var="status" items="${ss_miniblog_statuses}">
	    <li class="ss_list-style-image_miniblog">
		  <span class="ss_miniblog_subhead"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
					      value="${status.date}" type="both" 
						  timeStyle="short" dateStyle="short" /></span><br/>
		  <span class="">${status.description}</span>
	    </li>
	  </c:forEach>
	  </ul>
  </td>
  </tr>
  </table>
  <div align="right">
    <input type="button" class="ss_submit" value='<ssf:nlt tag="button.close"/>' 
      onClick="self.window.close();return false;"/>
  </div>
</ssf:form>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
