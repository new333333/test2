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
function setFileName${ssEntryId}${ss_namespace}(strURLValue)
{
	//var fileOpenAppletObject =  document.getElementById("fileopenobj${ssEntryId}${ss_namespace}");
	//fileOpenAppletObject.setFileToBeOpened(strURLValue);
}
function fileName() {
	//alert("${ssEntryAttachmentURL}");
}

function ss_checkEditClickLocal${ssEntryId}${ss_namespace}() {
	var strEditClickValue = parent.ss_checkEditClicked('${ssEntryId}', '${ss_namespace}');
	return strEditClickValue;
}

function ss_resetEditClickLocal${ssEntryId}${ss_namespace}() {
	parent.ss_resetEditClicked('${ssEntryId}', '${ss_namespace}');
}
</script>

<body align="top" class="ss_entryContent" onLoad="javascript:fileName()">
OpenFile Applet
<br/ >
	<%
	 boolean isIE = org.kablink.util.BrowserSniffer.is_ie(request);
	%>

	<!--NOVELL_REWRITE_ATTRIBUTE_ON='value'-->
	<c:if test="<%= isIE %>">
		<object id="fileopenobj${ssEntryId}${ss_namespace}" name="fileopenobj${ssEntryId}${ss_namespace}" classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" CLASS="fileopen" 
		  WIDTH = "1" HEIGHT = "1" NAME = "launcher" ALIGN = "middle" VSPACE = "0" HSPACE = "0" 
		  codebase="http://java.sun.com/update/1.7.0/jinstall-7u72-windows-i586.cab">
	</c:if>
	<c:if test="<%= !isIE %>">
	<applet name="fileopenobj${ssEntryId}${ss_namespace}" id="fileopenobj${ssEntryId}${ss_namespace}" CODE = "org.kablink.teaming.applets.fileopen.FileOpen" 
	  JAVA_CODEBASE = "<html:appletPath/>applets" 
	  ARCHIVE = "fileopen/kablink-teaming-fileopen-applet.jar" 
	  WIDTH = "1" HEIGHT = "1" MAYSCRIPT="true">
	</c:if>
	    <PARAM NAME="CODE" value = "org.kablink.teaming.applets.fileopen.FileOpen" />
	    <PARAM NAME ="CODEBASE" value = "<html:appletPath/>applets" />
	    <PARAM NAME ="ARCHIVE" value = "fileopen/kablink-teaming-fileopen-applet.jar" />
	    <PARAM NAME ="type" value="application/x-java-applet;version=1.7" />
	    <param name = "scriptable" value="true" />
	    <PARAM NAME = "NAME" value = "fileopen" />
	    <PARAM NAME = "startingDir" value=""/>
	    <PARAM NAME = "fileToOpen" value="${ssEntryAttachmentURL}"/>
	    <PARAM NAME = "editorType" value="${ssEntryAttachmentEditorType}"/>
        <PARAM NAME = "isLicenseRequiredEdition" value="${ssIsLicenseRequiredEdition}"/>
        <PARAM NAME = "isOfficeAddInAllowed" value="${ssIsOfficeAddInAllowed}"/>
        <PARAM NAME = "userName" value="${ssUser.name}"/>
	    <PARAM NAME = "checkEditClicked" value="ss_checkEditClickLocal${ssEntryId}${ss_namespace}"/>
	    <PARAM NAME = "resetEditClicked" value="ss_resetEditClickLocal${ssEntryId}${ss_namespace}"/>
	    <PARAM NAME = "operatingSystem" value="${ssOSInfo}"/>
		<PARAM NAME = "uploadErrorFileTooLarge" value="<ssf:nlt tag="applet.errorFileTooLarge" />" />
		<PARAM NAME = "fileUploadMaxSize" value="${ss_binder_file_max_file_size}" />
		<PARAM NAME = "fileUploadSizeExceeded" value="<ssf:nlt tag="file.maxSizeExceeded" />" />
	    <PARAM NAME = "uploadErrorMessage" value="<ssf:nlt tag="exception.codedError.title" />" />
	    <PARAM NAME = "editorErrorMessage" value="<ssf:nlt tag="applet.editorError" />" />
	<c:if test="<%= !isIE %>">
	</applet>
	</c:if>
	<c:if test="<%= isIE %>">
	</object>
	</c:if>
	<!--NOVELL_REWRITE_ATTRIBUTE_OFF='value'-->

</body>