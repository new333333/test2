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

<div class="ss_style ss_portlet">
<div class="ss_form" style="margin:6px;">
<div class="ss_rounded">
<div style="margin:6px;">
<span class="ss_bold ss_largerprint"><ssf:nlt tag="dashboard.setTitle"/></span>
<br>

<div class="ss_form">
<form method="post" action="<portlet:actionURL>
  <portlet:param name="action" value="modify_dashboard"/>
  <portlet:param name="binderId" value="${ssBinder.id}"/>
  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
  </portlet:actionURL>">
<div class="ss_form ss_buttonBarRight">
<input type="submit" class="ss_submit" name="closeBtn" 
  value="<ssf:nlt tag="button.close" text="Close"/>">
<input type="hidden" name="_returnView" value="binder"/>
</div>
</form>
</div>

<div style="width:100%;">
<span class="ss_bold"><ssf:nlt tag="dashboard.currentTitles" /></span>
<br/>
<div class="ss_indent_medium">
  <table>
    <tr>
      <td valign="top"><ssf:nlt tag="dashboard.currentTitle.local" />:&nbsp;</td>
      <td valign="top"><c:out value="${ssDashboard.dashboard_local.title}"/>
		<c:if test="${ssDashboard.dashboard_local.includeBinderTitle}">
		  <br>
		  <span class="ss_smallprint ss_italic"><ssf:nlt tag="dashboard.binderTitleIncluded"/></span>
		</c:if>
      </td>
    </tr>
    <tr>
      <td valign="top"><ssf:nlt tag="dashboard.currentTitle.global" />:&nbsp;</td>
      <td valign="top"><c:out value="${ssDashboard.dashboard_global.title}"/>
		<c:if test="${ssDashboard.dashboard_global.includeBinderTitle}">
		  <br>
		  <span class="ss_smallprint ss_italic"><ssf:nlt tag="dashboard.binderTitleIncluded"/></span>
		</c:if>
      </td>
    </tr>
    <tr>
      <td valign="top"><ssf:nlt tag="dashboard.currentTitle.binder" />:&nbsp;</td>
      <td valign="top"><c:out value="${ssDashboard.dashboard_binder.title}"/>
		<c:if test="${ssDashboard.dashboard_binder.includeBinderTitle}">
		  <br>
		  <span class="ss_smallprint ss_italic"><ssf:nlt tag="dashboard.binderTitleIncluded"/></span>
		</c:if>
      </td>
    </tr>
  </table>
</div>
<br/>

<span class="ss_bold"><ssf:nlt tag="dashboard.newTitle" /></span>
<br/>
<div class="ss_indent_medium">
	  <form method="post" action="<portlet:actionURL>
		  <portlet:param name="action" value="modify_dashboard"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
	      </portlet:actionURL>">
		<input type="text" name="title" size="80"/><br/>
		<input type="checkbox" name="includeBinderTitle"/>
		<span><ssf:nlt tag="dashboard.includeBinderTitle"/></span>
		<br/>
		<br/>
	
	    <span class="ss_bold"><ssf:nlt tag="dashboard.titleScope"/></span><br>
	    <input type="radio" name="_scope" value="local" checked/><ssf:nlt tag="dashboard.titleScope.local"/><br>
	    <input type="radio" name="_scope" value="global"/><ssf:nlt tag="dashboard.titleScope.global"/><br>
	    <c:if test="${ssDashboard.sharedModificationAllowed}">
	      <input type="radio" name="_scope" value="binder"/><ssf:nlt tag="dashboard.titleScope.binder"/><br>
	    </c:if>
		<br/>
		<br/>
		
		<input type="submit" class="ss_submit" name="set_title" 
		  value="<ssf:nlt tag="button.ok"/>"> 
		&nbsp;&nbsp;
		<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
		<input type="hidden" name="_returnView" value="binder"/>
	  </form>
</div>
</div>

</div>
</div>
</div>
</div>
</div>

