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
<ssf:ifadapter>
<body>
</ssf:ifadapter>
<form class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<ssf:url adapter="true" 
			portletName="ss_administration" 
			action="import_profiles" 
			actionUrl="true" ><ssf:param 
		    name="binderId" value="${ssBinder.id}"/></ssf:url>" >
<div class="ss_style ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="administration.import.profiles" /></span>
<br>

<div class="ss_divider"></div>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.profiles.file"/></span>
<br>
<table class="ss_style" border="0" cellpadding="5" cellspacing="0" width="95%">
<tr><td>
<input type="file" size="80" class="ss_text" name="profiles" ><br>
</td></tr></table>
<div class="ss_divider"></div>

<br/>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />"/>

<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
onClick="window.close();return false;"/>

</div>
</div>
</form>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
