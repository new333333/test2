/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.remoting.ws.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import net.fortuna.ical4j.data.ParserException;

import com.sitescape.team.domain.Event;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.survey.Survey;
import com.sitescape.team.util.DateUtil;
import com.sitescape.team.util.stringcheck.StringCheckUtil;

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
	private IcalModule icalModule;
	
	public DomInputData(Document doc, IcalModule icalModule) {
		this(doc.getRootElement(), icalModule);
		this.doc = doc;
	}
	
	public DomInputData(Element root, IcalModule icalModule) {
		this.root = root;
		this.icalModule = icalModule;
	}

	public String getSingleValue(String key) {
		Element elem = (Element) root.selectSingleNode("attribute[@name='" + key + "']");
		Element valueElem = (Element) root.selectSingleNode("attribute[@name='" + key + "']/value");
		
		if(valueElem != null) {
			return StringCheckUtil.check(valueElem.getText());
		} else if(elem != null) {
			return StringCheckUtil.check(elem.getText());
		}
		else {
			return null;
		}
	}

	public Date getDateValue(String key) {
		String textVal = getSingleValue(key);
		if(textVal != null) {
			return DateUtil.parseDate(textVal);
		}
		return null;
	}

	public Event getEventValue(String key, boolean hasDuration, boolean hasRecurrence)
	{
		String val = getSingleValue(key);
		if(val != null) {
			try {
				List<Event> events = icalModule.parseEvents(new StringReader(val));
				return events.get(0);
			} catch(IOException e) {
			} catch(ParserException e) {
			} catch(IndexOutOfBoundsException e) {
			}
		}
		return null;
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
				values[i] = StringCheckUtil.check(((Element) nodes.get(i)).getText());
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
