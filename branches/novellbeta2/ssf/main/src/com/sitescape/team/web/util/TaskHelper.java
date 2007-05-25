package com.sitescape.team.web.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.search.SearchFieldResult;
import com.sitescape.team.util.ResolveIds;
import com.sitescape.team.web.WebKeys;

public class TaskHelper {
	public static void extendTaskInfo(List folderEntries) {
		if (folderEntries == null || folderEntries.size() == 0) {
			return;
		}
		Iterator it = folderEntries.iterator();
		while (it.hasNext()) {
			 Map entry = (Map) it.next();
			 
			 Object assignment = entry.get(ObjectKeys.TASK_FIELD_ASSIGNMENT);
			 if (assignment != null) {
				 Iterator usersIt = null;
				 if (SearchFieldResult.class.isAssignableFrom(assignment.getClass())) {
					SearchFieldResult sfr = (SearchFieldResult) entry.get(ObjectKeys.TASK_FIELD_ASSIGNMENT);
					usersIt = sfr.getValueArray().iterator();
				 } else if (String.class.isAssignableFrom(assignment.getClass())) {
					usersIt = Collections.singleton(assignment).iterator();
				 }
			 
				 Collection ids = new ArrayList();
				 while (usersIt.hasNext()) {
					 Long userId = new Long((String)usersIt.next());
					 ids.add(userId);
				 }
				 if (ids != null && ids.size()>0) {
					 List assignedUsers = ResolveIds.getPrincipals(ids);
					 entry.put(WebKeys.ENTRY_USER_LIST, assignedUsers);
				 }
			 }
			 Object event = entry.get(ObjectKeys.TASK_FIELD_EVENT);
			 entry.put(WebKeys.ENTRY_DUE_DATE, event);
		}
	}

}
