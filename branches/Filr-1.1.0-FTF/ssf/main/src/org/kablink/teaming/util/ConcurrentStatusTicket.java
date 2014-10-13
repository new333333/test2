/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.util;

import java.util.concurrent.atomic.AtomicLong;

import org.kablink.teaming.web.WebKeys;

public class ConcurrentStatusTicket implements StatusTicket {
	private long estimatedTotalNumberOfBindersToIndex;
	private AtomicLong totalCount = new AtomicLong();
	private AtomicLong currentCount = new AtomicLong();
	private StatusTicket statusTicket; // delegatee
	
	public ConcurrentStatusTicket(StatusTicket statusTicket) {
		this.statusTicket = statusTicket;
	}
	private String getTotalCountForDisplay() {
		return (getTotalCount() > estimatedTotalNumberOfBindersToIndex)? String.valueOf(getTotalCount())+"+" : String.valueOf(estimatedTotalNumberOfBindersToIndex);
	}
	public void incrementCurrentAndTotalCounts() {
		currentCount.incrementAndGet();
		totalCount.incrementAndGet();
	    statusTicket.setStatus(NLT.get("index.indexingBinder", new Object[] {String.valueOf(getCurrentCount()), getTotalCountForDisplay()}));
	}
	public void incrementTotalCount(int delta) {
		totalCount.addAndGet(delta);
	    statusTicket.setStatus(NLT.get("index.indexingBinder", new Object[] {String.valueOf(getCurrentCount()), getTotalCountForDisplay()}));
	}
	public void incrementCurrentCount() {
		currentCount.incrementAndGet();
	    statusTicket.setStatus(NLT.get("index.indexingBinder", new Object[] {String.valueOf(getCurrentCount()), getTotalCountForDisplay()}));
	}
	public void indexingCompleted() {
    	statusTicket.setStatus(NLT.get("index.finished") + "<br/><br/>" + NLT.get("index.indexingBinder",  new Object[] {String.valueOf(getCurrentCount()), String.valueOf(getTotalCount())}));
    	statusTicket.setState(WebKeys.AJAX_STATUS_STATE_COMPLETED);
	}
	public long getTotalCount() {
		return totalCount.get();
	}
	public long getCurrentCount() {
		return currentCount.get();
	}
	public StatusTicket getStatusTicket() {
		return statusTicket;
	}
	@Override
	public String toString() {
		return "(currentCount=" + getCurrentCount() + ", totalCount=" + getTotalCountForDisplay() + ")";
	}
	public String getId() {
		return statusTicket.getId();
	}
	public void setStatus(String status) {
		statusTicket.setStatus(status);
	}
	public String getStatus() {
		return statusTicket.getStatus();
	}
	public String getState() {
		return statusTicket.getState();
	}
	public void setState(String state) {
		statusTicket.setState(state);
	}
	public void done() {
		statusTicket.done();
	}
	public boolean isDone() {
		return statusTicket.isDone();
	}
	public void setEstimatedTotalNumberOfBindersToIndex(long estimatedTotalNumberOfBindersToIndex) {
		this.estimatedTotalNumberOfBindersToIndex = estimatedTotalNumberOfBindersToIndex;
	}
}
