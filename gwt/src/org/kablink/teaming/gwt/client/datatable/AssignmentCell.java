/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.datatable;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.binderviews.folderdata.ColumnWidth;
import org.kablink.teaming.gwt.client.presence.GwtPresenceInfo;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.util.AssignmentInfo.AssigneeType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.AssignmentInfo;
import org.kablink.teaming.gwt.client.widgets.GroupMembershipPopup;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Data table cell that represents a list of assignments (for calendar
 * entries, tasks or milestones.)
 * 
 * @author drfoster@novell.com
 */
public class AssignmentCell extends AbstractCell<List<AssignmentInfo>> {
	private boolean							m_isIE;			//
	private ColumnWidth						m_cw;			//
	private GwtTeamingDataTableImageBundle	m_images;		//
	private GwtTeamingFilrImageBundle		m_filrImages;	//

	private final static boolean	USE_ELLIPSIS_WITH_PX_SIZES	= false;		// Controls whether styles to use an '...' to truncate long names are added with the column size is in pixels.
	private final static int		ASSIGNEE_WIDTH_ADJUST		= (25 + 16);	// 25 for the image + 16 for the padding on the <td>.
	private final static int		MIN_ASSIGNEE_WIDTH			= 50;			// The minimum width we'll allow an assignee label to be (no ellipsis below this.)
	
	/*
	 * Inner class used to encapsulate information about an event
	 * received by an AssignmentCell.
	 */
	private static class EventInfo {
		private boolean	m_isPresenceControl;	//
		private boolean	m_isPresenceLabel;		//
		private int		m_assignmentIndex;		//
		private int		m_stepsToTD;			//
		private String	m_eventTag        = "";	//
		private String	m_widgetAttribute = "";	//

		/**
		 * Constructor method.
		 * 
		 * @param parent
		 * @param event
		 */
		public EventInfo(Element parent, NativeEvent event) {
			// Scan up the event element's parentage until we find
			// a <TD>.
			Element eventTarget = Element.as(event.getEventTarget());
			m_eventTag          = eventTarget.getTagName();
			while (!(m_eventTag.equalsIgnoreCase("td"))) {
				// Is this one of our presence widgets?
				m_widgetAttribute = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
				if ((null != m_widgetAttribute) && (0 < m_widgetAttribute.length())) {
					// Yes!  Is it the image or label?
					m_isPresenceControl =                            m_widgetAttribute.startsWith(VibeDataTableConstants.CELL_WIDGET_PRESENCE       );
					m_isPresenceLabel   = ((!m_isPresenceControl) && m_widgetAttribute.startsWith(VibeDataTableConstants.CELL_WIDGET_PRESENCE_LABEL));

					// What's index to the AssignmentInfo in the list?
					int    pPos            = m_widgetAttribute.indexOf(".");
					String assignmentIndex = m_widgetAttribute.substring(pPos + 1);
					m_assignmentIndex      = Integer.parseInt(assignmentIndex);

					// We're done looking!  Break out of the scan loop.
					break;
				}

				// We haven't found a presence widget yet.  Move up a
				// level.
				m_stepsToTD += 1;
				eventTarget  = eventTarget.getParentElement();
				m_eventTag   = eventTarget.getTagName();
			}
			
			// debugDump();
		}
		
		/*
		 * Displays the contents of the EventInfo in an alert().
		 */
		@SuppressWarnings("unused")
		private void debugDump() {
			Window.alert(
				  "T:" + m_eventTag          +
				", A:" + m_isPresenceControl +
				", L:" + m_isPresenceLabel   +
				", I:" + m_assignmentIndex   +
				", W:" + m_widgetAttribute   +
				", S:" + m_stepsToTD);
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public int     getAssignmentIndex() {return m_assignmentIndex;                         }
		public boolean isPresence()         {return (isPresenceControl() || isPresenceLabel());}
		public boolean isPresenceControl()  {return m_isPresenceControl;                       }
		public boolean isPresenceLabel()    {return m_isPresenceLabel;                         }
	}
	
	/**
	 * Constructor method.
	 */
	public AssignmentCell(ColumnWidth cw) {
		// Sink the events we need to process assignments...
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);

		// ...store the parameter...
		m_cw = cw;
		
		// ...and initialize everything else.
		m_images     = GwtTeaming.getDataTableImageBundle();
		m_filrImages = GwtTeaming.getFilrImageBundle();
		m_isIE       = GwtClientHelper.jsIsIE();
	}

	/*
	 * Creates a Label with the appropriate styles.
	 */
	private Label createAssigneeLabel(String title, String hover) {
		// Create the label with the base styles.
		Label reply = new Label(title);
		reply.addStyleName("vibe-dataTableAssignment-label vibe-dataTableAssignment-enabled");
		if (m_isIE) {
			reply.addStyleName("vibe-dataTableAssignment-labelIE");
		}

		// If we have hover text for the label...
		if (GwtClientHelper.hasString(hover)) {
			// ...add it as the label's title.
			reply.setTitle(hover);
		}

		// Do we needs to style it to use ellipsis with long names?
		boolean useEllipsis = USE_ELLIPSIS_WITH_PX_SIZES;
		if (useEllipsis && m_cw.isPX()) {
			// Yes!  Since we it's a fixed width, add styles so that if
			// it's long, the name will get truncated and padded with
			// ellipsis.
			reply.addStyleName("vibe-dataTableAssignment-labelEllipsis");
			int pxWidth = (((int) m_cw.getWidth()) - ASSIGNEE_WIDTH_ADJUST);
			if (MIN_ASSIGNEE_WIDTH > pxWidth) {
				pxWidth = MIN_ASSIGNEE_WIDTH;
			}
			reply.setWidth(pxWidth + "px");
		}

		// If we get here, reply refers to the assignee Label.  Return
		// it.
		return reply;
	}

	/*
	 * Returns the URL to the image to display for presence for the
	 * cell.
	 */
	private String getPresenceImage(AssignmentInfo ai) {
		String reply = ai.getAvatarUrl();
		if (!(GwtClientHelper.hasString(reply))) {
			ImageResource ir;
			if (ai.isUserExternal())
			     ir = m_filrImages.filrExternalUser48();
			else ir = m_images.userPhoto();
			reply = ir.getSafeUri().asString();
		}
		return reply;
	}
	
	/*
	 * Called to invoke the group membership popup on the principal.
	 */
	private void invokeGroupMembership(AssignmentInfo ai, Element pElement) {
		GroupMembershipPopup gmp = ((GroupMembershipPopup) ai.getMembershipPopup());
		if (null == gmp) {
			gmp = new GroupMembershipPopup(
				true,	// true  -> Auto hide.
				false,	// false -> Not modal.
				ai.getTitle(),
				String.valueOf(ai.getId()));
			ai.setMembershipPopup(gmp);
		}
		gmp.showRelativeTo(GwtClientHelper.getUIObjectFromElement(pElement));
	}
	
	/*
	 * Called to invoke the simple profile dialog on the principal's
	 * presence.
	 */
	private void invokeSimpleProfile(AssignmentInfo ai, Element pElement) {
		Long wsId = ai.getPresenceUserWSId();
		String wsIdS = ((null == wsId) ? null : String.valueOf(wsId));
		GwtClientHelper.invokeSimpleProfile(pElement, String.valueOf(ai.getId()), wsIdS, ai.getTitle());
	}
	
	/**
     * Called when an event occurs in a rendered instance of this
     * cell.  The parent element refers to the element that contains
     * the rendered cell, NOT to the outermost element that the cell
     * rendered.
     * 
     * @param context
     * @param parent
     * @param aiList
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
    @Override
    public void onBrowserEvent(Context context, Element parent, List<AssignmentInfo> aiList, NativeEvent event, ValueUpdater<List<AssignmentInfo>> valueUpdater) {
		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, aiList, event, valueUpdater);
        	return;
    	}

    	// Is one of presence widgets is being operated on? 
		Element eventTarget  = Element.as(event.getEventTarget());
		EventInfo ei = new EventInfo(parent, event);
		if (ei.isPresence()) {
			// Yes!  What event are we handling?
	    	if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
	    		// A click!  Strip off any hover style and invoke the
    			// simple profile dialog.
    			eventTarget.removeClassName(ei.isPresenceControl() ? "cursorPointer" : "vibe-dataTableLink-hover");
    			AssignmentInfo ai = aiList.get(ei.getAssignmentIndex());
    			if (AssigneeType.GROUP.equals(ai.getAssigneeType()))
    			     invokeGroupMembership(ai, eventTarget);
    			else invokeSimpleProfile(  ai, eventTarget);
	    	}
	    	
	    	else if (VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
	    		// A mouse over!  Add the hover style.
				eventTarget.addClassName(ei.isPresenceControl() ? "cursorPointer" : "vibe-dataTableLink-hover");
	    	}
	    	
	    	else if (VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
	    		// A mouse out!  Remove the hover style.
				eventTarget.removeClassName(ei.isPresenceControl() ? "cursorPointer" : "vibe-dataTableLink-hover");
	    	}
		}
    }
    
    /**
     * Called when the user presses the ENTER key when the cell is
     * selected.  It's not required to override this method, but it's
     * a common convention that allows a cell to respond to key events.
     * 
     * Overrides AbstractCell.onEnterKeyDown()
     */
    @Override
    protected void onEnterKeyDown(Context context, Element parent, List<AssignmentInfo> aiList, NativeEvent event, ValueUpdater<List<AssignmentInfo>> valueUpdater) {
    	// Simply invoke the simple profile dialog on the appropriate
    	// assignment.
    	Element eventTarget = Element.as(event.getEventTarget());
    	AssignmentInfo ai = aiList.get(new EventInfo(parent, event).getAssignmentIndex());
		if (AssigneeType.GROUP.equals(ai.getAssigneeType()))
		     invokeGroupMembership(ai, eventTarget);
		else invokeSimpleProfile(  ai, eventTarget);
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param aiList
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, List<AssignmentInfo> aiList, SafeHtmlBuilder sb) {
		// If we weren't given a List<AssignmentInfo>...
		if ((null == aiList) || aiList.isEmpty()) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Create the panel to hold the HTML of the presence controls.
		VibeFlowPanel renderPanel = new VibeFlowPanel();

		// Scan the assignments.
		int assignmentIndex = 0;
		for (AssignmentInfo ai:  aiList) {
			// If this assignment doesn't have an assignee type...
			AssigneeType ait = ai.getAssigneeType();
			if (null == ait) {
				// ...skip it.
				continue;
			}
			
			// Do we have any hover text for this assignment?
			String hover = ai.getHover();
			if (!(GwtClientHelper.hasString(hover))) {
				hover = ai.getTitle();
			}
			
			// Generate a panel to hold the assignment...
			VibeFlowPanel fp = new VibeFlowPanel();
			fp.addStyleName("vibe-dataTableAssignment-panel displayBlock verticalAlignMiddle");
			if (0 < assignmentIndex) {
				fp.addStyleName("margintop3px");
			}
			renderPanel.add(fp);

			String assignmentIndexTail = ("." + (assignmentIndex++));
			switch (ait) {
			case INDIVIDUAL: {
				// Individual assignee!  Generate a presence control...
				GwtPresenceInfo presence = ai.getPresence();
				PresenceControl presenceControl = new PresenceControl(String.valueOf(ai.getId()), String.valueOf(ai.getPresenceUserWSId()), false, false, false, presence);
				presenceControl.setImageAlignment("top");
				presenceControl.addStyleName("vibe-dataTableAssignment-control displayInline verticalAlignMiddle");
				presenceControl.setAnchorStyleName("cursorPointer");
				presenceControl.getElement().setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, (VibeDataTableConstants.CELL_WIDGET_PRESENCE + assignmentIndexTail));
				presenceControl.setImageOverride(getPresenceImage(ai));
				presenceControl.addImageStyleName("vibe-dataTableAssignment-image");
				fp.add(presenceControl);
				
				// ...and add a name link for it.
				Label presenceLabel = createAssigneeLabel(ai.getTitle(), hover);
				presenceLabel.getElement().setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, (VibeDataTableConstants.CELL_WIDGET_PRESENCE_LABEL + assignmentIndexTail));
				fp.add(presenceLabel);
				break;
			}
				
			case GROUP:
			case TEAM: {
				// Group or team assignee!  Generate an appropriate
				// image...
				VibeFlowPanel imgPanel = new VibeFlowPanel();
				imgPanel.addStyleName("vibe-dataTableAssignment-control displayInline verticalAlignMiddle");
				Image assigneeImg = new Image();
				assigneeImg.addStyleName("vibe-dataTableAssignment-image");
				assigneeImg.setUrl(GwtClientHelper.getRequestInfo().getImagesPath() + ai.getPresenceDude());
				assigneeImg.getElement().setAttribute("align", "absmiddle");
				imgPanel.add(assigneeImg);
				fp.add(imgPanel);

				// ...and add a label.
				int    members       = ai.getMembers();
				String membersString = GwtTeaming.getMessages().vibeDataTable_MemberCount(String.valueOf(members));
				String assigneeLabel = (ai.getTitle() + " " + membersString);
				Label assignee = createAssigneeLabel(assigneeLabel, hover);
				if (AssigneeType.GROUP.equals(ait)) {
					assignee.getElement().setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, (VibeDataTableConstants.CELL_WIDGET_PRESENCE_LABEL + assignmentIndexTail));
				}
				fp.add(assignee);
				
				break;
			}
				
			case PUBLIC: {
				// Public assignee!  Generate an appropriate image...
				VibeFlowPanel imgPanel = new VibeFlowPanel();
				imgPanel.addStyleName("vibe-dataTableAssignment-control displayInline verticalAlignMiddle");
				Image assigneeImg = new Image();
				assigneeImg.addStyleName("vibe-dataTableAssignment-image");
				assigneeImg.setUrl(m_images.publicAssignee().getSafeUri().asString());
				assigneeImg.getElement().setAttribute("align", "absmiddle");
				imgPanel.add(assigneeImg);
				fp.add(imgPanel);

				// ...and add a label.
				fp.add(createAssigneeLabel(ai.getTitle(), hover));
				break;
			}
			
			case PUBLIC_LINK: {
				// Public link assignee!  Generate an appropriate image...
				VibeFlowPanel imgPanel = new VibeFlowPanel();
				imgPanel.addStyleName("vibe-dataTableAssignment-control displayInline verticalAlignMiddle");
				Image assigneeImg = new Image();
				assigneeImg.addStyleName("vibe-dataTableAssignment-image");
				assigneeImg.setUrl(m_images.publicLinkAssignee().getSafeUri().asString());
				assigneeImg.getElement().setAttribute("align", "absmiddle");
				imgPanel.add(assigneeImg);
				fp.add(imgPanel);
	
				// ...and add a label.
				fp.add(createAssigneeLabel(ai.getTitle(), hover));
				break;
			}
			}
		}
		
		// Finally, render the panel with the list of presence controls
		// into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(renderPanel.getElement().getInnerHTML());
		sb.append(rendered);
	}
}
