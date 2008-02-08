/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
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
