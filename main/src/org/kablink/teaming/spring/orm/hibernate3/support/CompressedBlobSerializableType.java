/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.hibernate.HibernateException;

import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;

public class CompressedBlobSerializableType extends org.springframework.orm.hibernate3.support.BlobSerializableType {

	private static final int OUTPUT_BYTE_ARRAY_INITIAL_SIZE = 1024;

	protected Object nullSafeGetInternal(
			ResultSet rs, String[] names, Object owner, LobHandler lobHandler)
			throws SQLException, IOException, HibernateException {

		InputStream is = lobHandler.getBlobAsBinaryStream(rs, names[0]);
		if (is != null) {
			GZIPInputStream gzis = new GZIPInputStream(is);
			ObjectInputStream ois = new ObjectInputStream(gzis);
			try {
				return ois.readObject();
			}
			catch (ClassNotFoundException ex) {
				throw new HibernateException("Could not deserialize BLOB contents", ex);
			}
			finally {
				ois.close();
			}
		}
		else {
			return null;
		}
	}

	protected void nullSafeSetInternal(
			PreparedStatement ps, int index, Object value, LobCreator lobCreator)
			throws SQLException, IOException {

		if (value != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(OUTPUT_BYTE_ARRAY_INITIAL_SIZE);
			GZIPOutputStream gzos = new GZIPOutputStream(baos);
			ObjectOutputStream oos = new ObjectOutputStream(gzos);
			try {
				oos.writeObject(value);
				gzos.finish();
				oos.flush();
				byte[] byteArray = baos.toByteArray();
				lobCreator.setBlobAsBytes(ps, index, byteArray);
			}
			finally {
				oos.close();
			}
		}
		else {
			lobCreator.setBlobAsBytes(ps, index, null);
		}
	}
	
}
