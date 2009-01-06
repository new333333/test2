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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.reload.definitions") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.reload.definitions">
<br/>

<form class="ss_style ss_form" method="post" 
		  action="<ssf:url action="import_definition" actionUrl="true"><ssf:param 
		  name="operation" value="reload"/><ssf:param 
		  name="binderId" value="${ssBinderId}"/></ssf:url>" 
		  name="${renderResponse.namespace}fm">

<span class="ss_titlebold"><ssf:nlt tag="administration.reset.definitions.select"/></span>
<table class="ss_style" width="100%"><tr><td>

<%@include file="/WEB-INF/jsp/administration/commonSelectTree.jsp" %>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="button" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick='self.location.href="<ssf:url action="manage_definitions" 
			actionUrl="false"><ssf:param name="binderId" value="${ssBinderId}"/></ssf:url>";return false;'>
<script type="text/javascript">
document.${renderResponse.namespace}fm.onsubmit=function() { return ss_selectAllIfNoneSelected.call(this,"id_");};
</script>
<br>
</td></tr></table>


<br/>
<br/>
<span>
<ssf:nlt tag="administration.reload.definitions.warning"/>
</span>
<br/>
<br/> 

</form>

</ssf:form>
</div>
</div>
</body>
</html>

