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

package org.kablink.teaming.gwt.client;

import com.google.gwt.i18n.client.Messages;


/**
 * This interface is used to retrieve strings from the file GwtTeamingMessages*.properties
 * @author jwootton
 *
 */
public interface GwtTeamingMessages extends Messages
{
	String _1();
	String _2();
	String _3();
	String _4();
	String _5();
	
	String cancel();
	String columnXWidth( int colNum );

	// Strings used with the Custom Jsp widget in the landing page editor.
	String customJspLabel();
	String customJspName();
	String customJspProperties();
	
	// Strings used with the Entry widget in the landing page editor
	String entryLabel();
	String entryProperties();
	
	String findEntry();
	
	// Strings used with the Graphic widget in the landing page editor.
	String graphicLabel();
	String graphicProperties();
	String noFileAttachmentsHint();
	String selectGraphicLabel();
	
	// lpe stands for Landing Page Editor
	String lpeCustomJSP();
	String lpeDeleteWidget();
	String lpeEntry();
	String lpeFolder();
	String lpeGraphic();
	String lpeHint();
	String lpeLinkEntry();
	String lpeLinkFolderWS();
	String lpeLinkURL();
	String lpeList();
	String lpeTable();
	String lpeUtilityElement();
	
	String linkToUrl();
	String linkToUrlLabel();
	String linkToUrlProperties();
	String linkToUrlUrl( String url );
	
	String listProperties();
	String numColumns();
	String ok();
	String openUrlInNewWnd();
	String showBorder();
	String showTitle();
	String tableProperties();
	String title();
	
	// Strings used with the Utility Element widget in the landing page editor.
	String utilityElementHint();
	String utilityElementLabel();
	String utilityElementProperties();
	String utilityElementLinkToAdminPage();
	String utilityElementLinkToMyWorkspace();
	String utilityElementLinkToShareFolderOrWorkspace();
	String utilityElementLinkToTrackFolderOrWorkspace();
	String utilityElementSignInForm();
	String utilityElementVideoTutorials();
	
	String testPanelState( String value );
	String testWaiting();
}// end GwtTeamingMessages
