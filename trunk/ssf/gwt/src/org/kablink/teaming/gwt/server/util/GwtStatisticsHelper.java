/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.gwt.server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.web.util.DefinitionHelper;

import com.sitescape.team.domain.Statistics;

/**
 * Helper methods for the GWT UI server code in dealing with Vibe
 * Statistics objects.
 *
 * @author drfoster@novell.com
 */
public class GwtStatisticsHelper {
	/*
	 * Inhibits this class from being instantiated. 
	 */
	private GwtStatisticsHelper() {
		// Nothing to do.
	}

	/**
	 * Returns a List<Map> of the entry definition maps for a given
	 * entry family contained in a Statistics object.
	 * 
	 * @param stats
	 * @param entryFamily
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Map> getEntryDefMaps(Statistics stats, String entryFamily) {
		// Allocate a List<Map> we can return.
		List<Map> reply = new ArrayList<Map>();

		// Does the Statistics object contain a values map?
		Map<String, Map> statsMap = stats.getValue();
		if (null != statsMap) {
			// Yes!  Scan that map's keys.
			Set<String> statsKeys = statsMap.keySet();
			for (String statsKey:  statsKeys) {
				// Is this a key for a definition of the given entry
				// family?
				Definition statsDef          = DefinitionHelper.getDefinition(statsKey);
				Document   statsDefDoc       = ((null == statsDef)    ? null : statsDef.getDefinition());
				String     statsDefDocFamily = ((null == statsDefDoc) ? null : DefinitionUtils.getFamily(statsDefDoc));
				if ((null != statsDefDocFamily) && statsDefDocFamily.equals(entryFamily)) {
					// Yes!  Is there a values map for that definition?
					Map defsMap = ((Map) statsMap.get(statsKey));
					if (null != defsMap) {
						// Yes!  Add it to the List<Map> we're
						// returning.
						reply.add(defsMap);
					}
				}
			}
		}
		
		// If we get here, reply refers to a List<Map> of the values
		// maps corresponding to an entry family in a Statistics
		// object.  Return it.
		return reply;
	}

	/**
	 * Returns the Statistics object stored as a custom attribute on a
	 * folder.
	 * 
	 * @param folder
	 * 
	 * @return
	 */
	public static Statistics getFolderStatistics(Folder folder) {
		// Does the folder contain a statistics custom attribute?
		Statistics reply = null;
		CustomAttribute caStats = folder.getCustomAttribute(Statistics.ATTRIBUTE_NAME);
		if (null != caStats) {
			// Yes!  Does that contain a statistics object?
			Object o = caStats.getValue();
			if ((null != o) && (o instanceof Statistics)) {
				// Yes!  Return it.
				reply = ((Statistics) o);
			}
		}
		
		// If we get here, reply refers to the Statistics object stored
		// on the folder or is null.  Return it.
		return reply;
	}
	
	public static Statistics getFolderStatistics(AllModulesInjected bs, Long folderId) {
		// Always use the initial form of the method.
		return getFolderStatistics(bs.getFolderModule().getFolder(folderId));
	}
}
