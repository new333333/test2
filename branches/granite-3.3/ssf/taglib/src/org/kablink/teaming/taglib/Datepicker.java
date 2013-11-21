/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
/*
 * Created on Apr 7, 2005
 *
 *	Stuff for the datepicker tag
 * 
 */
package org.kablink.teaming.taglib;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.util.MiscUtil;


import java.util.Date;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.GregorianCalendar;
import java.util.regex.*;


/**
 * @author billmers
 *;
 */
public class Datepicker extends TagSupport {
    private String id;
    private String formName;
    private String altText = "";
    private Date initDate = null;
    private String componentOrder = null;
    private String callbackRoutine = "";
    private Boolean showSelectors = new Boolean(true);
    private Boolean immediateMode = new Boolean(false);
    // used for all day events - they don't have time zone
    private Boolean ignoreTimeZone = Boolean.FALSE;
    private String popupDivId = "";
    private String calendarDivId = "";
    
    private boolean initDateProvided;
    private String contextPath;

    public int doStartTag() throws JspException {
	    JspWriter jspOut = pageContext.getOut(); 
	    if (id == null) {
	        throw new JspException("ssf:datepicker calls must include a unique id"); 
	    }
	    if (componentOrder == null) componentOrder = getLocaleComponentOrder();
	    String prefix = id;
	    Pattern pat;
	    Matcher mat;
	    
	    // check the id for embedded blanks (it may not have any) -- actually, entire prefix must be ok
	    pat = Pattern.compile("^.* .*$");
	    mat = pat.matcher(prefix);
	    if (mat.find()) {
	        throw new JspException("ssf:datepicker id (formName and id) must not contain spaces");
	    }
	    
	    if (initDate == null) {
	        initDate = new Date();
	        initDateProvided = false;
	    } else {
	        initDateProvided = true;
	    }
	    //If showing the calendar inline, don't also allow the popup div
	    if (!this.calendarDivId.equals("")) this.popupDivId = "";
	    
	    User user = RequestContextHolder.getRequestContext().getUser();
	    GregorianCalendar cal = new GregorianCalendar(user.getLocale());
	    cal.setTime(initDate);
	    if (!ignoreTimeZone) {
	    	cal.setTimeZone(user.getTimeZone());
	    }
	    
	    try {
			HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
			contextPath = MiscUtil.getFullStaticPath(req);
			String[] monthnames = { 
					NLT.get("calendar.january"),
					NLT.get("calendar.february"),
					NLT.get("calendar.march"),
					NLT.get("calendar.april"),
					NLT.get("calendar.may"),
					NLT.get("calendar.june"),
					NLT.get("calendar.july"),
					NLT.get("calendar.august"),
					NLT.get("calendar.september"),
					NLT.get("calendar.october"),
					NLT.get("calendar.november"),
					NLT.get("calendar.december")
			};
			
			String[] monthabvnames = { 
					NLT.get("calendar.abbreviation.january"),
					NLT.get("calendar.abbreviation.february"),
					NLT.get("calendar.abbreviation.march"),
					NLT.get("calendar.abbreviation.april"),
					NLT.get("calendar.abbreviation.may"),
					NLT.get("calendar.abbreviation.june"),
					NLT.get("calendar.abbreviation.july"),
					NLT.get("calendar.abbreviation.august"),
					NLT.get("calendar.abbreviation.september"),
					NLT.get("calendar.abbreviation.october"),
					NLT.get("calendar.abbreviation.november"),
					NLT.get("calendar.abbreviation.december")
			};
			
			String[] dayHeaders = { 
					NLT.get("calendar.day.abbrev1.su"),
					NLT.get("calendar.day.abbrev1.mo"),
					NLT.get("calendar.day.abbrev1.tu"),
					NLT.get("calendar.day.abbrev1.we"),
					NLT.get("calendar.day.abbrev1.th"),
					NLT.get("calendar.day.abbrev1.fr"),
					NLT.get("calendar.day.abbrev1.sa"),
			};
			
			if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);

	        StringBuffer sb = new StringBuffer();
	        // load support for datapicker 
	        sb.append("<script type=\"text/javascript\" src=\"")
	          .append(contextPath)
	          .append("/js/datepicker/CalendarPopup.js\"></script>\n");
	        sb.append("<script type=\"text/javascript\" src=\"")
	          .append(contextPath)
	          .append("/js/common/AnchorPosition.js\"></script>\n");
	        sb.append("<script type=\"text/javascript\" src=\"")
	          .append(contextPath)
	          .append("/js/common/PopupWindow.js\"></script>\n");
	        sb.append("<script type=\"text/javascript\" src=\"")
	          .append(contextPath)
	          .append("/js/datepicker/date.js\"></script>\n");
						
			
	        String varname = prefix; // for some backward compatibility
			// we have to keep the instance name and the setMultipleValues function name unique
			// in case there is more than one picker on the page
			
			sb.append("<script type=\"text/javascript\">\n");
			sb.append("function dpos_")
			  .append(prefix)
			  .append("() {\n")
			  .append("document.")
			  .append(formName)
			  .append(".")
			  .append(prefix)
			  .append("_hidden.value = getCurrentDateString_")
			  .append(prefix)
			  .append("(\"yyyymmdd\");\n")
			  .append("return true;\n};\n");
			sb.append("ss_createOnSubmitObj('datepickerOnsubmit_")
			  .append(prefix)
			  .append("','")
			  .append(formName)
			  .append("',dpos_")
			  .append(prefix)
			  .append(")\n");
			sb.append("var ").append(varname)
			  .append(" = new CalendarPopup('"+popupDivId+calendarDivId+"');\n");
			sb.append(varname).append(".setYearSelectStartOffset(7);\n");
			sb.append(varname).append(".setReturnFunction(\"setMultipleValues_")
			  .append(prefix).append("\");\n");
			sb.append(varname).append(".showNavigationDropdowns();\n");
			sb.append(varname).append(".setTodayText('"+ NLT.get("button.today").replaceAll("'", "\\\\'") +"');\n");
			sb.append(varname).append(".setOkText('"+ NLT.get("button.ok").replaceAll("'", "\\\\'") +"');\n");
			sb.append(varname).append(".setCancelText('"+ NLT.get("button.cancel").replaceAll("'", "\\\\'") +"');\n");
			sb.append(varname).append(".offsetX = -75;\n");
			if (!this.calendarDivId.equals("")) {
				sb.append(varname).append(".alwaysShowCalendar();\n");
				sb.append(varname).append(".noShowCancelButton();\n");
			}
			sb.append(varname).append(".noAutoHide();\n");
			sb.append(varname).append(".setMonthNames(");
			for (int i = 0; i <= 11; i++) {
				sb.append("'" + monthnames[i] + "'");
				if (i < 11) sb.append(",");
			}
			sb.append(");\n");
			sb.append(varname).append(".setMonthAbbreviations(");
			for (int i = 0; i <= 11; i++) {
				sb.append("'" + monthabvnames[i] + "'");
				if (i < 11) sb.append(",");
			}
			sb.append(");\n");
			sb.append(varname).append(".setDayHeaders(");
			for (int i = 0; i <= 6; i++) {
				sb.append("'" + dayHeaders[i] + "'");
				if (i < 6) sb.append(",");
			}
			sb.append(");\n");
			sb.append(varname).append(".setWeekStartDay(");
			sb.append(cal.getFirstDayOfWeek() - 1);
			sb.append(");\n");


			// this routine is the callback when the user selects a date in the pop-up picker
			sb.append("function setMultipleValues_").append(prefix).append("(y,m,d) {\n");
			sb.append("document.").append(formName).append(".")
			  .append(varname)
			  .append("_year.value=y;\n");
			if (immediateMode.booleanValue()) {
				sb.append("document.").append(formName).append(".")
			      .append(varname)
			      .append("_month.value=m;\n");
			    sb.append("document.").append(formName).append(".")
			      .append(varname)
			      .append("_date.value=d;\n");
				if (!this.callbackRoutine.equals("")) {
					sb.append(this.callbackRoutine).append("();\n");
				} else {
					sb.append("self.document.").append(formName).append(".submit();");
				}
			} else {
			    sb.append("document.").append(formName).append(".")
			      .append(varname)
			      .append("_month.selectedIndex=m;\n");
			    sb.append("document.").append(formName).append(".")
			      .append(varname)
			      .append("_date.selectedIndex=d;\n");
			    if (!this.callbackRoutine.equals("")) {
					sb.append(this.callbackRoutine).append("();\n");
				}
			}
			sb.append("}\n");

			sb.append("function getCurrentDateString_")
			  .append(prefix)
			  .append("(format) {\n")
			  .append("var m;\nvar d;\nvar y;\n")
			  .append("var mblank =0; var dblank = 0; var yblank = 0;\n");
			if (immediateMode.booleanValue()) {
			  sb.append("m = document.")
			    .append(formName)
			    .append(".")
			    .append(prefix)
			    .append("_month.value ;\n");
			} else {
			  sb.append("m = document.")
			    .append(formName)
			    .append(".")
			    .append(prefix)
			    .append("_month.selectedIndex ;\n");
			}
			sb.append("today = new Date();\n")
			  .append("if (m == 0) {m = today.getMonth()+1; mblank=1; } \n")
			  .append("if (m == 0 || m == 1 || m == 2 || m == 3 || m == 4 || m == 5 || m == 6 || m == 7 || m == 8 || m == 9) { \n")
			  .append("mm = '0' + m;\n")
			  .append("} else {\n")
			  .append("mm = m;\n}\n");
			if (immediateMode.booleanValue()) {
			  sb.append("d = document.")
			    .append(formName)
			    .append(".")
			    .append(prefix)
			    .append("_date.value ;\n");
			} else {
			  sb.append("d = document.")
			    .append(formName)
			    .append(".")
			    .append(prefix)
			    .append("_date.selectedIndex ;\n");
			}
			sb.append("if (d == 0) { d = today.getDate(); dblank = 1; } \n")
			  .append("if (d == 0 || d == 1 || d == 2 || d == 3 || d == 4 || d == 5 || d == 6 || d == 7 || d == 8 || d == 9) { \n")
			  .append("dd = '0' + d;\n")
			  .append("} else {\n")
			  .append("dd = d;\n}\n");
			sb.append("y = document.")
			  .append(formName)
			  .append(".")
			  .append(prefix)
			  .append("_year.value;\n");
			sb.append("if (y == '') { y = 1900 + today.getYear(); yblank =1; } \n")
			  .append("if (format == \"mdy\") {\n")
			  .append("return m + \"/\" + d + \"/\" + y;\n")
			  .append("} else {\n")
			  .append("if (mblank==1) {\n  if (dblank==1) {\n    if (yblank==1) { return \"\" }\n  }\n}\n")
			  .append("return y + \"-\" + mm + \"-\" + dd + \"T00\" + \":\" + \"00\" + \":\" + \"00\";\n }\n}\n");

			sb.append("</script>\n");
			
			String selected = new String("");
			for (int cp=0; cp<3; cp++) {
			    int i;
			    switch (componentOrder.charAt(cp)) {
			    	case 'm': 
				  if (!this.showSelectors.booleanValue() || 
						  immediateMode.booleanValue()) {
				    sb.append("<input type=\"hidden\"")
				      .append(" name=\"").append(prefix).append("_month\"")
				      .append(" id=\"").append(prefix).append("_month\"")
				      .append(" value=\"").append(cal.get(Calendar.MONTH)+1).append("\" />\n");
				  } else {
					sb.append("<label for=\"").append(prefix).append("_month\">&nbsp;</label>\n");
					sb.append("<select name=\"").append(prefix).append("_month\" id=\"").append(prefix).append("_month\">\n");
				    if (!initDateProvided) {
				      selected = "selected=\"selected\"";
				    }
				    sb.append("<option value=\"0\"")
				      .append(selected)
				      .append(" >")
				      .append("---")
				      .append("</option>\n");
				    for (i=1; i<13; i++) {
				      if (cal.get(Calendar.MONTH) == i-1 && initDateProvided) {
					selected = "selected=\"selected\"";
				      } else {
					selected = "";
				      }
				      sb.append("<option value=\"").append(i)
					.append("\" ").append(selected).append(">")
					.append(monthnames[i-1])
					.append("</option>\n");
				    }
				    sb.append("</select>\n");
				    sb.append("\n");
				  }
				  break;
			    	case 'd': 
				  if (!this.showSelectors.booleanValue() || 
						  immediateMode.booleanValue()) {
				    sb.append("<input type=\"hidden\"")
				      .append(" name=\"").append(prefix).append("_date\"")
				      .append(" id=\"").append(prefix).append("_date\"")
				      .append(" value=\"").append(cal.get(Calendar.DAY_OF_MONTH)).append("\" />\n");
				  } else {
					  sb.append("<label for=\"").append(prefix).append("_date\">&nbsp;</label>\n");
					sb.append("<select name=\"").append(prefix).append("_date\" id=\"").append(prefix).append("_date\">\n");
				    if (!initDateProvided) {
				      selected = "selected=\"selected\"";
				    }
				    sb.append("<option value=\"0\"")
				      .append(selected)
				      .append(" >")
				      .append("---")
				      .append("</option>\n");
				    selected = "";
				    for (i=1; i<32; i++) {
				      if (cal.get(Calendar.DATE) == i && initDateProvided) {
					selected = "selected=\"selected\"";
				      } else {
					selected = "";
				      }
				      sb.append("<option value=\"").append(i)
					.append("\" ").append(selected).append(">").append(i)
					.append("</option>\n");
				    }
				    sb.append("</select>\n");
				  }
				  break;
			    	case 'y': 
				  if (!this.showSelectors.booleanValue() || 
						  immediateMode.booleanValue()) {
				    sb.append("<input type=\"hidden\"")
				      .append(" name=\"").append(prefix).append("_year\"")
				      .append(" id=\"").append(prefix).append("_year\"")
				      .append(" value=\"").append(cal.get(Calendar.YEAR)).append("\" />\n");
				  } else {
					  sb.append("<label for=\"").append(prefix).append("_year\">&nbsp;</label>\n");
				    sb.append("<INPUT TYPE=\"text\" CLASS=\"ss_text\" NAME=\"").append(prefix)
				      .append("_year\" id=\"").append(prefix).append("_year\" VALUE=\"");
				    if (initDateProvided) {
				      sb.append(cal.get(Calendar.YEAR));
				    } else {
				      sb.append("\"\"");
				    }
				    sb.append("\" SIZE=\"4\" />\n");
				  }
				  break;
			    }
			}
			
			//Show the calendar itself or show the calendar popup icon
			if (calendarDivId.equals("")) {
				sb.append("<A class=\"ss_calendarButton\" HREF=\"#\" ");
	            sb.append("onClick=\"");
	            if (!popupDivId.equals("")) 
	            		sb.append("ss_moveDivToBody('").append(popupDivId).append("');");
	            sb.append("if (window.");
	            sb.append(prefix);
	            sb.append(") ");
	            sb.append(prefix);
				sb.append(".showCalendar('anchor_")
				  .append(prefix)
				  .append("', ")
				  .append("getCurrentDateString_")
				  .append(prefix)
				  .append("('mdy')); return false;\" ");
				sb.append("NAME=\"anchor_").append(prefix).append("\"");
				sb.append(" ID=\"anchor_").append(prefix)
				  .append("\"")
				  .append("><IMG BORDER=\"0\" SRC=\"")
				  .append(contextPath)
				  .append("/images/pics/1pix.gif\" ");
				sb.append("alt=\"").append(this.altText).append("\" title=\"").append(this.altText).append("\"/></A>\n");
			} else {
				sb.append("<A NAME=\"anchor_").append(prefix).append("\"");
				sb.append(" ID=\"anchor_").append(prefix)
				  .append("\"")
				  .append("></A>\n");
				sb.append("<script type=\"text/javascript\">\n");
				sb.append("function ");
				sb.append(prefix);
				sb.append("_outercall() {\n");
				sb.append(prefix);
				sb.append(".showCalendar('anchor_")
				  .append(prefix)
				  .append("', ")
				  .append("getCurrentDateString_")
				  .append(prefix)
				  .append("('mdy'));\n");
				sb.append("}\n");
				sb.append("ss_createOnLoadObj('");
				sb.append(prefix);
				sb.append("', ");
				sb.append(prefix);
				sb.append("_outercall);\n");
				sb.append("</script>\n");
			}
			sb.append("<input type=\"hidden\" name=\"")
			  .append(prefix)
			  .append("_hidden\" value=\"\" />\n");

			
			
			if (!ignoreTimeZone) {
				TimeZone tz = user.getTimeZone();
				sb.append("<input type=\"hidden\" name=\"")
				  .append(prefix)
				  .append("_timezoneid\" value=\"")
				  .append(tz.getID())
				  .append("\" />\n");
			}
			
			jspOut.print(sb.toString());
	    }
        catch (Exception e) {
	       throw new JspException(e);
	    }
        finally {
        	altText = "";
        	popupDivId = "";
        	calendarDivId = "";
        	initDate = null;
            componentOrder = null;
            callbackRoutine = "";
            showSelectors = new Boolean(true);
            immediateMode = new Boolean(false);
            ignoreTimeZone = Boolean.FALSE;
        }
	   return SKIP_BODY;
	}
  	
	public int doEndTag () throws JspException {
	    try {
	        this.initDate = null;
	    }
	    catch (Exception e) {
	        throw new JspException(e);
	    }
	    return SKIP_BODY;
	}
	
	public void setId(String id) {
	    this.id = id;
	}

	public void setPopupDivId(String id) {
	    this.popupDivId = id;
	}

	public void setCalendarDivId(String id) {
	    this.calendarDivId = id;
	}

	public void setInitDate(Date initDate) {
	    this.initDate = initDate;
	}
	
	//Routine to determine the proper component order depending on locale
	private String getLocaleComponentOrder() {
		String order = "dmy";
		User user = RequestContextHolder.getRequestContext().getUser();
		if (user.getLocale().equals(Locale.US)) order = "mdy";
		if (user.getLocale().getLanguage().equals(Locale.CHINESE.getLanguage())) order = "ymd";
		if (user.getLocale().getLanguage().equals(Locale.JAPANESE.getLanguage())) order = "ymd";
		if (user.getLocale().getLanguage().equals(Locale.KOREAN.getLanguage())) order = "ymd";
		
		return order;
	}

	// component order is the order of the three form elements, month, day, and year
	// the string should be any permutation of "m", "d", and "y", e.g. "mdy" or "ydm". 
	public void setComponentOrder(String componentOrder) 
	throws JspException {
	    // check for a valid format string
	    if (componentOrder.length() != 3) {
	        throw new JspException(
	                "componentOrder must be a string of exactly three characters");
	    }
	    for (int i = 0; i < 3; i++) {
	        if (componentOrder.charAt(i)!= 'm' && 
	                componentOrder.charAt(i) != 'd' &&
	                componentOrder.charAt(i) != 'y') {
	            throw new JspException("componentOrder contains an illegal format charater: " + 
	                    componentOrder.charAt(i));
	        }
	    }
	    this.componentOrder = componentOrder;
	}
	public void setFormName(String formName) {
	    this.formName = formName;
	}
	
	public void setAltText(String text) {
	    this.altText = text;
	}
	
	public void setCallbackRoutine(String callbackRoutine) {
		this.callbackRoutine = callbackRoutine;
	}

	public void setShowSelectors(Boolean value) {
		this.showSelectors = value;
	}

	public void setImmediateMode(Boolean value) {
		this.immediateMode = value;
	}

	public void setIgnoreTimeZone(Boolean ignoreTimeZone) {
		this.ignoreTimeZone = ignoreTimeZone;
	}

}
