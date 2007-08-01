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
<% // Show Link to Sign the guestbook and form for this %>



	<div style="text-align: right; margin: 5px; ">
	<c:if test="${!empty addDefaultEntryURL}">
		<a href="${addDefaultEntryURL}" onClick="ss_signGuestbook('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', this);return false;"><span class="ss_bold"><ssf:nlt tag="guestbook.addEntry"/></span></a>
	</c:if>		
	</div>

<div id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_add_entry_from_iframe" style="display:none; visibility:hidden;">
<iframe id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_new_guestbook_entry_iframe"
  name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_new_guestbook_entry_iframe"
  onLoad="ss_showSignGuestbookIframe('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', this);" 
  width="100%" frameBorder="0">xxx</iframe>
</div>