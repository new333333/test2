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
<%
//treename must start with editForum
String wsTreeName = "editForum_" + renderResponse.getNamespace();
%>
<form class="ss_style ss_form" action="<portlet:actionURL windowState="maximized"/>" method="post" name="${renderResponse.namespace}fm">

<table class="ss_style" width="100%"><tr><td>
<c:if test="${!empty ssFolderList}">
<table class="ss_style" cellspacing="0" cellpadding="0">
<tr><th align="left"><ssf:nlt tag="portlet.forum.selected.bookmarks"/></th></tr>
<c:forEach var="folder" items="${ssFolderList}">
<tr>
  <td>
    <input type="checkbox" name="del_${folder.id}" id="${renderResponse.namespace}del_${folder.id}"/>
    <label for="${renderResponse.namespace}del_${folder.id}"><c:if test="${!empty folder.parentBinder}">
    	${folder.parentBinder.title} // 
    </c:if>
    ${folder.title}</label>
  </td>
</tr>
</c:forEach>
</table>
<c:if test="${!empty ssFolderList}">
  <span class="ss_fineprint" style="padding-left:4px;">[<ssf:nlt tag="portlet.forum.delete.select.bookmarks"/>]</span>
  <br/>
</c:if>
<br>
</c:if>

<br>
<span class="ss_bold"><ssf:nlt tag="portlet.forum.select.forums"/></span>
<br>
<script type="text/javascript">
function <%= wsTreeName %>_showId(forum, obj) {
	if (obj.ownerDocument) {
		var cDocument = obj.ownerDocument;
	} else if (obj.document) {
		cDocument = obj.document;
	}
	if (cDocument) {
		var r = cDocument.getElementById("ss_tree_checkbox<%= wsTreeName %>id" + forum);
		if (r) {
			if (r.checked !== undefined) {
				r.checked = !r.checked;
			}
			if (r.onclick !== undefined) {
				r.onclick();
			}
		}
	}
	return false;
}
</script>
<ssf:tree treeName="<%= wsTreeName %>"  treeDocument="${ssWsDomTree}" 
 	topId="${ssWsDomTreeBinderId}" rootOpen="true" 
	  multiSelect="${ssBinderIdList}" multiSelectPrefix="id" />

<br>
<input type="submit" class="ss_submit" name="applyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">
<input style="margin-left:15px;" type="submit" class="ss_submit" name="closeBtn"
 value="<ssf:nlt tag="button.close"/>"
 onClick="self.location.href='<portlet:renderURL windowState="normal" portletMode="view"/>';return false;"/>
<br>

</td></tr></table>
</form>

