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
<% // View blog reply count %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="guestInternalId" value="<%= ObjectKeys.GUEST_USER_INTERNALID %>"/>
<%
	java.lang.Object thisEntry = (java.lang.Object) request.getAttribute("ssDefinitionEntry");
	boolean thisEntryIsEntry = false;
	if (thisEntry instanceof FolderEntry) {
		thisEntryIsEntry = true;
	} else if (thisEntry instanceof Map) {
		thisEntryIsEntry = false;
	}
%>
<c:set var="thisEntryIsEntry" value="<%= thisEntryIsEntry %>"/>

<% // Only show the replies if this is the top entry %>
<c:if test="${!thisEntryIsEntry && ssDefinitionEntry._entryType == 'entry'}" >
<div class="ss_clear"></div>
<div class="ss_blog_footer">
	<ssHelpSpot helpId="workspaces_folders/misc_tools/more_blog_tools" 
		offsetX="0" offsetY="8" 
		title="<ssf:nlt tag="helpSpot.moreBlogTools"/>">
	</ssHelpSpot>

	<ul class="ss_nobullet">
	<c:if test="${!empty ss_blog_reply_url}">
	<li>
		<a href="${ss_blog_reply_url}" 
		  onClick="ss_addBlogReply(this, '${renderResponse.namespace}', '${ssBinder.id}', '${ssDefinitionEntry._docId}');return false;"
		  <ssf:title tag="title.add.comment" />
		  ><span><img src="<html:imagesPath/>icons/add_comment.gif"/><ssf:nlt tag="blog.addComment"/></span></a>
	</li>
	</c:if>
	<li>
		<a href="javascript: ;" onClick="ss_showBlogReplies('${renderResponse.namespace}', '${ssBinder.id}','${ssDefinitionEntry._docId}');return false;"
		<ssf:title tag="title.view.comments">
			<ssf:param name="value" value="${ssDefinitionEntry._totalReplyCount}" />
		</ssf:title>
		><span><img src="<html:imagesPath/>icons/view_something.gif"/><ssf:nlt tag="blog.viewComments"/>
		[<span style="padding:0px !important;" 
		  id="${renderResponse.namespace}ss_blog_reply_count_${ssDefinitionEntry._docId}"
		>${ssDefinitionEntry._totalReplyCount}</span>]</span></a>
	</li>
<c:if test="${!empty ssUser.emailAddress}">
	<li>
	<a href="<ssf:url adapter="true" 
			portletName="ss_forum" 
		    action="send_entry_email"
		    binderId="${ssBinder.id}"
		    entryId="${ssDefinitionEntry._docId}"/>" 
	  onClick="ss_openUrlInWindow(this, '_blank');return false;"
	  <ssf:title tag="title.send.entry.to.friends" />
	><span><img src="<html:imagesPath/>icons/send_friend.gif"/><ssf:nlt tag="entry.sendtofriend"/></span></a>
	</li>
</c:if>
<c:if test="${ssUser.internalId != guestInternalId}">
	<li>
	<a onclick=" ss_createPopupDiv(this, '${renderResponse.namespace}ss_subscription_entry${ssDefinitionEntry._docId}');return false;" 
		href="<ssf:url
				adapter="true" 
				portletName="ss_forum" 
				action="__ajax_request" 
				actionUrl="false">
					<ssf:param name="operation" value="subscribe" />
					<ssf:param name="binderId" value="${ssBinder.id}" />
					<ssf:param name="entryId" value="${ssDefinitionEntry._docId}" />
					<ssf:param name="rn" value="ss_randomNumberPlaceholder" />
					<ssf:param name="namespace" value="${renderResponse.namespace}" />
			</ssf:url>" <ssf:title tag="title.subscribe.to.entry"/>
			><span><img src="<html:imagesPath/>icons/send_friend.gif"/><ssf:nlt tag="entry.subscribe"/></span></a>
	</li>	
</c:if>
</ul>
</div>


<div id="${renderResponse.namespace}ss_blog_replies_${ssDefinitionEntry._docId}" 
  style="display:none; visibility:hidden;"></div>
<div id="${renderResponse.namespace}ss_blog_add_reply_${ssDefinitionEntry._docId}" 
  style="display:none; visibility:hidden;">
<iframe <ssf:title tag="title.add.comment" />
  id="${renderResponse.namespace}ss_blog_add_reply_iframe_${ssDefinitionEntry._docId}"
  name="${renderResponse.namespace}ss_blog_add_reply_iframe_${ssDefinitionEntry._docId}"
  src="<html:rootPath/>js/forum/null.html" 
  onLoad="if (ss_showBlogReplyIframe) ss_showBlogReplyIframe(this, '${renderResponse.namespace}', '${ssBinder.id}','${ssDefinitionEntry._docId}');" 
  width="100%" frameBorder="0">Micro Focus Vibe</iframe>
</div>
</c:if>