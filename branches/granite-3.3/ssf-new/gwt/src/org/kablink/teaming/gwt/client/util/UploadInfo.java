/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to transfer information about what's to be uploaded to
 * the server.
 * 
 * @author drfoster@novell.com
 */
public class UploadInfo implements IsSerializable {
	private boolean	m_file;	// true -> This upload item is a file.  false -> It's a folder.
	private long	m_size;	// Size of the entity being uploaded.  Ignored for folders.
	private String	m_name;	// Name of the entity being uploaded.
	
	/**
	 * Constructor method.
	 * 
	 * GWT serialization requires a zero parameter constructor.
	 */
	public UploadInfo() {
		// Initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param name
	 * @param size
	 * @param file
	 */
	public UploadInfo(String name, long size, boolean file) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setName(name);
		setSize(size);
		setFile(file);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isFile()  {return m_file;}
	public long    getSize() {return m_size;}
	public String  getName() {return m_name;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setFile(boolean file) {m_file = file;}
	public void setSize(long    size) {m_size = size;}
	public void setName(String  name) {m_name = name;}
}
