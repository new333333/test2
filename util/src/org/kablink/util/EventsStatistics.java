/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.util;

import java.util.TreeMap;

/**
 * ?
 * 
 * @author ?
 */
public class EventsStatistics {
	// protected by "this"
	private TreeMap<String, EventStatistics> stats = new TreeMap<String, EventStatistics>();
	
	private long startTimeMillis; // The time this object is instantiated
	
	private volatile boolean enabled;

	public EventsStatistics() {
		startTimeMillis = System.currentTimeMillis();
		enabled = false;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public void enable() {
		enabled = true;
	}
	
	public void disable() {
		enabled = false;
	}
	
	public synchronized void clear() {
		stats.clear();
		startTimeMillis = System.currentTimeMillis();
	}
	
	public void addEvent(String eventName, long eventTimeNanos) {
		if(enabled) {
			EventStatistics es = getEventStatistics(eventName);
			es.add(eventTimeNanos);
		}
	}
	
	public synchronized String asString() {
		String NL = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder();
		sb.append("Elapsed time: " + (System.currentTimeMillis()-startTimeMillis)/1000 + " seconds");
		int i = 1;
		for(EventStatistics es:stats.values()) {
			sb.append(NL).append(i++ + ". ");
			es.appendAsString(sb);
		}
		return sb.toString();
	}
	
	private EventStatistics getEventStatistics(String eventName) {
		EventStatistics es = stats.get(eventName);
		if(es != null)
			return es;
		else 
			return makeEventStatistics(eventName);
	}
	
	private synchronized EventStatistics makeEventStatistics(String eventName) {
		EventStatistics es = stats.get(eventName);
		if(es == null) {
			es = new EventStatistics(eventName);
			stats.put(eventName, es);
		}
		return es;
	}
	
	private class EventStatistics {
		private String name; // Event type name
		private long count; // Number of occurrences of this type of event
		private long totalTimeNanos; // Total accumulated time in Nanos for this type of event
		EventStatistics(String name) {
			this.name = name;
		}
		synchronized void add(long eventTimeNanos) {
			totalTimeNanos += eventTimeNanos;
			count++;
		}
		// Since we synchronize on the owning EventsStatistics object before calling this, 
		// we don't need additional synchronization on this object.
		void appendAsString(StringBuilder sb) {
			sb.append(name)
			.append(": count=")
			.append(count)
			.append(", total time=")
			.append(((double)totalTimeNanos)/1000000)
			.append(" (ms)")
			.append(", average time=")
			.append((((double)totalTimeNanos)/count)/1000000)
			.append(" (ms)");
		}
	}
}
