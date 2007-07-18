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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<script type="text/javascript" src="<html:rootPath/>js/common/common.js"></script>
<div class="ss_portlet_style ss_portlet">
<div class="ss_style" style="padding:4px;" align="center">
<img src="<html:imagesPath/>pics/getting_started.gif">
<p>
  <a href="javascript:;" 
       onClick="ss_helpSystem.showInlineHelpSpotInfo(this, 'print_manuals', '', 200, 100, 'center', 'middle');">
     <span class="ss_getting_started"><ssf:nlt tag="help.viewBooks.title"/></span>
   </a>
</p>

</div>
</div>

