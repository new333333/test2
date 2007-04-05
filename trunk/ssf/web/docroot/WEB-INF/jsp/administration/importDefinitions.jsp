<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<form class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<portlet:actionURL>
		 <portlet:param name="action" value="import_definition"/>
		 </portlet:actionURL>" name="<portlet:namespace />fm">
<div class="ss_style ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="administration.import.definitions" /></span>
<br>

<div class="ss_divider"></div>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.selectFiles"/></span>
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
