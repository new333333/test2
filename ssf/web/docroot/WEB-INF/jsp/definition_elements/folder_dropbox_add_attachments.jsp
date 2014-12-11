<%
/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/servlet.include.jsp" %>
<body align="top" class="ss_entryContent ss_style">

<script>
function reloadFolderFromApplet${ssBinderId}${ss_namespace}() {
	//parent.ss_hideAddAttachmentDropboxAndAJAXCall${ssBinderId}${ss_namespace}();
}

function ss_hideFolderDropTarget${ssBinderId}${ss_namespace}(strErrorMessage) {
	if (strErrorMessage != "") {
		alert(strErrorMessage);
	}
	if (self.parent) {
		if (ss_isGwtUIActive) {
			window.top.ss_filesDropped("${ssBinderId}");
			window.top.m_requestInfo.refreshSidebarTree = true;
			window.top.ss_setContentLocation(window.top.ss_getUrlFromContentHistory(0))
		}
		else {
			self.parent.location.reload(true);
		}
		self.parent.focus();
	}
}

function getFolderAppletBgColor${ssBinderId}${ss_namespace}() {
	return "#ffffff";
}

function ss_hideLoadingFolderDropTargetDiv${ssBinderId}${ss_namespace}() {
	var divId = 'ss_divFolderDropTargetLoading${ssBinderId}${ss_namespace}';
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}

//Function to be Called when Applet is loaded
function ss_onAppletLoad${ssBinderId}${ss_namespace}() {
	ss_hideLoadingFolderDropTargetDiv${ssBinderId}${ss_namespace}();
	ss_showFolderAttachmentHelpTextDiv${ssBinderId}${ss_namespace}();
	ss_showFolderLibNonLibHelpTextDiv${ssBinderId}${ss_namespace}();
	
	var appletObj = document.getElementById('folderdropboxobj${ssBinderId}${ss_namespace}');
	if (appletObj != null) appletObj.focus();
}

//Function to be called when the files start loading
function ss_startLoadingFiles${ssBinderId}${ss_namespace}(fileNames) {
	ss_showLoadingFolderDiv${ssBinderId}${ss_namespace}(fileNames);
	ss_hideFolderAttachmentHelpTextDiv${ssBinderId}${ss_namespace}();
	ss_hideFolderLibNonLibHelpTextDiv${ssBinderId}${ss_namespace}();
}

//Function to be called when the files have completed loading
function ss_endLoadingFiles${ssBinderId}${ss_namespace}() {
	ss_hideLoadingFolderDiv${ssBinderId}${ss_namespace}();
	ss_showFolderAttachmentHelpTextDiv${ssBinderId}${ss_namespace}();
	ss_showFolderLibNonLibHelpTextDiv${ssBinderId}${ss_namespace}();
}

function ss_showLoadingFolderDropTargetDiv${ssBinderId}${ss_namespace}() {
	var divId = 'ss_divFolderDropTargetLoading${ssBinderId}${ss_namespace}';
	ss_showDiv(divId, 'no');
}

//Show Files Loading Div Tag
function ss_showLoadingFolderDiv${ssBinderId}${ss_namespace}(fileNames) {
	var divId = 'ss_divFolderFilesLoading${ssBinderId}${ss_namespace}';
	var divObj = document.getElementById(divId);
	divObj.innerHTML = '<span class="ss_bold"><ssf:escapeJavaScript><ssf:nlt tag="loading.files"/></ssf:escapeJavaScript></span>: ' + fileNames
	ss_showDiv(divId, 'no');
}

//Hide Files Loading Div Tag
function ss_hideLoadingFolderDiv${ssBinderId}${ss_namespace}() {
	var divId = 'ss_divFolderFilesLoading${ssBinderId}${ss_namespace}';
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}

//Show Folder Attachment Help Text Div
function ss_showFolderAttachmentHelpTextDiv${ssBinderId}${ss_namespace}() {
	var divId = 'ss_divFolderAttachmentHelpText${ssBinderId}${ss_namespace}';
	ss_showDiv(divId, 'no');
}

//Hide Folder Attachment Help Text Div
function ss_hideFolderAttachmentHelpTextDiv${ssBinderId}${ss_namespace}() {
	var divId = 'ss_divFolderAttachmentHelpText${ssBinderId}${ss_namespace}';
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}

//Show Folder Library/Non-Library Attachment Help Text
function ss_showFolderLibNonLibHelpTextDiv${ssBinderId}${ss_namespace}() {
	var divId = 'ss_divFolderLibNonLibHelpText${ssBinderId}${ss_namespace}';
	ss_showDiv(divId, 'no');
}

//Hide Folder Library/Non-Library Attachment Help Text
function ss_hideFolderLibNonLibHelpTextDiv${ssBinderId}${ss_namespace}() {
	var divId = 'ss_divFolderLibNonLibHelpText${ssBinderId}${ss_namespace}';
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}
</script>

	<%
	 boolean isIE = org.kablink.util.BrowserSniffer.is_ie(request);
	%>
	<table border="0" cellspacing="0" cellpadding="0" valign="top" height="100%" width="100%">
		<tr><td align="center">
			<div id="ss_divFolderDropTargetLoading${ssBinderId}${ss_namespace}">
				<ssf:nlt tag="loading.applet"/>
			</div>
			
			<!--NOVELL_REWRITE_ATTRIBUTE_ON='value'-->
			<c:if test="<%= isIE %>">
			<object id="folderdropboxobj${ssBinderId}${ss_namespace}" classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" CLASS="dropbox" 
			  WIDTH = "96" HEIGHT = "80" NAME = "launcher" ALIGN = "middle" VSPACE = "0" HSPACE = "0" 
			  codebase="http://java.sun.com/update/1.7.0/jinstall-7u72-windows-i586.cab">
			</c:if>
			<c:if test="<%= !isIE %>">
			<applet CODE = "org.kablink.teaming.applets.droptarget.TopFrame" 
			  JAVA_CODEBASE = "<html:appletPath/>applets" 
			  ARCHIVE = "droptarget/kablink-teaming-droptarget-applet.jar" 
			  WIDTH = "96" HEIGHT = "80" MAYSCRIPT>
			</c:if>
			    <PARAM NAME="CODE" value = "org.kablink.teaming.applets.droptarget.TopFrame" />
			    <PARAM NAME ="CODEBASE" value = "<html:appletPath/>applets" />
			    <PARAM NAME ="ARCHIVE" value = "droptarget/kablink-teaming-droptarget-applet.jar" />
			    <PARAM NAME ="type" value="application/x-java-applet;version=1.7" />
			    <param name = "scriptable" value="true" />
			    <PARAM NAME = "NAME" value = "folderdropboxobj${ssBinderId}${ss_namespace}" />
			    <PARAM NAME = "startingDir" value=""/>
			    <PARAM NAME = "reloadFunctionName" value="ss_hideFolderDropTarget${ssBinderId}${ss_namespace}"/>
			    <PARAM NAME = "savePreviousVersions" value="yes"/>
			    <PARAM NAME = "fileReceiverURL" value="${ssFolderAttachmentFileReceiverURL}" />
			    <PARAM NAME = "fileCheckExistsURL" value="${ssFolderAttachmentFileCheckExistsURL}" />
			    <PARAM NAME = "deactivationUrl" value=""/>
			    <PARAM NAME = "displayUrl" value="0"/>
				<c:if test="${ssBinderIsLibrary == 'false'}">
					<PARAM NAME = "loadDirectory" value="no" />
				</c:if>
				<c:if test="${ssBinderIsLibrary == 'true'}">
					<PARAM NAME = "loadDirectory" value="yes" />
				</c:if>
			    <PARAM NAME = "onLoadFunction" value="ss_onAppletLoad${ssBinderId}${ss_namespace}" />
			    <PARAM NAME = "onCancelFunction" value="" />
			    <PARAM NAME = "menuLabelPaste" value="<ssf:escapeQuotes><ssf:nlt tag="binder.add.files.applet.menu.paste" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "menuLabelCancel" value="<ssf:escapeQuotes><ssf:nlt tag="binder.add.files.applet.menu.cancel" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "menuLabelDeactivate" value="<ssf:escapeQuotes><ssf:nlt tag="binder.add.files.applet.menu.deactivate" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "directoryLoadErrorMessage" value="<ssf:escapeQuotes><ssf:nlt tag="binder.add.files.applet.no.directory.for.nonlibrary.folder" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "noFileAlertMessage" value="<ssf:escapeQuotes><ssf:nlt tag="binder.add.files.applet.no.files.in.clipboard" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "uploadInProgress" value="<ssf:escapeQuotes><ssf:nlt tag="binder.add.files.applet.upload.in.progress" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "fileLoadingInProgress" value="ss_startLoadingFiles${ssBinderId}${ss_namespace}" />
			    <PARAM NAME = "fileLoadingEnded" value="ss_endLoadingFiles${ssBinderId}${ss_namespace}" />
			    <PARAM NAME = "fileUploadNotSupported" value="<ssf:escapeQuotes><ssf:nlt tag="binder.add.files.applet.upload.not.supported" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "uploadErrorMessage" value="<ssf:escapeQuotes><ssf:nlt tag="exception.codedError.title" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "uploadErrorFileTooLarge" value="<ssf:escapeQuotes><ssf:nlt tag="applet.errorFileTooLarge" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "fileUploadMaxSize" value="${ss_binder_file_max_file_size}" />
			    <PARAM NAME = "fileUploadSizeExceeded" value="<ssf:escapeQuotes><ssf:nlt tag="file.maxSizeExceeded" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "isAppletChunkedStreamingModeSupported" value="${ss_isAppletChunkedStreamingModeSupported}" />
			    <PARAM NAME = "appletFileName" value="<ssf:appletFileName />" />
			    <PARAM NAME = "strYes" value="<ssf:escapeQuotes><ssf:nlt tag="button.Yes" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "strNo" value="<ssf:escapeQuotes><ssf:nlt tag="button.No" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "strFilesExist" value="<ssf:escapeQuotes><ssf:nlt tag="applet.filesExist" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "strFilesExistConfirm" value="<ssf:escapeQuotes><ssf:nlt tag="applet.filesExistConfirm" /></ssf:escapeQuotes>" />
			<c:if test="<%= !isIE %>">
			</applet>
			</c:if>
			<c:if test="<%= isIE %>">
			</object>
			</c:if>
			<!--NOVELL_REWRITE_ATTRIBUTE_OFF='value'-->
			
		</td></tr>
		<tr>
			<td align="center" style="padding-top: 20px">
				<div id="ss_divFolderFilesLoading${ssBinderId}${ss_namespace}" style="visibility:hidden; display:none;">
					<ssf:nlt tag="loading.files"/>
				</div>
				<div id="ss_divFolderAttachmentHelpText${ssBinderId}${ss_namespace}" style="visibility:hidden; display:none;">
					<ssf:nlt tag="folder.dropboxAddFolderAttachmentHelpText"/>
				</div>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td align="center" style="padding-top: 15px;">
				<div id="ss_divFolderLibNonLibHelpText${ssBinderId}${ss_namespace}" style="visibility:hidden; display:none;">
				<ssf:ifNotFilr>
				  <c:if test="${ssBinderIsLibrary == 'false'}">
					<b><ssf:nlt tag="note"/>:</b> <ssf:nlt tag="folder.dropboxAddNonLibraryFolderHelpText"/>
				  </c:if>
				  <c:if test="${ssBinderIsLibrary == 'true'}">
					<b><ssf:nlt tag="note"/>:</b> <ssf:nlt tag="folder.dropboxAddLibraryFolderHelpText"/>
				  </c:if>
				</ssf:ifNotFilr>
				</div>
			</td>
		</tr>
	</table>
</body>
</html>
