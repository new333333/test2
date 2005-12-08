package com.sitescape.ef.module.shared;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.sitescape.ef.PropertyNotFoundException;
import com.sitescape.ef.util.InvokeUtil;

public class ObjectBuilder  {
	public static void updateObject(Object target, Map data)  {
		Set kvps = data.entrySet();
		Map.Entry entry;
		for (Iterator iter=kvps.iterator(); iter.hasNext();) {
			entry = (Map.Entry)iter.next();
			String attr = (String)entry.getKey();
			Object val = entry.getValue();
			try {
				InvokeUtil.invokeSetter(target, attr, val);
			} catch (PropertyNotFoundException pe) {
				//just skip it
			}
		}
	}	
}
