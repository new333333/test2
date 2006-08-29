package com.sitescape.ef.remoting.impl;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.module.shared.InputDataAccessor;

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

}
