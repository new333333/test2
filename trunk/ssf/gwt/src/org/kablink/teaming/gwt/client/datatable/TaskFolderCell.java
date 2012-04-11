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
package org.kablink.teaming.gwt.client.datatable;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.GotoContentUrlEvent;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TaskFolderInfo;
import org.kablink.teaming.gwt.client.util.TaskStats;
import org.kablink.teaming.gwt.client.widgets.TaskStatusGraph;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

/**
 * Data table cell that represents a list of task folders.
 * 
 * @author drfoster@novell.com
 */
public class TaskFolderCell extends AbstractCell<List<TaskFolderInfo>> {
	/*
	 * Inner class used to encapsulate information about an event
	 * received by an TaskFolderCell.
	 */
	private static class EventInfo {
		private boolean	m_isTaskFolderPermalink;	//
		private int		m_taskFolderIndex;			//
		private int		m_stepsToTD;				//
		private String	m_eventTag        = "";		//
		private String	m_widgetAttribute = "";		//

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
				// Is this one of our task folder widgets?
				m_widgetAttribute = eventTarget.getAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE);
				if ((null != m_widgetAttribute) && (0 < m_widgetAttribute.length())) {
					// Yes!  Is it the task folder permalink?
					m_isTaskFolderPermalink = m_widgetAttribute.startsWith(VibeDataTableConstants.CELL_WIDGET_TASK_FOLDER);

					// What's index to the TaskFolderInfo in the list?
					int    pPos            = m_widgetAttribute.indexOf(".");
					String taskFolderIndex = m_widgetAttribute.substring(pPos + 1);
					m_taskFolderIndex      = Integer.parseInt(taskFolderIndex);

					// We're done looking!  Break out of the scan loop.
					break;
				}

				// We haven't found a task folder widget yet.  Move up
				// a level.
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
				", A:" + m_isTaskFolderPermalink +
				", I:" + m_taskFolderIndex   +
				", W:" + m_widgetAttribute   +
				", S:" + m_stepsToTD);
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public int     getTaskFolderIndex()    {return m_taskFolderIndex;      }
		public boolean isTaskFolderPermalink() {return m_isTaskFolderPermalink;}
	}
	
	/**
	 * Constructor method.
	 */
	public TaskFolderCell() {
		// Sink the events we need to process task folders.
		super(
			VibeDataTableConstants.CELL_EVENT_CLICK,
			VibeDataTableConstants.CELL_EVENT_KEYDOWN,
			VibeDataTableConstants.CELL_EVENT_MOUSEOVER,
			VibeDataTableConstants.CELL_EVENT_MOUSEOUT);
	}

	/*
	 * Called to invoke the permalink to a task folder.
	 */
	private void invokeTaskFolderPermalink(TaskFolderInfo tfi) {
		GwtTeaming.fireEvent(new GotoContentUrlEvent(tfi.getFolderPermalink()));
	}
	
	/**
     * Called when an event occurs in a rendered instance of this
     * cell.  The parent element refers to the element that contains
     * the rendered cell, NOT to the outermost element that the cell
     * rendered.
     * 
     * @param context
     * @param parent
     * @param tfiList
     * @param event
     * @param valueUpdater
     * 
     * Overrides AbstractCell.onBrowserEvent()
     */
    @Override
    public void onBrowserEvent(Context context, Element parent, List<TaskFolderInfo> tfiList, NativeEvent event, ValueUpdater<List<TaskFolderInfo>> valueUpdater) {
		// What type of event are we processing?
    	String eventType = event.getType();
    	if (VibeDataTableConstants.CELL_EVENT_KEYDOWN.equals(eventType)) {
        	// A key down!  Let AbstractCell handle it.  It will
    		// convert it to an entry key down, ... as necessary.
        	super.onBrowserEvent(context, parent, tfiList, event, valueUpdater);
        	return;
    	}

    	// Is one of task folder widgets being operated on? 
		Element eventTarget  = Element.as(event.getEventTarget());
		EventInfo ei = new EventInfo(parent, event);
		if (ei.isTaskFolderPermalink()) {
			// Yes!  What event are we handling?
	    	if (VibeDataTableConstants.CELL_EVENT_CLICK.equals(eventType)) {
	    		// A click!  Strip off any hover style and invoke the
    			// task folder permalink.
    			eventTarget.removeClassName(ei.isTaskFolderPermalink() ? "cursorPointer" : "vibe-dataTableLink-hover");
    			invokeTaskFolderPermalink(tfiList.get(ei.getTaskFolderIndex()));
	    	}
	    	
	    	else if (VibeDataTableConstants.CELL_EVENT_MOUSEOVER.equals(eventType)) {
	    		// A mouse over!  Add the hover style.
				eventTarget.addClassName(ei.isTaskFolderPermalink() ? "cursorPointer" : "vibe-dataTableLink-hover");
	    	}
	    	
	    	else if (VibeDataTableConstants.CELL_EVENT_MOUSEOUT.equals(eventType)) {
	    		// A mouse out!  Remove the hover style.
				eventTarget.removeClassName(ei.isTaskFolderPermalink() ? "cursorPointer" : "vibe-dataTableLink-hover");
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
    protected void onEnterKeyDown(Context context, Element parent, List<TaskFolderInfo> tfiList, NativeEvent event, ValueUpdater<List<TaskFolderInfo>> valueUpdater) {
		invokeTaskFolderPermalink(tfiList.get(new EventInfo(parent, event).getTaskFolderIndex()));
    }
    
	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param tfiList
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, List<TaskFolderInfo> tfiList, SafeHtmlBuilder sb) {
		// If we weren't given a List<TaskFolderInfo>...
		if ((null == tfiList) || tfiList.isEmpty()) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Create the panel to hold the HTML of the task folder
		// widgets.
		VibeFlowPanel renderPanel = new VibeFlowPanel();

		// Scan the task folders.
		int taskFolderIndex = 0;
		for (TaskFolderInfo tfi:  tfiList) {
			// Generate a panel to hold this task folder...
			VibeFlowPanel fp = new VibeFlowPanel();
			fp.addStyleName("vibe-dataTableTaskFolder-panel displayBlock verticalAlignTop");
			if (0 < taskFolderIndex) {
				fp.addStyleName("paddingTop3px");
			}

			// Render the task folder link.
			String taskFolderIndexTail = ("." + (taskFolderIndex++));
			Label tfLabel = new Label(tfi.getTitle());
			tfLabel.addStyleName("vibe-dataTableTaskFolder-label vibe-dataTableTaskFolder-enabled");
			tfLabel.getElement().setAttribute(VibeDataTableConstants.CELL_WIDGET_ATTRIBUTE, (VibeDataTableConstants.CELL_WIDGET_TASK_FOLDER + taskFolderIndexTail));
			fp.add(tfLabel);

			// Are there any tasks defined in the folder?
			TaskStats ts = tfi.getTaskStatistics();
			if (0 < ts.getTotalTasks()) {
				// Yes!  Create panel to hold their status graph.
				TaskStatusGraph tsgPanel = new TaskStatusGraph(ts, "vibe-dataTableTaskFolder-statsStatusBar", false);
				tsgPanel.addStyleName("vibe-dataTableTaskFolder-stats displayBlock verticalAlignTop");
				fp.add(tsgPanel);
			}

			// Add this task folder's panel to the render panel.
			renderPanel.add(fp);
		}
		
		// Finally, render the panel with the list of task folder
		// widgets into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(renderPanel.getElement().getInnerHTML());
		sb.append(rendered);
	}
}
