
package com.sitescape.ef.dao.util;

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
