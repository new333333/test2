<%@ taglib prefix="ssf" uri="http://www.sitescape.com/tags-ssf" %>
<%@ page isELIgnored="false" %>
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
if(request.getParameter("launchJnlp")!=null){	
	String url = request.getRequestURL().toString();
	String baseURL = url.substring(0, url.length() - request.getRequestURI().length()) + request.getContextPath() + "/";
%>
<jnlp
  spec="1.0+"
  codebase="<%=baseURL%>ssf/applets"
  href="">
  <information>
	<title>Micro Focus Filr</title>
	<vendor>Micro Focus</vendor>
	<homepage href="http://www.microfocus.com"/>
	<description>Filr WebDav Client.</description>
	<description kind="short">Filr WebDAV Client</description>
	<offline-allowed/>
  </information>
  <resources>
	<j2se version="1.5+"/>
	<jar href="<%= request.getContextPath() %>/applets/fileopen/kablink-teaming-fileopen-applet.jar"/>
  </resources>
  <security>
	<all-permissions/>
  </security>
    <applet-desc name="Filr Edit In Place" width="1" height="1" main-class="org.kablink.teaming.applets.fileopen.FileOpen">
	    <param name = "scriptable" value="true"></param>
	    <param name = "NAME" value = "fileopen"></param>
	    <param name = "startingDir" value=""></param>
	    <param name = "fileToOpen" value="${ssEntryAttachmentURL}"></param>
	    <param name = "editorType" value="${ssEntryAttachmentEditorType}"></param>
        <param name = "isLicenseRequiredEdition" value="${ssIsLicenseRequiredEdition}"></param>
        <param name = "isOfficeAddInAllowed" value="${ssIsOfficeAddInAllowed}"></param>
        <param name = "userName" value="${ssUser.name}"></param>
	    <param name = "checkEditClicked" value="ss_checkEditClickLocal${ssEntryId}${ss_namespace}"></param>
	    <param name = "resetEditClicked" value="ss_resetEditClickLocal${ssEntryId}${ss_namespace}"></param>
	    <param name = "operatingSystem" value="${ssOSInfo}"></param>
		<param name = "uploadErrorFileTooLarge" value="<ssf:nlt tag="applet.errorFileTooLarge"/>"></param>
		<param name = "fileUploadMaxSize" value="${ss_binder_file_max_file_size}"></param>
		<param name = "fileUploadSizeExceeded" value="<ssf:nlt tag="file.maxSizeExceeded"/>" ></param>
	    <param name = "uploadErrorMessage" value="<ssf:nlt tag="exception.codedError.title"/>"></param>
	    <param name = "editorErrorMessage" value="<ssf:nlt tag="applet.editorError" />"></param>
  </applet-desc>
</jnlp>
<%
}
else{
	String url=request.getRequestURL()+"?"+request.getQueryString()+"&launchJnlp=true"+"&jsessionid="+request.getSession().getId();
%>
    <script>
		window.location="<%=url%>"
	</script>
<%
}
%>