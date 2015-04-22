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
%><%--
--%><%@ include file="/WEB-INF/jsp/common/include.jsp" %><%--
--%><%@ page import="org.kablink.util.ParamUtil" %><%--
--%><portletadapter:defineObjects1/><%--
--%><ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter><%--
--%><ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter><%--
--%>
<c:if test="${empty ss_do_not_show_form_wrapper && empty ss_formTagTopApplied}">
<table cellspacing="0" cellpadding="0" width="100%"><tr><td>
<div class="${ss_formClass}">
    <div class="ss_form_header">
      <table cellspacing="0" cellpadding="0" width="100%">
      <tr>
      <td valign="top">
        <c:if test="${!empty ss_title_tag}">
    	  <ssf:nlt tag="${ss_title_tag}"/>
        </c:if>
        <c:if test="${!empty ss_title}">
    	  <ssf:nlt tag="${ss_title}" checkIfTag="true"/>
        </c:if>
		<c:if test="${!empty helpGuideName}"><ssf:showHelp guideName="${helpGuideName}" pageId="${helpPageId}" /></c:if>
      </td>
      <td valign="top" align="right">
		<%--
		<img border="0" <ssf:alt/>
			 height="16" hspace="0" 
			 name="p_${renderResponse.namespace}_close" 
			 src="<html:imagesPath/>icons/close_teal16.png" 
			 title="<ssf:nlt tag="icon.close" text="Close" />" 
			 vspace="0" width="16" >
		--%>
		<img border="0" <ssf:alt/>
			 height="16" hspace="0" 
			 name="p_${renderResponse.namespace}_close" 
			 src="<html:rootPath/>images/pics/1pix.gif" 
			 title="<ssf:nlt tag="icon.close" text="Close" />" 
			 vspace="0" width="16" >
      </td>
      </tr>
      </table>
    </div>
</c:if>
<c:set var="ss_formTagTopApplied" value="1"/>

