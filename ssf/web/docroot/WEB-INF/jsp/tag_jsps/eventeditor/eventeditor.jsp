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
<jsp:useBean id="initEvent" type="com.sitescape.ef.domain.Event" scope="request" />

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
<c:set var="prefix" value="${evid}" />

<table class="ss_style" border="0" cellpadding="0"><tr><td>
 <table class="ss_style" border="0" cellpadding="4" cellspacing="0">
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
  <c:set var="freqval" value="none" />
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
  

  <div style="text-align:left; ">
     <a href="javascript: ;" onClick="${prefix}_toggleRecur('${prefix}_recur_div')" >
     <img border="0" src="<html:imagesPath />pics/sym_s_expand.gif" name="${prefix}_expandgif" /></a>
     <img border="0" src="<html:imagesPath />pics/sym_s_repeat.gif" /> 
     <a href="javascript: ;" onClick="${prefix}_toggleRecur('${prefix}_recur_div')" >
     <b><ssf:nlt tag="event.recurrence" /></b></a><br></a>
  </div>

   <div name="${prefix}_recur_div" id="${prefix}_recur_div" style="visibility:hidden; display:none;">
     <table class="ss_style" border="0" cellpadding="4" cellspacing="0">

     <tr>
     <td colspan="3" class="contentbold">

     &nbsp;<ssf:nlt tag="event.frequency" />
    </td>
    </tr>
    <tr>
     <td colspan="2" >
     <input type="radio"  
      name="${prefix}_repeatUnit" id="norepeat" 
      value="none" 
    <c:if test="${freqval == 'none'}"> checked="checked" </c:if>
   >
    <label for="norepeat"> <ssf:nlt tag="event.no_repeat" /></label></td>
    </tr>
    <tr>
     <td nowrap="nowrap" >
      <input type="radio" name="${prefix}_repeatUnit" id="repeatday"
      value="day" 
     <c:if test="${freqval == 'day'}"> checked="checked" </c:if>
     >

      <ssf:nlt tag="event.every" /> <input type="text" name="${prefix}_everyNday" size="2" 
       value="${initEvent.interval}"
   > <ssf:nlt tag="event.days" /></td>
    </tr>

    <tr>
     <td  valign="top" nowrap="nowrap">
      <input type="radio" name="${prefix}_repeatUnit" id="repeatweek"
      <c:if test="${freqval == 'week'}">
      checked="checked"
      </c:if>
      value="week" >
      <ssf:nlt tag="event.every" /> <input type="text" name="${prefix}_everyNweek" size="2" 
       value="${initEvent.interval}" > <ssf:nlt tag="event.weeks" /> <ssf:nlt tag="event.occurson" /> 

   <input type="checkbox" name="${prefix}_day0" id="${prefix}_day0"
   <c:if test="${day0sel == 'yes'}"> checked="checked" </c:if>
   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.su" /></font>
   <input type="checkbox" name="${prefix}_day1" id="${prefix}_day1"
   <c:if test="${day1sel == 'yes'}"> checked="checked" </c:if>
   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.mo" /></font>
  <input type="checkbox" name="${prefix}_day2" id="${prefix}_day2"
   <c:if test="${day2sel == 'yes'}"> checked="checked" </c:if>
   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.tu" /></font>
   <input type="checkbox" name="${prefix}_day3" id="${prefix}_day3"
   <c:if test="${day3sel == 'yes'}"> checked="checked" </c:if>
   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.we" /></font>
   <input type="checkbox" name="${prefix}_day4" id="${prefix}_day4"
   <c:if test="${day4sel == 'yes'}"> checked="checked" </c:if>
   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.th" /></font>
   <input type="checkbox" name="${prefix}_day5" id="${prefix}_day5"
   <c:if test="${day5sel == 'yes'}"> checked="checked" </c:if>
   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.fr" /></font>
   <input type="checkbox" name="${prefix}_day6" id="${prefix}_day6"
   <c:if test="${day6sel == 'yes'}"> checked="checked" </c:if>
   >
   <font size="-2"><ssf:nlt tag="calendar.day.abbrevs.sa" /></font>
   </tr>

    <tr>
     <td  valign="top" nowrap="nowrap"><input 
      type="radio" name="${prefix}_repeatUnit" id="repeatmonth"
      <c:if test="${freqval == 'month'}">
      checked="checked"
      </c:if>
      value="month" >
      <ssf:nlt tag="event.every" /> <input type="text"  size="2"
      name="${prefix}_everyNmonth" value="${initEvent.interval}"
    > month(s) on the
   <select  name="${prefix}_onDayCard" title="select which week in the month on which this calendar entry will occur" > 
   <option  value="none"
   <c:if test="${daystring == 'none'}" > selected="selected" </c:if>
   ><ssf:nlt tag="general.please_select" /></option> 
   <option  value="first"
   <c:if test="${daystring == 'first'}" > selected="selected" </c:if>
   ><ssf:nlt tag="event.whichweek.first" /></option> 
   <option  value="second"
   <c:if test="${daystring == 'second'}" > selected="selected" </c:if>
   ><ssf:nlt tag="event.whichweek.second" /></option> 
   <option  value="third"
   <c:if test="${daystring == 'third'}" > selected="selected" </c:if>
   ><ssf:nlt tag="event.whichweek.third" /></option> 
   <option  value="fourth"
   <c:if test="${daystring == 'fourth'}" > selected="selected" </c:if>
   ><ssf:nlt tag="event.whichweek.fourth" /></option> 
   <option  value="last"
   <c:if test="${daystring == 'last'}" > selected="selected" </c:if>
   ><ssf:nlt tag="event.whichweek.last" /></option> 
   </select> 
   <select  name="${prefix}_dow" title="select the day of the week on which the repeated entry will occur" > 
   <option  value="none"
   <c:if test="${dowstring == 'none'}"> selected="selected" </c:if>
   ><ssf:nlt tag="general.please_select" /></option> 
   <option  value="Sunday"
   <c:if test="${dowstring == 'Sunday'}"> selected="selected" </c:if>
   ><ssf:nlt tag="calendar.day.names.su" /></option> 
   <option  value="Monday"
   <c:if test="${dowstring == 'Monday'}"> selected="selected" </c:if>
   ><ssf:nlt tag="calendar.day.names.mo" /></option> 
   <option  value="Tuesday"
   <c:if test="${dowstring == 'Tuesday'}"> selected="selected" </c:if>
   ><ssf:nlt tag="calendar.day.names.tu" /></option> 
   <option  value="Wednesday"
   <c:if test="${dowstring == 'Wednesday'}"> selected="selected" </c:if>
   ><ssf:nlt tag="calendar.day.names.we" /></option> 
   <option  value="Thursday"
   <c:if test="${dowstring == 'Thursday'}"> selected="selected" </c:if>
   ><ssf:nlt tag="calendar.day.names.th" /></option> 
   <option  value="Friday"
   <c:if test="${dowstring == 'Friday'}"> selected="selected" </c:if>
   ><ssf:nlt tag="calendar.day.names.fr" /></option> 
   <option  value="Saturday"
   <c:if test="${dowstring == 'Saturday'}"> selected="selected" </c:if>
   ><ssf:nlt tag="calendar.day.names.sa" /></option> 
   <option  value="weekday"
   <c:if test="${dowstring == 'weekday'}"> selected="selected" </c:if>
   ><ssf:nlt tag="calendar.day.names.weekday" /></option> 
   <option  value="weekendday"
   <c:if test="${dowstring == 'weekendday'}"> selected="selected" </c:if>
   ><ssf:nlt tag="calendar.day.names.weekendday" /></option> 
   </select> </td>
   </tr>
   <% /* 
       * Until stuff works like this:
       *   count == 0 means repeats forever
       *   count == -1 means until was specified and we don't know the count
       *   count > 0 means we do know the count and the until member is also there and computed from count
       */
   %>
   <c:choose>
   <c:when test="${empty initEvent.count}"> 
   <c:set var="count" value="0" />
   </c:when>
   <c:otherwise>
   <c:set var="count" value="${initEvent.count}" />
   </c:otherwise>
   </c:choose>   

   <tr>
   <td>
   <br>&nbsp;<ssf:nlt tag="event.repeatrange" />
   </td>
   </tr>
   <tr>
   <td>
   <input type="radio" name="${prefix}_rangeSel" value="count"
   <c:if test="${count > 0}" > checked="checked" </c:if>
   >
   <ssf:nlt tag="event.repeat" />
   <input type="text" size="2" name="${prefix}_repeatCount"
   <c:choose>
   <c:when test="${count > 0}" > value="${count}" </c:when>
   <c:otherwise> value="10" </c:otherwise>
   </c:choose>    
   >
   <ssf:nlt tag="event.times" />
   </td>
   </tr>

   <tr>
   <td>
   <input type="radio" name="${prefix}_rangeSel" value="until"
   <c:if test="${count == -1}"> checked="checked" </c:if>
   > 
   <ssf:nlt tag="event.repeat_until" /> 

   <c:choose>
   <c:when test="${empty initEvent.until}">
   <ssf:datepicker formName="<%= formName %>" id="<%= endrangeId %>" />
   </c:when>
   <c:otherwise>
   <ssf:datepicker formName="<%= formName %>" id="<%= endrangeId %>" 
         initDate= "<%= initEvent.getUntil().getTime() %>" />
   </c:otherwise>
   </c:choose>
    
   </td>
   </tr>
   <tr>
   <td>
   <input type="radio" name="${prefix}_rangeSel" value="forever" 
   <c:if test="${count == 0}"> checked="checked" </c:if>
   > 
   <ssf:nlt tag="event.repeat_forever" /> 
    
   </td>
   </tr>

   </table>

  </div>
</c:if>

</td></tr></table>

<% // recurrence stuff; emit and initialize various hidden fields from the initEvent %>
<c:if test="${attMap.hasRecur}">


<c:choose>
<c:when test="${empty initEvent.interval}">
<input type="hidden" name="${prefix}_everyN" value="1">
</c:when>
<c:otherwise>
<input type="hidden" name="${prefix}_everyN" value="${initEvent.interval}">
</c:otherwise>
</c:choose>

</c:if>


<script language="Javascript">

function getRadioButtonIdx(ptr, type, val) {
    for (i=0; i< ptr.length; i++) {
        if (ptr.elements[type][i].value ==  val) {
            return i
	}
    }
}

function ${prefix}_onsub() {
  <c:if test="${attMap.hasDur}">
  // tests iff duration is on the page
  // check for negative duration
  var ms;
  ms1 = getTimeMilliseconds('<%= formName %>', '<%= dateId %>');
  ms2 = getTimeMilliseconds('<%= formName %>', '<%= dateId2 %>');
  diff = ms2 - ms1;
  if (diff < 0) {
    alert("End time must be later than start time.");
    return(false);
  }
  </c:if>

  <c:if test="${attMap.hasRecur}">

  // tests iff recurrence on the page
  // copy one of the three everyN fields to everyN hiddenfield
  var fieldref = self.document.${formName};

  var dayrptidx = getRadioButtonIdx(fieldref, '${prefix}_repeatUnit', 'day');
  var weekrptidx = getRadioButtonIdx(fieldref, '${prefix}_repeatUnit', 'week');
  var monthrptidx = getRadioButtonIdx(fieldref, '${prefix}_repeatUnit', 'month');

  if (document.getElementById('${prefix}_repeatUnit')[dayrptidx].checked) {
      document.getElementById('${prefix}_everyN').value = document.getElementById('${prefix}_everyNday').value;
  }
  if (document.getElementById('${prefix}_repeatUnit')[weekrptidx].checked) {
      document.getElementById('${prefix}_everyN').value = document.getElementById('${prefix}_everyNweek').value;
  }
  if (document.getElementById('${prefix}_repeatUnit')[monthrptidx].checked) {
      document.getElementById('${prefix}_everyN').value = document.getElementById('${prefix}_everyNmonth').value;
  }

  </c:if>
  return(true);
}

createOnSubmitObj('${prefix}onsub', 
     '${formName}', ${prefix}_onsub);
     

</script>

