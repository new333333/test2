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

/**
 * @author Janet McCann
 *
 */
public class ObjectControls {
	private String [] attrNames;
	private Class clazz;
	
	public ObjectControls(Class clazz) {
		this.clazz = clazz;
	}
	public ObjectControls(Class clazz, String[] attrNames) {
		this.clazz = clazz;
		this.attrNames = attrNames;
	}
	public String[] getAttrNames() {
		return this.attrNames;
	}
	public void setAttrNames(String[] attrNames) {
		this.attrNames = attrNames;
	}
	public StringBuffer getSelectAndFrom(String alias) {
		StringBuffer query = getSelect(alias);
		query.append(" from " + alias + " in class " + clazz.getName());
		return query;
	}
	public StringBuffer getSelect(String alias) {
		StringBuffer query = new StringBuffer();
		if ((attrNames!=null) && (attrNames.length != 0)) {
  	 		query.append("select ");
   	 		for (int i=0; i<attrNames.length; ++i) {
   	 			query.append(alias + "." + attrNames[i] + ",");
   	 		}
   	 		//trim off trailing ,
   	 		query.deleteCharAt(query.length() -1);
		}
		return query;
	}
}
