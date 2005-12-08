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


<table border="0" cellpadding="20"><tr><td>
 <table border="0" cellpadding="4" cellspacing="0">
 <c:choose>
 <c:when test="${attMap.hasDur}">
 <tr>
   <td class="contentbold"><ssf:nlt tag="event.start" />:</td>
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
   <td class="contentbold"><ssf:nlt tag="event.end" />:</td>
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
   <td class="contentbold"><ssf:nlt tag="event.when" />:</td>
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
</table>


<script language="Javascript">

var ${prefix}_isRecurVisible=false;

function ${prefix}_toggleRecur(name) {
   if (${prefix}_isRecurVisible) {
     ss_showHideObj(name, 'hidden', 'none');
     ${prefix}_isRecurVisible = false;
     ss_replaceImage('${prefix}_expandgif', '<html:imagesPath />pics/sym_s_expand.gif');
   } else {
     ss_showHideObj(name, 'visible', 'block');
     ${prefix}_isRecurVisible = true;
     ss_replaceImage('${prefix}_expandgif', '<html:imagesPath />pics/sym_s_collapse.gif');
   }
     
}
</script>

<c:if test="${attMap.hasRecur}">
  <div style="text-align:left; ">
     <a href="javascript: ;" onClick="${prefix}_toggleRecur('${prefix}_recur_div')" >
     <img border="0" src="<html:imagesPath />pics/sym_s_expand.gif" name="${prefix}_expandgif" />
     <img border="0" src="<html:imagesPath />pics/sym_s_repeat.gif" /> 
     <b><ssf:nlt tag="event.recurrence" /></b></a><br>
  </div>
   <div name="${prefix}_recur_div" id="${prefix}_recur_div" style="visibility:hidden; display:none;">
     <table border="0" cellpadding="4" cellspacing="0">

     <tr>
     <td colspan="3" class="contentbold">

     &nbsp;<ssf:nlt tag="event.frequency" />
    </td>
    </tr>
    <tr>
     <td colspan="2" class="content"><input type="radio"  
      name="repeatUnit" value="none" id="norepeat"
      checked="checked"
   ><label for="norepeat"><ssf:nlt tag="event.no_repeat" /></label></td>
    </tr>
    <tr>
     <td nowrap="nowrap" class="content">

      <input type="radio" name="repeatUnit" id="repeatday"
      checked="checked"
      value="day"  > 
      <ssf:nlt tag="event.every" /> <input type="text" name="everyNday" size="2" 
      class="content" value=""
   "> <ssf:nlt tag="event.days" /></td>
    </tr>

    <tr>
     <td class="content" valign="top" nowrap="nowrap">
      <input type="radio" name="repeatUnit" id="repeatweek"
      checked="checked"
      value="week" >
      <ssf:nlt tag="event.every" /> <input type="text" name="everyNweek" size="2" 
      class="content" value=" " > <ssf:nlt tag="event.weeks" /> <ssf:nlt tag="event.occurson" /> 

   <input type="checkbox" name="day0" id="day0
   checked="checked"
   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.su" /></font>
   <input type="checkbox" name="day1" id="day1"
    checked="checked"
   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.mo" /></font>
  <input type="checkbox" name="day2" id="day2"
   checked="checked"
   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.tu" /></font>
   <input type="checkbox" name="day3" id="day3"
   checked="checked"

   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.we" /></font>
   <input type="checkbox" name="day4" id="day4"
   checked="checked"

   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.th" /></font>
   <input type="checkbox" name="day5" id="day5"
   checked="checked"
   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.fr" /></font>
   <input type="checkbox" name="day6" id="day6"
   checked="checked" 
   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.sa" /></font>
   </tr>

    <tr>
     <td class="content" valign="top" nowrap="nowrap"><input 
      type="radio" name="repeatUnit" id="repeatmonth"
      checked="checked"
      value="month" " >
      <ssf:nlt tag="event.every" /> <input type="text" class="content" size="2"
      name="everyNmonth" value=""
   " > month(s) on the
   <select class="content" name="onDayCardSel" title="select which week in the month on which this calendar entry will occur" name="onDayCardSel" > 
   <option class="content" value="none"
   selected="selected"
   ><ssf:nlt tag="general.please_select" /></option> 
   <option class="content" value="first"
   selected="selecte"
   ><ssf:nlt tag="event.whichweek.first" /></option> 
   <option class="content" value="second"
   selected="selected"

   ><ssf:nlt tag="event.whichweek.second" /></option> 
   <option class="content" value="third"
   selected="selected"
   ><ssf:nlt tag="event.whichweek.third" /></option> 
   <option class="content" value="fourth"
   selected="selected"
   ><ssf:nlt tag="event.whichweek.fourth" /></option> 
   <option class="content" value="last"
   selected="selected"
   ><ssf:nlt tag="event.whichweek.last" /></option> 
   </select> 
   <select class="content" name="dow" title="select the day of the week on which the repeated entry will occur" > 
   <option class="content" value="none"
   selected="selecte"
   ><ssf:nlt tag="general.please_select" /></option> 
   <option class="content" value="Sunday"
   selected="selected"
   ><ssf:nlt tag="calendar.day.names.su" /></option> 
   <option class="content" value="Monday"
   selected="selected"
   ><ssf:nlt tag="calendar.day.names.mo" /></option> 
   <option class="content" value="Tuesday"
   selected="selected"
   ><ssf:nlt tag="calendar.day.names.tu" /></option> 
   <option class="content" value="Wednesday"
   selected="selected"
   ><ssf:nlt tag="calendar.day.names.we" /></option> 
   <option class="content" value="Thursday"
   selected="selected"
   ><ssf:nlt tag="calendar.day.names.th" /></option> 
   <option class="content" value="Friday"
   selected="selected"
   ><ssf:nlt tag="calendar.day.names.fr" /></option> 
   <option class="content" value="Saturday"
   selected="selected"
   ><ssf:nlt tag="calendar.day.names.sa" /></option> 
   <option class="content" value="weekday"
   selected="selected"
   ><ssf:nlt tag="calendar.day.names.weekday" /></option> 
   <option class="content" value="weekendday"
   selected="selected"
   ><ssf:nlt tag="calendar.day.names.weekendday" /></option> 
   </select> </td>

    </tr>
    </table>

   </div>
</c:if>

</td></tr></table>

<% // recurrence stuff; emit and initialize various hidden fields from the initEvent %>
<c:if test="${attMap.hasRecur}">

<c:choose>
<c:when test="${empty initEvent.frequencyString}">
<c:set var="freqval" value="none" />
</c:when>
<c:when test="${initEvent.frequencyString == 'DAILY'}">
<c:set var="freqval" value="day" />
</c:when>
<c:when test="${initEvent.frequencyString == 'WEEKLY'}">
<c:set var="freqval" value="week" />
</c:when>
<c:when test="${initEvent.frequencyString == 'MONTHLY'}">
<c:set var="freqval" value="month" />
</c:when>
<c:otherwise>
<c:set var="freqval" value="" />
</c:otherwise>
</c:choose>
<input type="hidden" name="${prefix}_repeatUnit" value="${freqval}" >

<c:choose>
<c:when test="${empty initEvent.interval}">
<input type="hidden" name="${prefix}_everyN" value="1">
</c:when>
<c:otherwise>
<input type="hidden" name="${prefix}_everyN" value="${initEvent.interval}">
</c:otherwise>
</c:choose>

<c:set var="day0sel" value="" />
<c:set var="day1sel" value="" />
<c:set var="day2sel" value="" />
<c:set var="day3sel" value="" />
<c:set var="day4sel" value="" />
<c:set var="day5sel" value="" />
<c:set var="day6sel" value="" />
<c:set var="daynum" value="" />
<c:set var="dowstring" value="" />

<c:forEach var="daypos" items="${initEvent.byDay}">
<c:choose>
<c:when test="${daypos.dayOfWeek == 1}">
<c:set var="day0sel" value="yes" />
</c:when>
<c:when test="${daypos.dayOfWeek == 2}">
<c:set var="day1sel" value="yes" />
</c:when>
<c:when test="${daypos.dayOfWeek == 3}">
<c:set var="day2sel" value="yes" />
</c:when>
<c:when test="${daypos.dayOfWeek == 4}">
<c:set var="day3sel" value="yes" />
</c:when>
<c:when test="${daypos.dayOfWeek == 5}">
<c:set var="day4sel" value="yes" />
</c:when>
<c:when test="${daypos.dayOfWeek == 6}">
<c:set var="day5sel" value="yes" />
</c:when>
<c:when test="${daypos.dayOfWeek == 7}">
<c:set var="day6sel" value="yes" />
</c:when>
</c:choose>

<% // we only implement daynum (onDayCard) for months... in that case,
   // there will only be one DayPositiion entry in the array
%>
<c:choose>
<c:when test="${daypos.dayPosition == 0}" >
<c:set var="daystring" value="none" />
</c:when>
<c:when test="${daypos.dayPosition == 1}" >
<c:set var="daystring" value="first" />
</c:when>
<c:when test="${daypos.dayPosition == 2}" >
<c:set var="daystring" value="second" />
</c:when>
<c:when test="${daypos.dayPosition == 3}" >
<c:set var="daystring" value="third" />
</c:when>
<c:when test="${daypos.dayPosition == 4}" >
<c:set var="daystring" value="fourth" />
</c:when>
<c:when test="${daypos.dayPosition == 5}" >
<c:set var="daystring" value="last" />
</c:when>
</c:choose>

<c:set var="dowstring" value="${daypos.dayOfWeekString}" />

</c:forEach>

<input type="hidden" name="${prefix}_day0" value="${day0sel}">
<input type="hidden" name="${prefix}_day1" value="${day1sel}">
<input type="hidden" name="${prefix}_day2" value="${day2sel}">
<input type="hidden" name="${prefix}_day3" value="${day3sel}">
<input type="hidden" name="${prefix}_day4" value="${day4sel}">
<input type="hidden" name="${prefix}_day5" value="${day5sel}">
<input type="hidden" name="${prefix}_day6" value="${day6sel}">
<input type="hidden" name="${prefix}_onDayCard" value="${daystring}">
<input type="hidden" name="${prefix}_dow" value="${dowstring}">

<% // end of recurrence hidden fields %>
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
     
function ${prefix}_generatePopupContents() {
  // pc holds the string being built
  var pc = '';

  pc += '<form method="post" submit="" name="recurPopupForm">\n';

  pc += '<table border="0" cellpadding="4" cellspacing="0">\n';
  pc += ' <tr>\n';
  pc += '  <td colspan="3" class="contentbold">\n';

  pc += '  &nbsp;<ssf:nlt tag="event.frequency" />\n';
  pc += ' </td>\n';
  pc += ' </tr>\n';
  pc += ' <tr>\n';
  var repeatUnitVal = getHiddenField('${formName}', '${evid}', 'repeatUnit');
  pc += '  <td colspan="2" class="content"><input type="radio"  \n';
  pc += '   name="repeatUnit" value="none" id="norepeat"\n';
  if (repeatUnitVal == 'none') {
    pc += '   checked="checked"';
  }
  pc += '><label for="norepeat"><ssf:nlt tag="event.no_repeat" /></label></td>\n';
  pc += ' </tr>\n';
  pc += ' <tr>\n';
  pc += '  <td nowrap="nowrap" class="content">\n';

  pc += '   <input type="radio" name="repeatUnit" id="repeatday"\n';
  if (repeatUnitVal == 'day') {
    pc += '   checked="checked"';
  }
  pc += '   value="day"  > \n';
  var everyNtext = getHiddenField('${formName}', '${evid}', 'everyN');
  pc += '   <ssf:nlt tag="event.every" /> <input type="text" name="everyNday" size="2" \n';
  pc += '   class="content" value="';
  pc += everyNtext;
  pc += '"> <ssf:nlt tag="event.days" /></td>\n';
  pc += ' </tr>\n';
  pc += ' <tr>\n';
  pc += '  <td class="content" valign="top" nowrap="nowrap">\n';
  pc += '   <input type="radio" name="repeatUnit" id="repeatweek"\n';
  if (repeatUnitVal == 'week') {
    pc += '   checked="checked"';
  }
  pc += '   value="week" >\n';
  pc += '   <ssf:nlt tag="event.every" /> <input type="text" name="everyNweek" size="2" \n';
  pc += '   class="content" value="';
  pc += everyNtext;
  pc += '" > <ssf:nlt tag="event.weeks" /> <ssf:nlt tag="event.occurson" /> \n';

  var day0checked = getHiddenField('${formName}', '${evid}', 'day0');
  pc += '<input type="checkbox" name="day0" id="day0" ';
  if (day0checked == 'yes') {
    pc += 'checked="checked" ';
  }
  pc += '>\n';
  pc += '<font size="-2"><ssf:nlt tag="calendar.day.abbrevs.su" /></font>\n';
  var day1checked = getHiddenField('${formName}', '${evid}', 'day1');
  pc += '<input type="checkbox" name="day1" id="day1" ';
  if (day1checked == 'yes') {
    pc += 'checked="checked" ';
  }
  pc += '>\n';
  pc += '<font size="-2"><ssf:nlt tag="calendar.day.abbrevs.mo" /></font>\n';
  var day2checked = getHiddenField('${formName}', '${evid}', 'day2');
  pc += '<input type="checkbox" name="day2" id="day2" ';
  if (day2checked == 'yes') {
    pc += 'checked="checked" ';
  }
  pc += '>\n';
  pc += '<font size="-2"><ssf:nlt tag="calendar.day.abbrevs.tu" /></font>\n';
  var day3checked = getHiddenField('${formName}', '${evid}', 'day3');
  pc += '<input type="checkbox" name="day3" id="day3" ';
  if (day3checked == 'yes') {
    pc += 'checked="checked" ';
  }
  pc += '>\n';
  pc += '<font size="-2"><ssf:nlt tag="calendar.day.abbrevs.we" /></font>\n';
  var day4checked = getHiddenField('${formName}', '${evid}', 'day4');
  pc += '<input type="checkbox" name="day4" id="day4" ';
  if (day4checked == 'yes') {
    pc += 'checked="checked" ';
  }
  pc += '>\n';
  pc += '<font size="-2"><ssf:nlt tag="calendar.day.abbrevs.th" /></font>\n';
  var day5checked = getHiddenField('${formName}', '${evid}', 'day5');
  pc += '<input type="checkbox" name="day5" id="day5" ';
  if (day5checked == 'yes') {
    pc += 'checked="checked" ';
  }
  pc += '>\n';
  pc += '<font size="-2"><ssf:nlt tag="calendar.day.abbrevs.fr" /></font>\n';
  var day6checked = getHiddenField('${formName}', '${evid}', 'day6');
  pc += '<input type="checkbox" name="day6" id="day6" ';
  if (day6checked == 'yes') {
    pc += 'checked="checked" ';
  }
  pc += '>\n';
  pc += '<font size="-2"><ssf:nlt tag="calendar.day.abbrevs.sa" /></font>\n';
  pc += '</tr>\n';
 
  pc += ' <tr>\n';

  pc += '  <td class="content" valign="top" nowrap="nowrap"><input \n';
  pc += '   type="radio" name="repeatUnit" id="repeatmonth"\n';
  if (repeatUnitVal == 'month') {
    pc += '   checked="checked"';
  }
  pc += '   value="month" " >\n';
  pc += '   <ssf:nlt tag="event.every" /> <input type="text" class="content" size="2"\n';
  pc += '   name="everyNmonth" value="';
  pc += everyNtext;
  pc += '" > month(s) on the\n';
  pc += '<select class="content" name="onDayCardSel" title="select which week in the month on which this calendar entry will occur" name="onDayCardSel" > \n';
  var onDayCard = getHiddenField('${formName}', '${evid}', 'onDayCard'); 
  pc += '<option class="content" value="none" ';
  if (onDayCard == 'none') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="general.please_select" /></option> \n';
  var onDayCard = getHiddenField('${formName}', '${evid}', 'onDayCard'); 
  pc += '<option class="content" value="first" ';
  if (onDayCard == 'first') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="event.whichweek.first" /></option> \n';
  pc += '<option class="content" value="second" ';
  if (onDayCard == 'second') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="event.whichweek.second" /></option> \n';
  pc += '<option class="content" value="third" ';
  if (onDayCard == 'third') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="event.whichweek.third" /></option> \n';
  pc += '<option class="content" value="fourth" ';
  if (onDayCard == 'fourth') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="event.whichweek.fourth" /></option> \n';
  pc += '<option class="content" value="last" ';
  if (onDayCard == 'last') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="event.whichweek.last" /></option> \n';
  pc += '</select> \n';
  pc += '<select class="content" name="dow" title="select the day of the week on which the repeated entry will occur" > \n';
  var dow = getHiddenField('${formName}', '${evid}', 'dow');
  pc += '<option class="content" value="none"';
  if (dow == 'none') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="general.please_select" /></option> \n';
  pc += '<option class="content" value="Sunday"';
  if (dow == 'Sunday') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="calendar.day.names.su" /></option> \n';
  pc += '<option class="content" value="Monday"';
  if (dow == 'Monday') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="calendar.day.names.mo" /></option> \n';
  pc += '<option class="content" value="Tuesday"';
  if (dow == 'Tuesday') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="calendar.day.names.tu" /></option> \n';
  pc += '<option class="content" value="Wednesday"';
  if (dow == 'Wednesday') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="calendar.day.names.we" /></option> \n';
  pc += '<option class="content" value="Thursday"';
  if (dow == 'Thursday') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="calendar.day.names.th" /></option> \n';
  pc += '<option class="content" value="Friday"';
  if (dow == 'Friday') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="calendar.day.names.fr" /></option> \n';
  pc += '<option class="content" value="Saturday"';
  if (dow == 'Saturday') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="calendar.day.names.sa" /></option> \n';
  pc += '<option class="content" value="weekday"';
  if (dow == 'weekday') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="calendar.day.names.weekday" /></option> \n';
  pc += '<option class="content" value="weekendday"';
  if (dow == 'weekendday') {
    pc += 'selected="selected"';
  }
  pc += '><ssf:nlt tag="calendar.day.names.weekendday" /></option> \n';
  pc += '</select> </td>\n';

  pc += ' </tr>\n';
  pc += '</table>	\n';

  pc += '<ssf:nlt tag="event.repeatrange" /><br>\n';
  pc += '<' + 'ssf:datepicker formName="recurPopupForm" id="untildate" />\n';

  pc += '<br>\n';
  pc += '<center>\n';
  pc += '<table border="0" style="border:1px solid;">\n';
  pc += '<tr><td align="center">\n';
  pc += '<a href="javascript: ;" onClick="setOpenerHiddenFields(); self.close(); ">\n';
  pc += '<ssf:nlt tag="button.ok" /></a></td></tr></table>\n';
  pc += '</center>\n';

  pc += "</form>\n";


  pc += "<scr";
  pc += "ipt language='Javascript'>\n";

  pc += "var fieldref = self.document.recurPopupForm;\n";

  pc += "function getRadioButtonIdx(ptr, type, val) {\n";
  pc += "    for (i=0; i< ptr.length; i++) {\n";
  pc += "        if (ptr.elements[type][i].value ==  val) {\n";
  pc += "            return i;\n";
  pc += "        }\n";
  pc += "    }\n";
  pc += "}\n";

  pc += "var norptidx = getRadioButtonIdx(fieldref, 'repeatUnit', 'none');\n";
  pc += "var dayrptidx = getRadioButtonIdx(fieldref, 'repeatUnit', 'day');\n";
  pc += "var weekrptidx = getRadioButtonIdx(fieldref, 'repeatUnit', 'week');\n";
  pc += "var monthrptidx = getRadioButtonIdx(fieldref, 'repeatUnit', 'month');\n";


  // write data back to the opener
  pc += "function setOpenerHiddenFields() {\n";
  pc += "  if (fieldref.repeatUnit[norptidx].checked) {\n";
  pc += "    self.opener.setHiddenField('${formName}', '${evid}', 'repeatUnit', 'none');\n";
  pc += "  }\n";
  pc += "  if (fieldref.repeatUnit[dayrptidx].checked) {\n";
  pc += "    self.opener.setHiddenField('${formName}', '${evid}', 'repeatUnit', 'day');\n";
  pc += "    self.opener.setHiddenField('${formName}', '${evid}', 'everyN', fieldref.everyNday.value);\n";
  pc += "  }\n";
  pc += "  if (fieldref.repeatUnit[weekrptidx].checked) {\n";
  pc += "    self.opener.setHiddenField('${formName}', '${evid}', 'repeatUnit', 'week');\n";
  pc += "    self.opener.setHiddenField('${formName}', '${evid}', 'everyN', fieldref.everyNweek.value);\n";

  pc += "    if (fieldref.day0.checked) {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day0', 'yes')\n";
  pc += "    } else {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day0', 'no')\n";
  pc += "    }\n";
  pc += "    if (fieldref.day1.checked) {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day1', 'yes')\n";
  pc += "    } else {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day1', 'no')\n";
  pc += "    }\n";
  pc += "    if (fieldref.day2.checked) {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day2', 'yes')\n";
  pc += "    } else {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day2', 'no')\n";
  pc += "    }\n";
  pc += "    if (fieldref.day3.checked) {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day3', 'yes')\n";
  pc += "    } else {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day3', 'no')\n";
  pc += "    }\n";
  pc += "    if (fieldref.day4.checked) {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day4', 'yes')\n";
  pc += "    } else {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day4', 'no')\n";
  pc += "    }\n";
  pc += "    if (fieldref.day5.checked) {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day5', 'yes')\n";
  pc += "    } else {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day5', 'no')\n";
  pc += "    }\n";
  pc += "    if (fieldref.day6.checked) {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day6', 'yes')\n";
  pc += "    } else {\n";
  pc += "      self.opener.setHiddenField('${formName}','${evid}', 'day6', 'no')\n";
  pc += "    }\n";

  pc += "  }\n";
  pc += "  if (fieldref.repeatUnit[monthrptidx].checked) {\n";
  pc += "    self.opener.setHiddenField('${formName}', '${evid}', 'repeatUnit', 'month');\n";
  pc += "    self.opener.setHiddenField('${formName}', '${evid}', 'everyN', fieldref.everyNmonth.value);\n";
  pc += "    if (fieldref.onDayCardSel.options[0].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'onDayCard', 'none')\n";
  pc += "    }\n";
  pc += "    if (fieldref.onDayCardSel.options[1].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'onDayCard', 'first')\n";
  pc += "    }\n";
  pc += "    if (fieldref.onDayCardSel.options[2].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'onDayCard', 'second')\n";
  pc += "    }\n";
  pc += "    if (fieldref.onDayCardSel.options[3].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'onDayCard', 'third')\n";
  pc += "    }\n";
  pc += "     if (fieldref.onDayCardSel.options[4].selected) {\n";
  pc += "     self.opener.setHiddenField('${formName}', '${evid}', 'onDayCard', 'fourth')\n";
  pc += "    }\n";
  pc += "    if (fieldref.onDayCardSel.options[5].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'onDayCard', 'last')\n";
  pc += "    }\n";

  pc += "    if (fieldref.dow.options[0].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'none')\n";
  pc += "    }\n";
  pc += "    if (fieldref.dow.options[1].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Sunday')\n";
  pc += "    }\n";
  pc += "    if (fieldref.dow.options[2].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Monday')\n";
  pc += "    }\n";
  pc += "    if (fieldref.dow.options[3].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Tuesday')\n";
  pc += "    }\n";
  pc += "    if (fieldref.dow.options[4].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Wednesday')\n";
  pc += "    }\n";
  pc += "    if (fieldref.dow.options[5].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Thursday')\n";
  pc += "    }\n";
  pc += "    if (fieldref.dow.options[6].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Friday')\n";
  pc += "    }\n";
  pc += "    if (fieldref.dow.options[7].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'Saturday')\n";
  pc += "    }\n";
  pc += "    if (fieldref.dow.options[8].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'weekday')\n";
  pc += "    }\n";
  pc += "    if (fieldref.dow.options[9].selected) {\n";
  pc += "      self.opener.setHiddenField('${formName}', '${evid}', 'dow', 'weekendday')\n";
  pc += "    }\n";


  pc += "  }\n";
  pc += "}   \n";

  pc += "</sc";
  pc += "ript>\n";

  return pc;
}


// pop up the recurrence stuff
function ${prefix}_popupRecurrenceWindow() {;
   var win = new PopupWindow();
   win.setSize(500,220);
   win.autoHide();
   // should be conditional on IE
   win.offsetY = 25;
   win.populate(${prefix}_generatePopupContents());
   win.showPopup('${prefix}_anchor');
}

function setHiddenField(formName, id, fn, val) {
   eval('self.document.' + formName + '.' + formName + '_' + id + '_' + fn + '.value = val');
}

function getHiddenField(formName, id, fn) {
  var val = eval('self.document.' + formName + '.' + formName + '_' + id + '_' + fn + '.value');
  return val;
}

</script>

