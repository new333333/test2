<%
// The dashboard "workspace tree" component
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
<input type="radio" name="data_start" id="this" value="this" 
  <c:out value="${checked}"/> /><label for="this">&nbsp;<ssf:nlt tag="dashboard.startingPoint.current"/><br/></label>

<c:set var="checked" value=""/>
<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.start == 'select'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_start" id="select" value="select" 
  <c:out value="${checked}"/> /><label for="select">&nbsp;<ssf:nlt tag="dashboard.startingPoint.select"/><br/></label>
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
<input type="radio" name="data_rootOpen" id="true" value="true" 
  <c:out value="${checked}"/> /><label for="true">&nbsp;<ssf:nlt tag="general.yes"/><br/></label>

<c:set var="checked" value=""/>
<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.rootOpen == 'false'}">
  <c:set var="checked" value="checked=\"checked\""/>
</c:if>
<input type="radio" name="data_rootOpen" id="false" value="false" 
  <c:out value="${checked}"/> /><label for="false">&nbsp;<ssf:nlt tag="general.no"/><br/></label>
</div>
</td>
</tr>

</table>
