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
package org.kablink.teaming.rest.servlet;

import org.kablink.teaming.util.SPropsUtil;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.management.openmbean.CompositeData;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * User: david
 * Date: 3/16/11
 * Time: 3:35 PM
 */
@ManagedResource
public class PrioritizedRequestManager {
    private Map<String, PriorityInformation> requestsByPriority;

    public PrioritizedRequestManager() {
        requestsByPriority = new HashMap<String, PriorityInformation>();
    }

    public void setMaxRequests(String priority, int max) {
        requestsByPriority.put(priority, new PriorityInformation(priority, max));
    }

    public boolean incrementInProgressOrFail(String priority) throws InvalidPriorityException {
        PriorityInformation priorityInfo = requestsByPriority.get(priority);
        if (priorityInfo==null) {
            throw new InvalidPriorityException(priority);
        }
        Long newCount = priorityInfo.requestsInProgress.incrementAndGet();
        if (newCount>priorityInfo.maxRequests) {
            priorityInfo.requestsInProgress.decrementAndGet();
            return false;
        }
        return true;
    }

    public void decrementInProgress(String priority) throws InvalidPriorityException {
        PriorityInformation priorityInfo = requestsByPriority.get(priority);
        if (priorityInfo==null) {
            throw new InvalidPriorityException(priority);
        }
        priorityInfo.requestsInProgress.decrementAndGet();
    }

    public void incrementProcessed(String priority) throws InvalidPriorityException {
        PriorityInformation priorityInfo = requestsByPriority.get(priority);
        if (priorityInfo==null) {
            throw new InvalidPriorityException(priority);
        }
        priorityInfo.processedRequests.incrementAndGet();
    }

    public void incrementRejected(String priority) throws InvalidPriorityException {
        PriorityInformation priorityInfo = requestsByPriority.get(priority);
        if (priorityInfo==null) {
            throw new InvalidPriorityException(priority);
        }
        priorityInfo.rejectedRequests.incrementAndGet();
    }

    public void incrementFailed(String priority) throws InvalidPriorityException {
        PriorityInformation priorityInfo = requestsByPriority.get(priority);
        if (priorityInfo==null) {
            throw new InvalidPriorityException(priority);
        }
        priorityInfo.failedRequests.incrementAndGet();
    }

    @ManagedAttribute
    public String [] getPriorityNames() {
        return requestsByPriority.keySet().toArray(new String [requestsByPriority.size()]);
    }

    @ManagedAttribute
    public CompositeData [] getPrioritySummaries() {
        PriorityInformation [] priorityInfos = requestsByPriority.values().toArray(new PriorityInformation[requestsByPriority.size()]);
        CompositeData [] data = new CompositeData[priorityInfos.length];
        for (int i=0; i<data.length; i++) {
            data[i] = priorityInfos[i].toCompositeData(null);
        }
        return data;
    }

    // Ugly...I shouldn't have to expose
    @ManagedAttribute
    public CompositeData getFileSummary() {
        PriorityInformation priorityInfo = requestsByPriority.get("FILE");
        if (priorityInfo!=null) {
            return priorityInfo.toCompositeData(null);
        }
        return null;
    }

    @ManagedAttribute
    public CompositeData getExpensiveSummary() {
        PriorityInformation priorityInfo = requestsByPriority.get("EXPENSIVE");
        if (priorityInfo!=null) {
            return priorityInfo.toCompositeData(null);
        }
        return null;
    }

    @ManagedAttribute
    public CompositeData getInexpensiveSummary() {
        PriorityInformation priorityInfo = requestsByPriority.get("INEXPENSIVE");
        if (priorityInfo!=null) {
            return priorityInfo.toCompositeData(null);
        }
        return null;
    }

    @ManagedOperation
    public Map<String, Object> getPrioritySummary(String priority) {
        PriorityInformation priorityInfo = requestsByPriority.get(priority);
        if (priorityInfo!=null) {
            return priorityInfo.toMap();
        }
        return null;
    }

    private static class PriorityInformation extends AbstractCompositeDataView {
        private String priority;
        private long maxRequests;
        private AtomicLong requestsInProgress;
        private AtomicLong processedRequests;
        private AtomicLong rejectedRequests;
        private AtomicLong failedRequests;

        private PriorityInformation(String priority, int maxRequests) {
            this.priority = priority;
            this.maxRequests = (long)maxRequests;
            requestsInProgress = new AtomicLong(0);
            processedRequests = new AtomicLong(0);
            rejectedRequests = new AtomicLong(0);
            failedRequests = new AtomicLong(0);
        }

        public Map<String, Object> toMap() {
            Map<String, Object> values = new LinkedHashMap<String, Object>();
            values.put("priority", priority);
            values.put("max_requests", maxRequests);
            values.put("in_progress", requestsInProgress.get());
            values.put("processed", processedRequests.get());
            values.put("rejected", rejectedRequests.get());
            values.put("failed", failedRequests.get());
            return values;
        }
    }
}
