package org.kablink.teaming.gwt.client.presence;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.Window;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

public class InstantMessageClickHandler implements ClickHandler {
	String m_binderId;

	public InstantMessageClickHandler(String binderId){
		m_binderId = binderId;
	}

	/* Open IM URL */
	public void onClick(ClickEvent event) {
		GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();		
		rpcService.getImUrl(m_binderId, 
							new AsyncCallback<String>() {
								public void onFailure(Throwable t) {
									Window.alert("Error: " + t.toString());
								}

								public void onSuccess(String url) {
									if (url != null && url.length() > 0) {
										GwtClientHelper.jsLoadUrlInContentFrame(url);
									}
								}
							});
	}
}
