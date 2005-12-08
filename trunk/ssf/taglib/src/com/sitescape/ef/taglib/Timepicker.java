/*
 * Created on Jun 23, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.taglib;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.TimeZone;

import java.util.Calendar;
import java.util.GregorianCalendar;


import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;

/*
 * Created on Apr 7, 2005
 *
 *	Stuff for the datepicker tag
 * 
 */


/**
 * @author billmers
 *
 */

public class Timepicker extends TagSupport {
        
  private String contextPath;
  private String id;
  private String formName;
  private Date initDate;
  private String initTimeString;
  private String sequenceNumber;

  public int doStartTag() throws JspException {
    JspWriter jspOut = pageContext.getOut(); 
	    
    try {
      if (id == null) {
	throw new JspException("You must provide an Id"); 
      }
      if (sequenceNumber == null) {
          sequenceNumber = "0";
      }

      HttpServletRequest req2 = (HttpServletRequest) pageContext.getRequest();
      contextPath = req2.getContextPath();
      if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);
				
      ServletRequest req = null;
      req = new DynamicServletRequest((HttpServletRequest)pageContext.getRequest());

      String jsp = contextPath + "/WEB-INF/tags/timepicker/timepicker.jsp";
      String icon = contextPath + "/images/pics/sym_s_clock.gif";
      RequestDispatcher rd = req.getRequestDispatcher(jsp); 

      GregorianCalendar cal = new GregorianCalendar();
      TimeZone tz = TimeZone.getDefault();
      Integer hour = new Integer(99); // 99 is used as the "no hour selected" value
      Integer minute = new Integer(99); // 99 is used as the "no hour selected" value
            if (initDate != null) {
          cal.setTime(initDate);
          cal.setTimeZone(tz);
          hour = new Integer(cal.get(Calendar.HOUR_OF_DAY));
          int m = cal.get(Calendar.MINUTE); 
          // we need to pass the minutes to the picker as a multiple of 5
          int mm = m % 5;
          m = m - mm;
          minute = new Integer(m);
      };
      
      
      // any attributes we might want to pass into the jsp go here
      req.setAttribute("tpid", id);
      req.setAttribute("formName", formName);
      req.setAttribute("sequenceNumber", sequenceNumber);
      req.setAttribute("hour", hour);
      req.setAttribute("minute", minute);
      req.setAttribute("icon", icon);
      
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

  public void setId(String id) {
      this.id = id;
  }

  public void setFormName(String formName) {
      this.formName = formName;
  }

  public void setInitDate(Date initDate) {
      this.initDate = initDate;
  }

  public void setInitTimeString(String initTimeString) {
      this.initTimeString = initTimeString;
  }

  public void setSequenceNumber(String sequenceNumber) {
      this.sequenceNumber = sequenceNumber;
  }
  
}


