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
<%@ include file="/WEB-INF/jsp/common/servlet.include.jsp" %>

<script>
function reloadFolderFromApplet${ssBinderId}${ss_namespace}() {
	//parent.ss_hideAddAttachmentDropboxAndAJAXCall${ssBinderId}${ss_namespace}();
}

function ss_hideFolderDropTarget${ssBinderId}${ss_namespace}(strErrorMessage) {
	if (strErrorMessage != "") {
		alert(strErrorMessage);
	}
	if (self.parent) {
		self.parent.location.reload(true);
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
	ss_showDiv(divId, false);
}

//Show Files Loading Div Tag
function ss_showLoadingFolderDiv${ssBinderId}${ss_namespace}(fileNames) {
	var divId = 'ss_divFolderFilesLoading${ssBinderId}${ss_namespace}';
	var divObj = document.getElementById(divId);
	divObj.innerHTML = '<span class="ss_bold"><ssf:nlt tag="loading.files"/></span>: ' + fileNames
	ss_showDiv(divId, false);
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
	ss_showDiv(divId, false);
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
	ss_showDiv(divId, false);
}

//Hide Folder Library/Non-Library Attachment Help Text
function ss_hideFolderLibNonLibHelpTextDiv${ssBinderId}${ss_namespace}() {
	var divId = 'ss_divFolderLibNonLibHelpText${ssBinderId}${ss_namespace}';
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}
</script>

<body align="top" class="ss_entryContent ss_style">
	<%
	 boolean isIE = com.sitescape.util.BrowserSniffer.is_ie(request);
	%>
	<table border="0" cellspacing="0" cellpadding="0" valign="top" height="100%" width="100%">
		<tr><td align="center">
			<div id="ss_divFolderDropTargetLoading${ssBinderId}${ss_namespace}">
				<ssf:nlt tag="loading.applet"/>
			</div>
			
			<!--NOVELL_REWRITE_ATTRIBUTE_ON='value'-->
			<c:if test="<%= isIE %>">
			<object id="folderdropboxobj${ssBinderId}${ss_namespace}" classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" CLASS="dropbox" 
			  WIDTH = "20" HEIGHT = "20" NAME = "launcher" ALIGN = "middle" VSPACE = "0" HSPACE = "0" 
			  codebase="http://java.sun.com/update/1.5.0/jinstall-1_5-windows-i586.cab#Version=5,0,0,3">
			</c:if>
			<c:if test="<%= !isIE %>">
			<applet CODE = "com.sitescape.team.applets.droptarget.TopFrame" 
			  JAVA_CODEBASE = "<html:rootPath/>applets" 
			  ARCHIVE = "droptarget/ssf-droptarget-applet.jar" 
			  WIDTH = "22" HEIGHT = "22" MAYSCRIPT>
			</c:if>
			    <PARAM NAME="CODE" VALUE = "com.sitescape.team.applets.droptarget.TopFrame" />
			    <PARAM NAME ="CODEBASE" VALUE = "<html:rootPath/>applets" />
			    <PARAM NAME ="ARCHIVE" VALUE = "droptarget/ssf-droptarget-applet.jar" />
			    <PARAM NAME ="type" value="application/x-java-applet;version=1.5" />
			    <param name = "scriptable" value="true" />
			    <PARAM NAME = "NAME" VALUE = "folderdropboxobj${ssBinderId}${ss_namespace}" />
			    <PARAM NAME = "startingDir" VALUE=""/>
			    <PARAM NAME = "reloadFunctionName" VALUE="ss_hideFolderDropTarget${ssBinderId}${ss_namespace}"/>
			    <PARAM NAME = "bgcolorFunctionName" VALUE="getFolderAppletBgColor${ssBinderId}${ss_namespace}"/>
			    <PARAM NAME = "savePreviousVersions" VALUE="yes"/>
			    <PARAM NAME = "fileReceiverURL" VALUE="${ssFolderAttachmentFileReceiverURL}" />
			    <PARAM NAME = "deactivationUrl" VALUE=""/>
			    <PARAM NAME = "displayUrl" VALUE="0"/>
				<c:if test="${ssBinderIsLibrary == 'false'}">
					<PARAM NAME = "loadDirectory" VALUE="no" />
				</c:if>
				<c:if test="${ssBinderIsLibrary == 'true'}">
					<PARAM NAME = "loadDirectory" VALUE="yes" />
				</c:if>
			    <PARAM NAME = "onLoadFunction" VALUE="ss_onAppletLoad${ssBinderId}${ss_namespace}" />
			    <PARAM NAME = "onCancelFunction" VALUE="" />
			    <PARAM NAME = "menuLabelPaste" VALUE="<ssf:nlt tag="binder.add.files.applet.menu.paste" />" />
			    <PARAM NAME = "menuLabelCancel" VALUE="<ssf:nlt tag="binder.add.files.applet.menu.cancel" />" />
			    <PARAM NAME = "menuLabelDeactivate" VALUE="<ssf:nlt tag="binder.add.files.applet.menu.deactivate" />" />
			    <PARAM NAME = "directoryLoadErrorMessage" value="<ssf:nlt tag="binder.add.files.applet.no.directory.for.nonlibrary.folder" />" />
			    <PARAM NAME = "noFileAlertMessage" value="<ssf:nlt tag="binder.add.files.applet.no.files.in.clipboard" />" />
			    <PARAM NAME = "fileLoadingInProgress" value="ss_startLoadingFiles${ssBinderId}${ss_namespace}" />
			    <PARAM NAME = "fileLoadingEnded" value="ss_endLoadingFiles${ssBinderId}${ss_namespace}" />
			    <PARAM NAME = "fileUploadNotSupported" value="<ssf:nlt tag="binder.add.files.applet.upload.not.supported" />" />
			    <PARAM NAME = "uploadErrorMessage" value="<ssf:nlt tag="exception.codedError.title" />" />
			    <PARAM NAME = "appletFileName" value="<ssf:appletFileName />" />
			<c:if test="<%= !isIE %>">
			</applet>
			</c:if>
			<c:if test="<%= isIE %>">
			</object>
			</c:if>
			<!--NOVELL_REWRITE_ATTRIBUTE_OFF='value'-->
			
		</td></tr>
		<tr>
			<td class="ss_entrySignature">
				<div id="ss_divFolderFilesLoading${ssBinderId}${ss_namespace}" style="visibility:hidden;display:none;">
					<ssf:nlt tag="loading.files"/>
				</div>
				<div id="ss_divFolderAttachmentHelpText${ssBinderId}${ss_namespace}" style="visibility:hidden;display:none;">
					<ssf:nlt tag="folder.dropboxAddFolderAttachmentHelpText"/>
				</div>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td class="ss_entrySignature">
				<div id="ss_divFolderLibNonLibHelpText${ssBinderId}${ss_namespace}" style="visibility:hidden;display:none;">
				<c:if test="${ssBinderIsLibrary == 'false'}">
					<b><ssf:nlt tag="note"/>:</b> <ssf:nlt tag="folder.dropboxAddNonLibraryFolderHelpText"/>
				</c:if>
				<c:if test="${ssBinderIsLibrary == 'true'}">
					<b><ssf:nlt tag="note"/>:</b> <ssf:nlt tag="folder.dropboxAddLibraryFolderHelpText"/>
				</c:if>
				</div>
			</td>
		</tr>
	</table>
</body>