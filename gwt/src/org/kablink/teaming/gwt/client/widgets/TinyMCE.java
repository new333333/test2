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
package org.kablink.teaming.gwt.client.widgets;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 *
 * A wrapper widget for using tinyMCE.
 */
public class TinyMCE extends Composite
{
    private TextArea m_ta;
    private String m_id;
    private boolean m_loaded = false;
    private AbstractTinyMCEConfiguration m_config = null;
	
    /**
     * 
     * @param width
     * @param height
     */
    public TinyMCE( AbstractTinyMCEConfiguration config, int width, int height )
    {
        super();

        VerticalPanel panel;

        m_config = config;
        
        panel = initTinyMCE( width, height );

        initWidget( panel );
    }// end TinyMCE()

    
	/**
     * Wrapper for the native URL encoding methods
     * @param text - the text to encode
     * @return the encoded text
     */
    protected native String encodeURIComponent(String text) /*-{
        return encodeURIComponent(text);
    }-*/;

    
    /**
     * focusMCE() -
     *
     * Use this to set the focus to the MCE area
     * @param id - the element's ID
     */
    protected native void focusMCE(String id) /*-{
        $wnd.tinyMCE.execCommand('mceFocus', true, id);
    }-*/;

    
    /**
     * @param elementId
     * @return
     */
    protected static native String getEditorContents( String id ) /*-{
    	var editor;
    	
    	editor = $wnd.tinyMCE.get( id );

		//~JW:  return editor.getContent();
		return editor.getContent( {format: 'raw'} );
    }-*/;
    
    
    /**
     * @return the MCE element's ID
     */
    public String getID()
    {
        return m_id;
    }// end getID()

    
    /**
     * @return
     */
    public String getText()
    {
    	String text;
    	
//~JW:	getTextData( m_id );
//~JW:	text = m_ta.getText();
//~JW:	Window.alert( "text1: " + text );
        
        text = getEditorContents( m_id );
        
        return text;
    }// end getText()

    
    /**
     * 
     */
    protected native void getTextData() /*-{
        $wnd.tinyMCE.triggerSave();
    }-*/;

    
    /**
     * @param id - the element's ID
     */
    protected native void getTextData( String id ) /*-{
		$wnd.tinyMCE.activeEditor = $wnd.tinyMCE.get(id);
		$wnd.tinyMCE.activeEditor.save();
		$wnd.tinyMCE.triggerSave();
	}-*/;

    
	/**
	 * Initialize Tiny MCE editor
	 * http://wiki.moxiecode.com/index.php/TinyMCE:Configuration for details
	 */
	protected native void init( AbstractTinyMCEConfiguration conf ) /*-{
		$wnd.tinyMCE.init({
				// General options
				paste_postprocess: function(pi,o){o.node.innerHTML=$wnd.TinyMCEWebKitPasteFixup("paste_postprocess",o.node.innerHTML);},
				mode : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getMode()(),
				theme : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getTheme()(),
				onpageload : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getOnPageLoad()(),
				language : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getLanguage()(),
				content_css : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getContentCss()(),
				relative_urls: conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getRelativeUrls()(),
				remove_script_host : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getRemoveScriptHost()(),
				width : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getWidth()(),
				accessibility_warnings : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getAccessibilityWarnings()(),
				accessibility_focus : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getAccessibilityFocus()(),
				entities : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getEntities()(),
				gecko_spellcheck : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getGeckoSpellCheck()(),
				plugins : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getPlugins()(),

				pdw_toggle_on : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getPdwToggle()(),
				pdw_toggle_toolbars : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getPdwToggleToolbars()(),
				
				// Theme options
				// excluded buttons: ,fontselect,fontsizeselect,preview,image,help,|,forecolor,backcolor tablecontrols,|,,emotions,media,|,print
				theme_advanced_path : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedPath()(),
				theme_advanced_toolbar_location : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedToolbarLocation()(),
				theme_advanced_buttons1 : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedButtons1()(),
				theme_advanced_buttons2 : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedButtons2()(),
				theme_advanced_buttons3 : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedButtons3()(),
				//theme_advanced_buttons4 : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedButtons4()(),
				theme_advanced_buttons1_add : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedButtons1Add()(),
				theme_advanced_buttons2_add : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedButtons2Add()(),
				//theme_advanced_buttons3_add : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedButtons3Add()(),
				theme_advanced_blockformats : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedBlockFormats()(),
				theme_advanced_toolbar_align : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedToolbarAlign()(),
				theme_advanced_fonts : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedFonts()(),
				theme_advanced_more_colors : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedMoreColors()(),
				theme_advanced_row_height : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedRowHeight()(),
				theme_advanced_resize_horizontal : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedResizeHorizontal()(),
				theme_advanced_resizing_use_cookie : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedResizingUseCookie()(),
				theme_advanced_font_sizes : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedFontSizes()(),

				theme_advanced_statusbar_location : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedStatusbarLocation()(),
				theme_advanced_resizing : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedResizing()(),
				theme_advanced_styles : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedStyles()(),
				theme_advanced_disable : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getThemeAdvancedDisable()(),
				convert_fonts_to_spans : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getConvertFontsToSpans()(),

				document_base_url : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getDocumentBaseUrl()()
				
				//skin : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getSkin()(),
				//entity_encoding : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getEntityEncoding()(),
	
				// Drop lists for link/image/media/template dialogs
				//template_external_list_url : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getTemplateExternalListUrl()(),
				//external_link_list_url : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getExternalLinkListUrl()(),
				//external_image_list_url : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getExternalImageListUrl()(),
				//media_external_list_url : conf.@org.kablink.teaming.gwt.client.widgets.AbstractTinyMCEConfiguration::getMediaExternalListUrl()(),
			});
	}-*/;


    /**
     * 
     */
    private VerticalPanel initTinyMCE( int width, int height )
    {
    	VerticalPanel panel;
    	
        panel = new VerticalPanel();
        panel.setWidth( "100%" );

        m_id = HTMLPanel.createUniqueId();
        m_ta = new TextArea();
        m_ta.setCharacterWidth( width );
        m_ta.setVisibleLines( height );
        DOM.setElementAttribute( m_ta.getElement(), "id", m_id );
        DOM.setStyleAttribute( m_ta.getElement(), "width", "100%" );
        panel.add( m_ta );
        
        return panel;
    }// end initTinyMCE()
    

    /**
     * @see com.google.gwt.user.client.ui.Widget#onLoad()
     */
    protected void onLoad()
    {
        super.onLoad();
        
        // Are we initialized?
        if ( m_loaded == false )
        {
    		Scheduler.ScheduledCommand cmd;

        	// No
    		cmd = new Scheduler.ScheduledCommand()
    		{
    			public void execute()
    			{
                    setWidth( "100%" );
                    // Initialize the tinyMCE editor
                    init( m_config );
                    
                    // Add any language packs to the tinyMCE editor.
                    m_config.addLanguagePacks();
                    
                    // Associate the textarea with a tinyMCE editor.
                    setTextAreaToTinyMCE( m_id );
                    
                    m_loaded = true;
    			}
    		};
    		Scheduler.get().scheduleDeferred( cmd );
        }
    }// onLoad()

    
    /**
     * Remove a tiny MCE editing field from a text area
     * @param id - the text area's ID
     */
    protected native void removeMCE(String id) /*-{
        $wnd.tinyMCE.execCommand('mceRemoveControl', true, id);
    }-*/;

    
    /**
     * Use this if reusing the same MCE element, but losing focus
     */
    protected native void resetMCE() /*-{
        $wnd.tinyMCE.execCommand('mceResetDesignMode', true);
    }-*/;

    
    /**
     * @param elementId
     * @param html
     */
    protected static native void setEditorContents( String elementId, String html ) /*-{
//~JW:	$wnd.tinyMCE.execInstanceCommand( elementId, 'mceSetContent', false, html, false );
 		var editor;
 		
 		editor = $wnd.tinyMCE.get( elementId );
 		if ( editor != null )
 			editor.setContent( html, null );
 		else
 			alert( 'In setEditorContents(), editor is null' );
    }-*/;

    
    /**
     * @param enabled
     */
    public void setEnabled(boolean enabled)
    {
    	m_ta.setEnabled( enabled );
    }// end setEnabled()
    
    
    /**
     * 
     */
    public void setFocus()
    {
    	//~JW:  The following calling is throwing a JavaScript exception.  Not sure why
        //~JW:  focusMCE( m_id );
    }// end setFocus()
    
    
    /**
     * @param text
     */
    public void setText( String text )
    {
    	m_ta.setText( text );
        //~JW:  setEditorContents( m_id, text );
    }// end setText()

    
    /**
     * Change a text area to a tiny MCE editing field
     * @param id - the text area's ID
     */
    protected native void setTextAreaToTinyMCE(String id) /*-{
        $wnd.tinyMCE.execCommand('mceAddControl', true, id);
    }-*/;

    
    /**
     * Unload this MCE editor instance from active memory.
     * I use this in the onHide function of the containing widget. This helps
     * to avoid problems, especially when using tabs.
     */
    public void unload()
    {
        unloadMCE( m_id );
        m_loaded = false;
    }// end unload()

    
    /**
     * @param id - The element's ID
     * JSNI method to implement unloading the MCE editor instance from memory
     */
    protected native void unloadMCE(String id) /*-{
        $wnd.tinyMCE.execCommand('mceRemoveControl', false, id);
    }-*/;

    
    /**
     * Update the internal referenced content. Use this if you programatically change
     * the original text area's content (eg. do a clear)
     * @param id - the ID of the text area that contains the content you wish to copy
     */
    protected native void updateContent(String id) /*-{
        $wnd.tinyMCE.selectedInstance = $wnd.tinyMCE.getInstanceById(id);
        $wnd.tinyMCE.setContent($wnd.document.getElementById(id).value);
    }-*/;

}// end TinyMCE
