<%
// The dashboard "search" component
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
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
<br/>
<br/>
<table>
<tr>
<td><span><ssf:nlt tag="dashboard.search.resultsCount"/></span></td>
<td style="padding-left:10px;"><input type="text" name="data_resultsCount" size="5"
  value="${ssDashboard.dashboard.components[ssComponentId].data.resultsCount[0]}"/></td>
</tr>
<tr>
<td><span><ssf:nlt tag="dashboard.search.summardWordCount"/></span></td>
<td style="padding-left:10px;"><input type="text" name="data_summaryWordCount" size="5" 
  value="${ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount[0]}"/></td>
</tr>
</table>
<br/>
<br/>

<ssf:searchForm form="${ss_dashboard_config_form_name}" element="data.query" 
  data="${ssDashboard.beans[ssComponentId].ssSearchFormData}" />

<br/>