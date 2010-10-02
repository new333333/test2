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
<% //view the wiki tabs %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
  <c:if test="${empty ss_tagDivNumber}">
  	<c:set var="ss_tagDivNumber" value="0" scope="request"/>
  </c:if>

<script type="text/javascript">
function ss_showHideCommentsAndAttachmentsSection() {
	ss_hideTagsSection();
	var divObj = self.document.getElementById("ss_commentsAndAttachmentsSection");
	if (divObj != null) {
		if (divObj.style.display != "block") {
			divObj.style.display = "block";
		} else {
			divObj.style.display = "none";
		}
	}
}
function ss_hideCommentsAndAttachmentsSection() {
	var divObj = self.document.getElementById("ss_commentsAndAttachmentsSection");
	if (divObj != null) {
		if (divObj.style.display != "none") {
			divObj.style.display = "none";
		}
	}
}

function ss_showHideTagsSection() {
	ss_hideCommentsAndAttachmentsSection();
	var divObj = self.document.getElementById("ss_showHideTagsSection");
	if (divObj != null) {
		if (divObj.style.display != "block") {
			divObj.style.display = "block";
			ss_tagShow("${renderResponse.namespace}", "${ss_tagDivNumber}");
		} else {
			divObj.style.display = "none";
		}
	}
}
function ss_hideTagsSection() {
	var divObj = self.document.getElementById("ss_showHideTagsSection");
	if (divObj != null) {
		if (divObj.style.display != "none") {
			divObj.style.display = "none";
		}
	}
}

</script>
<div>
	<table cellpadding="0" cellspacing="0" style="white-space: nowrap;">
		<tr>
			<td nowrap>
			    <span class="wiki-menu">
			      <a href="javascript: ;" onClick="ss_showHideTagsSection();return false;">
			        <ssf:nlt tag="tags.tags"/>
			      </a>
			    </span>
			    <c:if test="${ss_commentsAndAttachmentsSectionRequested}">
			      <span class="wiki-menu">
			        <a href="javascript: ;" onClick="ss_showHideCommentsAndAttachmentsSection();return false;">
			          <ssf:nlt tag="wiki.commentsAndAttachments">
			            <ssf:param name="value" value="${ss_commentsAndAttachmentsReplyCount}"/>
			            <ssf:param name="value" value="${ss_commentsAndAttachmentsAttachmentCount}"/>
			          </ssf:nlt>
			        </a>
			      </span>
			    </c:if>
			</td>
		</tr>	
	</table>	
</div>

<div id="ss_showHideTagsSection" class="ss_entryContent" style="display:none;" >
  <jsp:include page="/WEB-INF/jsp/definition_elements/tag_view.jsp" />
</div>

<c:if test="${ss_commentsAndAttachmentsSectionRequested}">
  	<c:set var="ss_delayShowingCommentsAndAttachments" value="false" scope="request"/>
	<div id="ss_commentsAndAttachmentsSection" class="ss_entryContent" style="display:none;" >
	  <jsp:include page="/WEB-INF/jsp/definition_elements/view_entry_comments_and_attachments.jsp" />
	</div>
</c:if>

