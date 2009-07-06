package org.kablink.teaming.gwt.client;

import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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
              tutorialPanelStateText.setText( result );
          }
      };

      gwtRpcService = (GwtRpcServiceAsync) GWT.create( GwtRpcService.class );
      gwtRpcService.getTutorialPanelState( callback );
      
      RootPanel.get().add( tutorialPanelStateText );
  }// end onModuleLoad()
}// end GwtTeaming
