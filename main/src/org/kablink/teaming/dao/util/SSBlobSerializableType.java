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
/*
 * Created on Dec 9, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.dao.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.kablink.teaming.domain.SSBlobSerializable;
import org.kablink.util.jdbc.support.lob.NoLazyLobs;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.hibernate3.support.AbstractLobType;


/**
 * @author Janet McCann
 *
 * This class handles lazy loading of blobs which contain serialized objects.
 * We don't retrieve the blob data until needed.
 * Wrap blob in SSBlob. 
 */
public class SSBlobSerializableType extends AbstractLobType {

	/**
	 * Initial size for ByteArrayOutputStreams used for serialization output.
	 * <p>If a serialized object is larger than these 1024 bytes, the size of
	 * the byte array used by the output stream will be doubled each time the
	 * limit is reached.
	 */
    	public static final int OUTPUT_BYTE_ARRAY_INITIAL_SIZE = 1024;
	 
		public SSBlobSerializableType() {
			super();

		}
	    public int[] sqlTypes() {
	    	return new int[] {Types.BLOB};
	    }

	    public Class returnedClass() {
	    	return SSBlobSerializable.class;
	    }
	    

		protected Object nullSafeGetInternal(ResultSet rs, String[] names, Object owner, LobHandler lobHandler)
		throws SQLException, IOException, HibernateException {
			if (!(lobHandler instanceof NoLazyLobs)) {
				return new SSBlobSerializable(rs.getBlob(names[0]));
			} else {
				InputStream is = lobHandler.getBlobAsBinaryStream(rs, names[0]);
				if (is != null) {
					ObjectInputStream ois = new ObjectInputStream(is);
					try {
						return new SSBlobSerializable(ois.readObject());
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
		}

		protected void nullSafeSetInternal(PreparedStatement ps, int index, Object value, LobCreator lobCreator)
				throws SQLException,IOException {
			Object obj = null;
			if (value != null)
				obj = ((SSBlobSerializable)value).getValue();
			if (obj != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream(OUTPUT_BYTE_ARRAY_INITIAL_SIZE);
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				try {
					oos.writeObject(obj);
					oos.flush();
					lobCreator.setBlobAsBytes(ps, index, baos.toByteArray());
				}
				finally {
					oos.close();
				}
			}
			else {
				lobCreator.setBlobAsBytes(ps, index, null);
			}
	 
		}  
		//make sure blob is loaded for storage in cache
		public Serializable disassemble(Object value) throws HibernateException {
			if (value != null) {
				((SSBlobSerializable)value).getValue();
			}
			return (Serializable) value;
		}
	}