/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.util.BinderInfo;


/**
 * This class holds all of the information necessary to execute the
 * 'get binder has other components' command.
 * 
 * @author drfoster@novell.com
 */
public class GetBinderHasOtherComponentsCmd extends VibeRpcCmd {
	private BinderInfo	m_binderInfo;	//
	
	/*
	 * For GWT serialization, must have a zero parameter constructor
	 * method.
	 */
	private GetBinderHasOtherComponentsCmd() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param binderInfo
	 */
	public GetBinderHasOtherComponentsCmd(BinderInfo binderInfo) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		m_binderInfo = binderInfo;
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public BinderInfo getBinderInfo() {return m_binderInfo;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setBinderInfo(BinderInfo binderInfo) {m_binderInfo = binderInfo;}
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.GET_BINDER_HAS_OTHER_COMPONENTS.ordinal();
	}
}
