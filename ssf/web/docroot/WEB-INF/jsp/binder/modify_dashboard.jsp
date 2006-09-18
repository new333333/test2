<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_js.jsp" %>
<c:set var="ss_component_count" value="0" scope="request"/>

<div class="ss_style ss_portlet">
<div class="ss_form" style="margin:6px;">
<div style="margin:6px;">
<span class="ss_largerprint"><ssf:nlt tag="dashboard.configureDashboard"/></span>
<br/>

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

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<form method="post">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
<input type="hidden" name="_returnView" value="binder"/>
</form>
</div>

</div>
</div>
</div>

