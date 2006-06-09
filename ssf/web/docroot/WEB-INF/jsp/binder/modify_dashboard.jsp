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
<h3><ssf:nlt tag="summary.configure" text="Configure summary options"/></h3>

<form name="inheritanceForm" method="post" 
  onSubmit="return ss_onSubmit(this);"
  action="<portlet:actionURL>
		  <portlet:param name="action" value="modify_dashboard"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
		  </portlet:actionURL>">
<div class="ss_form">
<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>

<span calss="ss_bold"><ssf:nlt tag="dashboard.title" /></span><br/>
<input type="text" name="title" size="60" value="${ssDashboard.title}"/><br/>
<c:set var="checked" value=""/>
<c:if test="${ssDashboard.includeBinderTitle}">
  <c:set var="checked" value="checked=checked"/>
</c:if>
<input type="checkbox" name="includeBinderTitle" <c:out value="${checked}"/> />
<span><ssf:nlt tag="dashboard.includeBinderTitle"/></span>
<br/>

<input type="submit" class="ss_submit" name="set_title" 
  value="<ssf:nlt tag="button.apply" text="Apply"/>"> 
  
<br/>
<br/>

<div style="width:100%;">
<table border="1" style="width:100%;">
  <tr>
    <td colspan="2">
    
<c:forEach var="component" items="${ssDashboard.wide_top}">
  <c:set var="id" value="${component.value.id}"/>
<div>
<ssf:dashboard name="${ssDashboard.components[id].name}" 
  type="config" configuration="ssDashboard"/>
</div>
<br/>
</c:forEach>

      <br/>
      <br/>
      <select name="name">
        <option value="">--<ssf:nlt tag="dashboard.selectComponent"/>--</option>
        <c:forEach var="component" items="${ssDashboard.components_wide}">
          <option value="${component}"><ssf:nlt 
            tag="dashboard.componentTitle.${component}"/></option>
        </c:forEach>
      </select>
      <input type="submit" name="add_wideTop" 
        value="<ssf:nlt tag="button.add"/>"/>
    </td>
  </tr>
  <tr>
    <td valign="top" width="${ssDashboard.narrowFixedWidth}">
      <br/>
      <br/>
      <select name="name">
        <option value="">--<ssf:nlt tag="dashboard.selectComponent"/>--</option>
        <c:forEach var="component" items="${ssDashboard.components_narrow_fixed}">
          <option value="${component}"><ssf:nlt 
            tag="dashboard.componentTitle.${component}"/></option>
        </c:forEach>
      </select>
      <input type="submit" name="add_narrowFixed" 
        value="<ssf:nlt tag="button.add"/>"/>
    </td>
    <td valign="top">
      <br/>
      <br/>
      <select name="name">
        <option value="">--<ssf:nlt tag="dashboard.selectComponent"/>--</option>
        <c:forEach var="component" items="${ssDashboard.components_narrow_variable}">
          <option value="${component}"><ssf:nlt 
            tag="dashboard.componentTitle.${component}"/></option>
        </c:forEach>
      </select>
      <input type="submit" name="add_narrowVariable" 
        value="<ssf:nlt tag="button.add"/>"/>
    </td>
  </tr>
  <tr>
    <td colspan="2">
      <br/>
      <br/>
      <select name="name">
        <option value="">--<ssf:nlt tag="dashboard.selectComponent"/>--</option>
        <c:forEach var="component" items="${ssDashboard.components_wide}">
          <option value="${component}"><ssf:nlt 
            tag="dashboard.componentTitle.${component}"/></option>
        </c:forEach>
      </select>
      <input type="submit" name="add_wideBottom" 
        value="<ssf:nlt tag="button.add"/>"/>
    </td>
  </tr>
</table>
</div>

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>

</div>
</form>
</div>
</div>
</div>
</div>

