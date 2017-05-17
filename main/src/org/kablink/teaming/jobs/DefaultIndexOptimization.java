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
package org.kablink.teaming.jobs;

import java.util.List;

import org.kablink.teaming.domain.IndexNode;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.IndexOptimizationSchedule;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.StringUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class DefaultIndexOptimization extends SSCronTriggerJob implements IndexOptimization {

	/* (non-Javadoc)
	 * @see org.kablink.teaming.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
	@Override
	protected void doExecute(JobExecutionContext context)
			throws JobExecutionException {
		String[] nodeNames = null;
		String nodes = jobDataMap.getString(IndexOptimizationSchedule.NODES);
		if(nodes != null && !nodes.equals(""))
			nodeNames = StringUtil.split(nodes, ",");
		if(nodeNames != null) { // H/A setup
			// Invoke index optimization one index at a time, as opposed to invoking them
			// in parallel. This sequentialization helps minimize potential impact on user 
			// activities that might exist at the time of executing this operation.
			List<IndexNode> currentNodes = getAdminModule().retrieveIndexNodesHA();
			for(String nodeName:nodeNames) {
				IndexNode currentNode = findNode(currentNodes, nodeName);
				if(currentNode != null) {
					String userModeAccess = currentNode.getUserModeAccess();
					boolean noDeferredUpdateLogRecords = currentNode.getNoDeferredUpdateLogRecords();
					if(!userModeAccess.equals(IndexNode.USER_MODE_ACCESS_NO_ACCESS) &&
							noDeferredUpdateLogRecords) {
						// The node exists and is in a condition that allows this operation.
						logger.info("Invoking index optimization on " + currentNode.toString());
						doOptimize(nodeName);
					}
					else {
						// The node exists but is not in a condition that allows this operation.
						logger.info("Skipping index optimization on " + currentNode.toString() 
								+ " where userModeAccess=" + userModeAccess + 
								" and noDeferredUpdateLogRecords=" + noDeferredUpdateLogRecords);
					}
				}
				else {
					// The selected (and stored) node does not match any of the index nodes 
					// currently configured in the system. This means that the administrator
					// has re-configured the indexes and removed the previously selected node
					// from the system since the node was last stored in the scheduler.
					logger.warn("Skipping index optimization on " + nodeName + 
							" because the node is no longer found in the system");
				}
			}
		}
		else { // non-H/A setup
			logger.info("Invoking index optimization");
			doOptimize(null);
		}		
	}
	
	private IndexNode findNode(List<IndexNode> nodes, String nodeName) {
		for(IndexNode node:nodes) {
			if(node.getNodeName().equalsIgnoreCase(nodeName))
				return node; // found a match
		}
		return null; // no match
	}
	
	private void doOptimize(String nodeName) {
		try {
			getAdminModule().optimizeIndex((nodeName == null)? null : new String[]{nodeName});
		}
		catch(Exception e) {
			logger.error("Error executing index optimization on '" + nodeName + "'", e);
		}

	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.jobs.IndexOptimization#enable(boolean, java.lang.Long)
	 */
	@Override
	public void enable(boolean enable, Long zoneId) {
		enable(enable, new CronJobDescription(zoneId, zoneId.toString(),OPTIMIZATION_GROUP, OPTIMIZATION_DESCRIPTION));
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.jobs.IndexOptimization#getScheduleInfo(java.lang.Long)
	 */
	@Override
	public ScheduleInfo getScheduleInfo(Long zoneId) {
		return getScheduleInfo(new CronJobDescription(zoneId, zoneId.toString(),OPTIMIZATION_GROUP, OPTIMIZATION_DESCRIPTION));
	}

	/* (non-Javadoc)
	 * @see org.kablink.teaming.jobs.IndexOptimization#setScheduleInfo(org.kablink.teaming.jobs.ScheduleInfo)
	 */
	@Override
	public void setScheduleInfo(ScheduleInfo info) {
		setScheduleInfo(new CronJobDescription(info.getZoneId(), info.getZoneId().toString(),OPTIMIZATION_GROUP, OPTIMIZATION_DESCRIPTION), info);
	}

	private AdminModule getAdminModule() {
		return (AdminModule) SpringContextUtil.getBean("adminModule");
	}
}
