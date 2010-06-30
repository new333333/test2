package org.kablink.teaming.gwt.client.profile.widgets;

import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class GwtShowProfileView extends Composite {

	public GwtShowProfileView() {

		//Window.alert("Gwt Show Profile View");
		
		FlowPanel mainPanel = new FlowPanel();

		initSimpleUserProfileJS(this);
		
		// All composites must call initWidget() in their constructors.
		initWidget(mainPanel);
	}
	
	/*
	 * Invoke the Simple User Profile or Quick View
	 */
	private native void initSimpleUserProfileJS( GwtShowProfileView gwtShowProfileView ) /*-{
		$wnd.ss_invokeSimpleProfile = function( element, binderId, userName)
		{
			gwtShowProfileView.@org.kablink.teaming.gwt.client.profile.widgets.GwtShowProfileView::invokeSimpleProfile(Lcom/google/gwt/user/client/Element;Ljava/lang/String;Ljava/lang/String;)( element, binderId, userName);
		}//end ss_invokeSimpleProfile
	}-*/;
	
	/*
	 * Invoke the Simple Profile Dialog
	 */
	@SuppressWarnings("unused")
	private void invokeSimpleProfile(Element element, String binderId, String userName) {

		int posX = element.getAbsoluteLeft() + 80; 
		int posY = element.getAbsoluteTop() - 107;
		
		GwtQuickViewDlg dlg = new GwtQuickViewDlg(false, true, 0, 0, binderId, userName, element);
		GwtClientHelper.jsRegisterActionHandler(dlg);
		dlg.setPopupPosition(posX, posY);
		dlg.getElement().removeFromParent();
		dlg.show();
	}
}
