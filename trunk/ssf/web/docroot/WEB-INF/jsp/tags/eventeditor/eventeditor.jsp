<%@ page import="java.util.Map" %>
<%@ page import="com.sitescape.ef.domain.Event" %>
<%@ include file="/html/common/init.jsp" %>
<jsp:useBean id="evid" type="String" scope="request" />
<jsp:useBean id="formName" type="String" scope="request" />
<jsp:useBean id="recurIcon" type="String" scope="request" />
<jsp:useBean id="startDate" type="java.util.Date" scope="request" />
<jsp:useBean id="endDate" type="java.util.Date" scope="request" />

<c:set var="hasDuration" value="${attMap[hasDuration]}" />

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
<script language="Javascript" src="<%= contextPath %>/html/js/common/PopupWindow.js"></script>
<script language="Javascript" src="<%= contextPath %>/html/js/common/AnchorPosition.js"></script>
<c:set var="prefix" value="${formName}_${evid}" />


<table border="1">
 <tr><td>

 <table border="0" cellpadding="4" cellspacing="0">
 <c:choose>
 <c:when test="${hasDuration}">
 <tr>
   <td class="contentbold">Start:</td>
   <td>
   <sitescape:datepicker 
       formName="<%= formName %>"
       initDate="<%= startDate %>"
       id="<%= dateId %>" />
   </td>
</tr>
 <tr>
   <td class="contentbold">&nbsp;</td>
   <td>
   <sitescape:timepicker 
       formName="<%= formName %>"
       initDate="<%= startDate %>"
       id="<%= dateId %>" />
   </td>
</tr>
 <tr>
   <td class="contentbold">End:</td>
   <td>
   <sitescape:datepicker 
       formName="<%= formName %>"
       initDate="<%= endDate %>"
       id="<%= dateId2 %>" />
   </td>
</tr>
 <tr>
   <td class="contentbold">&nbsp;</td>
   <td>
   <sitescape:timepicker 
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
   <sitescape:datepicker 
       formName="<%= formName %>"
       initDate="<%= startDate %>"
       id="<%= dateId3 %>" />
   </td>
</tr>
 <tr>
   <td class="contentbold">&nbsp;</td>
   <td>
   <sitescape:timepicker 
       formName="<%= formName %>"
       initDate="<%= startDate %>"
       id="<%= dateId3 %>" />
   </td>
</tr>

</c:otherwise>
</c:choose>

<c:if test="${hasRecurrence}">
<tr><td colspan="2" align="center">
   <a name="<c:out value="${prefix}" />_anchor" id="<c:out value="${prefix}" />_anchor"></a>
   <a href="javascript: ;" onClick="<c:out value="${prefix}" />_popupRecurrenceWindow();" >
   <table style="border: 1px solid;">
   <tr><td>
   <img border="0" align="middle" src="<c:out value="${recurIcon}" />">
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

<c:if test="${hasRecurrence}">
<input type="hidden" name="<c:out value="${prefix}" />_repeatUnit">
<input type="hidden" name="<c:out value="${prefix}" />_everyN">
<input type="hidden" name="<c:out value="${prefix}" />_day0">
<input type="hidden" name="<c:out value="${prefix}" />_day1">
<input type="hidden" name="<c:out value="${prefix}" />_day2">
<input type="hidden" name="<c:out value="${prefix}" />_day3">
<input type="hidden" name="<c:out value="${prefix}" />_day4">
<input type="hidden" name="<c:out value="${prefix}" />_day5">
<input type="hidden" name="<c:out value="${prefix}" />_day6">
<input type="hidden" name="<c:out value="${prefix}" />_onDayCard">
<input type="hidden" name="<c:out value="${prefix}" />_dow">
</c:if>

<script language="Javascript">

function <c:out value="${prefix}" />_onsub() {
  <c:if test="${hasDuration}">
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

createOnSubmitObj('<c:out value="${prefix}" />onsub', 
     '<c:out value="${formName}" />', <c:out value="${prefix}" />_onsub);
     
var <c:out value="${prefix}" />_popupContents = "";
<c:out value="${prefix}" />_popupContents += '<form method="post" submit="" name="recurPopupForm">\n';

<c:out value="${prefix}" />_popupContents += '<table border="0" cellpadding="4" cellspacing="0">\n';
<c:out value="${prefix}" />_popupContents += ' <tr>\n';
<c:out value="${prefix}" />_popupContents += '  <td colspan="3" class="contentbold">\n';

<c:out value="${prefix}" />_popupContents += '  &nbsp;Frequency\n';
<c:out value="${prefix}" />_popupContents += ' </td>\n';
<c:out value="${prefix}" />_popupContents += ' </tr>\n';
<c:out value="${prefix}" />_popupContents += ' <tr>\n';
<c:out value="${prefix}" />_popupContents += '  <td colspan="2" class="content"><input type="radio"  \n';
<c:out value="${prefix}" />_popupContents += '   name="repeatUnit" value="none" id="norepeat"\n';
<c:out value="${prefix}" />_popupContents += '   checked="checked"><label for="norepeat">No repeat</label></td>\n';
<c:out value="${prefix}" />_popupContents += ' </tr>\n';
<c:out value="${prefix}" />_popupContents += ' <tr>\n';
<c:out value="${prefix}" />_popupContents += '  <td nowrap="nowrap" class="content">\n';

<c:out value="${prefix}" />_popupContents += '   <input type="radio" name="repeatUnit" id="repeatday"\n';
<c:out value="${prefix}" />_popupContents += '   value="day"  > \n';
<c:out value="${prefix}" />_popupContents += '   Every <input type="text" name="everyNday" size="2" \n';
<c:out value="${prefix}" />_popupContents += '   class="content" value="1"> day(s)</td>\n';
<c:out value="${prefix}" />_popupContents += ' </tr>\n';
<c:out value="${prefix}" />_popupContents += ' <tr>\n';
<c:out value="${prefix}" />_popupContents += '  <td class="content" valign="top" nowrap="nowrap">\n';
<c:out value="${prefix}" />_popupContents += '   <input type="radio" name="repeatUnit" id="repeatweek"\n';
<c:out value="${prefix}" />_popupContents += '   value="week" >\n';
<c:out value="${prefix}" />_popupContents += '   Every <input type="text" name="everyNweek" size="2" \n';
<c:out value="${prefix}" />_popupContents += '   class="content" value="1" > week(s) on \n';

<c:out value="${prefix}" />_popupContents += '<input type="checkbox" name="day0" id="day0" value="">\n';
<c:out value="${prefix}" />_popupContents += '<input type="checkbox" name="day1" id="day1" value="">\n';
<c:out value="${prefix}" />_popupContents += '<input type="checkbox" name="day2" id="day2" value="">\n';
<c:out value="${prefix}" />_popupContents += '<input type="checkbox" name="day3" id="day3" value="">\n';
<c:out value="${prefix}" />_popupContents += '<input type="checkbox" name="day4" id="day4" value="">\n';
<c:out value="${prefix}" />_popupContents += '<input type="checkbox" name="day5" id="day5" value="">\n';
<c:out value="${prefix}" />_popupContents += '<input type="checkbox" name="day6" id="day6" value="">\n';
<c:out value="${prefix}" />_popupContents += ' </tr>\n';
 
<c:out value="${prefix}" />_popupContents += ' <tr>\n';

<c:out value="${prefix}" />_popupContents += '  <td class="content" valign="top" nowrap="nowrap"><input \n';
<c:out value="${prefix}" />_popupContents += '   type="radio" name="repeatUnit" id="repeatmonth"\n';
<c:out value="${prefix}" />_popupContents += '   value="month" " >\n';
<c:out value="${prefix}" />_popupContents += '   Every <input type="text" class="content" size="2"\n';
<c:out value="${prefix}" />_popupContents += '   name="everyNmonth" value="1" > month(s) on the\n';
<c:out value="${prefix}" />_popupContents += '<select class="content" name="onDayCardSel" title="select which week in the month on which this calendar entry will occur" name="onDayCardSel" > \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="none">--select one--</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="first" >first</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="second" >second</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="third" >third</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="fourth" >fourth</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="last" >last</option> \n';
<c:out value="${prefix}" />_popupContents += '</select> \n';
<c:out value="${prefix}" />_popupContents += '<select class="content" name="dow" title="select the day of the week on which the repeated entry will occur" > \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="none">--select one--</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="Sunday" >Sunday</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="Monday" >Monday</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="Tuesday" >Tuesday</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="Wednesday" >Wednesday</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="Thursday" >Thursday</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="Friday" >Friday</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="Saturday" >Saturday</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="weekday" >weekday</option> \n';
<c:out value="${prefix}" />_popupContents += '<option class="content" value="weekend day" >weekend day</option> \n';
<c:out value="${prefix}" />_popupContents += '</select> </td>\n';

<c:out value="${prefix}" />_popupContents += ' </tr>\n';
<c:out value="${prefix}" />_popupContents += '</table>	\n';

<c:out value="${prefix}" />_popupContents += '<br>\n';
<c:out value="${prefix}" />_popupContents += '<center>\n';
<c:out value="${prefix}" />_popupContents += '<table border="0" style="border:1px solid;">\n';
<c:out value="${prefix}" />_popupContents += '<tr><td align="center">\n';
<c:out value="${prefix}" />_popupContents += '<a href="javascript: ;" onClick="setOpenerHiddenFields(); self.close(); ">\n';
<c:out value="${prefix}" />_popupContents += 'OK</a></td></tr></table>\n';
<c:out value="${prefix}" />_popupContents += '</center>\n';

<c:out value="${prefix}" />_popupContents += "</form>\n";


<c:out value="${prefix}" />_popupContents += "<scr"
<c:out value="${prefix}" />_popupContents += "ipt language='Javascript'>\n";

<c:out value="${prefix}" />_popupContents += "var fieldref = self.document.recurPopupForm;\n";

<c:out value="${prefix}" />_popupContents += "function getRadioButtonIdx(ptr, type, val) {\n";
<c:out value="${prefix}" />_popupContents += "    for (i=0; i< ptr.length; i++) {\n";
<c:out value="${prefix}" />_popupContents += "        if (ptr.elements[type][i].value ==  val) {\n";
<c:out value="${prefix}" />_popupContents += "            return i;\n";
<c:out value="${prefix}" />_popupContents += "        }\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "}\n";

<c:out value="${prefix}" />_popupContents += "var norptidx = getRadioButtonIdx(fieldref, 'repeatUnit', 'none');\n";
<c:out value="${prefix}" />_popupContents += "var dayrptidx = getRadioButtonIdx(fieldref, 'repeatUnit', 'day');\n";
<c:out value="${prefix}" />_popupContents += "var weekrptidx = getRadioButtonIdx(fieldref, 'repeatUnit', 'week');\n";
<c:out value="${prefix}" />_popupContents += "var monthrptidx = getRadioButtonIdx(fieldref, 'repeatUnit', 'month');\n";


// write data back to the opener
<c:out value="${prefix}" />_popupContents += "function setOpenerHiddenFields() {\n";
<c:out value="${prefix}" />_popupContents += "  if (fieldref.repeatUnit[norptidx].checked) {\n";
<c:out value="${prefix}" />_popupContents += "    self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'repeatUnit', 'none');\n";
<c:out value="${prefix}" />_popupContents += "  }\n";
<c:out value="${prefix}" />_popupContents += "  if (fieldref.repeatUnit[dayrptidx].checked) {\n";
<c:out value="${prefix}" />_popupContents += "    self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'repeatUnit', 'day');\n";
<c:out value="${prefix}" />_popupContents += "    self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'everyN', fieldref.everyNday.value);\n";
<c:out value="${prefix}" />_popupContents += "  }\n";
<c:out value="${prefix}" />_popupContents += "  if (fieldref.repeatUnit[weekrptidx].checked) {\n";
<c:out value="${prefix}" />_popupContents += "    self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'repeatUnit', 'week');\n";
<c:out value="${prefix}" />_popupContents += "    self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'everyN', fieldref.everyNweek.value);\n";

<c:out value="${prefix}" />_popupContents += "    if (fieldref.day0.checked) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />','<c:out value="${evid}" />', 'day0', 'yes')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.day1.checked) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />','<c:out value="${evid}" />', 'day1', 'yes')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.day2.checked) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />','<c:out value="${evid}" />', 'day2', 'yes')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.day3.checked) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />','<c:out value="${evid}" />', 'day3', 'yes')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.day4.checked) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />','<c:out value="${evid}" />', 'day4', 'yes')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.day5.chhecked) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />','<c:out value="${evid}" />', 'day5', 'yes')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.day6.chhecked) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />','<c:out value="${evid}" />', 'day6', 'yes')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";

<c:out value="${prefix}" />_popupContents += "  }\n";
<c:out value="${prefix}" />_popupContents += "  if (fieldref.repeatUnit[monthrptidx].checked) {\n";
<c:out value="${prefix}" />_popupContents += "    self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'repeatUnit', 'month');\n";
<c:out value="${prefix}" />_popupContents += "    self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'everyN', fieldref.everyNmonth.value);\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.onDayCardSel.options[0].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'onDayCard', 'none')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.onDayCardSel.options[1].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'onDayCard', 'first')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.onDayCardSel.options[2].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'onDayCard', 'second')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.onDayCardSel.options[3].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'onDayCard', 'third')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "     if (fieldref.onDayCardSel.options[4].selected) {\n";
<c:out value="${prefix}" />_popupContents += "     self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'onDayCard', 'fourth')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.onDayCardSel.options[5].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'onDayCard', 'last')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";

<c:out value="${prefix}" />_popupContents += "    if (fieldref.dow.options[0].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'dow', 'none')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.dow.options[1].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'dow', 'Sunday')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.dow.options[2].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'dow', 'Monday')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.dow.options[3].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'dow', 'Tuesday')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.dow.options[4].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'dow', 'Wednesday')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.dow.options[5].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'dow', 'Thursday')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.dow.options[6].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'dow', 'Friday')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.dow.options[7].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'dow', 'Saturday')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.dow.options[8].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'dow', 'weekday')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";
<c:out value="${prefix}" />_popupContents += "    if (fieldref.dow.options[9].selected) {\n";
<c:out value="${prefix}" />_popupContents += "      self.opener.setHiddenField('<c:out value="${formName}" />', '<c:out value="${evid}" />', 'dow', 'weekendday')\n";
<c:out value="${prefix}" />_popupContents += "    }\n";


<c:out value="${prefix}" />_popupContents += "  }\n";
<c:out value="${prefix}" />_popupContents += "}   \n";

<c:out value="${prefix}" />_popupContents += "</sc";
<c:out value="${prefix}" />_popupContents += "ript>\n";

// pop up the recurrence stuff
function <c:out value="${prefix}" />_popupRecurrenceWindow() {;
   var win = new PopupWindow();
   win.setSize(500,220);
   win.autoHide();
   // should be conditional on IE
   win.offsetY = 25;
   win.populate(<c:out value="${prefix}" />_popupContents);
   win.showPopup('<c:out value="${prefix}" />_anchor');
}

function setHiddenField(formName, id, fn, val) {
   eval('self.document.' + formName + '.' + formName + '_' + id + '_' + fn + '.value = val');
}
</script>










