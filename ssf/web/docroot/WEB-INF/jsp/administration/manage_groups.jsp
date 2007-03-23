<%
/**
 * Copyright (c) 2007 SiteScape, Inc. All rights reserved.
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
<form class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<portlet:actionURL>
		 <portlet:param name="action" value="manage_groups"/>
		 <portlet:param name="binderId" value="${ssBinder.id}"/>
		 </portlet:actionURL>" name="<portlet:namespace />fm">
<div class="ss_style ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="administration.manage.groups" /></span>
<br>

<div class="ss_divider"></div>
<br>
<form class="ss_style ss_form" method="post" 
	action="<portlet:actionURL><portlet:param 
	name="action" value="manage_groups"/></portlet:actionURL>">
		
	<span class="ss_bold"><ssf:nlt tag="administration.add.group"/></span>
	<input type="text" class="ss_text" size="70" name="groupName"><br>
		
	<input type="submit" class="ss_submit" name="addBtn" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>
<br/>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">

<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</div>
</div>
</form>
