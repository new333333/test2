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
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.ssfs.util.SsfsUtil" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_attachedFile" type="org.kablink.teaming.domain.FileAttachment" scope="request"/>
<c:set var="ss_attachments_namespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ss_namespace}"><c:set var="ss_attachments_namespace" value="${ss_namespace}"/></c:if>
<%
boolean isIECheck;
//If IE 7 or better, then pretend not IE so it works like the other browsers (i.e., no applet for viewing)
if (BrowserSniffer.is_ie_4(request) || BrowserSniffer.is_ie_5(request) || 
		BrowserSniffer.is_ie_5_5(request) || BrowserSniffer.is_ie_6(request)) {
	isIECheck = true;
} else {
	isIECheck = false;
}
String strBrowserType = "nonie";
if (isIECheck) strBrowserType = "ie";
boolean isAppletSupportedCheck = SsfsUtil.supportApplets(request);
String operatingSystem = BrowserSniffer.getOSInfo(request);
%>

<%
	String fn = ss_attachedFile.getFileItem().getName();
	String ext = "";
	if (fn.lastIndexOf(".") >= 0) ext = fn.substring(fn.lastIndexOf("."));
	boolean editInPlaceSupported = false;
	String fnBr = "";
	int cCount = 0;
	if (fn.length() > 40) {
	    for (int i = 0; i < fn.length(); i++) {
		    String c = String.valueOf(fn.charAt(i));
		    cCount++;
		    if (c.matches("[\\W_]?") || cCount > 15) {
			    fnBr += c + "<wbr/>";
			    cCount = 0;
		    } else {
			    fnBr += c;
			}
		}
	} else {
		fnBr = fn;
	}
%>
  <ssf:ifSupportsEditInPlace relativeFilePath="${ss_attachedFile.fileItem.name}" browserType="<%=strBrowserType%>">
<%  editInPlaceSupported = true;  %>
  </ssf:ifSupportsEditInPlace>

<c:if test="${ss_attachedFile.fileExists}">
<%
	if (!isIECheck || !ext.equals(".ppt") || !editInPlaceSupported) {
%>
    <a style="text-decoration: none;" 
				href="<ssf:fileUrl file="${ss_attachedFile}" useVersionNumber="${ss_useExplicitFileVersionNumbers}"/>" 
			    onClick="return ss_launchUrlInNewWindow(this, '<ssf:escapeJavaScript value="${ss_attachedFile.fileItem.name}"/>');"

		    <ssf:title tag="title.open.file">
			    <ssf:param name="value" value="${ss_attachedFile.fileItem.name}" />
		    </ssf:title>
			><%= fnBr %></a>
			<c:if test="${ss_attachedFile.encrypted}">
		        <img src="<html:imagesPath/>pics/encrypted.png" 
		          title="<%= NLT.get("file.encrypted").replaceAll("\"", "&QUOT;") %>" />
			</c:if>

<%  }
	if (isIECheck && ext.equals(".ppt") && editInPlaceSupported) {
%>
	<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="applet">
		<ssf:isFileEditorConfiguredForOS relativeFilePath="${ss_attachedFile.fileItem.name}" operatingSystem="<%= operatingSystem %>">
			<a style="text-decoration: none;" href="<ssf:ssfsInternalAttachmentUrl 
				binder="${ssDefinitionEntry.parentBinder}"
				entity="${ssDefinitionEntry}"
				fileAttachment="${ss_attachedFile}"/>" 
				onClick="javascript:ss_openWebDAVFile('${ssDefinitionEntry.parentBinder.id}', 
				    '${ssDefinitionEntry.id}', 
				    '${ss_attachments_namespace}', 
				    '<%= operatingSystem %>', 
					'${ss_attachedFile.id}');
					return false;"
		    	<ssf:title tag="title.open.file">
			      <ssf:param name="value" value="${ss_attachedFile.fileItem.name}" />
		    	</ssf:title>
			><%= fnBr %></a>
			<c:if test="${ss_attachedFile.encrypted}">
		        <img src="<html:imagesPath/>pics/encrypted.png" 
		          title="<%= NLT.get("file.encrypted").replaceAll("\"", "&QUOT;") %>" />
			</c:if>
		</ssf:isFileEditorConfiguredForOS>
	</ssf:editorTypeToUseForEditInPlace>
	
	<ssf:editorTypeToUseForEditInPlace browserType="<%=strBrowserType%>" editorType="webdav">
		<a href="<ssf:ssfsInternalAttachmentUrl 
				binder="${ssDefinitionEntry.parentBinder}"
				entity="${ssDefinitionEntry}"
				fileAttachment="${ss_attachedFile}"/>"
		><%= fnBr %></a>
		<c:if test="${ss_attachedFile.encrypted}">
	        <img src="<html:imagesPath/>pics/encrypted.png" 
	          title="<%= NLT.get("file.encrypted").replaceAll("\"", "&QUOT;") %>" />
		</c:if>
	</ssf:editorTypeToUseForEditInPlace>
<%  }  %>
</c:if>
<c:if test="${!ss_attachedFile.fileExists}">
	<span><%= fnBr %></span>
</c:if>

	<c:if test="${ss_attachedFile.currentlyLocked}">
	  <br/>
	  <img <ssf:alt tag="alt.locked"/> src="<html:imagesPath/>pics/sym_s_caution.gif"/>
	  <span class="ss_fineprint"><ssf:nlt tag="entry.lockedBy">
   		<ssf:param name="value" useBody="true"><ssf:userTitle user="${ss_attachedFile.fileLock.owner}"/></ssf:param>
	  </ssf:nlt></span>
	  <c:if test="${ss_canForceFileUnlock}">
	    <div>
	      <a href="<ssf:url     
		    adapter="true" 
		    portletName="ss_forum" 
		    binderId="${ssDefinitionEntry.parentBinder.id}" 
		    action="view_folder_entry" 
		    entryId="${ssDefinitionEntry.id}" actionUrl="true">
		    <ssf:param name="operation" value="force_unlock_file"/>
		    <ssf:param name="fileId" value="${ss_attachedFile.id}"/></ssf:url>"
		    onclick='if(confirm("<ssf:escapeJavaScript><ssf:nlt tag="entry.forceUnlockFileConfirm"/></ssf:escapeJavaScript>")){ss_postToThisUrl(this.href);return false;}else{return false};'
		    style="padding-left:10px;"
	        >
	        <span class="ss_fineprint"><ssf:nlt tag="entry.forceUnlockFile"/></span>
	      </a>
	    </div>
	  </c:if>
	</c:if>
