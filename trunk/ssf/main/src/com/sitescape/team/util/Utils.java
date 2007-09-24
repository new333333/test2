package com.sitescape.team.util;

import java.util.Iterator;
import java.util.Map;

public class Utils {

	public static String toStringML(Map map) {
		StringBuffer buf = new StringBuffer();
		buf.append("{");

		Iterator<Map.Entry> i = map.entrySet().iterator();
		boolean hasNext = i.hasNext();
		while (hasNext) {
			Map.Entry e = i.next();
			Object key = e.getKey();
			Object value = e.getValue();
			if (key == map)
				buf.append("(this Map)");
			else
				buf.append(key);
			buf.append("=");
			if (value == map)
				buf.append("(this Map)");
			else
				buf.append(value);
			hasNext = i.hasNext();
			if (hasNext)
				buf.append(Constants.NEWLINE);
		}

		buf.append("}");
		return buf.toString();
	}
}
