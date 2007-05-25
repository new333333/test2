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

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.team.module.shared.InputDataAccessor;

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
	
	public DomInputData(Document doc) {
		this.doc = doc;
		this.root = doc.getRootElement();
	}
	
	public DomInputData(Element root) {
		this.root = root;
	}
	
	public String getSingleValue(String key) {
		Element elem = (Element) root.selectSingleNode("property[@name='" + key + "']");
		
		if(elem != null) {
			return elem.getText();
		}
		else {
			return null;
		}
	}

	public String[] getValues(String key) {
		List nodes = root.selectNodes("property[@name='" + key + "']");
		
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
		if(root.selectSingleNode("property[@name='" + key + "']") != null)
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
