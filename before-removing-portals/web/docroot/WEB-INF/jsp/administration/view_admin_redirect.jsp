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
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

<c:if test="${empty ssErrorList}">
<script type="text/javascript">
var timeout = 0;
<c:if test="${!empty ssDownloadURL}">
timeout = 200;
</c:if>
setTimeout("self.window.close()", timeout);
</script>
</c:if>
<c:if test="${!empty ssErrorList}">
<form class="ss_style ss_form" >
<br/>
<br/>
<span class="ss_bold"><ssf:nlt tag="administration.errors"/></span>
<br/>
<br/>
<ul>
<c:forEach var="err" items="${ssErrorList}">
	<li>${err}</li>
</c:forEach>
</ul>
<input type="submit" class="ss_submit" name="cancelBtn" onClick="self.window.close();return false;" value="<ssf:nlt tag="button.ok" text="OK"/>">

</form>
</c:if>
</div>
</div>
</body>
</html>