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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/common/view_css.jsp" %>
<c:set var="ieBrowser" value="<%= org.kablink.util.BrowserSniffer.is_ie(request) %>"/>
<ssf:ifadapter>
<body class="tundra">
</ssf:ifadapter>

<ssf:form title="${ssWorkflowDefinition.title}">
<div style="padding:10px;">
<div style="border:1px solid #e5e5e5;">
<!--NOVELL_REWRITE_ATTRIBUTE_ON='value'-->
<c:if test="${ieBrowser == 'true'}">
<object classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" WIDTH = "100%" HEIGHT = "600"  
  codebase="http://java.sun.com/update/1.7.0/jinstall-7u72-windows-i586.cab">
</c:if>
<c:if test="${ieBrowser == 'false'}">
<applet CODE = "org.kablink.teaming.applets.workflowviewer.WorkflowViewer" 
  JAVA_CODEBASE = "<html:appletPath/>applets" 
  ARCHIVE = "workflow-viewer/kablink-teaming-workflowviewer-applet.jar,lib/colt.jar,lib/commons-collections.jar,lib/jung.jar,lib/dom4j.jar,lib/jaxen.jar" 
  WIDTH = "100%" HEIGHT = "600">
</c:if>
    <PARAM NAME = CODE value = "org.kablink.teaming.applets.workflowviewer.WorkflowViewer" >
    <PARAM NAME = CODEBASE value = "<html:appletPath/>applets" >
    <PARAM NAME = ARCHIVE value = "workflow-viewer/kablink-teaming-workflowviewer-applet.jar,lib/colt.jar,lib/commons-collections.jar,lib/jung.jar,lib/dom4j.jar,lib/jaxen.jar" >
    <param name="type" value="application/x-java-applet;version=1.7">
    <param name="scriptable" value="false">
	<param name="xmlGetUrl" value="<ssf:url 
    		webPath="viewDefinitionXml" >
			<ssf:param name="id" value="${ssWorkflowDefinitionId}" />
    		</ssf:url>&ss_random=<%= String.valueOf(new java.util.Date().getTime()) %>"/>"/>
	<param name="xmlPostUrl" value=""/>
	<param name="nltSaveLayout" value=""/>
<c:if test="${ieBrowser == 'false'}">
</applet>
</c:if>
<c:if test="${ieBrowser == 'true'}">
</object>
</c:if>
<!--NOVELL_REWRITE_ATTRIBUTE_OFF='value'-->
</div>
</div>
<input type="button" value="<ssf:nlt tag="button.close"/>" onClick="ss_cancelButtonCloseWindow();return false;">
</ssf:form>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
