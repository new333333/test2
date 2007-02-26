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
<ssf:ifadapter>
<body onLoad="ss_positionWindow();">
<script type="text/javascript">
var ss_scrollHeightFudge = 60
function ss_positionWindow() {
    var entryHeight = parseInt(self.document.body.scrollHeight) + ss_scrollHeightFudge
    window.innerHeight = entryHeight;
}
</script>
</ssf:ifadapter>

<form method="post">
<table class="ss_style" cellpadding="10" width="100%"><tr><td>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${ssConfigElement}" 
  configJspStyle="${ssConfigJspStyle}"
  entry="${ssEntry}"
  processThisItem="true" />
</td></tr></table>
<br/>
<input type="submit" name="editElementBtn" value="<ssf:nlt tag="button.ok"/>"/>&nbsp;&nbsp;&nbsp;<input
  type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" onClick="window.close();return false;"/>
</form>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>

