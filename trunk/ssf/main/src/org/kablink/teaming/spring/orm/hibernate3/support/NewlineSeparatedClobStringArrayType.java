/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.spring.orm.hibernate3.support;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.transaction.TransactionManager;

import org.hibernate.HibernateException;
import org.kablink.util.StringUtil;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.hibernate3.support.AbstractLobType;

/**
 * @author jong
 *
 */
public class NewlineSeparatedClobStringArrayType extends AbstractLobType {

	private static final String NEWLINE = "\n"; // Do NOT use OS-dependent newline character 
	
	public NewlineSeparatedClobStringArrayType() {
		super();
	}
	
	protected NewlineSeparatedClobStringArrayType(LobHandler lobHandler, TransactionManager jtaTransactionManager) {
		super(lobHandler, jtaTransactionManager);
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#sqlTypes()
	 */
	@Override
	public int[] sqlTypes() {
		return new int[] {Types.CLOB};
	}

	/* (non-Javadoc)
	 * @see org.hibernate.usertype.UserType#returnedClass()
	 */
	@Override
	public Class returnedClass() {
		return String[].class;
	}

	/* (non-Javadoc)
	 * @see org.springframework.orm.hibernate3.support.AbstractLobType#nullSafeGetInternal(java.sql.ResultSet, java.lang.String[], java.lang.Object, org.springframework.jdbc.support.lob.LobHandler)
	 */
	@Override
	protected Object nullSafeGetInternal(ResultSet rs, String[] names,
			Object owner, LobHandler lobHandler) throws SQLException,
			IOException, HibernateException {
		String str = lobHandler.getClobAsString(rs, names[0]);
		if(str != null)
			return StringUtil.split(str, NEWLINE);
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see org.springframework.orm.hibernate3.support.AbstractLobType#nullSafeSetInternal(java.sql.PreparedStatement, int, java.lang.Object, org.springframework.jdbc.support.lob.LobCreator)
	 */
	@Override
	protected void nullSafeSetInternal(PreparedStatement ps, int index,
			Object value, LobCreator lobCreator) throws SQLException,
			IOException, HibernateException {
		String[] array = (String[]) value;
		if(array != null) {
			StringBuilder sb = new StringBuilder();
			for(String s:array) {
				if(s.contains(NEWLINE))
					throw new IOException("A string must not contain newline character");
				sb.append(s).append(NEWLINE);
			}
			lobCreator.setClobAsString(ps, index, sb.toString());
		}
		else {
			lobCreator.setClobAsString(ps, index, null);
		}
	}

}
