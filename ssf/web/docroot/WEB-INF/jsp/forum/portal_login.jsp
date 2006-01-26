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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page contentType="text/html" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"> 
<html xmlns:svg="http://www.w3.org/2000/svg-20000303-stylable">
<head>

<%@ include file="/WEB-INF/jsp/forum/view_css.jsp" %>

<script type="text/javascript">

var ss_targetUrlLoadCount = 0;
function ss_loadTargetUrl() {
	ss_targetUrlLoadCount++;
	if (ss_targetUrlLoadCount > 1) {
		ss_showHideObj('iframe_window', 'hidden', 'block');
	}
	if (ss_targetUrlLoadCount > 2) {
		self.location.reload(true);
	}
}

var ss_transferUrl = self.location.href;

</script>

</head>
<body>
 <iframe id="iframe_window" name="iframe_window" 
    style="width:100%; height:95%; display:block;"
    src="/c" frameBorder="no" onLoad="ss_loadTargetUrl();">xxx</iframe>

</body>
</html>
