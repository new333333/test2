/*
 * Created on Jul 29, 2005
 *
 */
package com.sitescape.ef.taglib;

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

import com.liferay.portal.util.PropsUtil;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

import com.sitescape.util.cal.DayAndPosition;

import com.sitescape.ef.domain.Event;

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
        
      ServletContext ctx =
          pageContext.getServletContext().getContext(PropsUtil.get(PropsUtil.PORTAL_CTX));
      if (ctx == null) {
          ctx = pageContext.getServletContext();
      }
      HttpServletRequest req2 = (HttpServletRequest) pageContext.getRequest();
      contextPath = req2.getContextPath();
      if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);
				
      ServletRequest req = null;
      req = new DynamicServletRequest((HttpServletRequest)pageContext.getRequest());

      String jsp = contextPath + "/html/tags/eventtext/eventtext.jsp";
      RequestDispatcher rd = ctx.getRequestDispatcher(jsp); 

      // any attributes we might want to pass into the jsp go here
      req.setAttribute("event", event);
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
      
      req.setAttribute("bydays", bydays);
      req.setAttribute("bynum", bynum);
      
      Calendar st = event.getDtStart();
      Calendar en = event.getDtEnd();
      SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, h a");
      String startString = sdf.format(st.getTime());
      String endString = sdf.format(en.getTime());
      
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

  
  