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

import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GwtTeaming implements EntryPoint
{

  /**
   * This is the entry point method.
   */
  public void onModuleLoad()
  {
	  final Label 			tutorialPanelStateText = new Label();
      GwtRpcServiceAsync	gwtRpcService;

      // create an async callback to handle the result of the request to get the tutorial panel state:
      AsyncCallback<String> callback = new AsyncCallback<String>()
      {
    	  /**
    	   * 
    	   */
          public void onFailure(Throwable t)
          {
              // display error text if we can't get the tutorial panel state:
              tutorialPanelStateText.setText( "Failed to get the tutorial panel state" );
          }

          /**
           * 
           * @param result
           */
          public void onSuccess(String result)
          {
              // display the tutorial panel state in the label:
              tutorialPanelStateText.setText( "Tutorial Panel State--> " + result );
          }
      };

      tutorialPanelStateText.setText( "!!! Waiting to get the tutorial panel state!!!" );
      gwtRpcService = (GwtRpcServiceAsync) GWT.create( GwtRpcService.class );
      gwtRpcService.getTutorialPanelState( callback );
      
      RootPanel.get().add( tutorialPanelStateText );
  }// end onModuleLoad()
}// end GwtTeaming
