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
<%@ page import="com.sitescape.team.util.NLT" %>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

<form class="ss_style ss_form" name="${renderResponse.namespace}fm" id="${renderResponse.namespace}fm" method="post" 
	action="<ssf:url action="configure_search_nodes" actionUrl="true"/>">
<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" />">
<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
</div>

<c:if test="${!empty ssSearchNodes}">
  <c:forEach var="node" items="${ssSearchNodes}">
	<fieldset class="ss_fieldset">
	  <legend class="ss_legend">${node.title} (${node.id})</legend>	
	<table class="ss_style" border="0" cellspacing="3" cellpadding="3">
	<tr><td valign="top">
	<c:set var="properties" value="${node.displayProperties}"/>
	<c:if test="${!empty properties}">
		<c:forEach var="property" items="${properties}">
			<c:out value="${property.key}"/>: <c:out value="${property.value}"/><br/>
		</c:forEach>
	</c:if>
	<table class="ss_style" border="0" cellspacing="3" cellpadding="3">
	<tr><td valign="top">
	<hr shade=noshade size=1/>
	<c:if test="${!node.inSynch}">
		<blink><span style="color:red"><ssf:nlt tag="administration.search.node.synch.false" /></span></blink>
		<br/><input type="checkbox" name="synchronize${node.id}" <c:if test="${node.accessMode == 'offline'}">disabled</c:if>><span class="ss_labelRight ss_normal"><ssf:nlt tag="administration.search.node.synch.synchronize" text="Synchronize it now"/></span>
	</c:if>
	<c:if test="${node.inSynch}">
		<ssf:nlt tag="administration.search.node.synch.true" />
	</c:if>
	<br/>
	<table class="ss_style" border ="0" cellspacing="0" cellpadding="0" width="100%">
	<tr><td valign="top">
	<hr shade=noshade size=1/>
	<span class="ss_labelLeft"><ssf:nlt tag="administration.search.node.accessmode"/></span><br>
		<input type="radio" name="accessMode${node.id}" value="readwrite" <c:if test="${node.accessMode == 'readwrite'}">checked</c:if>><span class="ss_labelRight ss_normal"><ssf:nlt tag="administration.search.node.accessmode.readwrite" /></span><br>
		<input type="radio" name="accessMode${node.id}" value="writeonly" <c:if test="${node.accessMode == 'writeonly'}">checked</c:if>><span class="ss_labelRight ss_normal"><ssf:nlt tag="administration.search.node.accessmode.writeonly" /></span><br>
		<input type="radio" name="accessMode${node.id}" value="offline" <c:if test="${node.accessMode == 'offline'}">checked</c:if>><span class="ss_labelRight ss_normal"><ssf:nlt tag="administration.search.node.accessmode.offline" /></span><br>
	</td></tr>
	</table>
	</td></tr>
	</table>
	</td></tr>
	</table>
	</fieldset>
  </c:forEach>
</c:if>

<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" />">
<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="self.window.close();return false;"/>
</div>
</form>

</div>
</div>
</body>
</html>
