package org.kablink.teaming.gwt.client.presence;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

public class PresenceControl extends Composite {
	String m_binderId;
	boolean m_bShowStatusText;
	boolean m_bClickStartsIm;
	boolean m_bHideIfUnknown;
	FlowPanel panel;
	
	public PresenceControl(String binderId, boolean bShowStatusText, boolean bClickStartsIm, boolean bHideIfUnknown) {
		m_binderId = binderId;
		m_bShowStatusText = bShowStatusText;
		m_bClickStartsIm = bClickStartsIm;
		m_bHideIfUnknown = bHideIfUnknown;

		panel = new FlowPanel();

		getPresenceInfo();

		initWidget(panel);
	}

	private void getPresenceInfo() {
		final Anchor presenceA = new Anchor();

		AsyncCallback<GwtPresenceInfo> callback = new AsyncCallback<GwtPresenceInfo>() {
			public void onFailure(Throwable t) {
				panel.setVisible(false);
			}

			public void onSuccess(GwtPresenceInfo pi) {
				Image presenceImage;
				String statusText = pi.getStatusText();
				switch (pi.getStatus()) {
					case GwtPresenceInfo.STATUS_AVAILABLE:
						presenceImage = new Image(GwtTeaming.getImageBundle().presenceAvailable16());
						if (statusText == null) {
							statusText = GwtTeaming.getMessages().presenceAvailable();
						}
						break;
					case GwtPresenceInfo.STATUS_IDLE:
						presenceImage = new Image(GwtTeaming.getImageBundle().presenceAway16());
						if (statusText == null) {
							statusText = GwtTeaming.getMessages().presenceIdle();
						}
						break;
					case GwtPresenceInfo.STATUS_AWAY:
						presenceImage = new Image(GwtTeaming.getImageBundle().presenceAway16());
						if (statusText == null) {
							statusText = GwtTeaming.getMessages().presenceAway();
						}
						break;
					case GwtPresenceInfo.STATUS_BUSY:
						presenceImage = new Image(GwtTeaming.getImageBundle().presenceBusy16());
						if (statusText == null) {
							statusText = GwtTeaming.getMessages().presenceBusy();
						}
						break;
					case GwtPresenceInfo.STATUS_OFFLINE:
						presenceImage = new Image(GwtTeaming.getImageBundle().presenceOffline16());
						if (statusText == null) {
							statusText = GwtTeaming.getMessages().presenceOffline();
						}
						break;
					default:
						statusText = "";
						presenceImage = new Image(GwtTeaming.getImageBundle().presenceUnknown16());
				}
				if (pi.getStatus() != GwtPresenceInfo.STATUS_UNKNOWN || !m_bHideIfUnknown) {
					presenceA.addStyleName("presenceImgA");
					presenceA.getElement().appendChild(presenceImage.getElement());
					presenceA.setVisible(true);
					if (m_bClickStartsIm) {
						presenceA.addClickHandler(new InstantMessageClickHandler(m_binderId));
						presenceA.setTitle(GwtTeaming.getMessages().qViewInstantMessageTitle() + " (" + statusText + ")");
					} else {
						presenceA.setTitle(statusText);
					}
					panel.add(presenceA);
					if (m_bShowStatusText && statusText != null && statusText.length() > 0) {
						InlineLabel statusTextLabel = new InlineLabel("(" + statusText + ")");
						panel.add(statusTextLabel);
					}
				} else {
					panel.setVisible(false);
				}
			}
		};

		GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();		
		rpcService.getPresenceInfo(m_binderId, callback);
	}
}
