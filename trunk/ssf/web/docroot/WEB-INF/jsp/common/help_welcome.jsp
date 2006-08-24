<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${empty ssf_help_files_loaded}">
<c:set var="ssf_support_help_loaded" value="1" scope="request"/>

<script type="text/javascript">
var ss_helpWelcomeText = "<ssf:nlt tag="help.welcome"/>";
var ss_helpTocText = "<ssf:nlt tag="help.toc"/>";
var ss_helpPreviousText = "<ssf:nlt tag="general.Previous"/>";
var ss_helpNextText = "<ssf:nlt tag="general.Next"/>";
var ss_helpCloseButtonText = "<ssf:nlt tag="button.close"/>";
var ss_helpInstructions ="<ssf:nlt tag="help.instructions"/>";

ss_helpSystem.outputHelpWelcomeHtml();

var ss_helpSpotGifSrc = "<html:imagesPath/>pics/help_spot.gif";

</script>

</c:if>
