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
<% // View entry attachments %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.ssfs.util.SsfsUtil" %>
<%
	boolean presence_service_enabled = org.kablink.teaming.util.SPropsUtil.getBoolean("presence.service.enable", false);
	String webdavSuffix = org.kablink.teaming.util.SPropsUtil.getString("webdav.folder.url.suffix", "");
%>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_quotaMessage" value="" />
<c:if test="${ss_diskQuotaHighWaterMarkExceeded && !ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.nearLimit"><ssf:param name="value" useBody="true"
	    ><fmt:formatNumber value="${(ss_diskQuotaUserMaximum - ssUser.diskSpaceUsed)/1048576}" 
	    maxFractionDigits="2"/></ssf:param></ssf:nlt></c:set>
</c:if>
<c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.diskQuotaExceeded"/></c:set>
</c:if>

<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<%
boolean isIE = BrowserSniffer.is_ie(request);
boolean isAppletSupported = SsfsUtil.supportApplets(request);
%>
<script type="text/javascript">
var ss_labelButtonOK = "<ssf:nlt tag="button.ok"/>";
var ss_labelButtonCancel = "<ssf:nlt tag="button.cancel"/>";
var ss_labelEntryChooseFileWarning = "<ssf:nlt tag="entry.chooseFileWarningMessage"/>";
var ss_labelEntryBrowseAddAttachmentHelpText = "<ssf:nlt tag="entry.browseAddAttachmentHelpText"/>";
</script>
<div class="ss_entryContent"">

<table width="99%" border="0" cellpadding="0" cellspacing="0" class="marginbottom2">
<c:if test="${!empty ss_pseudoEntity && !ss_isBinderMirroredFolder}">
<tr>
	<td valign="top" style="padding-left: 9px; white-space: nowrap">
		<c:if test="${!empty ss_pseudoEntityRevert}">
		  <br/>
		  <span class="ss_style ss_smallprint"><ssf:nlt tag="entry.revert.select"/>
		</c:if>
	</td>
</tr>
</c:if>
<c:if test="${empty ss_pseudoEntity}">
<tr>
	<td style="padding-left: 19px; white-space: nowrap">
		<!-- We need the following image so the help spot has an initial position. -->
		<span id="ss_browse_div_position${ssDefinitionEntry.id}${ss_attachments_namespace}" class="ss_style ss_bold ss_smallprint">
		  	<ssHelpSpot helpId="workspaces_folders/entries/attachments" offsetX="-20" offsetY="-5"
    					title="<ssf:nlt tag="helpSpot.attachments"/>">
			</ssHelpSpot>
			<c:if test="${!empty property_caption}"><c:out value="${property_caption}"/>:&nbsp;&nbsp;</c:if>
		</span>
	</td>

	<td width="100%" align="left">
	<ul class="ss_nobullet">
	<ssf:ifnotaccessible>
	
		<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
			<% if (isAppletSupported) { %>
				<li style="float:right; padding:1px 10px 4px 0px;">
				<a class="ss_nowrap" 
				id="ss_dropbox_div_position${ssDefinitionEntry.id}${ss_attachments_namespace}" 
				href="javascript: ;" 
				<c:if test="${!ss_diskQuotaExceeded || ss_isBinderMirroredFolder}">
				  onClick='ss_showAddAttachmentDropbox("${ssDefinitionEntry.parentBinder.id}", "${ssDefinitionEntry.id}", "${ss_attachments_namespace}"); 
				    <c:if test="${!empty ss_quotaMessage}">alert("${ss_quotaMessage}");</c:if>return false;'
				</c:if>
				<c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
				  onClick='alert("${ss_quotaMessage}");return false;'
				</c:if>
				title="<ssf:nlt tag="entry.AttachFilesByApplet"/>">
				  <ssf:nlt tag="entry.AttachFilesByApplet"/>
				</a>
				</li>
			<% } %>
		</c:if>
	
	</ssf:ifnotaccessible>
		
	<% if (org.kablink.teaming.web.util.BinderHelper.isWebdavSupported(request)) { %>
	<c:if test="${ss_folderViewStyle == 'blog' && !empty ssFolderEntriesWebDAVURLs[ss_entryIDForWebDAV]}">
		<c:set var="ss_entryIDForWebDAV" value="${ssDefinitionEntry.id}" />
		<li style="float:right; padding:1px 10px 4px 0px;">
		<a class="ss_nowrap" title="<ssf:nlt tag="entry.AttachFilesByWebDav"/>"
		  style="behavior: url(#default#AnchorClick);" 
		  folder="${ssFolderEntriesWebDAVURLs[ss_entryIDForWebDAV]}" 
		  <c:if test="${!ss_diskQuotaExceeded || ss_isBinderMirroredFolder}">
		    href="${ssFolderEntriesWebDAVURLs[ss_entryIDForWebDAV]}" 
		    target="_blank"
		    <c:if test="${!empty ss_quotaMessage}">
		      onClick='alert("${ss_quotaMessage}");'
		    </c:if>
		  </c:if>
		  <c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
		    onClick='alert("${ss_quotaMessage}");'
		  </c:if>
		><ssf:nlt tag="entry.AttachFilesByWebDav"/></a>
		</li>
	</c:if>
	<c:if test="${ss_folderViewStyle != 'blog' && !empty ssWebDavURL}">
		<li style="float:right; padding:1px 10px 4px 0px;">
		<a class="ss_nowrap"  
		  title="<ssf:nlt tag="entry.AttachFilesByWebDav"/>"
		  style="behavior: url(#default#AnchorClick);" 
		  folder="${ssWebDavURL}<%= webdavSuffix %>"
		  <c:if test="${!ss_diskQuotaExceeded || ss_isBinderMirroredFolder}">
		    href="${ssWebDavURL}" 
		    target="_blank"
		    <c:if test="${!empty ss_quotaMessage}">
		      onClick='alert("${ss_quotaMessage}");'
		    </c:if>
		  </c:if>
		  <c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
		    onClick='alert("${ss_quotaMessage}");return false;'
		  </c:if>
		  ><ssf:nlt tag="entry.AttachFilesByWebDav"/></a>
		</li>	
	</c:if>
	<% } %>
	
	<c:if test="${ss_accessControlMap[ssDefinitionEntry.id]['modifyEntry']}">
	  <li style="float:right; padding:1px 10px 4px 0px;">
		<a class="ss_nowrap" 
		  title="<ssf:nlt tag="entry.AttachFilesByWebBrowse"/>" href="javascript: ;" 
		  <c:if test="${!ss_diskQuotaExceeded || ss_isBinderMirroredFolder}">
		    onClick='ss_showAddAttachmentBrowse("${ssDefinitionEntry.parentBinder.id}", "${ssDefinitionEntry.id}", "${ss_attachments_namespace}"); 
		      <c:if test="${!empty ss_quotaMessage}">alert("${ss_quotaMessage}");</c:if>return false;'
		  </c:if>
		  <c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
			onClick='alert("${ss_quotaMessage}");return false;'
		  </c:if>
		  ><ssf:nlt tag="entry.AttachFilesByWebBrowse"/></a>
	  </li>
	</c:if>

	<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
	  <li style="float:right; padding:1px 10px 4px 0px;">
		<a class="ss_nowrap" 
		  title="<ssf:nlt tag="entry.DownloadAllAttachmentsAsZip"/>" 
		  href="<ssf:fileUrl zipUrl="true" entity="${ssDefinitionEntry}"/>" 
		  ><ssf:nlt tag="entry.DownloadAllAttachments"/></a>
	  </li>
	</c:if>
		
	</ul>
	</td>
</tr>
<tr>
	<td style="padding-left: 30px;" colspan="2">
	
		<!-- Drag and Drop dialog -->
		<div id="ss_div_dropbox${ssDefinitionEntry.id}${ss_attachments_namespace}" class="teamingDlgBox" style="position: absolute; visibility:hidden; display:none;">
			<div class="popupContent" style="width:400px; background-color: #fff;">
				<div class="teamingDlgBoxHeader"><ssf:nlt tag="entry.AttachFilesByApplet"/>
					<div class="closebutton">
						<a onClick="ss_hideAddAttachmentDropbox('${ssDefinitionEntry.id}', '${ss_attachments_namespace}'); return false;"><img 
					  	border="0" align="absmiddle" <ssf:alt tag="alt.hide"/> src="<html:imagesPath/>icons/close_gray16.png"/></a>
					</div>	
				</div>	
				<iframe style="margin-top: 20px; margin-left: 10px;" 
				  title="<ssf:nlt tag="entry.AttachFilesByApplet" />" 
				  frameborder="0" 
				  id="ss_iframe_dropbox${ssDefinitionEntry.id}${ss_attachments_namespace}" 
				  name="ss_iframe_dropbox${ssDefinitionEntry.id}${ss_attachments_namespace}" 
				  src="<html:rootPath/>js/forum/null.html" 
				  height="180" width="95%" 
				  onClick="ss_hideAddAttachmentDropbox('${ssDefinitionEntry.id}', '${ss_attachments_namespace}'); return false;"
				>Micro Focus Vibe</iframe>
				<div align="right" style="padding:10px;">
				  <span class="ss_fineprint"><ssf:nlt tag="file.upload.having_trouble"/></span>
				  <ssf:showHelp guideName="user" pageId="trouble" sectionId="trouble_fileupload" />
				</div>
			</div>
		</div>

		<!-- Upload a Single File dialog -->
		<div id="ss_div_browse${ssDefinitionEntry.id}${ss_attachments_namespace}" class="teamingDlgBox" style="position: absolute; visibility:hidden; display:none;">
			<div class="popupContent" style="width:400px; background-color: #fff;">
				<div class="teamingDlgBoxHeader"><ssf:nlt tag="entry.AttachFilesByWebBrowse"/>
					<div class="closebutton">
						<a onClick="ss_hideAddAttachmentBrowse('${ssDefinitionEntry.id}', '${ss_attachments_namespace}'); return false;"><img 
						border="0" <ssf:alt tag="alt.hide"/> src="<html:imagesPath/>icons/close_gray16.png"/></a>
					</div>
				</div>		
				<iframe title="<ssf:nlt tag="entry.AttachFilesByWebBrowse" />" frameborder="0"  
					id="ss_iframe_browse${ssDefinitionEntry.id}${ss_attachments_namespace}" 
					name="ss_iframe_browse${ssDefinitionEntry.id}${ss_attachments_namespace}" 
					src="<html:rootPath/>js/attachments/entry_attachment_browse.html" 
					height="150" width="95%">Micro Focus Vibe</iframe>
			</div>	
		</div>					
	</td>	
</tr>
</c:if>
</table>

<c:set var="ss_viewEntryAttachmentDivId" value="ss_divAttachmentList${ssDefinitionEntry.id}${renderResponse.namespace}" scope="request"/>
<c:set var="ss_namespace_attach" value="${renderResponse.namespace}" scope="request"/>
<c:if test="${!empty ss_namespace}">
<c:set var="ss_viewEntryAttachmentDivId" value="ss_divAttachmentList${ssDefinitionEntry.id}${ss_namespace}" scope="request"/>
<c:set var="ss_namespace_attach" value="${ss_namespace}" scope="request"/>
</c:if>

<%@ include file="/WEB-INF/jsp/definition_elements/view_entry_attachments_list_dispatch.jsp" %>

</div>


