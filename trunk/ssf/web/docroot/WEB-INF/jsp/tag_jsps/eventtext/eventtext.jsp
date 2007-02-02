<%@ page import="com.sitescape.ef.domain.Event" %>
<%@ page import="com.sitescape.team.util.NLT" %>
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
    Calendar un = event.getUntil();

    // array of text strings for days of the week
    // these should be removed from this file (mhu)
    String days[] = new String[10];
    days[Calendar.SUNDAY] = NLT.get("calendar.day.abbrevs.su");
    days[Calendar.MONDAY] = NLT.get("calendar.day.abbrevs.mo");
    days[Calendar.TUESDAY] = NLT.get("calendar.day.abbrevs.tu");
    days[Calendar.WEDNESDAY] = NLT.get("calendar.day.abbrevs.we");
    days[Calendar.THURSDAY] = NLT.get("calendar.day.abbrevs.th");
    days[Calendar.FRIDAY] = NLT.get("calendar.day.abbrevs.fr");
    days[Calendar.SATURDAY] = NLT.get("calendar.day.abbrevs.sa");

    String nums[] = new String[6];
    nums[1] = NLT.get("calendar.first");
    nums[2] = NLT.get("calendar.second");
    nums[3] = NLT.get("calendar.third");
    nums[4] = NLT.get("calendar.fourth");
    nums[5] = NLT.get("calendar.last");

    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
    String startString = sdf.format(st.getTime());
    String endString = sdf.format(en.getTime());

    long interval = event.getDuration().getInterval();
    String freqString = event.getFrequencyString();
    String onString = "";
    String untilString = "";
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
    if (event.getFrequencyString() != null) {
        untilString += "<br>Repeats: ";
        if (event.getCount() == 0) {
            untilString += "indefinitely";
        } else if (event.getCount() == -1) {
            untilString += "until " + sdf.format(un.getTime());
        } else {
            untilString += event.getCount() + " times";
        }
    }
	
%>

<%
    if (interval > 0) {
%>
<span>
    <ssf:nlt tag="calendar.start" text="Start"/>: <%= startString %><br />
    <ssf:nlt tag="calendar.end" text="End"/>: <%= endString %><br />
    <ssf:nlt tag="calendar.frequency" text="Frequency"/>: <%= freqString %> <%= onString %> <%= untilString %>
</span>
<%
    } else {
%>
<span>
    <ssf:nlt tag="calendar.when" text="When"/>: <%= startString %> <br />
    <ssf:nlt tag="calendar.frequency" text="Frequency"/>: <%= freqString %> <%= onString %> <%= untilString %>
</span>
<%
    }
%>








