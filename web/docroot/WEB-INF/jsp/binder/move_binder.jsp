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
<c:set var="wsTreeName" value="${renderResponse.namespace}_wsTree"/>
<script type="text/javascript">
function ${wsTreeName}_showId(forum, obj, action) {
	if (obj.ownerDocument) {
		var cDocument = obj.ownerDocument;
	} else if (obj.document) {
		cDocument = obj.document;
	}
	if (cDocument) {
		var r = cDocument.getElementById("ss_tree_radio${wsTreeName}destination" + forum);
		if (r) {
			if (r.checked !== undefined) {
				r.checked = true;
			}
			if (r.onclick !== undefined) {
				r.onclick();
			}
		}
	}
}

</script>

<div class="ss_style ss_portlet">
<div style="padding:4px;">
<c:if test="${ssOperation == 'move'}">
<c:if test="${ssBinder.entityType == 'folder'}">
  <span class="ss_bold ss_largerprint"><ssf:nlt tag="move.folder"/></span>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <span class="ss_bold ss_largerprint"><ssf:nlt tag="move.workspace"/></span>
</c:if>
</c:if>
<c:if test="${ssOperation != 'move'}">
<c:if test="${ssBinder.entityType == 'folder'}">
  <span class="ss_bold ss_largerprint"><ssf:nlt tag="copy.folder"/></span>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <span class="ss_bold ss_largerprint"><ssf:nlt tag="copy.workspace"/></span>
</c:if>
</c:if>
<br/>
<br/>
<c:if test="${ssBinder.entityType == 'folder'}">
  <span><ssf:nlt tag="move.currentFolder"/>: </span>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <span><ssf:nlt tag="move.currentWorkspace"/></span>
</c:if>
<span class="ss_bold"><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>
<br/>
<form class="ss_style ss_form" method="post" > 
		
<br/>

<span class="ss_bold"><ssf:nlt tag="move.selectDestination"/></span>
<br/>
<div class="ss_indent_large">
<ssf:tree treeName="${wsTreeName}"
	treeDocument="${ssWsDomTree}"  
 	rootOpen="true"
	singleSelect="" 
	singleSelectName="destination" />

</div>

<br/>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>">
</form>
</div>
</div>
