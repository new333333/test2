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

<div class="ss_style ss_portlet">
<div class="ss_form" style="margin:6px;">
<div class="ss_rounded">
<div style="margin:6px;">
<h3><ssf:nlt tag="dashboard.configure" text="Configure dashboard options"/></h3>

<div class="ss_form"><form 
class="ss_form" method="post"><div class="ss_form ss_buttonBarRight">
<input type="submit" class="ss_submit" name="closeBtn" 
  value="<ssf:nlt tag="button.close" text="Close"/>">
<input type="hidden" name="_returnView" value="${ssDashboard.returnView}"/>
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
<br/>
</div>
<br/>
<br/>

  
<form method="post" >
<div style="width:100%;">
<span class="ss_bold"><ssf:nlt tag="dashboard.layout" /></span><br/>
<table border="1" style="width:100%;">
  <tr>
    <td>
 		<c:set var="id" value="${ssDashboard.ssComponentId}"/>
		<div class="ss_dashboard_config">
		<span class="ss_bold"><ssf:nlt checkIfTag="true"
		  tag="${ssDashboard.component_titles[ssDashboard.dashboard.components[id].name]}"/></span>
		<br/>
		<form method="post">
		<div style="margin:5px;">
		<ssf:dashboard id="${id}" type="config" configuration="${ssDashboard}"/>
		<input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
		<input type="hidden" name="_componentId" value="${id}">
		<input type="submit" name="_saveConfigData" value="<ssf:nlt tag="button.saveChanges"/>">
		&nbsp;&nbsp;&nbsp;
		<input type="submit" name="_deleteComponent" value="<ssf:nlt tag="button.delete"/>">
		
		</div>
		<input type="hidden" name="_scope" value="${ssDashboard.scope}"/>
		<input type="hidden" name="_returnView" value="${ssDashboard.returnView}"/>
		</form>
		</div>
    </td>
  </tr>
</table>
</div>

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<form method="post">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
<input type="hidden" name="_returnView" value="${ssDashboard.returnView}"/>
</form>
</div>

</div>
</div>
</div>
</div>
</div>

