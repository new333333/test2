/*
 * Created on Feb 1, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.util.dao.hibernate;

import org.hibernate.dialect.FrontBaseDialect;

/**
 * @author Janet McCann
 *
 */
public class FrontBase4Dialect extends FrontBaseDialect {

	public boolean supportsIdentityColumns() {
		return true;
	}
	public String appendIdentitySelectToInsert(String insertSQL) {
		return insertSQL + "; values identity";
	}
	public String getIdentitySelectString() {
		return "values identity";
	}
	public String getIdentityInsertString() {
		return "default";
	}
	public String getIdentityColumnString() {
		return "default unique";
	}

}
