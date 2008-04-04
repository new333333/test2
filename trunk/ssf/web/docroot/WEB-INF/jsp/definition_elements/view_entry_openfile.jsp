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
	 boolean isIE = com.sitescape.util.BrowserSniffer.is_ie(request);
	%>

	<!--NOVELL_REWRITE_ATTRIBUTE_ON='value'-->
	<c:if test="<%= isIE %>">
		<object id="fileopenobj${ssEntryId}${ss_namespace}" name="fileopenobj${ssEntryId}${ss_namespace}" classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" CLASS="fileopen" 
		  WIDTH = "1" HEIGHT = "1" NAME = "launcher" ALIGN = "middle" VSPACE = "0" HSPACE = "0" 
		  codebase="http://java.sun.com/update/1.5.0/jinstall-1_5-windows-i586.cab#Version=5,0,0,3">
	</c:if>
	<c:if test="<%= !isIE %>">
	<applet name="fileopenobj${ssEntryId}${ss_namespace}" id="fileopenobj${ssEntryId}${ss_namespace}" CODE = "com.sitescape.team.applets.fileopen.FileOpen" 
	  JAVA_CODEBASE = "<html:rootPath/>applets" 
	  ARCHIVE = "fileopen/ssf-fileopen-applet.jar" 
	  WIDTH = "1" HEIGHT = "1" MAYSCRIPT="true">
	</c:if>
	    <PARAM NAME="CODE" value = "com.sitescape.team.applets.fileopen.FileOpen" />
	    <PARAM NAME ="CODEBASE" value = "<html:rootPath/>applets" />
	    <PARAM NAME ="ARCHIVE" value = "fileopen/ssf-fileopen-applet.jar" />
	    <PARAM NAME ="type" value="application/x-java-applet;version=1.5" />
	    <param name = "scriptable" value="true" />
	    <PARAM NAME = "NAME" value = "fileopen" />
	    <PARAM NAME = "startingDir" value=""/>
	    <PARAM NAME = "fileToOpen" value="${ssEntryAttachmentURL}"/>
	    <PARAM NAME = "editorType" value="${ssEntryAttachmentEditorType}"/>
	    <PARAM NAME = "checkEditClicked" value="ss_checkEditClickLocal${ssEntryId}${ss_namespace}"/>
	    <PARAM NAME = "resetEditClicked" value="ss_resetEditClickLocal${ssEntryId}${ss_namespace}"/>
	    <PARAM NAME = "operatingSystem" value="${ssOSInfo}"/>
	    <PARAM NAME = "uploadErrorMessage" value="<ssf:nlt tag="exception.codedError.title" />" />
	<c:if test="<%= !isIE %>">
	</applet>
	</c:if>
	<c:if test="<%= isIE %>">
	</object>
	</c:if>
	<!--NOVELL_REWRITE_ATTRIBUTE_OFF='value'-->

</body>