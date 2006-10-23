//copied from hibernate 3.2 distribution
package com.sitescape.util.dao.hibernate;
import org.hibernate.dialect.MySQL5Dialect;
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

}
