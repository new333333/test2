package org.kablink.teaming.gwt.client.profile;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProfileClientUtil {
	
	/**
	 * Create the Profile Main Content Section, this creates the grid that
	 * should hold all of the user attribute name value pairs
	 * 
	 * @param cat
	 * @param grid
	 * @param rowCount
	 * @return
	 */
	public static int createProfileInfoSection(final ProfileCategory cat, Grid grid, int rowCount, boolean isEditable, boolean showHeading) {
		int row = rowCount;
		
		if(showHeading) {
			Label sectionHeader = new Label(cat.getTitle());
			sectionHeader.setStyleName("sectionHeading");

			grid.insertRow(row);
			grid.setWidget(row, 0, sectionHeader);

			// remove the bottom border from the section heading titles
			grid.getCellFormatter().setStyleName(row, 0, "sectionHeadingRBB");
			grid.getCellFormatter().setStyleName(row, 1, "sectionHeadingRBB");
			grid.getCellFormatter().setStyleName(row, 2, "sectionHeadingRBB");
			row = row + 1;
		}

		for (ProfileAttribute attr : cat.getAttributes()) {

			if(attr.getDataName().equals("picture")){
				continue;
			}
			
			Label title = new Label(attr.getTitle() + ":");
			title.setStyleName("attrLabel");
			Widget value = new ProfileAttributeWidget(attr, isEditable).getWidget();

			grid.insertRow(row);
			grid.setWidget(row, 0, title);
			grid.setWidget(row, 1, value);
			grid.getCellFormatter().setWidth(row, 1, "70%");
			grid.getCellFormatter().setHorizontalAlignment(row, 1,
					HasHorizontalAlignment.ALIGN_LEFT);

			row = row + 1;
		}

		return row;
	}

	
	/**
	 * Uses JavaScript native method to URI encode a string.
	 * 
	 * @param s
	 * 
	 * @return
	 */
	public static native String jsLaunchMiniBlog(String userId,int page, boolean popup) /*-{
		return window.top.ss_viewMiniBlog(userId, page, popup);
	}-*/;
	
}
