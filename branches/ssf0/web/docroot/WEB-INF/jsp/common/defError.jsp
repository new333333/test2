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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@page import="java.io.PrintWriter" %>

<h1><spring:message code="exception.generalError.title"/></h1>

<p><spring:message code="exception.contactAdmin"/></p>


<ssf:ifadapter>
<input type="button" value="<ssf:nlt tag="button.returnToForm"/>" onclick="history.go(-1);"/><input type="button" value="<ssf:nlt tag="button.close"/>" onclick="window.close();"/>
</ssf:ifadapter>
<ssf:ifnotadapter>
<p style="text-align:center;">
<a href="<portlet:renderURL portletMode="view" windowState="normal"/>">- <spring:message code="button.home"/> -</a>
</p>
</ssf:ifnotadapter>

<p>
${exception.class}<br/>
${exception.localizedMessage == null ? exception : exception.localizedMessage }
</p>

<input type="button" id="ss_show" value="<ssf:nlt tag="button.showDetails"/>" onclick="document.getElementById('ss_details').style.display='block'; document.getElementById('ss_hide').style.display='inline'; this.style.display='none'"/>
<input type="button" id="ss_hide" value="<ssf:nlt tag="button.hideDetails"/>" onclick="document.getElementById('ss_details').style.display='none'; document.getElementById('ss_show').style.display='inline'; this.style.display='none'" style="display:none;"/>
<div id="ss_details" style="display:none;">
<%((Exception)request.getAttribute("exception")).printStackTrace(new PrintWriter(out)); %>
</div>

<ssf:ifnotadapter>
<p style="text-align:center;"><a href="<portlet:renderURL portletMode="view" windowState="normal"/>">- <spring:message code="button.home"/> -</a></p>
</ssf:ifnotadapter>
