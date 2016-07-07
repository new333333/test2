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
import org.kablink.teaming.gwt.client.GwtConstants;
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
    public void add(Widget child) {
        showWidget(child);
    }

    @Override
    public void showWidget(Widget widget) {
        if (currRow>=numRows) {
            this.resize(numRows+1, numColumns);
        }
        this.setWidget(currRow, currCol, widget);
        currCol++;
        if (currCol>=numColumns) {
            currCol = 0;
            currRow++;
        }
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
        GwtClientHelper.deferCommand(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onResizeNow();
            }
        });
    }//end onResizeAsync()

    /*
     * Synchronously resizes the flow panel.
     */
    private void onResizeNow()
    {
        GwtClientHelper.consoleLog("VibeGrid.onResizeNow(): numRows=" + numRows + "; numCols=" + numColumns);
        for (int row=0; row<numRows; row++) {
            GwtClientHelper.consoleLog("VibeGrid.onResizeNow(): row " + row + " height: " + getRowHeight(row));
            for (int col=0; col<numColumns; col++) {
                Widget child = this.getWidget(row, col);
                if (child!=null && child instanceof RequiresResize) {
                    GwtClientHelper.consoleLog("Calling onResize() for child: " + child.getClass().getSimpleName());
                    ((RequiresResize) child).onResize();
                } else if (child!=null) {
                    GwtClientHelper.consoleLog("Won't call onResize() for child because it doesn't implement RequiresResize: " + child.getClass().getSimpleName());
                } else {
                    GwtClientHelper.consoleLog("Child is null: row=" + row + "; col=" + col);
                }
            }
        }
    }//end onResizeNow()

    @Override
    public int getContainingHeight(Widget widget) {
        int row = getRow(widget);
        GwtClientHelper.consoleLog("VibeGrid.getContainingHeight(): widget=" + widget.getClass().getSimpleName() + "; row=" + row);
        return getRowHeight(row);
    }

    @Override
    public int getContainingWidth(Widget widget) {
        int col = getCol(widget);
        GwtClientHelper.consoleLog("VibeGrid.getContainingWidth(): widget=" + widget.getClass().getSimpleName() + "; col=" + col);
        return getColumnWidth(col);
    }

    private int getRowHeight(int row) {
        if (row>=0 && row<numRows) {
            return getRowFormatter().getElement(row).getOffsetHeight() - 2;
        }
        return 0;
    }

    private int getColumnWidth(int col) {
        if (col>=0 && col<numColumns) {
            return getColumnFormatter().getElement(col).getOffsetWidth() - 2;
        }
        return 0;
    }

    private int getCol(Widget widget) {
        for (int row=0; row<numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                Widget child = this.getWidget(row, col);
                if (child == widget) {
                    return col;
                }
            }
        }
        return -1;
    }

    private int getRow(Widget widget) {
        for (int row=0; row<numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                Widget child = this.getWidget(row, col);
                if (child == widget) {
                    return row;
                }
            }
        }
        return -1;
    }

}
