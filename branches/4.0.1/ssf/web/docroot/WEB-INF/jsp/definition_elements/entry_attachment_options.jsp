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

<script>
function reloadUrlFromApplet${ssEntryId}${ss_namespace}(strErrorMessage) {
	if (strErrorMessage != "") {
		alert(strErrorMessage);
	}
	parent.ss_hideAddAttachmentDropboxAndAJAXCall('${ssBinderId}', '${ssEntryId}', '${ss_namespace}');
}
function ss_hideDropTarget${ssEntryId}${ss_namespace}() {
	parent.ss_hideAddAttachmentDropbox('${ssEntryId}', '${ss_namespace}');
}
function getWindowBgColor${ssEntryId}${ss_namespace}() {
	return "#ffffff";
}
function ss_hideLoadingDropTargetDiv${ssEntryId}${ss_namespace}() {
	var divId = 'ss_divDropTargetLoading${ssEntryId}${ss_namespace}';
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
	var appletObj = document.getElementById('dropboxobj${ssEntryId}${ss_namespace}');
	if (appletObj != null) appletObj.focus();
}
function ss_showLoadingDropTargetDiv${ssEntryId}${ss_namespace}() {
	var divId = 'ss_divDropTargetLoading${ssEntryId}${ss_namespace}';
	ss_showDiv(divId);
}
</script>

<body align="top" class="ss_entryContent ss_style" onLoad="javascript:ss_showLoadingDropTargetDiv${ssEntryId}${ss_namespace}();">

	<%
	 boolean isIE = org.kablink.util.BrowserSniffer.is_ie(request);
	%>
	<table border="0" cellspacing="0" cellpadding="0" valign="top" height="100%" width="100%">
		<tr><td align="center">
			<div id="ss_divDropTargetLoading${ssEntryId}${ss_namespace}">
				
				<ssf:nlt tag="Loading"/>
			</div>
			<!--NOVELL_REWRITE_ATTRIBUTE_ON='value'-->
			<c:if test="<%= isIE %>">
			<object id="dropboxobj${ssEntryId}${ss_namespace}" classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" CLASS="dropbox" 
			  WIDTH = "96" HEIGHT = "80" NAME = "launcher" ALIGN = "middle" VSPACE = "0" HSPACE = "0" 
			  codebase="http://java.sun.com/update/1.7.0/jinstall-7u72-windows-i586.cab">
			</c:if>
			<c:if test="<%= !isIE %>">
			<applet CODE = "org.kablink.teaming.applets.droptarget.TopFrame" name="dropboxobj${ssEntryId}${ss_namespace}"
			  JAVA_CODEBASE = "<html:appletPath/>applets" 
			  ARCHIVE = "droptarget/kablink-teaming-droptarget-applet.jar" 
			  WIDTH = "96" HEIGHT = "80" MAYSCRIPT>
			</c:if>
			    <PARAM NAME="CODE" value = "org.kablink.teaming.applets.droptarget.TopFrame" />
			    <PARAM NAME ="CODEBASE" value = "<html:appletPath/>applets" />
			    <PARAM NAME ="ARCHIVE" value = "droptarget/kablink-teaming-droptarget-applet.jar" />
			    <PARAM NAME ="type" value="application/x-java-applet;version=1.7" />
			    <param name = "scriptable" value="true" />
			    <PARAM NAME = "NAME" value = "droptarget" />
			    <PARAM NAME = "startingDir" value=""/>
			    <PARAM NAME = "reloadFunctionName" value="reloadUrlFromApplet${ssEntryId}${ss_namespace}"/>
			    <PARAM NAME = "onLoadFunction" value="ss_hideLoadingDropTargetDiv${ssEntryId}${ss_namespace}" />
			    <PARAM NAME = "onCancelFunction" value="ss_hideDropTarget${ssEntryId}${ss_namespace}" />
			    <PARAM NAME = "savePreviousVersions" value="yes"/>
			    <PARAM NAME = "fileReceiverURL" value="${ssAttachmentFileReceiverURL}" />
			    <PARAM NAME = "fileCheckExistsURL" value="${ssAttachmentFileCheckExistsURL}" />
			    <PARAM NAME = "deactivationUrl" value=""/>
			    <PARAM NAME = "displayUrl" value="0"/>
			    <PARAM NAME = "loadDirectory" value="yes" />
			    <PARAM NAME = "menuLabelPaste" value="<ssf:escapeQuotes><ssf:nlt tag="binder.add.files.applet.menu.paste" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "menuLabelCancel" value="<ssf:escapeQuotes><ssf:nlt tag="binder.add.files.applet.menu.cancel" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "menuLabelDeactivate" value="<ssf:escapeQuotes><ssf:nlt tag="binder.add.files.applet.menu.deactivate" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "directoryLoadErrorMessage" value="<ssf:escapeQuotes><ssf:nlt tag="binder.add.files.applet.no.directory" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "noFileAlertMessage" value="<ssf:escapeQuotes><ssf:nlt tag="binder.add.files.applet.no.files.in.clipboard" /></ssf:escapeQuotes>" />
			    <PARAM NAME = "uploadInProgress" value="<ssf:escapeQuotes><ssf:nlt tag="binder.add.files.applet.upload.in.progress" /></ssf:escapeQuotes>" />
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
			<td align="center" style="padding-top: 15px;">
				<ssf:nlt tag="entry.dropboxAddAttachmentHelpText"/>
			</td>
		</tr>
	</table>
</body>