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
<c:set var="ss_dashboard_config_form_name" value="<%= renderResponse.getNamespace() + "searchfm" %>"/>

<form method="post" class="ss_style ss_form"  name="${ss_dashboard_config_form_name}" id="${ss_dashboard_config_form_name}"
	action="<portlet:actionURL/>"  onSubmit="return ss_onSubmit(this);">

<input type="hidden" name="componentName" value="gallery"/>
<%@ include file="/WEB-INF/jsp/dashboard/gallery_config.jsp" %>
<br/>
<br>
<input type="submit" class="ss_submit" name="applyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">

</form>