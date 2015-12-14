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
package org.kablink.teaming.gwt.client.rpc.shared;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used in GWT RPC calls to represent information about HTML5
 * uploading.
 * 
 * @author drfoster@novell.com
 */
public class Html5SpecsRpcResponseData implements IsSerializable, VibeRpcResponseData {
	private boolean			m_encode;			//
	private boolean			m_md5HashValidate;	//
	private Html5UploadMode	m_mode;				//
	private int				m_varBlobsPerFile;	//
	private long			m_fixedBlobSize;	//
	private long			m_varMinBlobSize;	//
	private long			m_varMaxBlobSize;	//
	
	/**
	 * Enumeration that defines the HTML5 upload mode.
	 */
	public enum Html5UploadMode implements IsSerializable {
		FIXED,
		VARIABLE,
	}
	
	/**
	 * Constructor method.
	 * 
	 * Zero parameter constructor as per GWT serialization
	 * requirements.
	 */
	public Html5SpecsRpcResponseData() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param encode
	 * @param md5HashValidate
	 * @param fixedBlobSize
	 */
	public Html5SpecsRpcResponseData(boolean encode, boolean md5HashValidate, long fixedBlobSize) {
		// Initialize this object...
		this();

		// ...store the parameters...
		setMode(           Html5UploadMode.FIXED);
		setEncode(         encode               );
		setMd5HashValidate(md5HashValidate      );
		setFixedBlobSize(  fixedBlobSize        );
		
		// ...and initialize the fields that don't apply.
		setVariableBlobsPerFile(-1);
		setVariableMinBlobSize( -1);
		setVariableMaxBlobSize( -1);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param encode
	 * @param md5HashValidate
	 * @param blobsPerFile
	 */
	public Html5SpecsRpcResponseData(boolean encode, boolean md5HashValidate, int varBlobsPerFile, long varMinBlobSize, long varMaxBlobSize) {
		// Initialize this object...
		this();
		
		// ...store the parameters...
		setMode(                Html5UploadMode.VARIABLE);
		setEncode(              encode                  );
		setMd5HashValidate(     md5HashValidate         );
		setVariableBlobsPerFile(varBlobsPerFile         );
		setVariableMinBlobSize( varMinBlobSize          );
		setVariableMaxBlobSize( varMaxBlobSize          );
		
		// ...and initialize the fields that don't apply.
		setFixedBlobSize(-1);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean         isEncode()                {return m_encode;         }
	public boolean         isMd5HashValidate()       {return m_md5HashValidate;}
	public Html5UploadMode getMode()                 {return m_mode;           }
	public int             getVariableBlobsPerFile() {return m_varBlobsPerFile;}
	public long            getFixedBlobSize()        {return m_fixedBlobSize;  }
	public long            getVariableMinBlobSize()  {return m_varMinBlobSize; }
	public long            getVariableMaxBlobSize()  {return m_varMaxBlobSize; }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setEncode(              boolean         encode)          {m_encode          = encode;         }
	public void setMd5HashValidate(     boolean         md5HashValidate) {m_md5HashValidate = md5HashValidate;}
	public void setMode(                Html5UploadMode mode)            {m_mode            = mode;           }
	public void setVariableBlobsPerFile(int             varBlobsPerFile) {m_varBlobsPerFile = varBlobsPerFile;}
	public void setFixedBlobSize(       long            fixedBlobSize)   {m_fixedBlobSize   = fixedBlobSize;  }
	public void setVariableMinBlobSize( long            varMinBlobSize)  {m_varMinBlobSize  = varMinBlobSize; }
	public void setVariableMaxBlobSize( long            varMaxBlobSize)  {m_varMaxBlobSize  = varMaxBlobSize; }
	
	/**
	 * Returns true if we're uploading fixed sized blobs and false if
	 * we're uploaded variable sized blobs.
	 * 
	 * @return
	 */
	public boolean isFixed() {
		return Html5UploadMode.FIXED.equals(getMode());
	}
	
	/**
	 * Returns true if we're uploading variable sized blobs and false
	 * if we're uploaded fixed sized blobs.
	 * 
	 * @return
	 */
	public boolean isVariable() {
		return Html5UploadMode.VARIABLE.equals(getMode());
	}
}
