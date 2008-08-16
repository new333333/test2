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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body class="ss_style_body">
</ssf:ifadapter>
<script type="text/javascript">
function ss_showMiniblog${renderResponse.namespace}(id, obj) {
	ss_viewMiniBlog(id, false);
}
</script>

<div id="ss_pseudoPortalDiv${renderResponse.namespace}">
<div class="ss_style ss_portlet ss_content_outer">
  <div>
    <div style="float:right;">
	      <span class="ss_labelAbove"><ssf:nlt tag="navigation.findPerson"/></span>
	      <ssf:find type="user"
		    clickRoutine="ss_showMiniblog${renderResponse.namespace}"
		    leaveResultsVisible="false"
		    width="100px" singleItem="true"/> 
	</div>
    <div><h2>xxx MiniBlog xxx</h2></div>
    <div class="ss_clear_float"></div>
    <h3><ssf:showUser user="${ss_miniblog_user}"/></h3>
    
  </div>
  <table>
  <tr>
  <td valign="top" align="center">
	  <ssf:buddyPhoto style="ss_thumbnail_standalone ss_thumbnail_standalone_small" 
					photos="${ss_miniblog_user.customAttributes['picture'].value}" 
					folderId="${ss_miniblog_user.parentBinder.id}" entryId="${ss_miniblog_user.id}" />
  </td>
  <td valign="top" style="padding-left:20px;">
	  <ul>
	  <c:forEach var="status" items="${ss_miniblog_statuses}">
	    <li style="padding-bottom:10px;">
		  <span><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
					      value="${status.date}" type="both" 
						  timeStyle="short" dateStyle="short" /></span><br/>
		  <span class="ss_italic">${status.description}</span>
	    </li>
	  </c:forEach>
	  </ul>
  </td>
  </tr>
  </table>
</div>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
