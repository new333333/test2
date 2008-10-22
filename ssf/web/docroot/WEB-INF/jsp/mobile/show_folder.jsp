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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${!empty ssBinder.title}">
  <c:set var="ss_windowTitle" value="${ssBinder.title}" scope="request"/>
</c:if>
<%@ include file="/WEB-INF/jsp/mobile/mobile_init.jsp" %>
<div class="ss_mobile">
<%@ include file="/WEB-INF/jsp/mobile/masthead.jsp" %>

<div>
  <a href="<ssf:url adapter="true" portletName="ss_forum" 
					action="__ajax_mobile" actionUrl="false" 
					binderId="${ssBinder.id}"
					operation="mobile_whats_new" ><ssf:param
					name="type" value="whatsNew"/></ssf:url>">
    <span class="ss_bold"><ssf:nlt tag="toolbar.menu.whatsNew"/></span>
  </a>&nbsp;
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  <a href="<ssf:url adapter="true" portletName="ss_forum" 
					action="__ajax_mobile" actionUrl="false" 
					binderId="${ssBinder.id}"
					operation="mobile_whats_new" ><ssf:param
					name="type" value="unseen"/></ssf:url>">
    <span class="ss_bold"><ssf:nlt tag="toolbar.menu.whatsUnseen"/></span>
  </a>
</div>
<br/>
<div class="ss_mobile_breadcrumbs">
<c:if test="${empty ssBinder.parentFolder && !empty ssBinder.parentBinder}">
//<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.parentBinder.id}" 
	action="__ajax_mobile" operation="mobile_show_workspace" 
	actionUrl="false" />">${ssBinder.parentBinder.title}</a>
<br/>&nbsp;&nbsp;</c:if>
<c:if test="${!empty ssBinder.parentFolder}">
//<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.parentFolder.id}" 
	action="__ajax_mobile" operation="mobile_show_folder" 
	actionUrl="false" />">${ssBinder.parentFolder.title}</a>
<br/>&nbsp;&nbsp;</c:if>
//<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.id}" 
	action="__ajax_mobile" operation="mobile_show_folder" 
	actionUrl="false" />">${ssBinder.title}</a>
</div>
<br/>

<c:if test="${!empty ss_mobileBinderDefUrlList}">
  <form name="addEntryForm" 
  		action="<ssf:url adapter="true" portletName="ss_forum" 
			binderId="${ssBinder.id}" 
			action="__ajax_mobile" 
			operation="mobile_add_entry" 
			actionUrl="true" />" 
		method="post">
  <table>
  <tr>
  <td valign="top">
  <select name="url" size="1">
    <c:if test="${fn:length(ss_mobileBinderDefUrlList) == 1}">
	  <c:forEach var="def" items="${ss_mobileBinderDefUrlList}">
	    <option value="${def.url}"><ssf:nlt tag="button.add"/>: ${def.title}</option>
	  </c:forEach>
    </c:if>
    <c:if test="${fn:length(ss_mobileBinderDefUrlList) > 1}">
      <option value="">--<ssf:nlt tag="mobile.add"/>--</option>
	  <c:forEach var="def" items="${ss_mobileBinderDefUrlList}">
	    <option value="${def.url}">${def.title}</option>
	  </c:forEach>
	</c:if>
  </select>
  </td>
  <td valign="top">
  <input type="submit" name="goBtn" value="<ssf:nlt tag="button.ok"/>">
  </td>
  </tr>
  </table>  
  </form>
</c:if>

<table class="ss_mobile" cellspacing="0" cellpadding="0" border="0">
<c:forEach var="entry1" items="${ssFolderEntries}" >
<jsp:useBean id="entry1" type="java.util.HashMap" />
  <tr><td align="right" valign="top" style="padding:0px 4px 2px 4px;">${entry1._docNum}.</td>
	<td>
	<div class="ss_mobile_folder_list">
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
		folderId="${ssBinder.id}"  entryId="${entry1._docId}"
		action="__ajax_mobile" operation="mobile_show_entry" actionUrl="false" />">
    	<c:if test="${empty entry1.title}">
    		(<ssf:nlt tag="entry.noTitle"/>)
    	</c:if>
		<c:out value="${entry1.title}"/>
	  </a>
	</div>
  </td></tr>
</c:forEach>
<tr><td></td><td></td></tr>
<tr><td colspan="2">
<table><tr><td width="30">
<c:if test="${!empty ss_prevPage}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.id}" 
	action="__ajax_mobile" 
	operation="mobile_show_folder" 
	actionUrl="false" ><ssf:param name="pageNumber" value="${ss_prevPage}"/></ssf:url>">&lt;&lt;&lt;</a>
</c:if>
</td><td style="padding-left:30px;">
<c:if test="${!empty ss_nextPage}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.id}" 
	action="__ajax_mobile" 
	operation="mobile_show_folder" 
	actionUrl="false" ><ssf:param name="pageNumber" value="${ss_nextPage}"/></ssf:url>">&gt;&gt;&gt;</a>
</c:if>
</td></tr></table>
</td></tr>
</table>
<br/>
<div class="ss_mobile_breadcrumbs ss_mobile_small">
<c:if test="${empty ssBinder.parentFolder && !empty ssBinder.parentBinder}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.parentBinder.id}" 
	action="__ajax_mobile" operation="mobile_show_workspace" 
	actionUrl="false" />"><ssf:nlt tag="mobile.returnToParentWorkspace"/></a>
<br/>
</c:if>
<c:if test="${!empty ssBinder.parentFolder}">
<a href="<ssf:url adapter="true" portletName="ss_forum" 
	folderId="${ssBinder.parentFolder.id}" 
	action="__ajax_mobile" operation="mobile_show_folder" 
	actionUrl="false" />"><ssf:nlt tag="mobile.returnToParentFolder"/></a>
</c:if>
</div>

<br/>
<%@ include file="/WEB-INF/jsp/mobile/footer.jsp" %>
</div>

</body>
</html>
