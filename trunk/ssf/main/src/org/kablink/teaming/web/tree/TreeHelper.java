package org.kablink.teaming.web.tree;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.Validator;

public class TreeHelper {

	public static Collection<Long> getSelectedIds(String[] values, String prefix) {
		HashSet<Long> ids = new HashSet();
		
		if (values == null) {
			return ids;
		}

		//Get the binders for reporting
		for (int i = 0; i < values.length; i++) {
			String[] valueSplited = values[i].split("\\s");
			for (int j = 0; j < valueSplited.length; j++) {
				if (Validator.isNotNull(valueSplited[j])) {
					String id=null;
					if (Validator.isNotNull(prefix)) {
						if (!valueSplited[j].startsWith(prefix + WebKeys.URL_ID_CHOICES_SEPARATOR)) continue;
						id = valueSplited[j].replace(prefix + WebKeys.URL_ID_CHOICES_SEPARATOR, "");
					} else {
						//just look for separator
						id = valueSplited[j].substring(valueSplited[j].indexOf(WebKeys.URL_ID_CHOICES_SEPARATOR) + 1);
					}
					try {
						ids.add(Long.valueOf(id));
					} catch (NumberFormatException nf) {};
				}
			}
		}
		return ids;
	}
	public static Collection<Long> getSelectedIds(Map params) {
		return getSelectedIds((String[])params.get(WebKeys.URL_ID_CHOICES), null);
	}
	public static Collection<Long> getSelectedIds(Map params, String prefix) {
		return getSelectedIds((String[])params.get(WebKeys.URL_ID_CHOICES), prefix);
	}
	public static Collection<Long> getSelectedIds(InputDataAccessor params, String prefix) {
		return getSelectedIds(params.getValues(WebKeys.URL_ID_CHOICES), prefix);
	}
	public static Collection<String> getSelectedStringIds(Map params, String prefix) {
		HashSet<String> ids = new HashSet();

		//Get the binders for reporting
		String[] values = (String[])params.get(WebKeys.URL_ID_CHOICES);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				String[] valueSplited = values[i].split("\\s");
				for (int j = 0; j < valueSplited.length; j++) {
					if (Validator.isNotNull(valueSplited[j])) {
						String id=null;
						if (Validator.isNotNull(prefix)) {
							if (!valueSplited[j].startsWith(prefix + WebKeys.URL_ID_CHOICES_SEPARATOR)) continue;
							id = valueSplited[j].replace(prefix + WebKeys.URL_ID_CHOICES_SEPARATOR, "");
						} else {
							//just look for separator
							id = valueSplited[j].substring(valueSplited[j].indexOf(WebKeys.URL_ID_CHOICES_SEPARATOR) + 1);
						}
						ids.add(id);
					}
				}
			}
		}
		return ids;
	}	
	public static Long getSelectedId(Map params) {
		Collection<Long>ids = getSelectedIds(params);
		if (ids.isEmpty()) return null;
		return ids.iterator().next();

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
