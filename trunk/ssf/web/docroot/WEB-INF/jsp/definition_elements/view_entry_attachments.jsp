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

<%
boolean isIE = BrowserSniffer.is_ie(request);
boolean isAppletSupported = SsfsUtil.supportApplets();
%>

<div class="ss_entryContent">

<table width="100%" border="0" valign="top" cellpadding="1" cellspacing="0">
<tr>
	<td valign="top">
		<span id="ss_browse_div_position${ssDefinitionEntry.id}<portlet:namespace/>" 
		class="ss_style ss_labelLeft"><c:out value="${property_caption}"/>&nbsp;&nbsp;</span>
	</td>

	<td valign="top" width="100%" align="left">
  <ssHelpSpot helpId="tools/attachments" offsetX="0" 
    title="<ssf:nlt tag="helpSpot.attachments"/>"></ssHelpSpot>
	<ssf:ifnotaccessible>
	
		<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
			<% if (isAppletSupported) { %>
				<a id="ss_dropbox_div_position${ssDefinitionEntry.id}<portlet:namespace/>" href="javascript: ;" onClick="ss_showAddAttachmentDropbox('${ssDefinitionEntry.parentBinder.id}', '${ssDefinitionEntry.id}', '<portlet:namespace/>'); return false;"
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

		<a class="" title="<ssf:nlt tag="entry.AttachFilesByWebBrowse"/>" href="javascript: ;" onClick="ss_showAddAttachmentBrowse('${ssDefinitionEntry.parentBinder.id}', '${ssDefinitionEntry.id}', '<portlet:namespace/>'); return false;"
		  ><img class="ss_icon_link"  src="<html:imagesPath/>icons/upload_browser.gif"/></a>
	
	</c:if>
	
		<div id="ss_div_fileopen${ssDefinitionEntry.id}<portlet:namespace/>" width="1px" height="1px" name="ss_div_fileopen${ssDefinitionEntry.id}<portlet:namespace/>" style="visibility:hidden;display:none;">
			<div align="right">
				<iframe frameborder="0" scrolling="no" id="ss_iframe_fileopen${ssDefinitionEntry.id}<portlet:namespace/>" name="ss_iframe_fileopen${ssDefinitionEntry.id}<portlet:namespace/>" height="100%" width="100%">xxx</iframe>
			</div>
		</div>
	</td>	
</tr>
<tr>
	<td style="padding-left: 30px;" colspan="2" align="left" width="100%">
					<div id="ss_div_dropbox${ssDefinitionEntry.id}<portlet:namespace/>" class="ss_border_light" style="visibility:hidden;display:none;">
						<div align="right">
						<a onClick="ss_hideAddAttachmentDropbox('${ssDefinitionEntry.id}', '<portlet:namespace />'); return false;"><img 
						  border="0" <ssf:alt tag="alt.hide"/> src="<html:imagesPath/>icons/close_off.gif"/></a>
						</div>	
						<iframe frameborder="0" scrolling="no" id="ss_iframe_dropbox${ssDefinitionEntry.id}<portlet:namespace/>" name="ss_iframe_dropbox${ssDefinitionEntry.id}<portlet:namespace/>" height="70%" width="100%" onClick="ss_hideAddAttachmentDropbox('${ssDefinitionEntry.id}', '<portlet:namespace />'); return false;">xxx</iframe>
					</div>

					<div id="ss_div_browse${ssDefinitionEntry.id}<portlet:namespace/>" class="ss_border_light" style="visibility:hidden;display:none;">
						<div align="right">
						<a onClick="ss_hideAddAttachmentBrowse('${ssDefinitionEntry.id}', '<portlet:namespace/>'); return false;"><img 
						  border="0" <ssf:alt tag="alt.hide"/> src="<html:imagesPath/>icons/close_off.gif"/></a>
						</div>	
						<iframe frameborder="0" scrolling="no" id="ss_iframe_browse${ssDefinitionEntry.id}<portlet:namespace/>" 
							name="ss_iframe_browse${ssDefinitionEntry.id}<portlet:namespace/>" 
							src="<html:rootPath/>js/attachments/entry_attachment_browse.html" height="75%" width="100%">xxx</iframe>
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
