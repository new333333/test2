<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<% //YouTube view %>
<c:if test="${!empty ss_youTubeUrl}">
<%
	//Adding the object can only be done to a real www.youtube.com URL
	String str_youTubeUrl = (String) request.getAttribute("ss_youTubeUrl");
	Boolean is_youTubeUrl = str_youTubeUrl.toLowerCase().startsWith("http://www.youtube.com/");
%>
<%
	if (is_youTubeUrl) {
%>
<c:if test="${empty ss_youTubeDivNum}">
<c:set var="ss_youTubeDivNum" value="0" scope="request"/>
</c:if>
<c:set var="ss_youTubeDivNum" value="${ss_youTubeDivNum + 1}" scope="request"/>
<script type="text/javascript">
ss_createSpannedAreaObj("ss_youTubeDiv${ss_youTubeDivNum}");
</script>"
<div id="ss_youTubeDiv${ss_youTubeDivNum}" class="ss_videoDiv" style="padding:4px 10px 20px 10px;">
  <object width="425" height="344"><param name="movie"
    value="${ss_youTubeUrl}"></param><param
    name="allowFullScreen" value="true"></param><param name="allowscriptaccess"
    value="always"></param><param name="wmode"
    value="opaque"></param><embed
    src="${ss_youTubeUrl}"
    type="application/x-shockwave-flash" allowscriptaccess="always"
    allowfullscreen="true" width="425" height="344" wmode="opaque"></embed></object>
</div>
<%
	} else {
%>
<div style="padding:4px 10px 20px 10px;">
  <a target="_blank" href="${ss_youTubeUrl}"><ssf:nlt tag="__youTubeWatch"/></a>
</div>
<%
	}
%>
</c:if>
