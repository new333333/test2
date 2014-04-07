/*
 * Copyright © 2009-2010 Novell, Inc.  All Rights Reserved.
 *
 * THIS WORK IS SUBJECT TO U.S. AND INTERNATIONAL COPYRIGHT LAWS AND TREATIES.  IT MAY NOT BE USED, COPIED,
 * DISTRIBUTED, DISCLOSED, ADAPTED, PERFORMED, DISPLAYED, COLLECTED, COMPILED, OR LINKED WITHOUT NOVELL'S
 * PRIOR WRITTEN CONSENT.  USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT THE
 * PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 *
 * NOVELL PROVIDES THE WORK "AS IS," WITHOUT ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING WITHOUT THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND NON-INFRINGEMENT. NOVELL, THE
 * AUTHORS OF THE WORK, AND THE OWNERS OF COPYRIGHT IN THE WORK ARE NOT LIABLE FOR ANY CLAIM, DAMAGES,
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT, OR OTHERWISE, ARISING FROM, OUT OF, OR IN
 * CONNECTION WITH THE WORK OR THE USE OR OTHER DEALINGS IN THE WORK.
 */
package com.novell.aca.api.util;

import com.novell.aca.util.AbstractCompositeDataView;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import javax.management.openmbean.CompositeData;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * User: david
 * Date: 3/16/11
 * Time: 3:35 PM
 */
@ManagedResource
public class PrioritizedRequestManager {
    private Map<String, PriorityInformation> requestsByPriority;

    @Required
    public void setAllowedRequestsByPriority(Map<String, Integer> allowedRequestsByPriority) {
        requestsByPriority = new HashMap<String, PriorityInformation>();
        for (Map.Entry<String, Integer> entry : allowedRequestsByPriority.entrySet()) {
            requestsByPriority.put(entry.getKey(), new PriorityInformation(entry.getKey(), entry.getValue()));
        }
    }

    public boolean incrementInProgressOrFail(String priority) throws InvalidPriorityException {
        PriorityInformation priorityInfo = requestsByPriority.get(priority);
        if (priorityInfo==null) {
            throw new InvalidPriorityException(priority);
        }
        Integer newCount = priorityInfo.requestsInProgress.incrementAndGet();
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
        private int maxRequests;
        private AtomicInteger requestsInProgress;
        private AtomicInteger processedRequests;
        private AtomicInteger rejectedRequests;
        private AtomicInteger failedRequests;

        private PriorityInformation(String priority, int maxRequests) {
            this.priority = priority;
            this.maxRequests = maxRequests;
            requestsInProgress = new AtomicInteger(0);
            processedRequests = new AtomicInteger(0);
            rejectedRequests = new AtomicInteger(0);
            failedRequests = new AtomicInteger(0);
        }

        @Override
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
