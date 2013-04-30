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
package org.kablink.teaming.gwt.client.rpc.shared;

/**
 * This class holds all of the information necessary to execute the
 * 'Save Folder Entry Dialog Position' command.
 *
 * @author drfoster@novell.com
 */
public class SaveFolderEntryDlgPositionCmd extends VibeRpcCmd {
	private int	m_x;	//
	private int	m_y;	//
	private int	m_cx;	//
	private int	m_cy;	//
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public SaveFolderEntryDlgPositionCmd() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method. 
	 */
	public SaveFolderEntryDlgPositionCmd(int x, int y, int cx, int cy) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setX( x );
		setY( y );
		setCX(cx);
		setCY(cy);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @param
	 */
	public int getX()  {return m_x; }
	public int getY()  {return m_y; }
	public int getCX() {return m_cx;}
	public int getCY() {return m_cy;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setX( int x)  {m_x  = x; }
	public void setY( int y)  {m_y  = y; }
	public void setCX(int cx) {m_cx = cx;}
	public void setCY(int cy) {m_cy = cy;}
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.SAVE_FOLDER_ENTRY_DLG_POSITION.ordinal();
	}
}
