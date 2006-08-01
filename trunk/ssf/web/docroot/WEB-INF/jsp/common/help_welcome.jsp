<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:if test="${empty ssf_help_files_loaded}">
<c:set var="ssf_support_help_loaded" value="1" scope="request"/>

<script type="text/javascript">
var undefined;
if (!ss_helpSpotGifSrc || ss_helpSpotGifSrc == undefined || ss_helpSpotGifSrc == "undefined") {
	var ss_helpSpotGifSrc = "<html:imagesPath/>pics/help_spot.gif";
	var s = "";	
	s += "<div id=\"ss_help_welcome\" class=\"ss_style ss_helpWelcome\" \n";
	s += "  positionX=\"center\" positionY=\"top\" align=\"center\">\n";
	s += "  <span class=\"ss_style ss_bold ss_largestprint\"><ssf:nlt tag="help.welcome"/></span>\n";
	s += "  <br>\n";
	s += "  <table width=\"350\">\n";
	s += "  <tr>\n";
	s += "  <td><a href=\"#\" onClick=\"ss_helpSystem.showPreviousHelpSpot();return false;\"\n";
	s += "    >&lt;&lt;&lt; <ssf:nlt tag="general.Previous"/></a></td>\n";
	s += "  <td><a href=\"#\" \n";
	s += "    onClick=\"ss_helpSystem.toggleTOC();return false;\"><ssf:nlt tag="help.toc"/></a></td>\n";
	s += "  <td align=\"right\"><a href=\"#\" onClick=\"ss_helpSystem.showNextHelpSpot();return false;\"\n";
	s += "    ><ssf:nlt tag="general.Next"/> &gt;&gt;&gt;</a></td>\n";
	s += "  </tr>\n";
	s += "  <tr>\n";
	s += "  <td align=\"center\" colspan=\"3\">\n";
	s += "    <a class=\"ss_linkButton ss_smallprint\" href=\"#\" \n";
	s += "      onClick=\"ss_helpSystem.hide(); return false;\"><ssf:nlt tag="button.close"/></a>\n";
	s += "  </td>\n";
	s += "  </tr>\n";
	s += "  </table>\n";
	s += "  <table>\n";
	s += "  <tr>\n";
	s += "  <td>&nbsp;</td>\n";
	s += "  <td align=\"center\"><div id=\"ss_help_toc\" class=\"ss_helpToc\" align=\"left\"></td>\n";
	s += "  <td>&nbsp;</td>\n";
	s += "  </tr>\n";
	s += "  </table>\n";
	s += "  </div>\n";
	s += "</div>\n";
	//alert(s)
	document.writeln(s);
}
</script>

</c:if>
