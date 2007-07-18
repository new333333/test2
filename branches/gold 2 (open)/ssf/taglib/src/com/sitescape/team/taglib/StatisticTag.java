/**
 * Copyright (c) 2000-2005 Liferay, LLC. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sitescape.team.taglib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.portlet.RenderRequest;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;

import com.sitescape.team.domain.Statistics;
import com.sitescape.util.servlet.StringServletResponse;

public class StatisticTag extends BodyTagSupport {
	
	public static final String ColoredBar = "coloredBar";
	
	private Map statistic;
	private String style = ColoredBar;
	private Boolean showLabel = true;
	private Boolean showLegend = true;
	
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();

			if (this.statistic.get(Statistics.VALUES_LIST) != null) {
				
				Integer internalTotal = new Integer(0);
				List statisticValuesList = (List)this.statistic.get(Statistics.VALUES_LIST);
				Map statisticValues = (Map)this.statistic.get(Statistics.VALUES);
				Map statisticLabels = (Map)this.statistic.get(Statistics.CAPTIONS);
				Integer total = (Integer)this.statistic.get(Statistics.TOTAL_KEY);
				String label = (String)this.statistic.get(Statistics.ATTRIBUTE_CAPTION);
				Iterator<Integer> iterator = statisticValues.values().iterator();
				while (iterator.hasNext()) {
					Integer value = iterator.next();
					if (value != null && value>0) {
						internalTotal += value;
					}
				}
				
				OrderedMap percentStatistic = new LinkedMap();
				if (internalTotal > 0) {
					Iterator it = statisticValuesList.iterator();
					while (it.hasNext()) {
						Object key = it.next();
						if (key != null && statisticValues.get(key) != null) {
							OrderedMap attrValues = new LinkedMap();
							attrValues.put("percent", ((Integer)statisticValues.get(key)*100)/internalTotal);
							attrValues.put("value", (Integer)statisticValues.get(key));
							attrValues.put("total", total);
							if (statisticLabels != null) {
								attrValues.put("label", statisticLabels.get(key));
							}
							percentStatistic.put(key, attrValues);
						}
					}
				}
				
				
				httpReq.setAttribute("percentStatistic", percentStatistic);
				httpReq.setAttribute("statisticLabel", label);
			}
			httpReq.setAttribute("showLabel", this.showLabel);
			httpReq.setAttribute("showLegend", this.showLegend);
			httpReq.setAttribute("barStyle", this.style);
			
			String jsp = "/WEB-INF/jsp/tag_jsps/charts/statistic.jsp";
			RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
			ServletRequest req = pageContext.getRequest();
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString().trim());
		}
		catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		}
		finally {
		}
		return EVAL_PAGE;		
	}

	public void setStatistic(Map statistic) {
		this.statistic = statistic;
	} 

	public void setStyle(String style) {
		this.style = style;
	}
	
	public void setShowLabel(Boolean showLabel) {
		this.showLabel = showLabel;
	}
	
	public void setShowLegend(Boolean showLegend) {
		this.showLegend = showLegend;
	}
}