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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>
<div class="ss_style ss_portlet">
<div class="ss_form" style="margin:6px;">
<div style="margin:6px;">

<c:if test="${ssOperation == 'add'}">
<c:if test="${cfgType == '-2'}">
<form class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<ssf:url adapter="true" 
			portletName="ss_administration" 
			action="configure_configuration" 
			actionUrl="true" ><ssf:param name="operation" value="add"/></ssf:url>">
<div class="ss_style ss_portlet">
<span class="ss_titlebold"><ssf:nlt tag="administration.configure_cfg.import" /></span>
<br>
<input type="hidden" name="cfgType" value="-2"/>
<div class="ss_divider"></div>
<br>
<span class="ss_bold"><ssf:nlt tag="administration.selectFiles"/></span>
<br>
<table class="ss_style" border="0" cellpadding="5" cellspacing="0" width="95%">
<tr><td>
<input type="file" size="80" class="ss_text" name="template1" ><br>
<input type="file" size="80" class="ss_text" name="template2" ><br>
<input type="file" size="80" class="ss_text" name="template3" ><br>
<input type="file" size="80" class="ss_text" name="template4" ><br>
<input type="file" size="80" class="ss_text" name="template5" ><br>
</td></tr></table>
<div class="ss_divider"></div>

<br/>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
  onClick="window.close();return false;" >
</div>
</div>
</form>

</c:if>
</c:if>
</div>
</div>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

