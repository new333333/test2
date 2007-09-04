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
<% // View entry attachments %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.ssfs.util.SsfsUtil" %>

<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<%
boolean isIE = BrowserSniffer.is_ie(request);
boolean isAppletSupported = SsfsUtil.supportApplets();
%>

<div class="ss_entryContent">

<table width="90%" border="0" valign="top" cellpadding="1" cellspacing="0">
<tr>
	<td valign="top">
		<span id="ss_browse_div_position${ssDefinitionEntry.id}${ss_attachments_namespace}" 
		class="ss_style ss_labelLeft"><c:out value="${property_caption}"/>&nbsp;&nbsp;</span>
	</td>

	<td valign="top" width="100%" align="left">
  <ssHelpSpot helpId="workspaces_folders/entries/attachments" 
  
    <c:if test="<%= isIE %>">
    offsetX="-10" offsetY="2"  
    </c:if>
    <c:if test="<%= !isIE %>">
    offsetX="-10" offsetY="-20"   
    </c:if>
    
    
     
    title="<ssf:nlt tag="helpSpot.attachments"/>"></ssHelpSpot>
	<ssf:ifnotaccessible>
	
		<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
			<% if (isAppletSupported) { %>
				<a id="ss_dropbox_div_position${ssDefinitionEntry.id}${ss_attachments_namespace}" 
				href="javascript: ;" 
				onClick="ss_showAddAttachmentDropbox('${ssDefinitionEntry.parentBinder.id}', '${ssDefinitionEntry.id}', '${ss_attachments_namespace}'); return false;"
				title="<ssf:nlt tag="entry.AttachFilesByApplet"/>"><img class="ss_icon_link" src="<html:imagesPath/>icons/upload_applet.gif"/></a>
			<% } %>
		</c:if>
	
	</ssf:ifnotaccessible>
		
	<% if (com.sitescape.team.web.util.BinderHelper.isWebdavSupported(request)) { %>
	<c:if test="${ss_folderViewStyle == 'blog' && !empty ssFolderEntriesWebDAVURLs[ss_entryIDForWebDAV]}">
		<c:set var="ss_entryIDForWebDAV" value="${ssDefinitionEntry.id}" />
		<a class="" title="<ssf:nlt tag="entry.AttachFilesByWebDav"/>" src="<html:imagesPath/>icons/upload_webdav.gif"/>
		  style="behavior: url(#default#AnchorClick);" 
		  folder="${ssFolderEntriesWebDAVURLs[ss_entryIDForWebDAV]}" 
		  href="${ssFolderEntriesWebDAVURLs[ss_entryIDForWebDAV]}" 
		  target="_blank"><img class="ss_icon_link"</a>		
	</c:if>
	<c:if test="${ss_folderViewStyle != 'blog' && !empty ssWebDavURL}">
		<a class=""  title="<ssf:nlt tag="entry.AttachFilesByWebDav"/>"
		  style="behavior: url(#default#AnchorClick);" 
		  folder="${ssWebDavURL}" href="${ssWebDavURL}" 
		  target="_blank"
		  ><img class="ss_icon_link" src="<html:imagesPath/>icons/upload_webdav.gif"/></a>		
	</c:if>
	<% } %>
	
	<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">

		<a class="" title="<ssf:nlt tag="entry.AttachFilesByWebBrowse"/>" href="javascript: ;" 
		  onClick="ss_showAddAttachmentBrowse('${ssDefinitionEntry.parentBinder.id}', '${ssDefinitionEntry.id}', '${ss_attachments_namespace}'); return false;"
		  ><img class="ss_icon_link"  src="<html:imagesPath/>icons/upload_browser.gif"/></a>
	
	</c:if>
	
	<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
	  <% /* TODO: Add test if IC Broker is enabled (ICBrokerModule.isEnabled()) */ %>
	  <a class="" title="<ssf:nlt tag="attachMeeting.attachResults"/>" 
	    href="javascript: ;" 
	    onClick="ss_showAttachMeetingRecords('${ssDefinitionEntry.parentBinder.id}', '${ssDefinitionEntry.id}', '${ss_attachments_namespace}'); return false;"
	    ><img class="ss_icon_link"  src="<html:imagesPath/>icons/upload_meeting.gif"/></a>
	  <div style="display: none; " id="ss_status_message"></div>
	
	  <div id="ss_div_fileopen${ssDefinitionEntry.id}${ss_attachments_namespace}" 
	    name="ss_div_fileopen${ssDefinitionEntry.id}${ss_attachments_namespace}" 
	    style="visibility:visible;display:block; width:1px; height:1px;">
		<div align="right">
			<iframe frameborder="0" scrolling="no" 
			  id="ss_iframe_fileopen${ssDefinitionEntry.id}${ss_attachments_namespace}" 
			  name="ss_iframe_fileopen${ssDefinitionEntry.id}${ss_attachments_namespace}" 
			  height="1" width="1">xxx</iframe>
		</div>
	  </div>
	</c:if>		
	</td>
</tr>
<tr>
	<td style="padding-left: 30px;" colspan="2" align="left" width="100%">
					<div id="ss_div_dropbox${ssDefinitionEntry.id}${ss_attachments_namespace}" 
					  class="ss_border_light" style="visibility:hidden;display:none;">
						<div align="right">
						<a onClick="ss_hideAddAttachmentDropbox('${ssDefinitionEntry.id}', '${ss_attachments_namespace}'); return false;"><img 
						  border="0" <ssf:alt tag="alt.hide"/> src="<html:imagesPath/>icons/close_off.gif"/></a>
						</div>	
						<iframe <ssf:title tag="entry.AttachFilesByApplet" /> frameborder="0" scrolling="no" 
						  id="ss_iframe_dropbox${ssDefinitionEntry.id}${ss_attachments_namespace}" 
						  name="ss_iframe_dropbox${ssDefinitionEntry.id}${ss_attachments_namespace}" 
						height="70%" width="100%" 
						onClick="ss_hideAddAttachmentDropbox('${ssDefinitionEntry.id}', '${ss_attachments_namespace}'); return false;">xxx</iframe>
					</div>

					<div id="ss_div_browse${ssDefinitionEntry.id}${ss_attachments_namespace}" 
					  class="ss_border_light" style="visibility:hidden;display:none;">
						<div align="right">
						<a onClick="ss_hideAddAttachmentBrowse('${ssDefinitionEntry.id}', '${ss_attachments_namespace}'); return false;"><img 
						  border="0" <ssf:alt tag="alt.hide"/> src="<html:imagesPath/>icons/close_off.gif"/></a>
						</div>	
						<iframe <ssf:title tag="entry.AttachFilesByWebBrowse" /> frameborder="0" scrolling="no" 
						  id="ss_iframe_browse${ssDefinitionEntry.id}${ss_attachments_namespace}" 
							name="ss_iframe_browse${ssDefinitionEntry.id}${ss_attachments_namespace}" 
							src="<html:rootPath/>js/attachments/entry_attachment_browse.html" height="75%" width="100%">xxx</iframe>
					</div>
					
					<div id="ss_div_attach_meeting_records${ssDefinitionEntry.id}${ss_attachments_namespace}" 
					  style="display: none; padding: 5px; " class="ss_border_light">
						<div align="right">
						<a onClick="ss_hideAddAttachmentMeetingRecords('${ssDefinitionEntry.id}', '${ss_attachments_namespace}'); return false;"><img 
						  border="0" <ssf:alt tag="alt.hide"/> src="<html:imagesPath/>icons/close_off.gif"/></a>
						</div>
						<div id="ss_div_attach_meeting_records_content${ssDefinitionEntry.id}${ss_attachments_namespace}">
						</div>
					</div>
	</td>	
</tr>
</table>

<c:set var="ss_viewEntryAttachmentDivId" value="ss_divAttachmentList${ssDefinitionEntry.id}${renderResponse.namespace}" scope="request"/>
<c:set var="ss_namespace_attach" value="${renderResponse.namespace}" scope="request"/>

<%@ include file="/WEB-INF/jsp/definition_elements/view_entry_attachments_list.jsp" %>

</div>

<c:if test="${ssConfigJspStyle == 'mail'}">
<% // The mail support is in "definition_elements/mail" %>
</c:if>
