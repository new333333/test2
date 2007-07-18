/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */

package com.sitescape.team.dao.util;
import java.util.LinkedList;
/**
 * @author Janet McCann
 *
 */
public class OrderBy {
	public static final int ASCENDING=1;
	public static final int DESCENDING=2;
	
	private LinkedList columns = new LinkedList();
	private LinkedList order = new LinkedList();
	
	public OrderBy() {
	}
	public OrderBy(String name) {
		addColumn(name);
	}
	public OrderBy(String name, int direction) {
		addColumn(name, direction);
	}
	public void addColumn(String name) {
		addColumn(name, ASCENDING);
	}
	public void addColumn(String name, int direction) {
		columns.add(name);
		if (direction == ASCENDING) {
			order.add(" asc");
		} else if (direction == DESCENDING) {
			order.add(" desc");
		} else {
			throw new IllegalArgumentException("direction invalid");
		}
	}
	public String getOrderByClause(String alias) {
		StringBuffer buf = new StringBuffer();
		for (int i=0; i<columns.size(); ++i) {
			if (i != 0) buf.append(",");
			buf.append(alias + "." + (String)columns.get(i) + (String)order.get(i));			
		}
		return buf.toString();
	}
	
}
