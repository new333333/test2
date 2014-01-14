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

package org.kablink.teaming.gwt.client.util.runasync;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;

/**
 * This class holds the data needed to execute a "Create" runAsync command
 * for a dialog.
 * @author jwootton
 *
 */
public class RunAsyncCreateDlgParams implements RunAsyncParams
{
	private EditCanceledHandler m_editCanceledHandler;
	private EditSuccessfulHandler m_editSuccessfulHandler;
	private Boolean m_autoHide;
	private Boolean m_modal;
	private Integer m_x;
	private Integer m_y;
	private Integer m_width;
	private Integer m_height;
	
	/**
	 * 
	 */
	public RunAsyncCreateDlgParams()
	{
	}

	/**
	 * 
	 */
	public Boolean getAutoHide()
	{
		return m_autoHide;
	}

	/**
	 * 
	 */
	public EditCanceledHandler getEditCanceledHandler()
	{
		return m_editCanceledHandler;
	}

	/**
	 * 
	 */
	public EditSuccessfulHandler getEditSuccessfulHandler()
	{
		return m_editSuccessfulHandler;
	}

	/**
	 * 
	 */
	public Integer getHeight()
	{
		return m_height;
	}

	/**
	 * 
	 */
	public Integer getLeft()
	{
		return m_x;
	}

	/**
	 * 
	 */
	public Boolean getModal()
	{
		return m_modal;
	}

	/**
	 * 
	 */
	public Integer getTop()
	{
		return m_y;
	}

	/**
	 * 
	 */
	public Integer getWidth()
	{
		return m_width;
	}

	/**
	 * 
	 */
	public void setAutoHide( Boolean autoHide )
	{
		m_autoHide = autoHide;
	}

	/**
	 * 
	 */
	public void setEditCanceledHandler( EditCanceledHandler handler )
	{
		m_editCanceledHandler = handler;
	}

	/**
	 * 
	 */
	public void setEditSuccessfulHandler( EditSuccessfulHandler handler )
	{
		m_editSuccessfulHandler = handler;
	}

	/**
	 * 
	 */
	public void setHeight( Integer height )
	{
		m_height = height;
	}

	/**
	 * 
	 */
	public void setLeft( Integer left )
	{
		m_x = left;
	}

	/**
	 * 
	 */
	public void setModal( Boolean modal )
	{
		m_modal = modal;
	}

	/**
	 * 
	 */
	public void setTop( Integer top )
	{
		m_y = top;
	}

	/**
	 * 
	 */
	public void setWidth( Integer width )
	{
		m_width = width;
	}
}
