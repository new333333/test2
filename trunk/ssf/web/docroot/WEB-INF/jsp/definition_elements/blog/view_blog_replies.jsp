<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% // View blog reply count %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<% // Only show the replies if this is the top entry %>
<c:if test="${empty ssDefinitionEntry.topEntry}" >

<c:set var="ssPersonalTags" value="${ssBlogEntries[ss_blog_docId].ssPersonalTags}" scope="request"/>
<c:set var="ssCommunityTags" value="${ssBlogEntries[ss_blog_docId].ssCommunityTags}" scope="request"/>


<div style="padding-bottom: 14px; padding-left: 22px">
<table cellspacing="0" cellpadding="0" border="0"><tbody><tr>
<c:if test="${!empty ss_blog_reply_url}">
<td valign="top" style="white-space: nowrap;">
<a href="${ss_blog_reply_url}" 
  onClick="ss_addBlogReply<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(this, '${ssDefinitionEntry.id}');return false;"
  <ssf:title tag="title.add.comment" />
  >
<div class="ss_iconed_label ss_add_comment"><ssf:nlt tag="blog.addComment"/></div>
</a>
</c:if>
</td>
<td valign="top" style="white-space: nowrap;">
<a href="javascript: ;" onClick="ss_showBlogReplies<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('${ssDefinitionEntry.id}', '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>');return false;"
<ssf:title tag="title.view.comments">
	<ssf:param name="value" value="${ssDefinitionEntry.totalReplyCount}" />
</ssf:title>
>
<div class="ss_iconed_label ss_view_something">
<ssf:nlt tag="blog.viewComments"/> [<span id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_reply_count_${ssDefinitionEntry.id}">${ssDefinitionEntry.totalReplyCount}</span>]
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
<a onclick=" ss_createPopupDiv(this, '<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_subscription_entry${ssDefinitionEntry.id}');return false;" 
	href="<ssf:url
			adapter="true" 
			portletName="ss_forum" 
			action="__ajax_request" 
			actionUrl="true">
				<ssf:param name="operation" value="entry_subscribe" />
				<ssf:param name="binderId" value="${ssDefinitionEntry.parentBinder.id}" />
				<ssf:param name="entryId" value="${ssDefinitionEntry.id}" />
				<ssf:param name="rn" value="ss_randomNumberPlaceholder" />
				<ssf:param name="namespace" value="${renderResponse.namespace}" />
		</ssf:url>" <ssf:title tag="title.subscribe.to.entry"/>>
		<div class="ss_iconed_label ss_subscribe"><ssf:nlt tag="entry.subscribe"/></div>
</a>
</td>	
<td valign="top">
<%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>
</td>
</tr></tbody></table>
</div>


<div id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_replies_${ssDefinitionEntry.id}" 
  style="display:none; visibility:hidden;"></div>
<div id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_add_reply_${ssDefinitionEntry.id}" 
  style="display:none; visibility:hidden;">
<iframe <ssf:title tag="title.add.reply" />
  id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_add_reply_iframe_${ssDefinitionEntry.id}"
  name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>ss_blog_add_reply_iframe_${ssDefinitionEntry.id}"
  onLoad="if (parent.ss_showBlogReplyIframe<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>) parent.ss_showBlogReplyIframe<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(this, '${ssDefinitionEntry.id}');" 
  width="100%" frameBorder="0">xxx</iframe>
</div>
</c:if>