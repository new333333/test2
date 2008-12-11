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
<% // View entry attachments %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.ssfs.util.SsfsUtil" %>

<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<%
boolean isIE = BrowserSniffer.is_ie(request);
boolean isAppletSupported = SsfsUtil.supportApplets();
%>
<script type="text/javascript">
var ss_labelButtonOK = "<ssf:nlt tag="button.ok"/>";
var ss_labelButtonCancel = "<ssf:nlt tag="button.cancel"/>";
var ss_labelEntryChooseFileWarning = "<ssf:nlt tag="entry.chooseFileWarningMessage"/>";
var ss_labelEntryBrowseAddAttachmentHelpText = "<ssf:nlt tag="entry.browseAddAttachmentHelpText"/>";
</script>
<div class="ss_entryContent">

<table width="90%" border="0" valign="top" cellpadding="1" cellspacing="0">
<tr>
	<td valign="bottom" style="padding-left: 19px;">
		<!-- We need the following image so the help spot has an initial position. -->
		<span id="ss_browse_div_position${ssDefinitionEntry.id}${ss_attachments_namespace}" class="ss_style ss_bold ss_smallprint">
		  	<ssHelpSpot helpId="workspaces_folders/entries/attachments" offsetX="-20" offsetY="-5"
    					title="<ssf:nlt tag="helpSpot.attachments"/>">
			</ssHelpSpot>
			<c:out value="${property_caption}"/>:&nbsp;&nbsp;
		</span>
	</td>

	<td valign="top" width="100%" align="left">
	<ssf:ifnotaccessible>
	
		<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
			<% if (isAppletSupported) { %>
				<a class="ss_fineprint ss_nowrap" id="ss_dropbox_div_position${ssDefinitionEntry.id}${ss_attachments_namespace}" 
				href="javascript: ;" 
				onClick="ss_showAddAttachmentDropbox('${ssDefinitionEntry.parentBinder.id}', '${ssDefinitionEntry.id}', '${ss_attachments_namespace}'); return false;"
				title="<ssf:nlt tag="entry.AttachFilesByApplet"/>">
				  <ssf:nlt tag="entry.AttachFilesByApplet"/>
				</a>&nbsp;&nbsp;
			<% } %>
		</c:if>
	
	</ssf:ifnotaccessible>
		
	<% if (org.kablink.teaming.web.util.BinderHelper.isWebdavSupported(request)) { %>
	<c:if test="${ss_folderViewStyle == 'blog' && !empty ssFolderEntriesWebDAVURLs[ss_entryIDForWebDAV]}">
		<c:set var="ss_entryIDForWebDAV" value="${ssDefinitionEntry.id}" />
		<a class="ss_fineprint ss_nowrap" title="<ssf:nlt tag="entry.AttachFilesByWebDav"/>"
		  style="behavior: url(#default#AnchorClick);" 
		  folder="${ssFolderEntriesWebDAVURLs[ss_entryIDForWebDAV]}" 
		  href="${ssFolderEntriesWebDAVURLs[ss_entryIDForWebDAV]}" 
		  target="_blank"><ssf:nlt tag="entry.AttachFilesByWebDav"/></a>		
	</c:if>
	<c:if test="${ss_folderViewStyle != 'blog' && !empty ssWebDavURL}">
		<a class="ss_fineprint"  title="<ssf:nlt tag="entry.AttachFilesByWebDav"/>"
		  style="behavior: url(#default#AnchorClick);" 
		  folder="${ssWebDavURL}" href="${ssWebDavURL}" 
		  target="_blank"
		  ><ssf:nlt tag="entry.AttachFilesByWebDav"/></a>&nbsp;&nbsp;		
	</c:if>
	<% } %>
	
	<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">

		<a class="ss_fineprint ss_nowrap" title="<ssf:nlt tag="entry.AttachFilesByWebBrowse"/>" href="javascript: ;" 
		  onClick="ss_showAddAttachmentBrowse('${ssDefinitionEntry.parentBinder.id}', '${ssDefinitionEntry.id}', '${ss_attachments_namespace}'); return false;"
		  ><ssf:nlt tag="entry.AttachFilesByWebBrowse"/></a>&nbsp;&nbsp;
	
	</c:if>
	
	<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
	  <% /* TODO: Add test if IC Broker is enabled (ICBrokerModule.isEnabled()) and	if user has zone name defined */ %>
	  <a class="ss_fineprint ss_nowrap" title="<ssf:nlt tag="attachMeeting.attachResults"/>" 
	    href="javascript: ;" 
	    onClick="ss_showAttachMeetingRecords('${ssDefinitionEntry.parentBinder.id}', '${ssDefinitionEntry.id}', '${ss_attachments_namespace}'); return false;"
	    ><ssf:nlt tag="attachMeeting.attachResults"/></a>&nbsp;&nbsp;
	
	  <div id="ss_div_fileopen${ssDefinitionEntry.id}${ss_attachments_namespace}" 
	    name="ss_div_fileopen${ssDefinitionEntry.id}${ss_attachments_namespace}" 
	    style="visibility:visible;display:block; width:1px; height:1px;">
		<div align="right">
			<iframe frameborder="0" scrolling="no" 
			  id="ss_iframe_fileopen${ssDefinitionEntry.id}${ss_attachments_namespace}" 
			  name="ss_iframe_fileopen${ssDefinitionEntry.id}${ss_attachments_namespace}" 
			  src="<html:rootPath/>js/forum/null.html" 
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
						  src="<html:rootPath/>js/forum/null.html" 
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
					  style="display: none; padding: 5px;" class="ss_border_light">
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
<c:if test="${!empty ss_namespace}">
<c:set var="ss_viewEntryAttachmentDivId" value="ss_divAttachmentList${ssDefinitionEntry.id}${ss_namespace}" scope="request"/>
<c:set var="ss_namespace_attach" value="${ss_namespace}" scope="request"/>
</c:if>

<%@ include file="/WEB-INF/jsp/definition_elements/view_entry_attachments_list.jsp" %>

</div>


