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


import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextArea;


/**
 * 
 * @author jwootton
 */
public class TextAreaWithMax extends TextArea
{
    private int m_maxLength;
 
    // Constructor
    public TextAreaWithMax()
    {
        super( Document.get().createTextAreaElement() );
        
        setStyleName( "gwt-TextArea" );
        sinkEvents( Event.ONPASTE | Event.ONKEYDOWN );
 
        TextAreaWithMax.this.addValueChangeHandler( new ValueChangeHandler<String>()
        {
        	@Override
        	public void onValueChange( ValueChangeEvent<String> event)
        	{
        		String newText;
        		String text;

        		TextAreaWithMax.this.setText( event.getValue() );

        		try
        		{
        			text = TextAreaWithMax.this.getText();
        		}
        		catch ( Exception e )
        		{
        			text = "";
        		}

        		newText = text.substring( 0, TextAreaWithMax.this.m_maxLength );
        		TextAreaWithMax.this.setValue( newText );
        	}
        });
    }
 
    /**
     * 
     */
    @Override
    public void onBrowserEvent( Event event )
    {
        // Checking for paste event
        if ( event.getTypeInt() == Event.ONPASTE )
        {
            Scheduler.get().scheduleDeferred( new ScheduledCommand()
            {
                @Override
                public void execute()
                {
                    ValueChangeEvent.fire( TextAreaWithMax.this, TextAreaWithMax.this.getText() );
                }
            });
 
            return;
        }

        // Checking for key down event.
        if ( event.getTypeInt() == Event.ONKEYDOWN )
        {
        	String str;
        	int keyCode;
        	
       	 	try
       	 	{
       	 		str = TextAreaWithMax.this.getText();
       	 	}
       	 	catch ( Exception e )
       	 	{
       	 		str = "";
       	 	}

       	 	keyCode = event.getKeyCode();
       	 	
        	if ( str.length() >= this.getMaxLength() &&
        			keyCode != KeyCodes.KEY_TAB &&
        			keyCode != KeyCodes.KEY_BACKSPACE &&
        			keyCode != KeyCodes.KEY_DELETE &&
        			keyCode != KeyCodes.KEY_ENTER &&
        			keyCode != KeyCodes.KEY_HOME &&
        			keyCode != KeyCodes.KEY_END &&
        			keyCode != KeyCodes.KEY_LEFT &&
        			keyCode != KeyCodes.KEY_UP &&
        			keyCode != KeyCodes.KEY_RIGHT &&
        			keyCode != KeyCodes.KEY_DOWN && 
        			keyCode != KeyCodes.KEY_SHIFT &&
        			keyCode != KeyCodes.KEY_CTRL )
        	{
        		event.preventDefault();
        	}
        	
        	return;
        }
    }

    /**
     * 
     */
    public int getMaxLength()
    {
        return m_maxLength;
    }

    /**
     * 
     */
    public void setMaxLength( int maxLength )
    {
        m_maxLength = maxLength;
    }
}