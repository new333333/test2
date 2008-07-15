package com.sitescape.team.web.tree;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import com.sitescape.team.web.WebKeys;
import com.sitescape.util.Validator;
public class TreeHelper {

	public static Collection getSelectedIds(Map params) {
		HashSet<Long> ids = new HashSet();

		//Get the binders for reporting
		String[] values = (String[])params.get(WebKeys.URL_ID_CHOICES);
		for (int i = 0; i < values.length; i++) {
			String[] valueSplited = values[i].split("\\s");
			for (int j = 0; j < valueSplited.length; j++) {
				if (Validator.isNotNull(valueSplited[j])) {
					String binderId = valueSplited[j].substring(valueSplited[j].indexOf("%") + 1);
					try {
						ids.add(Long.valueOf(binderId));
					} catch (NumberFormatException nf) {};
				}
			}
		}
		return ids;
	}
	public static String getSelectedIdsAsString(Map params) {
		//blank at begining and end
		StringBuffer ids = new StringBuffer(" ");

		//Get the binders for reporting
		String[] values = (String[])params.get(WebKeys.URL_ID_CHOICES);
		for (int i = 0; i < values.length; i++) {
			ids.append(values[i] + " ");
		}
		return ids.toString();
	}
	
}
