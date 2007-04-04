/*
 * Created on Jul 29, 2005
 *
 */
package com.sitescape.team.taglib;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.NLT;

import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.GregorianCalendar;
import java.util.regex.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sitescape.team.domain.Event;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

import com.sitescape.util.cal.DayAndPosition;


/**
 * @author billmers
 *;
 */

public class Eventtext extends TagSupport {
        
  private String contextPath;
  private Event event = null;

  public int doStartTag() throws JspException {
    JspWriter jspOut = pageContext.getOut(); 
	    
    try {
        if (event == null) {
        	throw new JspException("You must provide an event"); 
        }

      /* TODO To be removed - JK 8/23/05 commented out for now
      ServletContext ctx =
          pageContext.getServletContext().getContext(PropsUtil.get(PropsUtil.PORTAL_CTX));
      */
      ServletContext ctx = null;
      if (ctx == null) {
          ctx = pageContext.getServletContext();
      }
      HttpServletRequest req2 = (HttpServletRequest) pageContext.getRequest();
      contextPath = req2.getContextPath();
      if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);
				
      ServletRequest req = null;
      req = new DynamicServletRequest((HttpServletRequest)pageContext.getRequest());

      String jsp = "/WEB-INF/jsp/tag_jsps/eventtext/eventtext.jsp";
      RequestDispatcher rd = ctx.getRequestDispatcher(jsp); 

      // in addition to the raw event, we disintangle some of the recurrence stuff
      // to make the jsp page less complex
      DayAndPosition dpa[] = event.getByDay();
      int dpalen = 0;
      // what we'll actually pass in is a list of ints representing the days
      ArrayList bydays = new ArrayList();
      Integer bynum = new Integer(0);
      if (dpa != null) {
          dpalen = dpa.length;
      }
      for (int i=0; i<dpalen; i++) {
          Integer dd = new Integer(event.getByDay()[i].getDayOfWeek());
          Integer nn = new Integer(event.getByDay()[i].getDayPosition());
          bydays.add(dd);
          bynum = nn;
      }
      
      User user = RequestContextHolder.getRequestContext().getUser();

      req.setAttribute("bydays", bydays);
      req.setAttribute("bynum", bynum);      

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

      SimpleDateFormat sdf = null;
      if (event.hasDuration()) {
    	  sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a z");
    	  sdf.setTimeZone(user.getTimeZone());
      } else {
    	  // no duration -> all day event -> no time, no time zone
    	  sdf = new SimpleDateFormat("dd MMM yyyy");
    	  sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
      }
      
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
  	
      
      req.setAttribute("startString", startString);
      req.setAttribute("endString", endString);
      req.setAttribute("freqString", freqString);
      req.setAttribute("onString", onString);
      req.setAttribute("untilString", untilString);
      if (interval > 0) {
    	  req.setAttribute("hasDuration", "yes");
      } else {
    	  req.setAttribute("hasDuration", "no");
      }
      
      
      StringServletResponse res =
          new StringServletResponse((HttpServletResponse)pageContext.getResponse());
      // this next line invokes the jsp and captures it into res
      rd.include(req, res);
      // and now dump it out into this response
      pageContext.getOut().print(res.getString());
    }

    catch (Exception e) {
      throw new JspException(e);
    }
    return SKIP_BODY;
  }

  public int doEndTag() throws JspException {
      return SKIP_BODY;
  }

  public void setEvent(Event ev) {
      this.event = ev;
  }
  
}

  
  