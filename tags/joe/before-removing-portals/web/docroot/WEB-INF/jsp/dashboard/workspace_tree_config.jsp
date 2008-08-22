<%
// The dashboard "workspace tree" component
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
<br/>
<c:set var="treeName" value="wsTree${ssComponentId}${renderResponse.namespace}"/>
<script type="text/javascript">
function ${treeName}_showId(forum, obj) {
	return ss_checkTree(obj, "ss_tree_radio${treeName}data_topId" + forum);
}
</script>
<table>

<tr>
<td valign="top">
<span class="ss_bold"><ssf:nlt tag="dashboard.startingPoint"/></span>
<br/>
<div class="ss_indent_medium">
<c:set var="checked" value=""/>
<c:if test="${empty ssDashboard.dashboard.components[ssComponentId].data.start || 
    ssDashboard.dashboard.components[ssComponentId].data.start== 'this'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_start" value="this" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="dashboard.startingPoint.current"/><br/>

<c:set var="checked" value=""/>
<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.start == 'select'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_start" value="select" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="dashboard.startingPoint.select"/><br/>
</div>
</td>
</tr>
<tr><td>
<ssf:tree 
  treeName="${treeName}"
  treeDocument="${ssDashboard.beans[ssComponentId].workspaceTree}"  
  rootOpen="true" 
  singleSelect="${ssDashboard.beans[ssComponentId].ssBinder.id}" 
  singleSelectName="data_topId"
/>
</td></tr>
<tr>
<td valign="top"><br/></td>
</tr>

<tr>
<td valign="top">
<span class="ss_bold"><ssf:nlt tag="dashboard.rootOpen"/></span>
<br/>
<div class="ss_indent_medium">
<c:set var="checked" value=""/>
<c:if test="${empty ssDashboard.dashboard.components[ssComponentId].data.rootOpen || 
    ssDashboard.dashboard.components[ssComponentId].data.rootOpen== 'true'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_rootOpen" value="true" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="general.yes"/><br/>

<c:set var="checked" value=""/>
<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.rootOpen == 'false'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_rootOpen" value="false" 
  <c:out value="${checked}"/> />&nbsp;<ssf:nlt tag="general.no"/><br/>
</div>
</td>
</tr>

</table>
