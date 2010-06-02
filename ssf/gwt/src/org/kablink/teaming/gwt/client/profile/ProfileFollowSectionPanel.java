package org.kablink.teaming.gwt.client.profile;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.mainmenu.SavedSearchInfo;
import org.kablink.teaming.gwt.client.util.ActionTrigger;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class ProfileFollowSectionPanel extends ProfileSectionPanel {

	private List<SavedSearchInfo> fList;

	public ProfileFollowSectionPanel(ProfileRequestInfo profileRequestInfo, String title, ActionTrigger trigger) {
		
		super(profileRequestInfo, title, trigger);
		
		setStyleName("tracking-subhead");
		
		//populateFollowingLinks();

	}

//	private void populateFollowingLinks() {
//		GwtTeaming.getRpcService().getSavedSearches(new AsyncCallback<List<ProfileFollowInfo>>() {
//			public void onFailure(Throwable t) {
//				Window.alert(t.toString());
//			}
//			public void onSuccess(List<ProfileFollowInfo> followingList)  {
//				fList = followingList;
//				buildFollowingLinks();
//			}
//		});
//	}
//
//	private void buildFollowingLinks() {
//		
//	}
	
}
