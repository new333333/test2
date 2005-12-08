<%@ page import="java.util.Map" %>
<%@ page import="com.sitescape.ef.domain.Event" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<% // these beans need to be here because we need to
   // access them via scriptlets; they need to be 
   // passed into other tags (can't be done via JSTL) %>
<jsp:useBean id="evid" type="String" scope="request" />
<jsp:useBean id="formName" type="String" scope="request" />
<jsp:useBean id="startDate" type="java.util.Date" scope="request" />
<jsp:useBean id="endDate" type="java.util.Date" scope="request" />

<jsp:useBean id="attMap" type="java.util.HashMap" scope="request" />

<% 
   String dateId;
   String dateId2;
   String dateId3;
   String endrangeId;
   dateId = "dp_" + evid;
   dateId2 = "dp2_" + evid;
   dateId3 = "dp3_" + evid;
   endrangeId = "endRange_" + evid;
%>
<script language="Javascript" src="<html:rootPath />js/common/PopupWindow.js"></script>
<script language="Javascript" src="<html:rootPath />js/common/AnchorPosition.js"></script>
<c:set var="prefix" value="${formName}_${evid}" />

<table border="1">
 <tr><td>

 <table border="0" cellpadding="4" cellspacing="0">
 <c:choose>
 <c:when test="${attMap.hasDur}">
 <tr>
   <td class="contentbold">Start:</td>
   <td>
   <ssf:datepicker 
       formName="<%= formName %>"
       initDate="<%= startDate %>"
       id="<%= dateId %>" />
   </td>
</tr>
 <tr>
   <td class="contentbold">&nbsp;</td>
   <td>
   <ssf:timepicker 
       formName="<%= formName %>"
       initDate="<%= startDate %>"
       id="<%= dateId %>" />
   </td>
</tr>
 <tr>
   <td class="contentbold">End:</td>
   <td>
   <ssf:datepicker 
       formName="<%= formName %>"
       initDate="<%= endDate %>"
       id="<%= dateId2 %>" />
   </td>
</tr>
 <tr>
   <td class="contentbold">&nbsp;</td>
   <td>
   <ssf:timepicker 
       formName="<%= formName %>"
       initDate="<%= endDate %>"
       id="<%= dateId2 %>" />
   </td>
</tr>



 </c:when>
 <c:otherwise>

 <tr>
   <td class="contentbold">When:</td>
   <td>
   <ssf:datepicker 
       formName="<%= formName %>"
       initDate="<%= startDate %>"
       id="<%= dateId3 %>" />
   </td>
</tr>
 <tr>
   <td class="contentbold">&nbsp;</td>
   <td>
   <ssf:timepicker 
       formName="<%= formName %>"
       initDate="<%= startDate %>"
       id="<%= dateId3 %>" />
   </td>
</tr>

</c:otherwise>
</c:choose>

<c:if test="${attMap.hasRecur}">
<tr><td colspan="2" align="center">
   <a name="${prefix}_anchor" id="${prefix}_anchor"></a>
   <a href="javascript: ;" onClick="${prefix}_popupRecurrenceWindow();" >
   <table style="border: 1px solid;">
   <tr><td>
   <img border="0" align="middle" src="<html:imagesPath />pics/sym_s_repeat.gif">
   </td>
   <td> Recurrence </td>
   </tr>
   </table>
   </a>
</td></tr>
</c:if>

</table>
</td></tr>
</table>

<c:if test="${attMap.hasRecur}">
<input type="hidden" name="${prefix}_repeatUnit">
<input type="hidden" name="${prefix}_everyN">
<input type="hidden" name="${prefix}_day0">
<input type="hidden" name="${prefix}_day1">
<input type="hidden" name="${prefix}_day2">
<input type="hidden" name="${prefix}_day3">
<input type="hidden" name="${prefix}_day4">
<input type="hidden" name="${prefix}_day5">
<input type="hidden" name="${prefix}_day6">
<input type="hidden" name="${prefix}_onDayCard">
<input type="hidden" name="${prefix}_dow">
</c:if>

<script language="Javascript">

function ${prefix}_onsub() {
  <c:if test="${attMap.hasDur}">
  var ms;
  ms1 = getTimeMilliseconds('<%= formName %>', '<%= dateId %>');
  ms2 = getTimeMilliseconds('<%= formName %>', '<%= dateId2 %>');
  diff = ms2 - ms1;
  if (diff < 0) {
    alert("End time must be later than start time.");
    return(false);
  } else {
    return(true);
  }
  </c:if>
  return(true);
}

createOnSubmitObj('${prefix}onsub', 
     '${formName}', ${prefix}_onsub);
     
var ${prefix}_popupContents = "";
${prefix}_popupContents += '<form method="post" submit="" name="recurPopupForm">\n';

${prefix}_popupContents += '<table border="0" cellpadding="4" cellspacing="0">\n';
${prefix}_popupContents += ' <tr>\n';
${prefix}_popupContents += '  <td colspan="3" class="contentbold">\n';

${prefix}_popupContents += '  &nbsp;Frequency\n';
${prefix}_popupContents += ' </td>\n';
${prefix}_popupContents += ' </tr>\n';
${prefix}_popupContents += ' <tr>\n';
${prefix}_popupContents += '  <td colspan="2" class="content"><input type="radio"  \n';
${prefix}_popupContents += '   name="repeatUnit" value="none" id="norepeat"\n';
${prefix}_popupContents += '   checked="checked"><label for="norepeat">No repeat</label></td>\n';
${prefix}_popupContents += ' </tr>\n';
${prefix}_popupContents += ' <tr>\n';
${prefix}_popupContents += '  <td nowrap="nowrap" class="content">\n';

${prefix}_popupContents += '   <input type="radio" name="repeatUnit" id="repeatday"\n';
${prefix}_popupContents += '   value="day"  > \n';
${prefix}_popupContents += '   Every <input type="text" name="everyNday" size="2" \n';
${prefix}_popupContents += '   class="content" value="1"> day(s)</td>\n';
${prefix}_popupContents += ' </tr>\n';
${prefix}_popupContents += ' <tr>\n';
${prefix}_popupContents += '  <td class="content" valign="top" nowrap="nowrap">\n';
${prefix}_popupContents += '   <input type="radio" name="repeatUnit" id="repeatweek"\n';
${prefix}_popupContents += '   value="week" >\n';
${prefix}_popupContents += '   Every <input type="text" name="everyNweek" size="2" \n';
${prefix}_popupContents += '   class="content" value="1" > week(s) on \n';

${prefix}_popupContents += '<input type="checkbox" name="day0" id="day0" value="">\n';
${prefix}_popupContents += '<input type="checkbox" name="day1" id="day1" value="">\n';
${prefix}_popupContents += '<input type="checkbox" name="day2" id="day2" value="">\n';
${prefix}_popupContents += '<input type="checkbox" name="day3" id="day3" value="">\n';
${prefix}_popupContents += '<input type="checkbox" name="day4" id="day4" value="">\n';
${prefix}_popupContents += '<input type="checkbox" name="day5" id="day5" value="">\n';
${prefix}_popupContents += '<input type="checkbox" name="day6" id="day6" value="">\n';
${prefix}_popupContents += ' </tr>\n';
 
${prefix}_popupContents += ' <tr>\n';

${prefix}_popupContents += '  <td class="content" valign="top" nowrap="nowrap"><input \n';
${prefix}_popupContents += '   type="radio" name="repeatUnit" id="repeatmonth"\n';
${prefix}_popupContents += '   value="month" " >\n';
${prefix}_popupContents += '   Every <input type="text" class="content" size="2"\n';
${prefix}_popupContents += '   name="everyNmonth" value="1" > month(s) on the\n';
${prefix}_popupContents += '<select class="content" name="onDayCardSel" title="select which week in the month on which this calendar entry will occur" name="onDayCardSel" > \n';
${prefix}_popupContents += '<option class="content" value="none">--select one--</option> \n';
${prefix}_popupContents += '<option class="content" value="first" >first</option> \n';
${prefix}_popupContents += '<option class="content" value="second" >second</option> \n';
${prefix}_popupContents += '<option class="content" value="third" >third</option> \n';
${prefix}_popupContents += '<option class="content" value="fourth" >fourth</option> \n';
${prefix}_popupContents += '<option class="content" value="last" >last</option> \n';
${prefix}_popupContents += '</select> \n';
${prefix}_popupContents += '<select class="content" name="dow" title="select the day of the week on which the repeated entry will occur" > \n';
${prefix}_popupContents += '<option class="content" value="none">--select one--</option> \n';
${prefix}_popupContents += '<option class="content" value="Sunday" >Sunday</option> \n';
${prefix}_popupContents += '<option class="content" value="Monday" >Monday</option> \n';
${prefix}_popupContents += '<option class="content" value="Tuesday" >Tuesday</option> \n';
${prefix}_popupContents += '<option class="content" value="Wednesday" >Wednesday</option> \n';
${prefix}_popupContents += '<option class="content" value="Thursday" >Thursday</option> \n';
${prefix}_popupContents += '<option class="content" value="Friday" >Friday</option> \n';
${prefix}_popupContents += '<option class="content" value="Saturday" >Saturday</option> \n';
${prefix}_popupContents += '<option class="content" value="weekday" >weekday</option> \n';
${prefix}_popupContents += '<option class="content" value="weekend day" >weekend day</option> \n';
${prefix}_popupContents += '</select> </td>\n';

${prefix}_popupContents += ' </tr>\n';
${prefix}_popupContents += '</table>	\n';

${prefix}_popupContents += '<br>\n';
${prefix}_popupContents += '<center>\n';
${prefix}_popupContents += '<table border="0" style="border:1px solid;">\n';
${prefix}_popupContents += '<tr><td align="center">\n';
${prefix}_popupContents += '<a href="javascript: ;" onClick="setOpenerHiddenFields(); self.close(); ">\n';
${prefix}_popupContents += 'OK</a></td></tr></table>\n';
${prefix}_popupContents += '</center>\n';

${prefix}_popupContents += "</form>\n";


${prefix}_popupContents += "<scr"
${prefix}_popupContents += "ipt language='Javascript'>\n";

${prefix}_popupContents += "var fieldref = self.document.recurPopupForm;\n";

${prefix}_popupContents += "function getRadioButtonIdx(ptr, type, val) {\n";
${prefix}_popupContents += "    for (i=0; i< ptr.length; i++) {\n";
${prefix}_popupContents += "        if (ptr.elements[type][i].value ==  val) {\n";
${prefix}_popupContents += "            return i;\n";
${prefix}_popupContents += "        }\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "}\n";

${prefix}_popupContents += "var norptidx = getRadioButtonIdx(fieldref, 'repeatUnit', 'none');\n";
${prefix}_popupContents += "var dayrptidx = getRadioButtonIdx(fieldref, 'repeatUnit', 'day');\n";
${prefix}_popupContents += "var weekrptidx = getRadioButtonIdx(fieldref, 'repeatUnit', 'week');\n";
${prefix}_popupContents += "var monthrptidx = getRadioButtonIdx(fieldref, 'repeatUnit', 'month');\n";


// write data back to the opener
${prefix}_popupContents += "function setOpenerHiddenFields() {\n";
${prefix}_popupContents += "  if (fieldref.repeatUnit[norptidx].checked) {\n";
${prefix}_popupContents += "    self.opener.setHiddenField('${formName}', '${evid}', 'repeatUnit', 'none');\n";
${prefix}_popupContents += "  }\n";
${prefix}_popupContents += "  if (fieldref.repeatUnit[dayrptidx].checked) {\n";
${prefix}_popupContents += "    self.opener.setHiddenField('${formName}', '${evid}', 'repeatUnit', 'day');\n";
${prefix}_popupContents += "    self.opener.setHiddenField('${formName}', '${evid}', 'everyN', fieldref.everyNday.value);\n";
${prefix}_popupContents += "  }\n";
${prefix}_popupContents += "  if (fieldref.repeatUnit[weekrptidx].checked) {\n";
${prefix}_popupContents += "    self.opener.setHiddenField('${formName}', '${evid}', 'repeatUnit', 'week');\n";
${prefix}_popupContents += "    self.opener.setHiddenField('${formName}', '${evid}', 'everyN', fieldref.everyNweek.value);\n";

${prefix}_popupContents += "    if (fieldref.day0.checked) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}','${evid}', 'day0', 'yes')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.day1.checked) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}','${evid}', 'day1', 'yes')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.day2.checked) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}','${evid}', 'day2', 'yes')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.day3.checked) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}','${evid}', 'day3', 'yes')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.day4.checked) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}','${evid}', 'day4', 'yes')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.day5.chhecked) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}','${evid}', 'day5', 'yes')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.day6.chhecked) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}','${evid}', 'day6', 'yes')\n";
${prefix}_popupContents += "    }\n";

${prefix}_popupContents += "  }\n";
${prefix}_popupContents += "  if (fieldref.repeatUnit[monthrptidx].checked) {\n";
${prefix}_popupContents += "    self.opener.setHiddenField('${formName}', '${evid}', 'repeatUnit', 'month');\n";
${prefix}_popupContents += "    self.opener.setHiddenField('${formName}', '${evid}', 'everyN', fieldref.everyNmonth.value);\n";
${prefix}_popupContents += "    if (fieldref.onDayCardSel.options[0].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'onDayCard', 'none')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.onDayCardSel.options[1].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'onDayCard', 'first')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.onDayCardSel.options[2].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'onDayCard', 'second')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.onDayCardSel.options[3].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'onDayCard', 'third')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "     if (fieldref.onDayCardSel.options[4].selected) {\n";
${prefix}_popupContents += "     self.opener.setHiddenField('${formName}', '${evid}', 'onDayCard', 'fourth')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.onDayCardSel.options[5].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'onDayCard', 'last')\n";
${prefix}_popupContents += "    }\n";

${prefix}_popupContents += "    if (fieldref.dow.options[0].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'none')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.dow.options[1].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Sunday')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.dow.options[2].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Monday')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.dow.options[3].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Tuesday')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.dow.options[4].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Wednesday')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.dow.options[5].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Thursday')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.dow.options[6].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Friday')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.dow.options[7].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Saturday')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.dow.options[8].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'weekday')\n";
${prefix}_popupContents += "    }\n";
${prefix}_popupContents += "    if (fieldref.dow.options[9].selected) {\n";
${prefix}_popupContents += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'weekendday')\n";
${prefix}_popupContents += "    }\n";


${prefix}_popupContents += "  }\n";
${prefix}_popupContents += "}   \n";

${prefix}_popupContents += "</sc";
${prefix}_popupContents += "ript>\n";

// pop up the recurrence stuff
function ${prefix}_popupRecurrenceWindow() {;
   var win = new PopupWindow();
   win.setSize(500,220);
   win.autoHide();
   // should be conditional on IE
   win.offsetY = 25;
   win.populate(${prefix}_popupContents);
   win.showPopup('${prefix}_anchor');
}

function setHiddenField(formName, id, fn, val) {
   eval('self.document.' + formName + '.' + formName + '_' + id + '_' + fn + '.value = val');
}
</script>










