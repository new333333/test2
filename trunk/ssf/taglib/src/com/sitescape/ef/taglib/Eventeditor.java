/*
 * Created on Jun 10, 2005
 *
 */
package com.sitescape.ef.taglib;

import java.lang.Boolean;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;
import com.sitescape.ef.domain.Event;

/**
 * @author billmers
 *;
 */

public class Eventeditor extends TagSupport {
        
  private String contextPath;
  private String id;
  private String formName;
  private Event initEvent = null;
  private Boolean hasDuration = new Boolean("false");
  private Boolean hasRecurrence = new Boolean("true");

  public int doStartTag() throws JspException {
    JspWriter jspOut = pageContext.getOut(); 
	    
    try {
        if (id == null) {
        	throw new JspException("You must provide an element name"); 
        }
        if (formName == null) {
        	throw new JspException("You must provide a form name"); 
        }
        
      HttpServletRequest req2 = (HttpServletRequest) pageContext.getRequest();
      contextPath = req2.getContextPath();
      if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);
				
      ServletRequest req = null;
      req = new DynamicServletRequest((HttpServletRequest)pageContext.getRequest());

      String jsp = "/WEB-INF/tags/eventeditor/eventeditor.jsp";
      String icon = contextPath + "/images/pics/sym_s_repeat.gif";
      RequestDispatcher rd = req.getRequestDispatcher(jsp); 
      
      // if initEvent is provided, take it apart and pass in two dates
      Date startDate = new Date();
      Date endDate = new Date();
      if (initEvent != null) {
          Calendar startCal = initEvent.getDtStart();
          Calendar endCal = initEvent.getDtEnd();
          startDate = startCal.getTime();
          endDate = endCal.getTime();
      }

      // any attributes we might want to pass into the jsp go here
      req.setAttribute("evid", id);
      req.setAttribute("formName", formName);
      req.setAttribute("recurIcon", icon);
      // req.setAttribute("hasDuration", hasDuration);
      req.setAttribute("hasRecurrence", hasRecurrence);
      req.setAttribute("startDate", startDate);
      req.setAttribute("endDate", endDate);

      
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

  public void setHasDuration(Boolean hasDuration) {
      this.hasDuration = hasDuration;
  }

  public void setHasRecurrence(Boolean hasRecurrence) {
      this.hasRecurrence = hasRecurrence;
  }

  public void setInitEvent(Event initEvent) {
      this.initEvent = initEvent;
  }
  
  public HashMap getAttMap() {
      HashMap attMap = new HashMap();
      attMap.put("hasDuration", hasDuration);
      return attMap;
  }
}

