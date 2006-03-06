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
<form class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<portlet:actionURL>
		 <portlet:param name="action" value="import_definition"/>
		 </portlet:actionURL>" name="<portlet:namespace />fm">
<div class="ss_style ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="administration.importDefinitions" text="Select files:"/></span>
<br>

<div class="ss_divider"></div>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.selectFiles" text="Add definition files:"/></span>
<br>
<table class="ss_style" border="0" cellpadding="5" cellspacing="0" width="95%">
<tr><td>
<input type="file" class="ss_text" name="definition1" ><br>
<input type="file" class="ss_text" name="definition2" ><br>
<input type="file" class="ss_text" name="definition3" ><br>
<input type="file" class="ss_text" name="definition4" ><br>
<input type="file" class="ss_text" name="definition5" ><br>
</td></tr></table>
<div class="ss_divider"></div>

<br/>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</div>
</div>
</form>
