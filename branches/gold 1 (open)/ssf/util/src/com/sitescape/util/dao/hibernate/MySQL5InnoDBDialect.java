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
//copied and extended from hibernate 3.2 distribution
package com.sitescape.util.dao.hibernate;
import org.hibernate.dialect.MySQL5Dialect;
import com.sitescape.util.StringUtil;
/**
 * @author Gavin King, Scott Marlow
 */
public class MySQL5InnoDBDialect extends MySQL5Dialect {

	public boolean supportsCascadeDelete() {
		return true;
	}

	public String getTableTypeString() {
		return " ENGINE=InnoDB";
	}

	public boolean hasSelfReferentialForeignKeyBug() {
		return true;
	}
	
	//The inherited behavior adds an index on each foreign key
	//don't know why, but it adds lots of unused junk
	public String getAddForeignKeyConstraintString(
			String constraintName, 
			String[] foreignKey, 
			String referencedTable, 
			String[] primaryKey, boolean referencesPrimaryKey
	) {
		return new StringBuffer(30)
			.append(" add constraint ")
			.append(constraintName)
			.append(" foreign key (")
			.append(StringUtil.merge(foreignKey, ", "))
			.append(") references ")
			.append(referencedTable)
			.append(" (")
			.append( StringUtil.merge(primaryKey, ", ") )
			.append(')')
			.toString();
	}
}
