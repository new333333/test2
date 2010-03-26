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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
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
    private boolean m_initialized = false;
    private String m_setText = null;

    /**
     * 
     * @param width
     * @param height
     */
    public TinyMCE( int width, int height )
    {
        super();

        VerticalPanel panel;

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
    protected static native String getEditorContents( String elementId ) /*-{
		return $wnd.tinyMCE.get(elementId).getContent();
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
        getTextData();
//!!!        return m_ta.getText();
    	return getEditorContents( m_id );
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
	protected native void init() /*-{
		$wnd.tinyMCE.init( {
				// General options
				mode : "textareas",
				theme : "advanced",
				language: 'en',
				content_css: "http://jwootton2.provo.novell.com:8080/ssf/s/viewCss?sheet=editor",
				relative_urls: false, 
				remove_script_host : false,
				document_base_url : "http://jwootton2.provo.novell.com:8080/ssf/s/readFile/workspace/40/-/1269355009029/last/",
				width: "100%",
				accessibility_warnings: true,
				accessibility_focus: true,
				entities:  "39,#39,34,quot,38,amp,60,lt,62,gt",
				gecko_spellcheck : true,
				plugins: "compat2x,table,ss_addimage,preview,paste,ss_wikilink,ss_youtube",
				theme_advanced_buttons3_add : "pastetext,pasteword,selectall",
				theme_advanced_toolbar_location: "top", theme_advanced_toolbar_align: "top", 
				theme_advanced_toolbar_align: "left", theme_advanced_statusbar_location: "bottom", 
				theme_advanced_resizing: true, 
				convert_fonts_to_spans: true,
				theme_advanced_styles: "8px=ss_size_8px;9px=ss_size_9px;10px=ss_size_10px;11px=ss_size_11px;12px=ss_size_12px;13px=ss_size_13px;14px=ss_size_14px;15px=ss_size_15px;16px=ss_size_16px;18px=ss_size_18px;20px=ss_size_20px;24px=ss_size_24px;28px=ss_size_28px;32px=ss_size_32px",
				theme_advanced_buttons1_add: "forecolor,backcolor",
				theme_advanced_buttons2_add: "pastetext,pasteword,ss_addimage,ss_wikilink,ss_youtube",
				theme_advanced_path: false,
				theme_advanced_buttons3_add: "tablecontrols", 
				theme_advanced_disable : "image,advimage",
				theme_advanced_resizing_use_cookie : true });

		$wnd.tinyMCE.addI18n( 'en.ss_addimage_dlg',{
													overQuota : " ",
													srcFile : "Upload an Image",
													addFile : "Upload an Image File",
													addUrl : "Select an Attached Image File",
													imageName : "Image Name",
													imageSelectBox : "ss_imageSelectionsss_htmleditor_description",
													missing_img : "No image file was selected"
													});

		$wnd.tinyMCE.addI18n( 'en.ss_addimage',{
												desc_no : "Images cannot be inserted because your quota has been exceeded."
												});
		
		$wnd.tinyMCE.addI18n('en.ss_wikilink',{
												desc : "Insert a Link to Another Teaming Page"
												});

		$wnd.tinyMCE.addI18n('en.ss_youtube',{
												desc : "Embed a Video",
												youTubeUrl : "URL of a Video on YouTube",
												dimensions : "Dimensions"
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
//!!!        m_ta.addStyleName( "mceEditable" );
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
        if ( m_initialized == false )
        {
            Command cmd;

        	// No
            cmd = new Command()
            {
            	/**
            	 * 
            	 */
                public void execute()
                {
                    setWidth( "100%" );
                    init();
                    setTextAreaToTinyMCE( m_id );
                    
                    m_initialized = true;
                }
            };
            DeferredCommand.addCommand( cmd );
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
		$wnd.tinyMCE.execInstanceCommand( elementId, 'mceSetContent', false, html, false );
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
        focusMCE( m_id );
    }// end setFocus()
    
    
    /**
     * @param text
     */
    public void setText( String text )
    {
    	// Are we initialized?
    	if ( m_initialized )
    	{
    		// Yes
            m_ta.setText(text);
//!!!           	setEditorContents( m_id, text );
    	}
    	else
    	{
            Command cmd;

        	// No
            m_setText = text;
            cmd = new Command()
            {
            	/**
            	 * 
            	 */
                public void execute()
                {
                	setText( m_setText );
                }
            };
            DeferredCommand.addCommand( cmd );
    	}
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
