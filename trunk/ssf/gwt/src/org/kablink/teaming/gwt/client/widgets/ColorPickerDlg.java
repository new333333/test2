/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;


/**
 * 
 * @author jwootton
 *
 */
public class ColorPickerDlg extends DlgBox
{
	/**
	 * This class is used to hold information about a color.
	 */
	public class Color
	{
		private String m_localizedName = null;
		private String m_name = null;
		private String m_hexValue = null;
		
		/**
		 * 
		 */
		public Color( String localizedName, String name, String hexValue )
		{
			m_localizedName = localizedName;
			m_name = name;
			m_hexValue = hexValue;
		}// end Color()
		
		
		/**
		 * 
		 */
		public String getHexValue()
		{
			return m_hexValue;
		}// end getHexValue()
		
		
		/**
		 * 
		 */
		public String getLocalizedName()
		{
			return m_localizedName;
		}// end getLocalizedName()
		
		
		/**
		 * 
		 */
		public String getName()
		{
			return m_name;
		}// end getName()
		
	}// end Color
	

	private int m_selectedColorRow = -1;
	private FlexTable m_table = null;
	private final Color[] m_colors =
	{
		new Color( GwtTeaming.getMessages().aliceBlue(), "AliceBlue", "#F0F8FF" ),
		new Color( GwtTeaming.getMessages().antiqueWhite(), "AntiqueWhite", "#FAEBD7" ),
		new Color( GwtTeaming.getMessages().aqua(), "Aqua", "#00FFFF" ),
		new Color( GwtTeaming.getMessages().aquaMarine(), "AquaMarine", "#7FFFD4" ),
		new Color( GwtTeaming.getMessages().azure(), "Azure", "#F0FFFF" ),
		new Color( GwtTeaming.getMessages().beige(), "Beige", "#F5F5DC" ),
		new Color( GwtTeaming.getMessages().bisque(), "Bisque", "#FFE4C4" ),
		new Color( GwtTeaming.getMessages().black(), "black", "#000000" ),
		new Color( GwtTeaming.getMessages().blanchedAlmond(), "blanchedAlmond", "#FFEBCD" ),
		new Color( GwtTeaming.getMessages().blue(), "blue", "#0000FF" ),
		new Color( GwtTeaming.getMessages().blueViolet(), "blueViolet", "#8A2BE2" ),
		new Color( GwtTeaming.getMessages().brown(), "brown", "#A52A2A" ),
		new Color( GwtTeaming.getMessages().burlyWood(), "burlyWood", "#DEB887" ),
		new Color( GwtTeaming.getMessages().cadetBlue(), "cadetBlue", "#5F9EA0" ),
		new Color( GwtTeaming.getMessages().chartreuse(), "chartreuse", "#7FFF00" ),
		new Color( GwtTeaming.getMessages().chocolate(), "chocolate", "#D2691E" ),
		new Color( GwtTeaming.getMessages().coral(), "coral", "#FF7F50" ),
		new Color( GwtTeaming.getMessages().cornflowerBlue(), "cornflowerBlue", "#6495ED" ),
		new Color( GwtTeaming.getMessages().cornSilk(), "cornSilk", "#FFF8DC" ),
		new Color( GwtTeaming.getMessages().crimson(), "crimson", "#DC143C" ),
		new Color( GwtTeaming.getMessages().cyan(), "cyan", "#00FFFF" ),
		new Color( GwtTeaming.getMessages().darkBlue(), "darkBlue", "#00008B" ),
		new Color( GwtTeaming.getMessages().darkCyan(), "darkCyan", "#008B8B" ),
		new Color( GwtTeaming.getMessages().darkGoldenRod(), "darkGoldenRod", "#B8860B" ),
		new Color( GwtTeaming.getMessages().darkGray(), "darkGray", "#A9A9A9"  ),
		new Color( GwtTeaming.getMessages().darkGreen(), "darkGreen", "#006400" ),
		new Color( GwtTeaming.getMessages().darkKhaki(), "darkKhaki", "#BDB76B" ),
		new Color( GwtTeaming.getMessages().darkMagenta(), "darkMagenta", "#8B008B" ),
		new Color( GwtTeaming.getMessages().darkOliveGreen(), "darkOliveGreen", "#556B2F" ),
		new Color( GwtTeaming.getMessages().darkOrange(), "darkOrange", "#FF8C00" ),
		new Color( GwtTeaming.getMessages().darkOrchid(), "darkOrchid", "#9932CC" ),
		new Color( GwtTeaming.getMessages().darkRed(), "darkRed", "#8B0000" ),
		new Color( GwtTeaming.getMessages().darkSalmon(), "darkSalmon", "#E9967A" ),
		new Color( GwtTeaming.getMessages().darkSeaGreen(), "darkSeaGreen", "#8FBC8F" ),
		new Color( GwtTeaming.getMessages().darkSlateBlue(), "darkSlateBlue", "#483D8B" ),
		new Color( GwtTeaming.getMessages().darkSlateGray(), "darkSlateGray", "#2F4F4F" ),
		new Color( GwtTeaming.getMessages().darkTurquoise(), "darkTurquoise", "#00CED1" ),
		new Color( GwtTeaming.getMessages().darkViolet(), "darkViolet", "#9400D3" ),
		new Color( GwtTeaming.getMessages().deepPink(), "deepPink", "#FF1493" ),
		new Color( GwtTeaming.getMessages().deepSkyBlue(), "deepSkyBlue", "#00BFFF" ),
		new Color( GwtTeaming.getMessages().dimGray(), "dimGray", "#696969" ),
		new Color( GwtTeaming.getMessages().dodgerBlue(), "dodgerBlue", "#1E90FF" ),
		new Color( GwtTeaming.getMessages().fireBrick(), "fireBrick", "#B22222" ),
		new Color( GwtTeaming.getMessages().floralWhite(), "floralWhite", "#FFFAF0" ),
		new Color( GwtTeaming.getMessages().forestGreen(), "forestGreen", "#228B22" ),
		new Color( GwtTeaming.getMessages().fuchsia(), "fuchsia", "#FF00FF" ),
		new Color( GwtTeaming.getMessages().gainsboro(), "gainsboro", "#DCDCDC" ),
		new Color( GwtTeaming.getMessages().ghostWhite(), "ghostWhite", "#F8F8FF" ),
		new Color( GwtTeaming.getMessages().gold(), "gold", "#FFD700" ),
		new Color( GwtTeaming.getMessages().goldenRod(), "goldenRod", "#DAA520" ),
		new Color( GwtTeaming.getMessages().gray(), "gray", "#808080" ),
		new Color( GwtTeaming.getMessages().green(), "green", "#008000" ),
		new Color( GwtTeaming.getMessages().greenYellow(), "greenYellow", "#ADFF2F" ),
		new Color( GwtTeaming.getMessages().honeyDew(), "honeyDew", "#F0FFF0" ),
		new Color( GwtTeaming.getMessages().hotPink(), "hotPink", "#FF69B4" ),
		new Color( GwtTeaming.getMessages().indianRed(), "indianRed", "#CD5C5C" ),
		new Color( GwtTeaming.getMessages().indigo(), "indigo", "#4B0082" ),
		new Color( GwtTeaming.getMessages().ivory(), "ivory", "#FFFFF0" ),
		new Color( GwtTeaming.getMessages().khaki(), "khaki", "#F0E68C" ),
		new Color( GwtTeaming.getMessages().lavender(), "lavender", "#E6E6FA" ),
		new Color( GwtTeaming.getMessages().lavenderBlush(), "lavenderBlush", "#FFF0F5" ),
		new Color( GwtTeaming.getMessages().lawnGreen(), "lawnGreen", "#7CFC00" ),
		new Color( GwtTeaming.getMessages().lemonChiffon(), "lemonChiffon", "#FFFACD" ),
		new Color( GwtTeaming.getMessages().lightBlue(), "lightBlue", "#ADD8E6" ),
		new Color( GwtTeaming.getMessages().lightCoral(), "lightCoral", "#F08080" ),
		new Color( GwtTeaming.getMessages().lightCyan(), "lightCyan", "#E0FFFF" ),
		new Color( GwtTeaming.getMessages().lightGoldenRodYellow(), "lightGoldenRodYellow", "#FAFAD2" ),
		new Color( GwtTeaming.getMessages().lightGrey(), "lightGrey", "#D3D3D3" ),
		new Color( GwtTeaming.getMessages().lightGreen(), "lightGreen", "#90EE90" ),
		new Color( GwtTeaming.getMessages().lightPink(), "lightPink", "#FFB6C1" ),
		new Color( GwtTeaming.getMessages().lightSalmon(), "lightSalmon", "#FFA07A" ),
		new Color( GwtTeaming.getMessages().lightSeaGreen(), "lightSeaGreen", "#20B2AA" ),
		new Color( GwtTeaming.getMessages().lightSkyBlue(), "lightSkyBlue", "#87CEFA" ),
		new Color( GwtTeaming.getMessages().lightSlateGray(), "lightSlateGray", "#778899" ),
		new Color( GwtTeaming.getMessages().lightSteelBlue(), "lightSteelBlue", "#B0C4DE" ),
		new Color( GwtTeaming.getMessages().lightYellow(), "lightYellow", "#FFFFE0" ),
		new Color( GwtTeaming.getMessages().lime(), "lime", "#00FF00" ),
		new Color( GwtTeaming.getMessages().limeGreen(), "limeGreen", "#32CD32" ),
		new Color( GwtTeaming.getMessages().linen(), "linen", "#FAF0E6" ),
		new Color( GwtTeaming.getMessages().magenta(), "magenta", "#FF00FF" ),
		new Color( GwtTeaming.getMessages().maroon(), "maroon", "#800000" ),
		new Color( GwtTeaming.getMessages().mediumAquaMarine(), "mediumAquaMarine", "#66CDAA" ),
		new Color( GwtTeaming.getMessages().mediumBlue(), "mediumBlue", "#0000CD" ),
		new Color( GwtTeaming.getMessages().mediumOrchid(), "mediumOrchid", "#BA55D3" ),
		new Color( GwtTeaming.getMessages().mediumPurple(), "mediumPurple", "#9370D8" ),
		new Color( GwtTeaming.getMessages().mediumSeaGreen(), "mediumSeaGreen", "#3CB371" ),
		new Color( GwtTeaming.getMessages().mediumSlateBlue(), "mediumSlateBlue", "#7B68EE" ),
		new Color( GwtTeaming.getMessages().mediumSpringGreen(), "mediumSpringGreen", "#00FA9A" ),
		new Color( GwtTeaming.getMessages().mediumTurquoise(), "mediumTurquoise", "#48D1CC" ),
		new Color( GwtTeaming.getMessages().mediumVioletRed(), "mediumVioletRed", "#C71585" ),
		new Color( GwtTeaming.getMessages().midnightBlue(), "midnightBlue", "#191970" ),
		new Color( GwtTeaming.getMessages().mintCream(), "mintCream", "#F5FFFA" ),
		new Color( GwtTeaming.getMessages().mistyRose(), "mistyRose", "#FFE4E1" ),
		new Color( GwtTeaming.getMessages().moccasin(), "moccasin", "#FFE4B5" ),
		new Color( GwtTeaming.getMessages().navajoWhite(), "navajoWhite", "#FFDEAD" ),
		new Color( GwtTeaming.getMessages().navy(), "navy", "#000080" ),
		new Color( GwtTeaming.getMessages().oldLace(), "oldLace", "#FDF5E6" ),
		new Color( GwtTeaming.getMessages().olive(), "olive", "#808000" ),
		new Color( GwtTeaming.getMessages().oliveDrab(), "oliveDrab", "#6B8E23" ),
		new Color( GwtTeaming.getMessages().orange(), "orange", "#FFA500" ),
		new Color( GwtTeaming.getMessages().orangeRed(), "orangeRed", "#FF4500" ),
		new Color( GwtTeaming.getMessages().orchid(), "orchid", "#DA70D6" ),
		new Color( GwtTeaming.getMessages().paleGoldenRod(), "paleGoldenRod", "#EEE8AA" ),
		new Color( GwtTeaming.getMessages().paleGreen(), "paleGreen", "#98FB98" ),
		new Color( GwtTeaming.getMessages().paleTurquoise(), "paleTurquoise", "#AFEEEE" ),
		new Color( GwtTeaming.getMessages().paleVioletRed(), "paleVioletRed", "#D87093" ),
		new Color( GwtTeaming.getMessages().papayaWhip(), "papayaWhip", "#FFEFD5" ),
		new Color( GwtTeaming.getMessages().peachPuff(), "peachPuff", "#FFDAB9" ),
		new Color( GwtTeaming.getMessages().peru(), "peru", "#CD853F" ),
		new Color( GwtTeaming.getMessages().pink(), "pink", "#FFC0CB" ),
		new Color( GwtTeaming.getMessages().plum(), "plum", "#DDA0DD" ),
		new Color( GwtTeaming.getMessages().powderBlue(), "powderBlue", "#B0E0E6" ),
		new Color( GwtTeaming.getMessages().purple(), "purple", "#800080" ),
		new Color( GwtTeaming.getMessages().red(), "red", "#FF0000" ),
		new Color( GwtTeaming.getMessages().rosyBrown(), "rosyBrown", "#BC8F8F" ),
		new Color( GwtTeaming.getMessages().royalBlue(), "royalBlue", "#4169E1" ),
		new Color( GwtTeaming.getMessages().saddleBrown(), "saddleBrown", "#8B4513" ),
		new Color( GwtTeaming.getMessages().salmon(), "salmon", "#FA8072" ),
		new Color( GwtTeaming.getMessages().sandyBrown(), "sandyBrown", "#F4A460" ),
		new Color( GwtTeaming.getMessages().seaGreen(), "seaGreen", "#2E8B57" ),
		new Color( GwtTeaming.getMessages().seaShell(), "seaShell", "#FFF5EE" ),
		new Color( GwtTeaming.getMessages().sienna(), "sienna", "#A0522D" ),
		new Color( GwtTeaming.getMessages().silver(), "silver", "#C0C0C0" ),
		new Color( GwtTeaming.getMessages().skyBlue(), "skyBlue", "#87CEEB" ),
		new Color( GwtTeaming.getMessages().slateBlue(), "slateBlue", "#6A5ACD" ),
		new Color( GwtTeaming.getMessages().slateGray(), "slateGray", "#708090" ),
		new Color( GwtTeaming.getMessages().snow(), "snow", "#FFFAFA" ),
		new Color( GwtTeaming.getMessages().springGreen(), "springGreen", "#00FF7F" ),
		new Color( GwtTeaming.getMessages().steelBlue(), "steelBlue", "#4682B4" ),
		new Color( GwtTeaming.getMessages().tan(), "tan", "#D2B48C" ),
		new Color( GwtTeaming.getMessages().teal(), "teal", "#008080" ),
		new Color( GwtTeaming.getMessages().thistle(), "thistle", "#D8BFD8" ),
		new Color( GwtTeaming.getMessages().tomato(), "tomato", "#FF6347" ),
		new Color( GwtTeaming.getMessages().turquoise(), "turquoise", "#40E0D0" ),
		new Color( GwtTeaming.getMessages().violet(), "violet", "#F5DEB3" ),
		new Color( GwtTeaming.getMessages().wheat(), "wheat", "#F5DEB3" ),
		new Color( GwtTeaming.getMessages().white(), "white", "#FFFFFF" ),
		new Color( GwtTeaming.getMessages().whiteSmoke(), "whiteSmoke", "#F5F5F5" ),
		new Color( GwtTeaming.getMessages().yellow(), "yellow", "#FFFF00" ),
		new Color( GwtTeaming.getMessages().yellowGreen(), "yellowGreen", "#9ACD32" )
	};
	
	/**
	 * 
	 */
	public ColorPickerDlg(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		Object properties )
	{
		super( autoHide, modal, xPos, yPos );
	
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().colorPickerDlgHeader(), editSuccessfulHandler, editCanceledHandler, properties ); 
	}// end ColorPickerDlg()
	

	/**
	 * Add a color to the dialog
	 */
	private void addColor( Color color, int row, HTMLTable.CellFormatter cellFormatter )
	{
		InlineLabel colorName;
		InlineLabel hexValue;
		FlowPanel flowPanel;
		
		colorName = new InlineLabel( color.getLocalizedName() );
		cellFormatter.addStyleName( row, 0, "colorPickerColorNameTD" );
		m_table.setWidget( row, 0, colorName );
		
		flowPanel = new FlowPanel();
		
		// Create a <span> that will display a box that is colored with the given color. 
		hexValue = new InlineLabel();
		hexValue.addStyleName( "colorPickerHexValueSpan" );
		hexValue.getElement().getStyle().setBackgroundColor( color.getName() );
		cellFormatter.addStyleName( row, 1, "colorPickerHexValueTD" );
		flowPanel.add( hexValue );
		
		// Create a <span> that will hold the hex value of the color.
		hexValue = new InlineLabel( color.getHexValue() );
		flowPanel.add( hexValue );
		
		m_table.setWidget( row, 1, flowPanel );
	}// end addColor()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		HTMLTable.CellFormatter cellFormatter;
		ClickHandler clickHandler;
		FlowPanel mainPanel = null;
		FlowPanel contentPanel = null;
		int i;
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		// Create a panel that will hold the table that holds the colors.
		contentPanel = new FlowPanel();
		contentPanel.addStyleName( "colorPickerContentPanel" );
		mainPanel.add( contentPanel );
		
		// Create a table for the colors to live in.
		m_table = new FlexTable();
		m_table.setCellSpacing( 0 );
		m_table.setBorderWidth( 0 );
		m_table.setWidth( "100%" );
		m_table.addStyleName( "dlgContent" );
		m_table.addStyleName( "colorPickerTable" );
		
		// Add a ClickHandler to the table so we will know when the user clicks on a color.
		clickHandler = new ClickHandler()
		{
			/**
			 * 
			 */
			public void onClick( ClickEvent event )
			{
				HTMLTable.Cell cell;
				
				// Get the cell the user clicked on.
				cell = m_table.getCellForEvent( event );
				if ( cell != null )
				{
					int row;
					
					// Get the row the user clicked on.
					row = cell.getRowIndex();
					
					// Did the user click on a color?
					if ( row > 0 )
					{
						// Yes, select it.
						selectColor( row );
					}
				}
			}// onClick()
		};
		m_table.addClickHandler( clickHandler );

		cellFormatter = m_table.getCellFormatter();
		
		// Add the column headings to the table.
		{
			InlineLabel heading;
			HTMLTable.RowFormatter rowFormatter;
			
			rowFormatter = m_table.getRowFormatter();
			rowFormatter.addStyleName( 0, "colorPickerHeaderTR" );
			
			heading = new InlineLabel( GwtTeaming.getMessages().colorName() );
			m_table.setWidget( 0, 0, heading );
			cellFormatter.addStyleName( 0, 0, "colorPickerHeaderTD" );
			
			heading = new InlineLabel( GwtTeaming.getMessages().hexValue() );
			m_table.setWidget( 0, 1, heading );
			cellFormatter.addStyleName( 0, 1, "colorPickerHeaderTD" );
		}
		
		contentPanel.add( m_table );
		
		// Add the colors we know about to the dialog.
		for (i = 0; i < m_colors.length; ++i)
		{
			addColor( m_colors[i], i+1, cellFormatter );
		}
		

		init( props );

		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Return the selected color.
	 */
	public Object getDataFromDlg()
	{
		// Make sure we have a selected color.
		if ( m_selectedColorRow == -1 || m_selectedColorRow == 0 || m_selectedColorRow > m_colors.length )
		{
			Window.alert( "The selected color is invalid.  This should never happen." );
			return m_colors[0];
		}
		
		return m_colors[m_selectedColorRow-1];
	}// end getDataFromDlg()
	
	
	/**
	 * We don't have a widget to give the focus to. 
	 */
	public FocusWidget getFocusWidget()
	{
		return null;
	}// end getFocusWidget()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the given object.
	 * Currently there is nothing to initialize.
	 */
	public void init( Object props )
	{
		// Select the first color in the table.
		selectColor( 1 );
	}// end init()
	
	
	/**
	 * Select the color in the given row in the table.
	 */
	private void selectColor( int row )
	{
		HTMLTable.CellFormatter cellFormatter;
		
		cellFormatter = m_table.getCellFormatter();
		
		// Yes
		// Do we have a currently selected color?
		if ( m_selectedColorRow != -1 )
		{
			// Yes, remove the highlight
			cellFormatter.removeStyleName( m_selectedColorRow, 0, "color-control-bg" );
			cellFormatter.removeStyleName( m_selectedColorRow, 1, "color-control-bg" );
		}
		
		// Highlight the newly selected color.
		m_selectedColorRow = row;
		cellFormatter.addStyleName( row, 0, "color-control-bg" );
		cellFormatter.addStyleName( row, 1, "color-control-bg" );
	}// end selectColor()
}// end ColorPickerDlg
