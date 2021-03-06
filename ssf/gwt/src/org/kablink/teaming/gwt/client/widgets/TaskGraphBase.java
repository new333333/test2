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

package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.TaskStats;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Base class for a group of objects that draws graphs based on the
 * statistics from a TaskStats object.
 * 
 * @author drfoster@novell.com
 */
public abstract class TaskGraphBase extends VibeFlowPanel {
	private FlexCellFormatter	m_gridCellFormatter;	//
	private FlexTable			m_grid;					//
	private TaskStats			m_taskStats;			//
	private String				m_gridStyles;			//
	
	protected GwtTeamingMessages	m_messages = GwtTeaming.getMessages();	//
	
	/**
	 * Constructor method.
	 * 
	 * @param taskStats
	 * @param gridStyles
	 * @param showLegend
	 */
	public TaskGraphBase(TaskStats taskStats, String gridStyles, boolean showLegend) {
		// Initialize the super class...
		super();
		
		// ...store the data members...
		setTaskStatistics(taskStats );
		setGridStyles(    gridStyles);
		
		// ...initialize anything else about the graph...
		addStyleName("taskGraphs-stats");

		// ...and render it.
		renderGraph(showLegend);
	}

	/**
	 * Get'er methods
	 * 
	 * @param
	 */
	final protected TaskStats getTaskStatistics() {return m_taskStats;}
	
	/*
	 * Set'er methods.
	 */
	private void setGridStyles(    String    gridStyles) {m_gridStyles = gridStyles;}
	private void setTaskStatistics(TaskStats taskStats)  {m_taskStats  = taskStats; }
	
	/**
	 * Adds a colored bar segment to a grid.
	 * 
	 * @param count
	 * @param percent
	 * @param style
	 * @param message
	 */
	protected void addBarSegment(int count, int percent, String style, String message) {
		// If this segment doesn't show anything...
		if (0 == count) {
			// ...skip it.
			return;
		}
		
		String width = (percent + "%");
		InlineLabel il = new InlineLabel(width);
		il.addStyleName("taskGraphs-statsBarSegment");
		il.setWordWrap(false);
		il.setTitle(message);
		int cell;
		try                  {cell = m_grid.getCellCount(0);}
		catch (Exception ex) {cell = 0;                   }
		m_grid.setWidget(0, cell, il);
		m_gridCellFormatter.setWidth(0, cell, width);
		m_gridCellFormatter.addStyleName(0, cell, style);
	}

	/**
	 * Adds a statistic bar to a graph.
	 * 
	 * @param vp
	 * @param style
	 * @param message
	 */
	protected void addLegendBar(String style, String message) {
		// Create a panel for the bar...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("taskGraphs-statsLegendBar");
		add(fp);

		// ...create the colored box...
		VibeFlowPanel colorBox = new VibeFlowPanel();
		colorBox.addStyleName("taskGraphs-statsLegendBox " + style);
		fp.add(colorBox);

		// ...and create its label.
		InlineLabel il = new InlineLabel(message);
		il.addStyleName("taskGraphs-statsLegendLabel");
		fp.add(il);
	}

	/**
	 * Renders the graph.
	 * 
	 * This is implemented by the classes that extend this class to
	 * render their graph.
	 * 
	 * @param showLegend
	 */
	protected abstract void render(boolean showLegend);
	
	/*
	 * Renders the components of the graph.
	 */
	private void renderGraph(boolean showLegend) {
		renderGrid();		// Constructs the grid the graph will live in...
		render(showLegend);	// ...allows the implementing class to render what it wants.
	}

	/*
	 * Renders the grid that the graph lives in.
	 */
	private void renderGrid() {
		m_grid = new FlexTable();
		if (GwtClientHelper.hasString(m_gridStyles)) {
			m_grid.addStyleName(m_gridStyles);
		}
		m_grid.setCellPadding(0);
		m_grid.setCellSpacing(0);
		add(m_grid);
		m_gridCellFormatter = m_grid.getFlexCellFormatter();
	}
}
