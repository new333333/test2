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
<% // Folder page %>
<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.ssfs.util.SsfsUtil" %>
<%
boolean isIECheck = BrowserSniffer.is_ie(request);
String strBrowserType = "";
if (isIECheck) strBrowserType = "ie";
%>
<jsp:useBean id="ssUserFolderProperties" type="java.util.Map" scope="request" />
<jsp:useBean id="ssBinder" type="org.kablink.teaming.domain.Binder" scope="request" />
<c:set var="ss_folderViewColumnsType" value="folder" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/folder_column_defaults.jsp" %>
<c:set var="slidingTableStyle" value="sliding"/>
<c:if test="${empty ss_folderViewStyle || ss_folderViewStyle == 'folder'}">
  <c:set var="slidingTableStyle" value="fixed"/>
</c:if>

<div <c:if test="${slidingTableStyle == 'fixed'}">id="ss_folder_view_common${renderResponse.namespace}"</c:if>>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_view_common2.jsp" %>
</div>
