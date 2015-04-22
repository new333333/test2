<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.Utils" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%@ page import="org.kablink.teaming.util.ResolveIds" %>

<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<jsp:useBean id="ssWorkArea" type="org.kablink.teaming.security.function.WorkArea" scope="request" />
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("access.configure") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript" src="<html:rootPath />js/binder/ss_access.js"></script>
<script type="text/javascript">

/**
 * 
 */
function handleCloseBtn()
{
<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
		// Tell the Teaming GWT ui to close the administration content panel.
		if ( window.parent.ss_closeAdministrationContentPanel ) {
			window.parent.ss_closeAdministrationContentPanel();
		} else {
			ss_cancelButtonCloseWindow();
		}

		return false;
<% 	}
	else { %>
		ss_cancelButtonCloseWindow();
		return false;
<%	} %>
	
}// end handleCloseBtn()

function ss_treeShowIdAccessControl${renderResponse.namespace}(id, obj, action, namespace) {
	action = 'configure_access_control';
	if (typeof namespace == "undefined" || namespace == null) namespace = "";
	var binderId = id;
	// See if the id is formatted (e.g., "ss_favorites_xxx")
	if (binderId.indexOf("_") >= 0) {
		var binderData = id.substr(13).split("_");
		binderId = binderData[binderData.length - 1];
	}

	// Try to find the base urls from this namespace
	var url = "";
	try {
		eval("url = ss_baseBinderUrlNoWS" + namespace)
	} catch(e) {}
	
	// Build a url to go to
	if (url == "") url = ss_baseBinderUrlNoWS;
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	url += "&operation2=debug";
	// console.log(url);
	ss_setSelfLocation(url);
	return false;
}

function goBackToAccessControlForm(binderId) {
	action = 'configure_access_control';

	// Try to find the base urls from this namespace
	var url = ss_baseBinderUrlNoWS;
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	// console.log(url);
	ss_setSelfLocation(url);
	return false;
}

</script>

<div style="padding:4px 0px 6px 10px;">
<a href="javascript:" onClick="goBackToAccessControlForm('${ss_accessNetFolderMap['folder'].id}');">View the Access Control Form...</a>
</div>

<c:set var="ss_breadcrumbsShowIdRoutine" 
  value="ss_treeShowIdAccessControl${renderResponse.namespace}" 
  scope="request" />
<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />

<div>
<c:if test="${operation2 == 'debug' && !empty ss_accessNetFolderMap}">
  <br/>
  <br/>

  <ssf:box style="rounded">
    <table cellspacing="2" cellpadding="2" border="1">
      <tr>
        <td>Net Folder:</td>
        <td>${ss_accessNetFolderMap['folder'].title}</td>
      </tr>
      <tr>
        <td>Folder Creator from File System:</td>
        <td>Id = ${ss_accessNetFolderMap['creator'].id}, Name = ${ss_accessNetFolderMap['creator'].name}, Title = ${ss_accessNetFolderMap['creator'].title}</td>
      </tr>
      <tr>
       <td>Folder Owner from Filr:</td>
        <td>Id = ${ss_accessNetFolderMap['owner'].id}, Name = ${ss_accessNetFolderMap['owner'].name}, Title = ${ss_accessNetFolderMap['owner'].title}</td>
      </tr>
      <tr>
       <td>Folder Creator from Filr:</td>
        <td>Id = ${ss_accessNetFolderMap['filr_creator'].id}, Name = ${ss_accessNetFolderMap['filr_creator'].name}, Title = ${ss_accessNetFolderMap['filr_creator'].title}</td>
      </tr>
      <tr>
        <td>dirAcl:</td>
        <td>${ss_accessNetFolderMap['dirAcl']}</td>
      </tr>
      <tr>
        <td>normalizedAcl:</td>
        <td>
          <c:forEach var="functionMap" items="${ss_accessNetFolderMap['normalizedAcl']}">
              Function: <ssf:nlt tag="${functionMap.key.name }"/><br/>
              <c:if test="${empty functionMap.value}">&nbsp;&nbsp;&nbsp;No membership!<br/></c:if>
              <c:forEach var="p" items="${functionMap.value }">
                &nbsp;&nbsp;&nbsp;Principal: Id=${p.id}, Name=${p.name }, Title=${p.title} }<br/>
              </c:forEach>
          </c:forEach>
        </td>
      </tr>
      <tr>
        <td>Function Membership Inherited:</td>
        <td>
          ${ss_accessNetFolderMap['function_membership_inherited'] }
        </td>
      </tr>
      <tr>
        <td>External Function Membership Inherited:</td>
        <td>
          ${ss_accessNetFolderMap['ext_function_membership_inherited'] }
        </td>
      </tr>
      <tr>
        <td>functionMemberships:</td>
        <td>
        <c:forEach var="functionMap" items="${ss_accessNetFolderMap['functionMemberships']}">
          Function: <ssf:nlt tag="${functionMap.key.name }"/><br/>
          Membership: ${functionMap.value}<br/>
        </c:forEach>
        </td>
      </tr>
      <tr>
        <td>Vibe Roles:</td>
        <td>
          <c:forEach var="functionMap" items="${ss_accessNetFolderMap['vibeRoles']}">
              Function: <ssf:nlt tag="${functionMap.key.name }"/><br/>
              <c:if test="${empty functionMap.value}">&nbsp;&nbsp;&nbsp;No membership!<br/></c:if>
              <c:forEach var="p" items="${functionMap.value }">
                &nbsp;&nbsp;&nbsp;Principal: Id=${p.id}, Name=${p.name }, Title=${p.title} }<br/>
              </c:forEach>
          </c:forEach>
        </td>
      </tr>
    </table>
    
    <br/>
    <br/>
    
    <div>
    <span>Sub-Folders in "${ss_accessNetFolderMap['folder'].title}"</span>
    </div>
    <div style="padding-left:6px;">
	    <table cellspacing="2" cellpadding="2" border="1">
	    <th>Title</th>
	    <th>Owner</th>
	    <th>ACL Inherited</th>
	      <c:forEach var="resourceItem" items="${ss_accessNetFolderMap['childrenDirList']}">
		      <tr>
		        <td>${resourceItem.name }</td>
		        <td>${resourceItem.ownerId }</td>
		        <td>
		          <c:if test="${resourceItem.aclInherited }">True</c:if>
		          <c:if test="${!resourceItem.aclInherited }">False</c:if>
		        </td>
		      </tr>
	      </c:forEach>
	    </table>
    </div>

    <br/>
    <br/>
    
    <div>
    <span>Files in "${ss_accessNetFolderMap['folder'].title}"</span>
    </div>
    <div style="padding-left:6px;">
	    <table cellspacing="2" cellpadding="2" border="1">
	    <th>Title</th>
	    <th>Owner</th>
	    <th>ACL Inherited</th>
	      <c:forEach var="resourceItem" items="${ss_accessNetFolderMap['childrenFileList']}">
		      <tr>
		        <td>${resourceItem.name }</td>
		        <td>${resourceItem.ownerId }</td>
		        <td>
		          <c:if test="${resourceItem.aclInherited }">True</c:if>
		          <c:if test="${!resourceItem.aclInherited }">False</c:if>
		        </td>
		      </tr>
	      </c:forEach>
	    </table>
	</div>
    
  </ssf:box>
</c:if>

<br/>
<br/>

</div>

</body>
</html>
