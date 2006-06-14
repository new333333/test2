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
class="ss_form" method="post"><div class="ss_form ss_buttonBarRight"><input 
type="submit" class="ss_submit" name="closeBtn" 
  value="<ssf:nlt tag="button.close" text="Close"/>"></form></div>

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

  
<form method="post" >
<span class="ss_bold"><ssf:nlt tag="dashboard.title" /></span><br/>
<input type="text" name="title" size="60" value="${ssDashboard.dashboard.title}"/><br/>
<c:set var="checked" value=""/>
<c:if test="${ssDashboard.dashboard.includeBinderTitle}">
  <c:set var="checked" value="checked=checked"/>
</c:if>
<input type="checkbox" name="includeBinderTitle" <c:out value="${checked}"/> />
<span><ssf:nlt tag="dashboard.includeBinderTitle"/></span>
<br/>

<input type="submit" class="ss_submit" name="set_title" 
  value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
<input type="hidden" name="scope" value="${ssDashboard.scope}"/>
</form>  
<br/>
<br/>

<div style="width:100%;">
<span class="ss_bold"><ssf:nlt tag="dashboard.layout" /></span><br/>
<table border="1" style="width:100%;">
  <tr>
    <td colspan="2">
    
      <c:forEach var="component" items="${ssDashboard.dashboard.wide_top}">
		<c:set var="id" value="${component.id}"/>
		<div class="ss_dashboard_config">
		<span class="ss_bold"><ssf:nlt checkIfTag="true"
		  tag="${ssDashboard.component_titles[ssDashboard.dashboard.components[id].name]}"/></span>
		<br/>
		<form method="post">
		<div style="margin:5px;">
		<ssf:dashboard id="${id}" type="config" configuration="${ssDashboard}"/>
		<input type="hidden" name="_dashboardList" value="wide_top">
		<input type="hidden" name="_componentId" value="${id}">
		<input type="submit" name="_saveConfigData" value="<ssf:nlt tag="button.saveChanges"/>">
		&nbsp;&nbsp;&nbsp;
		<input type="submit" name="_deleteComponent" value="<ssf:nlt tag="button.delete"/>">
		&nbsp;&nbsp;&nbsp;
		<input type="submit" name="_moveUp" value="<ssf:nlt tag="button.moveUp"/>">
		&nbsp;&nbsp;&nbsp;
		<input type="submit" name="_moveDown" value="<ssf:nlt tag="button.moveDown"/>">
		
		</div>
		<input type="hidden" name="scope" value="${ssDashboard.scope}"/>
		</form>
		</div>
		<br/>
	  </c:forEach>

      <br/>
      <br/>
	  <form method="post">
      <select name="name">
        <option value="">--<ssf:nlt tag="dashboard.selectComponent"/>--</option>
        <c:forEach var="component" items="${ssDashboard.components_wide}">
          <option value="${component}"><ssf:nlt checkIfTag="true"
            tag="${ssDashboard.component_titles[component]}"/></option>
        </c:forEach>
      </select>
      <input type="submit" name="add_wideTop" 
        value="<ssf:nlt tag="button.add"/>"/>
      <input type="hidden" name="scope" value="${ssDashboard.scope}"/>
      </form>
    </td>
  </tr>
  <tr>
    <td valign="top">
	    <img src="<html:imagesPath/>pics/1pix.gif" 
	      hspace="${ssDashboard.narrowFixedWidth/2}px" vspace="0px"/><br/>
		<c:forEach var="component" items="${ssDashboard.dashboard.narrow_fixed}">
		  <c:set var="id" value="${component.id}"/>
		<div class="ss_dashboard_config">
		<span class="ss_bold"><ssf:nlt checkIfTag="true"
		  tag="${ssDashboard.component_titles[ssDashboard.dashboard.components[id].name]}"/></span>
		<br/>
		<form method="post">
		<div style="margin:5px;">
		<ssf:dashboard id="${id}" type="config" configuration="${ssDashboard}"/>
		<input type="hidden" name="_dashboardList" value="narrow_fixed">
		<input type="hidden" name="_componentId" value="${id}">
		<input type="submit" name="_saveConfigData" value="<ssf:nlt tag="button.saveChanges"/>">
		&nbsp;&nbsp;&nbsp;
		<input type="submit" name="_deleteComponent" value="<ssf:nlt tag="button.delete"/>">
		&nbsp;&nbsp;&nbsp;
		<input type="submit" name="_moveUp" value="<ssf:nlt tag="button.moveUp"/>">
		&nbsp;&nbsp;&nbsp;
		<input type="submit" name="_moveDown" value="<ssf:nlt tag="button.moveDown"/>">
		
		</div>
		<input type="hidden" name="scope" value="${ssDashboard.scope}"/>
		</form>
		</div>
		<br/>
		</c:forEach>

        <br/>
        <br/>
        <form method="post">
        <select name="name">
          <option value="">--<ssf:nlt tag="dashboard.selectComponent"/>--</option>
          <c:forEach var="component" items="${ssDashboard.components_narrow_fixed}">
            <option value="${component}"><ssf:nlt checkIfTag="true"
              tag="${ssDashboard.component_titles[component]}"/></option>
          </c:forEach>
        </select>
        <input type="submit" name="add_narrowFixed" 
          value="<ssf:nlt tag="button.add"/>"/>
        <input type="hidden" name="scope" value="${ssDashboard.scope}"/>
        </form>
    </td>
    <td valign="top">
		<c:forEach var="component" items="${ssDashboard.dashboard.narrow_variable}">
		  <c:set var="id" value="${component.id}"/>
		<div class="ss_dashboard_config">
		<span class="ss_bold"><ssf:nlt checkIfTag="true"
		  tag="${ssDashboard.component_titles[ssDashboard.dashboard.components[id].name]}"/></span>
		<br/>
		<form method="post">
		<div style="margin:5px;">
		<ssf:dashboard id="${id}" type="config" configuration="${ssDashboard}"/>
		<input type="hidden" name="_dashboardList" value="narrow_variable">
		<input type="hidden" name="_componentId" value="${id}">
		<input type="submit" name="_saveConfigData" value="<ssf:nlt tag="button.saveChanges"/>">
		&nbsp;&nbsp;&nbsp;
		<input type="submit" name="_deleteComponent" value="<ssf:nlt tag="button.delete"/>">
		&nbsp;&nbsp;&nbsp;
		<input type="submit" name="_moveUp" value="<ssf:nlt tag="button.moveUp"/>">
		&nbsp;&nbsp;&nbsp;
		<input type="submit" name="_moveDown" value="<ssf:nlt tag="button.moveDown"/>">
		
		</div>
		<input type="hidden" name="scope" value="${ssDashboard.scope}"/>
		</form>
		</div>
		<br/>
		</c:forEach>

        <br/>
        <br/>
        <form method="post">
        <select name="name">
          <option value="">--<ssf:nlt tag="dashboard.selectComponent"/>--</option>
          <c:forEach var="component" items="${ssDashboard.components_narrow_variable}">
            <option value="${component}"><ssf:nlt checkIfTag="true"
              tag="${ssDashboard.component_titles[component]}"/></option>
          </c:forEach>
        </select>
        <input type="submit" name="add_narrowVariable" 
          value="<ssf:nlt tag="button.add"/>"/>
        <input type="hidden" name="scope" value="${ssDashboard.scope}"/>
        </form>
    </td>
  </tr>
  <tr>
    <td colspan="2">

		<c:forEach var="component" items="${ssDashboard.dashboard.wide_bottom}">
		  <c:set var="id" value="${component.id}"/>
		<div class="ss_dashboard_config">
		<span class="ss_bold"><ssf:nlt checkIfTag="true"
		  tag="${ssDashboard.component_titles[ssDashboard.dashboard.components[id].name]}"/></span>
		<br/>
		<form method="post">
		<div style="margin:5px;">
		<ssf:dashboard id="${id}" type="config" configuration="${ssDashboard}"/>
		<input type="hidden" name="_dashboardList" value="wide_bottom">
		<input type="hidden" name="_componentId" value="${id}">
		<input type="submit" name="_saveConfigData" value="<ssf:nlt tag="button.saveChanges"/>">
		&nbsp;&nbsp;&nbsp;
		<input type="submit" name="_deleteComponent" value="<ssf:nlt tag="button.delete"/>">
		&nbsp;&nbsp;&nbsp;
		<input type="submit" name="_moveUp" value="<ssf:nlt tag="button.moveUp"/>">
		&nbsp;&nbsp;&nbsp;
		<input type="submit" name="_moveDown" value="<ssf:nlt tag="button.moveDown"/>">
		
		</div>
		<input type="hidden" name="scope" value="${ssDashboard.scope}"/>
		</form>
		</div>
		<br/>
		</c:forEach>

        <br/>
        <br/>
	    <form method="post">
        <select name="name">
          <option value="">--<ssf:nlt tag="dashboard.selectComponent"/>--</option>
          <c:forEach var="component" items="${ssDashboard.components_wide}">
            <option value="${component}"><ssf:nlt checkIfTag="true"
              tag="${ssDashboard.component_titles[component]}"/></option>
          </c:forEach>
        </select>
        <input type="submit" name="add_wideBottom" 
          value="<ssf:nlt tag="button.add"/>"/>
        <input type="hidden" name="scope" value="${ssDashboard.scope}"/>
        </form>
    </td>
  </tr>
</table>
</div>

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<form method="post">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</form>
</div>

</div>
</div>
</div>
</div>
</div>

