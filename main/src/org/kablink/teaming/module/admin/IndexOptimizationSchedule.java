/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.domain.IndexNode;
import org.kablink.teaming.jobs.Schedule;
import org.kablink.teaming.jobs.ScheduleInfo;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.GetterUtil;
import org.kablink.util.StringUtil;

public class IndexOptimizationSchedule {

	public static final String NODES = "index.optimization.schedule.nodes";
	
	private static final String NODE_NAME_DELIMITER = ",";
	
	private ScheduleInfo scheduleInfo;
	
	public IndexOptimizationSchedule(ScheduleInfo scheduleInfo) {
		this.scheduleInfo = new ScheduleInfo(scheduleInfo.getDetails());
		this.scheduleInfo.setSchedule(scheduleInfo.getSchedule());
		this.scheduleInfo.setEnabled(scheduleInfo.isEnabled());
	}

	public String[] getNodeNames() {
		// Comma separated list of Lucene server node names - applicable to H/A system only.
		String nodes = GetterUtil.getString((String)getDetails().get(NODES), null);
		
		if(nodes != null && !nodes.equals(""))
			return StringUtil.split(nodes, NODE_NAME_DELIMITER);
		else
			return null;
	}
	
	public void setNodeNames(String[] nodeNames) {
		String nodes = null;
		if(nodeNames != null && nodeNames.length > 0)
			nodes = StringUtil.merge(nodeNames, NODE_NAME_DELIMITER);
		getDetails().put(NODES, nodes);
	}
	
	public Map<String, Boolean> getNodeSelectionMap() {
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		List<IndexNode> nodes = getAdminModule().retrieveIndexNodesHA();
		if(nodes != null) {
			for(IndexNode node:nodes) {
				if(!node.getUserModeAccess().equals(IndexNode.USER_MODE_ACCESS_NO_ACCESS) &&
						node.getNoDeferredUpdateLogRecords() &&
						isPreviouslySelected(node.getNodeName()))
					result.put(node.getNodeName(), Boolean.TRUE);
				else
					result.put(node.getNodeName(), Boolean.FALSE);
			}
		}
		return result;
	}
	
	private boolean isPreviouslySelected(String nodeName) {
		String[] names = getNodeNames();
		if(names != null) {
			for(String name:names) {
				if(name.equalsIgnoreCase(nodeName))	
					return true;
			}
		}
		return false;
	}
	
	public ScheduleInfo getScheduleInfo()
	{
		return scheduleInfo;
	}

	public boolean isEnabled()
	{
		return scheduleInfo.isEnabled();
	}

	public Schedule getSchedule()
	{
		return scheduleInfo.getSchedule();
	}

	protected Map getDetails()
	{
		return scheduleInfo.getDetails();
	}

	private AdminModule getAdminModule() {
		return (AdminModule) SpringContextUtil.getBean("adminModule");
	}
}
