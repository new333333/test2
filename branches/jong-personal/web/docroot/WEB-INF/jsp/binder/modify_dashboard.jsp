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

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_js.jsp" %>
<c:set var="ss_component_count" value="0" scope="request"/>
<c:set var="ss_dashboard_scope" value="${ssDashboard.scope}" scope="request"/>

<body class="ss_style_body tundra">
<div class="ss_style ss_portlet">
<ssf:form titleTag="dashboard.configureDashboard">
<div class="ss_form" style="margin:6px;">
<div style="margin:6px;">

<div class="ss_form">
<form class="ss_form" method="post">
<div class="ss_form ss_buttonBarRight">
<input type="submit" class="ss_submit" name="closeBtn" 
  value="<ssf:nlt tag="button.close" text="Close"/>">
<input type="hidden" name="_returnView" value="binder"/>
</div>
</form>
</div>

<div class="ss_form">
<c:if test="${ssDashboard.scope == 'local'}">
  <span class="ss_largerprint ss_bold"><ssf:nlt tag="dashboard.localDashboard" /></span>
  <br/>
</c:if>
<c:if test="${ssDashboard.scope == 'global'}">
  <span class="ss_largerprint ss_bold"><ssf:nlt tag="dashboard.globalDashboard" /></span>
  <br/>
</c:if>
<c:if test="${ssDashboard.scope == 'binder'}">
  <span class="ss_largerprint ss_bold"><ssf:nlt tag="dashboard.binderDashboard" /></span>
  <br/>
</c:if>
</div>
<br/>
<br/>

<div style="width:100%;">
<c:if test="${ssDashboard.scope == 'local'}">
  <span class="ss_bold"><ssf:nlt tag="dashboard.layout" /></span>
</c:if>
<c:if test="${ssDashboard.scope == 'global'}">
  <span class="ss_bold"><ssf:nlt tag="dashboard.setDefaultLayout" /></span>
</c:if>
<c:if test="${ssDashboard.scope == 'binder'}">
  <span class="ss_bold"><ssf:nlt tag="dashboard.setDefaultLayout" /></span>
</c:if>
<br/>

<c:set var="ss_dashboard_table_scope" value="${ssDashboard.scope}" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_table.jsp" %>

<c:if test="${empty ssDashboard.wide_top && 
                empty ssDashboard.narrow_fixed && 
                empty ssDashboard.narrow_variable && 
                empty ssDashboard.wide_bottom}">
<table cellpadding="6" style="width:100%;">
  <tr>
    <td nowrap>
      <span class="ss_italics ss_smallprint">
        <ssf:nlt tag="dashboard.noComponents"/>
      </span>
    </td>
  </tr>
</table>
</c:if>
</div>

<div class="ss_formBreak"></div>

<div class="ss_buttonBarLeft">
<form method="post">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
<input type="hidden" name="_returnView" value="binder"/>
</form>
</div>

</div>
</div>
</div>
</ssf:form>
</div>

</body>
</html>
