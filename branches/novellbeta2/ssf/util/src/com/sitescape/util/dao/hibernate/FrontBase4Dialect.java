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
