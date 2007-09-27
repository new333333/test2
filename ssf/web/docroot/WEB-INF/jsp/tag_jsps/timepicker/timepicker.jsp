<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<%@ page import="java.util.Map" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<jsp:useBean id="tpid" type="String" scope="request" />
<jsp:useBean id="formName" type="String" scope="request" />
<jsp:useBean id="sequenceNumber" type="String" scope="request" />
<jsp:useBean id="hour" type="Integer" scope="request" />
<jsp:useBean id="minute" type="Integer" scope="request" />

<c:set var="prefix" value="${tpid}_${sequenceNumber}" />

<script type="text/javascript" src="<html:rootPath />js/common/PopupWindow.js"></script>
<script type="text/javascript" src="<html:rootPath />js/common/AnchorPosition.js"></script>
<script type="text/javascript">

var ${prefix}_popupContents  = "";

${prefix}_popupContents += "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n";
${prefix}_popupContents += "<ht"+"ml><head>";
${prefix}_popupContents += "<link rel=\"stylesheet\" href=\"<html:rootPath />css/forum.css\" type=\"text/css\"> \n";
${prefix}_popupContents += "<title>Select a time</title>  \n";
${prefix}_popupContents += "</head><body>";

${prefix}_popupContents += "<table border=\"0\" class=\"ss_style\" style=\"font-size:smaller\"><tr><td>\n";
${prefix}_popupContents += " <tr><td>Select hour:</td><td>minutes:</td></tr><td>\n";

${prefix}_popupContents += "<table border=\"0\" class=\"ss_style\" cellpadding=\"4\" cellspacing=\"0\"  style=\"border: 1px solid #666666;\">\n";
${prefix}_popupContents += " <tr>\n";

<c:forEach var="i" begin="8" end="11" step="1">
${prefix}_popupContents += "   <td><a  \n";
${prefix}_popupContents += "         href=\"javascript: ;\" \n";
${prefix}_popupContents += "         onclick=\"sethr('<fmt:formatNumber value="${i}" minIntegerDigits="2" />');\">${i}AM</a>&nbsp;</td>\n";
${prefix}_popupContents += "\n";
</c:forEach>

${prefix}_popupContents += "   <td><a  \n";
${prefix}_popupContents += "         href=\"javascript: ;\" \n";
${prefix}_popupContents += "         onclick=\"sethr('12');\">12PM</a>&nbsp;</td>\n";
${prefix}_popupContents += "\n";

${prefix}_popupContents += "   <td><a  \n";
${prefix}_popupContents += "         href=\"javascript: ;\" \n";
${prefix}_popupContents += "         onclick=\"sethr('13');\">1PM</a>&nbsp;</td>\n";
${prefix}_popupContents += "\n";
${prefix}_popupContents += " </tr>\n";
${prefix}_popupContents += " <tr> \n";

<c:forEach var="i" begin="2" end="7" step="1">
${prefix}_popupContents += "  <td><a  \n";
${prefix}_popupContents += "         href=\"javascript: ;\" \n";
${prefix}_popupContents += "         onclick=\"sethr('<fmt:formatNumber value="${i+12}" minIntegerDigits="2" />');\">${i}PM</a>&nbsp;</td>\n";
${prefix}_popupContents += "\n";
</c:forEach>

${prefix}_popupContents += " </tr>\n";
${prefix}_popupContents += "</table>\n";

${prefix}_popupContents += " </td><td>\n";

${prefix}_popupContents += "<table class=\"ss_style\" border=\"0\" cellpadding=\"4\" cellspacing=\"0\" style=\"border: 1px solid #666666;\">\n";
${prefix}_popupContents += " <tr>\n";
${prefix}_popupContents += "  <td><a  \n";
${prefix}_popupContents += "      href=\"javascript: ;\" onclick=\"setmin('00');\">:00</a>&nbsp;</td>\n";
${prefix}_popupContents += "  <td><a  \n";
${prefix}_popupContents += "      href=\"javascript: ;\" onclick=\"setmin('15');\">:15</a>&nbsp;</td>\n";
${prefix}_popupContents += " </tr>\n";
${prefix}_popupContents += " <tr>\n";
${prefix}_popupContents += "  <td><a  \n";
${prefix}_popupContents += "      href=\"javascript: ;\" onclick=\"setmin('30');\">:30</a>&nbsp;</td>\n";
${prefix}_popupContents += "  <td><a  \n";
${prefix}_popupContents += "      href=\"javascript: ;\" onclick=\"setmin('45');\">:45</a>&nbsp;</td>\n";
${prefix}_popupContents += " </tr>\n";
${prefix}_popupContents += "</table>\n";

${prefix}_popupContents += "</td></tr></table>\n";
${prefix}_popupContents += "<center><a href=\"javascript: ;\" onClick=\"self.close()\">Close</a></center>\n";


// must break end script tag to fool the html process, which doesn't understand quotes
${prefix}_popupContents += "<scr" + "ipt language=\"Javascript\">\n";

${prefix}_popupContents += "function sethr(str) {\n";
${prefix}_popupContents += "    self.opener.sethrOpener(str, '${tpid}', '${formName}', '${sequenceNumber}');\n";
${prefix}_popupContents += "}\n";

${prefix}_popupContents += "function setmin(str) {\n";
${prefix}_popupContents += "    self.opener.setminOpener(str, '${tpid}', '${formName}', '${sequenceNumber}');\n";
${prefix}_popupContents += "}\n";


${prefix}_popupContents += "</scr" + "ipt>\n";


${prefix}_popupContents += "</body></html>";

// pop up the time picker 
function ${prefix}_popupTimepicker() {
   var win = new PopupWindow();
   win.setSize(400,125);
   win.autoHide();
   // should be conditional on IE
   win.offsetY = 25;
   win.populate(${prefix}_popupContents);
   win.showPopup('${prefix}_anchor');
}

function sethrOpener(hr, id, formName, sequenceNumber) {
    var hourName = id + "_" + sequenceNumber + "_hour";
    var minuteName = id + "_" + sequenceNumber + "_minute";
    var valref;
    var timeIndex;

    valref = eval('self.document.' + formName + '.' + hourName);

    // switch the minute from '--' to '00' 
    if (valref.options[0].selected == true) {
        setminOpener('00', id, formName, sequenceNumber);
    }
   
    if (hr.substring(0,1) == '0') {
        hr = hr.substring(1,2);
    }
    timeIndex = eval(parseInt(hr) + 1);
    valref.options[timeIndex].selected = true;
}

function setminOpener(min, id, formName, sequenceNumber) {
    var minuteName = id + "_" + sequenceNumber + "_minute";
    var valref;
    var timeIndex;

    valref = eval('self.document.' + formName + '.' + minuteName);

    if (min.substring(0,1) == '0') {
        min = min.substring(1,2);
    }
    min2 = eval(parseInt(min));
    timeIndex = (min2/15)*3 + 1;
    
    valref.options[timeIndex].selected = true;
}

// this is used elsewhere, to compute the duration of events
function getTimeMilliseconds(formName, id) {
  var dt = new Date();
  dt.setTime(0);
  dt.setDate(1);  // setTime(0) might be Dec 31 in your timezone.  Make it Dec 1 so setMonth doesn't roll.
  var datePrefix = id + "_"; 
  var timePrefix = id + "_" + "0_";
  var yr;
  eval("yr = self.document." + formName + "." + datePrefix + "year.value");
  // year blank means no year selected
  if (yr == "") {
    return(0);
  }
  dt.setYear(yr);
  var month;
  eval("month = self.document." + formName + "." + datePrefix + "month.selectedIndex");
  month = month - 1;
  dt.setMonth(month);
  var date;
  eval("date = self.document." + formName + "." + datePrefix + "date.selectedIndex");
  dt.setDate(date);
  var hour; 
  eval("hour = self.document." + formName + "." + timePrefix + "hour.selectedIndex");
  dt.setHours(hour);
  var min;
  eval("min = self.document." + formName + "." + timePrefix + "minute.selectedIndex");
  min *= 5;
  dt.setMinutes(min);
  dt.setSeconds(0);
  return(dt.getTime());
}

</script>

<table class="ss_style" border="0" cellpadding="0" style="border-spacing: 1px;">
 <tr>
  <td class="content" align="center">
<select name="${prefix}_hour" size="1" id="${prefix}_hour"
     class="content" >
<option class="content" value="99" <c:if test="${hour==99}"> selected </c:if> > -- </option>
 <option class="content" value="0" <c:if test="${hour==0}"> selected </c:if> >midnight</option> 

<c:forEach var="i" begin="1" end="11" step="1">
   <option class="content" value="${i}"<c:if test="${hour==i}"> selected</c:if>> ${i}AM</option> 
</c:forEach>   

 <option class="content" value="12" <c:if test="${hour==12}"> selected </c:if> >12PM</option> 

<c:forEach var="i" begin="1" end="11" step="1">
   <option class="content" value="${i+12}"<c:if test="${hour==i+12}"> selected</c:if>> ${i}PM</option> 
</c:forEach>  
 
 </select></td><td class="contentbold">&nbsp;:&nbsp;</td>
 <td class="content">
  <select name="${prefix}_minute" id="${prefix}_minute" size="1" 
  class="content" >
<option class="content"  value="99" > -- </option> 
<c:forEach var="i" begin="0" end="11" step="1">
   <option class-"content" value="<fmt:formatNumber value="${i*5}" minIntegerDigits="2" />"<c:if test="${minute==i*5}"> selected</c:if>><fmt:formatNumber value="${i*5}" minIntegerDigits="2" /></option> 
</c:forEach>
  </select>&nbsp;
  <a name="${prefix}_anchor" id="${prefix}_anchor"></a>
  <a href="javascript: ;" onClick="${prefix}_popupTimepicker();" >
   <img border="0" align="middle" <ssf:nlt tag="alt.selectTime"/> src="<html:imagesPath />pics/sym_s_clock.gif"> </a>
  </td>
   </tr>
   </table>



