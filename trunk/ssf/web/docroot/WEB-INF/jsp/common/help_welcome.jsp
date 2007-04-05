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
<c:if test="${empty ssf_help_files_loaded}">
<c:set var="ssf_support_help_loaded" value="1" scope="request"/>

<script type="text/javascript">
var ss_helpWelcomeText = "<ssf:nlt tag="help.welcome"/>";
var ss_helpTocText = "<ssf:nlt tag="help.toc"/>";
var ss_helpPreviousText = "<ssf:nlt tag="general.Previous"/>";
var ss_helpNextText = "<ssf:nlt tag="general.Next"/>";
var ss_helpCloseButtonText = "<ssf:nlt tag="help.button.exit.help"/>";
var ss_helpInstructions ="<ssf:nlt tag="help.instructions"/>";
var ss_helpInstructions ="<ssf:nlt tag="help.instructions"/>";
var ss_helpManualsButtonText ="<ssf:nlt tag="help.button.viewBooks"/>"
ss_helpSystem.outputHelpWelcomeHtml();

var ss_helpSpotGifSrc = "<html:imagesPath/>icons/toolbar_help.gif";

</script>

</c:if>
