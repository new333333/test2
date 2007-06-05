/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.remoting.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.domain.Event;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.survey.Survey;
import com.sitescape.team.web.util.DateHelper;
import com.sitescape.team.web.util.EventHelper;

/**
 * An implementation of <code>InputDataAccessor</code> based on element-only
 * flat dom tree.
 * 
 * @author jong
 *
 */
public class DomInputData implements InputDataAccessor {

	private Document doc;
	private Element root;
	private static SimpleDateFormat[] formats = {
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"),
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"),
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mmz"),
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz"),
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm"),
		new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"),
		new SimpleDateFormat("yyyy-MM-dd")
	};
	public DomInputData(Document doc) {
		this.doc = doc;
		this.root = doc.getRootElement();
	}
	
	public DomInputData(Element root) {
		this.root = root;
	}
	
	public String getSingleValue(String key) {
		Element elem = (Element) root.selectSingleNode("attribute[@name='" + key + "']");
		Element valueElem = (Element) root.selectSingleNode("attribute[@name='" + key + "']/value");
		
		if(valueElem != null) {
			return valueElem.getText();
		} else if(elem != null) {
			return elem.getText();
		}
		else {
			return null;
		}
	}

	private static Date parseDate(String text)
	{
		for(SimpleDateFormat sdf : formats) {
			try {
				return sdf.parse(text);
			} catch(java.text.ParseException e) {
			}
		}
		return null;
	}

	public Date getDateValue(String key) {
		String textVal = getSingleValue(key);
		if(textVal != null) {
			return parseDate(textVal);
		}
		return null;
	}

	public Event getEventValue(String key, boolean hasDuration, boolean hasRecurrence)
	{
		Event e = null;
		Element startElem = (Element) root.selectSingleNode("attribute[@name='" + key + "']/startDate");
		Element endElem = (Element) root.selectSingleNode("attribute[@name='" + key + "']/endDate");
		if(startElem != null && endElem != null) {
			Date startDate = parseDate(startElem.getText());
			Date endDate = parseDate(endElem.getText());
			if(startDate != null && endDate != null) {
	            GregorianCalendar startc = new GregorianCalendar();
	            GregorianCalendar endc = new GregorianCalendar();
	            startc.setTime(startDate);
	            endc.setTime(endDate);
	            e = new Event();
	            e.setDtStart(startc);
	            e.setDtEnd(endc);				
			}
		}
		
		return e;
	}

	public Survey getSurveyValue(String key)
	{
		String textVal = getSingleValue(key);
		if(textVal != null) {
			return new Survey(textVal);
		}
		return null;
	}
	public String[] getValues(String key) {
		List nodes = root.selectNodes("attribute[@name='" + key + "']/value");
		if(nodes.size() == 0) {
			nodes = root.selectNodes("attribute[@name='" + key + "']");
		}
		int size = nodes.size();
		
		if(size > 0) {
			String[] values = new String[size];
			for(int i = 0; i < size; i++) {
				values[i] = ((Element) nodes.get(i)).getText();
			}
			return values;
		}
		else {
			return null;
		}
	}

	public boolean exists(String key) {
		if(root.selectSingleNode("attribute[@name='" + key + "']") != null)
			return true;
		else
			return false;
	}

	public Object getSingleObject(String key) {
		return getSingleValue(key);
	}
	public int getCount() {
		return root.nodeCount();
	}
}
