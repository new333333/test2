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
<c:set var="formName" value="<%= renderResponse.getNamespace() + "searchfm" %>"/>

<form method="post" class="ss_style ss_form"  name="${formName}" id="${formName}"
	action="<portlet:actionURL/>"  onSubmit="return ss_onSubmit(this);">

<input type="hidden" name="componentName" value="search"/>
<br/>
<br/>
<ssf:nlt tag="dashboard.config.search"/>
<br/>
<br/>

<c:if test="${!empty ssDashboard}">
<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
<ssf:searchForm form="${formName}" element="data.query" 
   data="${ssDashboard.beans[componentId].ssSearchFormData}" />
</c:if>
<c:if test="${empty ssDashboard}">
<ssf:searchForm form="${formName}" element="data.query" 
  data="<%= new java.util.HashMap() %>"/>
</c:if>
<br/>
<br>
<input type="submit" class="ss_submit" name="applyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">

</form>