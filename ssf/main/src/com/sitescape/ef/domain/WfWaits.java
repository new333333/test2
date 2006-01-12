package com.sitescape.ef.domain;

import java.util.List;

/*********************************************************************
 * Helper classes
 * @author Janet McCann
 *
 */
public class WfWaits {
   	private String toStateName;
	private List threads;
	public WfWaits (String toStateName,  List threads) {
		this.toStateName = toStateName;
		this.threads = threads;
	}
	public String getToStateName() {
		return toStateName;
	}
	public List getThreads() {
		return threads;
	}

}