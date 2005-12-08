<%@ page import="com.sitescape.ef.domain.Event" %>
<%@ page import="edu.columbia.cpl.Duration" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<jsp:useBean id="event" type="com.sitescape.ef.domain.Event" scope="request" />
<jsp:useBean id="bydays" type="java.util.ArrayList" scope="request" />
<jsp:useBean id="bynum" type="java.lang.Integer" scope="request" />


<% 
    Calendar st = event.getDtStart();
    Calendar en = event.getDtEnd();
    
    // array of text strings for days of the week
    String days[] = new String[10];
    days[Calendar.SUNDAY] = "SU";
    days[Calendar.MONDAY] = "MO";
    days[Calendar.TUESDAY] = "TU";
    days[Calendar.WEDNESDAY] = "WE";
    days[Calendar.THURSDAY] = "TH";
    days[Calendar.FRIDAY] = "FR";
    days[Calendar.SATURDAY] = "SA";

    String nums[] = new String[6];
    nums[1] = "first";
    nums[2] = "second";
    nums[3] = "third";
    nums[4] = "fourth";
    nums[5] = "last";

    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
    String startString = sdf.format(st.getTime());
    String endString = sdf.format(en.getTime());

    long interval = event.getDuration().getInterval();
    String freqString = event.getFrequencyString();
    String onString = "";
    String onStringSeparator = "";
    if (freqString == null) {
      freqString = "does not repeat";
    } else {
      freqString = freqString.toLowerCase();
      if (event.getInterval() > 1) {
	freqString = "every " + event.getInterval();
	if (event.getFrequency() == Event.DAILY) {
	  freqString += " days";
	}
	if (event.getFrequency() == Event.WEEKLY) {
	  freqString += " weeks";
	}
	if (event.getFrequency() == Event.MONTHLY) {
	  freqString += " months";
	}
      }
      Iterator it = bydays.listIterator();

      // format weekly events as comma-separated list of ondays
      if (event.getFrequency() == Event.WEEKLY && it.hasNext()) {
	onString += "on ";
	while (it.hasNext()) {
	  Integer ii = (Integer) it.next();
	  onString += onStringSeparator + days[ii.intValue()];
	  onStringSeparator = ", ";
	}
      }
      // monthly events include the ondaycard stuff
      // note that bydays will now only have one entry (it may be "weekday")
      // and bynum will be meaningful here (again, it is a singleton, not a list)
      if (event.getFrequency() == Event.MONTHLY && it.hasNext()) {
	Integer ii = (Integer) it.next();
	onString += "on the " + nums[bynum.intValue()] + " ";
	onString += days[ii.intValue()];
      }
    }

	
%>

<%
    if (interval > 0) {
%>
    Start: <%= startString %> End: <%= endString %> Frequency: <%= freqString %> <%= onString %>
<%
	       } else {
%>
    When: <%= startString %> Frequency: <%= freqString %> <%= onString %>
<%
	      }
%>





