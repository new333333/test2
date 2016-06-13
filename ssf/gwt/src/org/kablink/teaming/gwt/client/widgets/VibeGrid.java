/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.kablink.teaming.gwt.client.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

/**
 * Created by david on 4/25/16.
 */
public class VibeGrid extends Grid
        implements ProvidesResize, RequiresResize, VibeEntityViewPanel
{
    private int currRow = 0;
    private int currCol = 0;

    public VibeGrid() {
        super();
    }

    public VibeGrid(int rows, int columns) {
        super(rows, columns);
    }

    @Override
    public void showWidget(Widget widget) {
        GwtClientHelper.consoleLog("VibeGrid.showWidget() before: currRow=" + currRow + "; currCol=" + currCol + "; numRows=" + numRows + "; numCols=" + numColumns);
        if (currRow>=numRows) {
            GwtClientHelper.consoleLog("VibeGrid.showWidget() resize: numRows=" + (numRows+1) + "; numCols=" + numColumns);
            this.resize(numRows+1, numColumns);
        }
        GwtClientHelper.consoleLog("VibeGrid.showWidget() setWidget: currRow=" + currRow + "; currCol=" + currCol + "; widget=" + widget.getClass().getSimpleName());
        this.setWidget(currRow, currCol, widget);
        currCol++;
        if (currCol>=numColumns) {
            currCol = 0;
            currRow++;
        }
        GwtClientHelper.consoleLog("VibeGrid.showWidget() after: currRow=" + currRow + "; currCol=" + currCol + "; numRows=" + numRows + "; numCols=" + numColumns);
    }

    /**
     */
    @Override
    public void onResize()
    {
        onResizeAsync();
    }//end onResize()

    /*
     * Asynchronously resizes the flow panel.
     */
    private void onResizeAsync()
    {
        GwtClientHelper.deferCommand(new Scheduler.ScheduledCommand()
        {
            @Override
            public void execute()
            {
                onResizeNow();
            }
        });
    }//end onResizeAsync()

    /*
     * Synchronously resizes the flow panel.
     */
    private void onResizeNow()
    {
        for (int row=0; row<numRows; row++) {
            for (int col=0; col<numColumns; col++) {
                Widget child = this.getWidget(row, col);
                if (child!=null && child instanceof RequiresResize) {
                    ((RequiresResize) child).onResize();
                }
            }
        }
    }//end onResizeNow()

}
