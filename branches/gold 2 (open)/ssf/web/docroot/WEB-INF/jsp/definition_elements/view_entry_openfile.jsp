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
	    <PARAM NAME="CODE" VALUE = "com.sitescape.team.applets.fileopen.FileOpen" />
	    <PARAM NAME ="CODEBASE" VALUE = "<html:rootPath/>applets" />
	    <PARAM NAME ="ARCHIVE" VALUE = "fileopen/ssf-fileopen-applet.jar" />
	    <PARAM NAME ="type" value="application/x-java-applet;version=1.5" />
	    <param name = "scriptable" value="true" />
	    <PARAM NAME = "NAME" VALUE = "fileopen" />
	    <PARAM NAME = "startingDir" VALUE=""/>
	    <PARAM NAME = "fileToOpen" VALUE="${ssEntryAttachmentURL}"/>
	    <PARAM NAME = "editorType" VALUE="${ssEntryAttachmentEditorType}"/>
	    <PARAM NAME = "checkEditClicked" VALUE="ss_checkEditClickLocal${ssEntryId}${ss_namespace}"/>
	    <PARAM NAME = "resetEditClicked" VALUE="ss_resetEditClickLocal${ssEntryId}${ss_namespace}"/>
	    <PARAM NAME = "operatingSystem" VALUE="${ssOSInfo}"/>
	<c:if test="<%= !isIE %>">
	</applet>
	</c:if>
	<c:if test="<%= isIE %>">
	</object>
	</c:if>

</body>