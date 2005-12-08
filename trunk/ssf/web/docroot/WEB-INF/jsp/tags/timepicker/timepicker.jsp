<%@ page import="java.util.Map" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<jsp:useBean id="tpid" type="String" scope="request" />
<jsp:useBean id="formName" type="String" scope="request" />
<jsp:useBean id="sequenceNumber" type="String" scope="request" />
<jsp:useBean id="hour" type="Integer" scope="request" />
<jsp:useBean id="minute" type="Integer" scope="request" />
<jsp:useBean id="icon" type="String" scope="request" />

<c:set var="prefix" value="${formName}_${tpid}_${sequenceNumber}" />

<script language="Javascript" src="<%= contextPath %>/html/js/common/PopupWindow.js"></script>
<script language="Javascript" src="<%= contextPath %>/html/js/common/AnchorPosition.js"></script>
<script language="Javascript">

var <c:out value="${prefix}" />_popupContents  = "";

<c:out value="${prefix}" />_popupContents += "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n";
<c:out value="${prefix}" />_popupContents += "<ht"+"ml><head>";
<c:out value="${prefix}" />_popupContents += "<link rel=\"stylesheet\" href=\"/html/css/forum.css\" type=\"text/css\"> \n";
<c:out value="${prefix}" />_popupContents += "<title>Select a time</title>  \n";
<c:out value="${prefix}" />_popupContents += "</head><body>";

<c:out value="${prefix}" />_popupContents += "<table border=\"0\" class=\"fineprint\"><tr><td>\n";
<c:out value="${prefix}" />_popupContents += " <tr><td>Select hour:</td><td>minutes:</td></tr><td>\n";

<c:out value="${prefix}" />_popupContents += "<table border=\"0\" cellpadding=\"4\" cellspacing=\"0\"  style=\"border: 1px solid #666666;\">\n";
<c:out value="${prefix}" />_popupContents += " <tr>\n";

<c:forEach var="i" begin="8" end="11" step="1">
<c:out value="${prefix}" />_popupContents += "   <td><a  \n";
<c:out value="${prefix}" />_popupContents += "         href=\"javascript: ;\" \n";
<c:out value="${prefix}" />_popupContents += "         onclick=\"sethr('<fmt:formatNumber value="${i}" minIntegerDigits="2" />');\"><c:out value="${i}" />AM</a>&nbsp;</td>\n";
<c:out value="${prefix}" />_popupContents += "\n";
</c:forEach>

<c:out value="${prefix}" />_popupContents += "   <td><a  \n";
<c:out value="${prefix}" />_popupContents += "         href=\"javascript: ;\" \n";
<c:out value="${prefix}" />_popupContents += "         onclick=\"sethr('12');\">12PM</a>&nbsp;</td>\n";
<c:out value="${prefix}" />_popupContents += "\n";

<c:out value="${prefix}" />_popupContents += "   <td><a  \n";
<c:out value="${prefix}" />_popupContents += "         href=\"javascript: ;\" \n";
<c:out value="${prefix}" />_popupContents += "         onclick=\"sethr('13');\">1PM</a>&nbsp;</td>\n";
<c:out value="${prefix}" />_popupContents += "\n";
<c:out value="${prefix}" />_popupContents += " </tr>\n";
<c:out value="${prefix}" />_popupContents += " <tr> \n";

<c:forEach var="i" begin="2" end="7" step="1">
<c:out value="${prefix}" />_popupContents += "  <td><a  \n";
<c:out value="${prefix}" />_popupContents += "         href=\"javascript: ;\" \n";
<c:out value="${prefix}" />_popupContents += "         onclick=\"sethr('<fmt:formatNumber value="${i+12}" minIntegerDigits="2" />');\"><c:out value="${i}" />PM</a>&nbsp;</td>\n";
<c:out value="${prefix}" />_popupContents += "\n";
</c:forEach>

<c:out value="${prefix}" />_popupContents += " </tr>\n";
<c:out value="${prefix}" />_popupContents += "</table>\n";

<c:out value="${prefix}" />_popupContents += " </td><td>\n";

<c:out value="${prefix}" />_popupContents += "<table border=\"0\" cellpadding=\"4\" cellspacing=\"0\" style=\"border: 1px solid #666666;\">\n";
<c:out value="${prefix}" />_popupContents += " <tr>\n";
<c:out value="${prefix}" />_popupContents += "  <td><a  \n";
<c:out value="${prefix}" />_popupContents += "      href=\"javascript: ;\" onclick=\"setmin('00');\">:00</a>&nbsp;</td>\n";
<c:out value="${prefix}" />_popupContents += "  <td><a  \n";
<c:out value="${prefix}" />_popupContents += "      href=\"javascript: ;\" onclick=\"setmin('15');\">:15</a>&nbsp;</td>\n";
<c:out value="${prefix}" />_popupContents += " </tr>\n";
<c:out value="${prefix}" />_popupContents += " <tr>\n";
<c:out value="${prefix}" />_popupContents += "  <td><a  \n";
<c:out value="${prefix}" />_popupContents += "      href=\"javascript: ;\" onclick=\"setmin('30');\">:30</a>&nbsp;</td>\n";
<c:out value="${prefix}" />_popupContents += "  <td><a  \n";
<c:out value="${prefix}" />_popupContents += "      href=\"javascript: ;\" onclick=\"setmin('45');\">:45</a>&nbsp;</td>\n";
<c:out value="${prefix}" />_popupContents += " </tr>\n";
<c:out value="${prefix}" />_popupContents += "</table>\n";

<c:out value="${prefix}" />_popupContents += "</td></tr></table>\n";
<c:out value="${prefix}" />_popupContents += "<center><a href=\"javascript: ;\" onClick=\"self.close()\">Close</a></center>\n";


// must break end script tag to fool the html process, which doesn't understand quotes
<c:out value="${prefix}" />_popupContents += "<scr" + "ipt language=\"Javascript\">\n";

<c:out value="${prefix}" />_popupContents += "function sethr(str) {\n";
<c:out value="${prefix}" />_popupContents += "    self.opener.sethrOpener(str, '<c:out value="${tpid}" />', '<c:out value="${formName}" />', '<c:out value="${sequenceNumber}" />');\n";
<c:out value="${prefix}" />_popupContents += "}\n";

<c:out value="${prefix}" />_popupContents += "function setmin(str) {\n";
<c:out value="${prefix}" />_popupContents += "    self.opener.setminOpener(str, '<c:out value="${tpid}" />', '<c:out value="${formName}" />', '<c:out value="${sequenceNumber}" />');\n";
<c:out value="${prefix}" />_popupContents += "}\n";


<c:out value="${prefix}" />_popupContents += "</scr" + "ipt>\n";


<c:out value="${prefix}" />_popupContents += "</body></html>";

// pop up the time picker 
function <c:out value="${prefix}" />_popupTimepicker() {
   var win = new PopupWindow();
   win.setSize(350,60);
   win.autoHide();
   // should be conditional on IE
   win.offsetY = 25;
   win.populate(<c:out value="${prefix}" />_popupContents);
   win.showPopup('<c:out value="${prefix}" />_anchor');
}

function sethrOpener(hr, id, formName, sequenceNumber) {
    var hourName = formName + "_" + id + "_" + sequenceNumber + "_hour";
    var minuteName = formName + "_" + id + "_" + sequenceNumber + "_minute";
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
    var minuteName = formName + "_" + id + "_" + sequenceNumber + "_minute";
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
  var datePrefix = formName + "_" + id + "_"; 
  var timePrefix = formName + "_" + id + "_" + "0_";
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

<table border="0" cellpadding="0" cellspacing="1">
 <tr>
  <td class="content" align="center">
<select name="<c:out value="${prefix}_hour" />" size="1" id="<c:out value="${prefix}_hour" />"
     class="content" >
<option class="content" value="99" <c:if test="${hour==99}"> selected </c:if> > -- </option>
 <option class="content" value="0" <c:if test="${hour==0}"> selected </c:if> >midnight</option> 

<c:forEach var="i" begin="1" end="11" step="1">
   <option class="content" value="<c:out value="${i}" />"<c:if test="${hour==i}"> selected</c:if>> <c:out value="${i}" />AM</option> 
</c:forEach>   

 <option class="content" value="12" <c:if test="${hour==12}"> selected </c:if> >12PM</option> 

<c:forEach var="i" begin="1" end="11" step="1">
   <option class="content" value="<c:out value="${i+12}"/>"<c:if test="${hour==i+12}"> selected</c:if>> <c:out value="${i}" />PM</option> 
</c:forEach>  
 
 <option class="content" value="00" >All day</option>

 </select></td><td class="contentbold">&nbsp;:&nbsp;</td>
 <td class="content">
  <select name="<c:out value="${prefix}_minute" />" id="<c:out value="${prefix}_minute" />" size="1" 
  class="content" >
<option class="content" value="99" > -- </option> 
<c:forEach var="i" begin="0" end="11" step="1">
   <option class-"content" value="<fmt:formatNumber value="${i*5}" minIntegerDigits="2" />"<c:if test="${minute==i*5}"> selected</c:if>><fmt:formatNumber value="${i*5}" minIntegerDigits="2" /></option> 
</c:forEach>
  </select>&nbsp;
  <a name="<c:out value="${prefix}" />_anchor" id="<c:out value="${prefix}" />_anchor"></a>
  <a href="javascript: ;" onClick="<c:out value="${prefix}" />_popupTimepicker();" >
   <img border="0" align="middle" src="<c:out value="${icon}" />"> </a>
  </td>
   </tr>
   </table>



