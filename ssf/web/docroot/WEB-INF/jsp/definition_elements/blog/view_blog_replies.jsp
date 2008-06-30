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
<% // View blog reply count %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<% // Only show the replies if this is the top entry %>
<c:if test="${empty ssDefinitionEntry.topEntry}" >

<c:set var="ssPersonalTags" value="${ssBlogEntries[ss_blog_docId].ssPersonalTags}" scope="request"/>
<c:set var="ssCommunityTags" value="${ssBlogEntries[ss_blog_docId].ssCommunityTags}" scope="request"/>


<div class="ss_blog_footer">
<table cellspacing="0" cellpadding="0" border="0"><tbody><tr>
<c:if test="${!empty ss_blog_reply_url}">
<td valign="top" style="white-space: nowrap;">
<a href="${ss_blog_reply_url}" 
  onClick="ss_addBlogReply(this, '${renderResponse.namespace}', '${ssBinder.id}', '${ssDefinitionEntry.id}');return false;"
  <ssf:title tag="title.add.comment" />
  >
<div class="ss_iconed_label ss_add_comment"><ssf:nlt tag="blog.addComment"/></div>
</a>
</c:if>
</td>
<td valign="top" style="white-space: nowrap;">
<a href="javascript: ;" onClick="ss_showBlogReplies('${renderResponse.namespace}', '${ssBinder.id}','${ssDefinitionEntry.id}');return false;"
<ssf:title tag="title.view.comments">
	<ssf:param name="value" value="${ssDefinitionEntry.totalReplyCount}" />
</ssf:title>
>
<div class="ss_iconed_label ss_view_something">
<ssf:nlt tag="blog.viewComments"/> [<span id="${renderResponse.namespace}ss_blog_reply_count_${ssDefinitionEntry.id}">${ssDefinitionEntry.totalReplyCount}</span>]
</div>
</a>
</td>
<td valign="top" style="white-space: nowrap;">
<a href="<ssf:url adapter="true" 
		portletName="ss_forum" 
	    action="send_entry_email"
	    binderId="${ssDefinitionEntry.parentBinder.id}"
	    entryId="${ssDefinitionEntry.id}"/>" 
  onClick="ss_openUrlInWindow(this, '_blank');return false;"
  <ssf:title tag="title.send.entry.to.friends" />
><div class="ss_iconed_label ss_send_friend"><ssHelpSpot helpId="workspaces_folders/misc_tools/more_blog_tools" 
offsetX="-25" offsetY="-15" 
title="<ssf:nlt tag="helpSpot.moreBlogTools"/>"></ssHelpSpot><ssf:nlt tag="entry.sendtofriend"/></div></a>
</td>
<td valign="top" style="white-space: nowrap;">
<a onclick=" ss_createPopupDiv(this, '${renderResponse.namespace}ss_subscription_entry${ssDefinitionEntry.id}');return false;" 
	href="<ssf:url
			adapter="true" 
			portletName="ss_forum" 
			action="__ajax_request" 
			actionUrl="false">
				<ssf:param name="operation" value="subscribe" />
				<ssf:param name="binderId" value="${ssDefinitionEntry.parentBinder.id}" />
				<ssf:param name="entryId" value="${ssDefinitionEntry.id}" />
				<ssf:param name="rn" value="ss_randomNumberPlaceholder" />
				<ssf:param name="namespace" value="${renderResponse.namespace}" />
		</ssf:url>" <ssf:title tag="title.subscribe.to.entry"/>>
		<div class="ss_iconed_label ss_subscribe"><ssf:nlt tag="entry.subscribe"/></div>
</a>
</td>	
<td valign="top">
<c:set var="ss_tagObject" value="${ssDefinitionEntry}" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
</td>
</tr></tbody></table>
</div>


<div id="${renderResponse.namespace}ss_blog_replies_${ssDefinitionEntry.id}" 
  style="display:none; visibility:hidden;"></div>
<div id="${renderResponse.namespace}ss_blog_add_reply_${ssDefinitionEntry.id}" 
  style="display:none; visibility:hidden;">
<iframe <ssf:title tag="title.add.reply" />
  id="${renderResponse.namespace}ss_blog_add_reply_iframe_${ssDefinitionEntry.id}"
  name="${renderResponse.namespace}ss_blog_add_reply_iframe_${ssDefinitionEntry.id}"
  src="<html:rootPath/>js/forum/null.html" 
  onLoad="if (parent.ss_showBlogReplyIframe) parent.ss_showBlogReplyIframe(this, '${renderResponse.namespace}', '${ssBinder.id}','${ssDefinitionEntry.id}');" 
  width="100%" frameBorder="0">xxx</iframe>
</div>
</c:if>