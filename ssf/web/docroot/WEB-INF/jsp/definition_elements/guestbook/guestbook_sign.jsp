<% // Show Link to Sign the guestbook and form for this %>



	<div style="text-align: right; margin: 5px; ">
	<c:if test="${!empty addDefaultEntryURL}">
		<a href="${addDefaultEntryURL}" onClick="ss_signGuestbook<portlet:namespace/>(this);return false;"><span class="ss_bold"><ssf:nlt tag="guestbook.addEntry"/></span></a>
	</c:if>		
	</div>

<div id="<portlet:namespace/>_add_entry_from_iframe" style="display:none; visibility:hidden;">
<iframe id="<portlet:namespace/>_new_guestbook_entry_iframe"
  name="<portlet:namespace/>_new_guestbook_entry_iframe"
  onLoad="ss_showSignGuestbookIframe<portlet:namespace/>(this);" 
  width="100%">xxx</iframe>
</div>