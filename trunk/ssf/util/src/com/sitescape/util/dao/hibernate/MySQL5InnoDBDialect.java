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
