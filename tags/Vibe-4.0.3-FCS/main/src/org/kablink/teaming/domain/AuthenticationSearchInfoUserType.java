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
package org.kablink.teaming.domain;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.hibernate.HibernateException;
import org.kablink.teaming.domain.LdapConnectionConfig.HomeDirCreationOption;
import org.kablink.teaming.util.XmlUtil;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.orm.hibernate3.support.ClobStringType;

public class AuthenticationSearchInfoUserType extends ClobStringType { 

	private final static String CUSTOM_CONFIG_OPTION = "customConfig";
	private final static String HOME_DIR_ATTRIBUTE_OPTION = "homeDirAttribute";
	private final static String CUSTOM_ATTRIBUTE_OPTION = "customAttribute";
	private final static String DONT_CREATE_OPTION = "dontCreate";
	
	private final static String HOME_DIR_CONFIG_TAG_NAME = "homeDirConfig";
	private final static String CUSTOM_CONFIG_TAG_NAME = "customConfig";
	private final static String CUSTOM_ATTRIBUTE_TAG_NAME = "customAttribute";
	
	private final static String CREATION_OPTION_ATTRIBUTE_NAME = "creationOption";
	private final static String NET_FOLDER_SERVER_NAME_ATTRIBUTE_NAME = "netFolderServerName";
	private final static String NET_FOLDER_PATH_ATTRIBUTE_NAME = "netFolderPath";
	private final static String CUSTOM_ATTRIBUTE_ATTRIBUTE_NAME = "attributeName";
	
    @Override
	public Class returnedClass() { 
        return List.class; 
    }
 
    @Override
    protected Object nullSafeGetInternal(ResultSet resultSet, String[] names, Object owner, LobHandler lobHandler) throws SQLException{ 
        List<LdapConnectionConfig.SearchInfo> result = new LinkedList<LdapConnectionConfig.SearchInfo>();
        if (!resultSet.wasNull()) {
			String searches = lobHandler.getClobAsString(resultSet,names[0]);
    		try {
    			Document doc = XmlUtil.parseText(searches);
    			for(Object o : doc.selectNodes("//search")) {
    				LdapConnectionConfig.SearchInfo searchInfo;
    				Node homeDirConfigNode;
    				Node node = (Node) o;
    				
    				String baseDn = node.selectSingleNode("baseDn").getText();
    				String filter = node.selectSingleNode("filter").getText();
    				boolean searchSubtree = "true".equals(node.selectSingleNode("@searchSubtree").getText());
    				searchInfo = new LdapConnectionConfig.SearchInfo(baseDn, filter, searchSubtree);

    				// Is there a <homeDirConfig> node?
    				homeDirConfigNode = node.selectSingleNode( HOME_DIR_CONFIG_TAG_NAME );
    				if ( homeDirConfigNode != null )
    				{
    					LdapConnectionConfig.HomeDirConfig homeDirConfig;
    					Node creationOptionNode;
    					
    					// Yes
    					homeDirConfig = new LdapConnectionConfig.HomeDirConfig();
    					homeDirConfig.setCreationOption( HomeDirCreationOption.USE_HOME_DIRECTORY_ATTRIBUTE );
    					
    					// Get the "creationOption" attribute.
    					creationOptionNode = homeDirConfigNode.selectSingleNode( "@" + CREATION_OPTION_ATTRIBUTE_NAME );
    					if ( creationOptionNode != null )
    					{
    						String value;
    						
    						value = creationOptionNode.getText();
    						if ( value != null )
    						{
    							if ( value.equalsIgnoreCase( CUSTOM_CONFIG_OPTION ) )
    							{
    								Node customConfigNode;

    								// Get the <customConfig> element
    								customConfigNode = homeDirConfigNode.selectSingleNode( CUSTOM_CONFIG_TAG_NAME );
    								if ( customConfigNode != null )
    								{
    									String netFolderServerName = null;
    									String path = null;
    									Node attribNode;
    									
    									// Get the "netFolderServerName" attribute
    									attribNode = customConfigNode.selectSingleNode( "@" + NET_FOLDER_SERVER_NAME_ATTRIBUTE_NAME );
    									if ( attribNode != null )
    										netFolderServerName = attribNode.getText();
    									
    									// Get the "netFolderPath" attribute
    									attribNode = customConfigNode.selectSingleNode( "@" + NET_FOLDER_PATH_ATTRIBUTE_NAME );
    									if ( attribNode != null )
    										path = attribNode.getText();
    									
    									if ( netFolderServerName != null && path != null )
    									{
    	    								homeDirConfig.setCreationOption( HomeDirCreationOption.USE_CUSTOM_CONFIG );
    										homeDirConfig.setNetFolderServerName( netFolderServerName );
    										homeDirConfig.setPath( path );
    									}
    								}
    							}
    							else if ( value.equalsIgnoreCase( HOME_DIR_ATTRIBUTE_OPTION ) )
    							{
    								homeDirConfig.setCreationOption( HomeDirCreationOption.USE_HOME_DIRECTORY_ATTRIBUTE );
    							}
    							else if ( value.equalsIgnoreCase( CUSTOM_ATTRIBUTE_OPTION ) )
    							{
    								Node customAttribNode;
    								
    								// Get the <customAttribute> element
    								customAttribNode = homeDirConfigNode.selectSingleNode( CUSTOM_ATTRIBUTE_TAG_NAME );
    								if ( customAttribNode != null )
    								{
    									String attribName = null;
    									Node attribNode;
    									
    									// Get the "attributeName" attribute
    									attribNode = customAttribNode.selectSingleNode( "@" + CUSTOM_ATTRIBUTE_ATTRIBUTE_NAME );
    									if ( attribNode != null )
    										attribName = attribNode.getText();
    									
    									if ( attribName != null )
    									{
    										homeDirConfig.setAttributeName( attribName );
    	    								homeDirConfig.setCreationOption( HomeDirCreationOption.USE_CUSTOM_ATTRIBUTE );
    									}
    								}
    							}
    							else if ( value.equalsIgnoreCase( DONT_CREATE_OPTION ) )
    							{
    								homeDirConfig.setCreationOption( HomeDirCreationOption.DONT_CREATE_HOME_DIR_NET_FOLDER );
    							}
    							else
    							{
    								homeDirConfig.setCreationOption( HomeDirCreationOption.USE_HOME_DIRECTORY_ATTRIBUTE );
    							}
    						}
    					}
    					
    					searchInfo.setHomeDirConfig( homeDirConfig );
    				}
    				
    				result.add( searchInfo );
    			}
    		} catch(Exception e) {
    			logger.warn("Unable to parse searches: " + searches);
    		}
        } 
        return result; 
    }
 
    @Override
	public void nullSafeSetInternal(PreparedStatement preparedStatement, int index, Object value, LobCreator lobCreator) throws SQLException { 
        if (null == value) { 
            preparedStatement.setNull(index, Types.CLOB); 
        } else {
        	List<LdapConnectionConfig.SearchInfo> searches = (List<LdapConnectionConfig.SearchInfo>) value;
    		StringBuffer xml = new StringBuffer("<searches>");
    		for(LdapConnectionConfig.SearchInfo us : searches) {
    			xml.append("<search searchSubtree=\"" + (us.isSearchSubtree()?"true":"false") + "\">");

				// If the base dn has a '&', '<', or '>' in it, the xml will not parse when we read it from the db
				// and try to create an xml document.  Wrap the base dn with <![CDATA[]]>.
    			xml.append( "<baseDn>" + wrapWithCDATA( us.getBaseDn() ) + "</baseDn>");

				// If the filter has a '&', '<', or '>' in it, the xml will not parse when we read it from the db
				// and try to create an xml document.  Wrap the filter with <![CDATA[]]>.
    			xml.append( "<filter>" + wrapWithCDATA( us.getFilter() ) + "</filter>");
    			
    			// Add the HomeDirConfig info
    			{
    				LdapConnectionConfig.HomeDirConfig homeDirConfig;
    				
    				homeDirConfig = us.getHomeDirConfig();
    				if ( homeDirConfig != null )
    				{
    					StringBuffer tmpXml;
    					
    					tmpXml = new StringBuffer( "" );
    					
        				switch ( homeDirConfig.getCreationOption() )
        				{
        				case USE_CUSTOM_CONFIG:
        					String netFolderServerName = null;
        					String netFolderPath = null;

        					netFolderServerName = homeDirConfig.getNetFolderServerName();
        					netFolderPath = homeDirConfig.getPath();
            				if ( netFolderServerName != null && netFolderServerName.length() > 0 &&
              						 netFolderPath != null && netFolderPath.length() > 0 )
          					{
                				// Add "<homeDirConfig"
                				tmpXml.append( "<" + HOME_DIR_CONFIG_TAG_NAME + " " );

   	        					// Add creationOption="customConfig"
   	            				tmpXml.append( CREATION_OPTION_ATTRIBUTE_NAME + "=\"" + CUSTOM_CONFIG_OPTION + "\" >" );
   	            				
   	            				// Add "<customConfig"
   	            				tmpXml.append( "<" + CUSTOM_CONFIG_TAG_NAME + " " );
   	            				
   	            				// Add netFolderServerName="some value"
   	            				tmpXml.append( NET_FOLDER_SERVER_NAME_ATTRIBUTE_NAME + "=\"" + netFolderServerName + "\" " );
   	            				
   	            				// Add netFolderPath="some value"
   	            				tmpXml.append( NET_FOLDER_PATH_ATTRIBUTE_NAME + "=\"" + netFolderPath + "\" />" );
   	            				
   	            				// Add </homeDirConfig>
   	            				tmpXml.append( "</" + HOME_DIR_CONFIG_TAG_NAME + ">" );
   	            				
   	            				xml.append( tmpXml );
          					}
        					break;
        				
        				case USE_CUSTOM_ATTRIBUTE:
        					String customAttribName = null;
        					
        					customAttribName = homeDirConfig.getAttributeName();
        					
        					if ( customAttribName != null && customAttribName.length() > 0 )
        					{
                				// Add "<homeDirConfig"
        						tmpXml.append( "<" + HOME_DIR_CONFIG_TAG_NAME + " " );

                				// Add creationOption="customAttribute"
        						tmpXml.append( CREATION_OPTION_ATTRIBUTE_NAME + "=\"" + CUSTOM_ATTRIBUTE_OPTION + "\" >" );
	            				
	            				// Add "<customAttribute"
        						tmpXml.append( "<" + CUSTOM_ATTRIBUTE_TAG_NAME + " ");
	            				
	            				// Add customAttributeName="some value"
        						tmpXml.append( CUSTOM_ATTRIBUTE_ATTRIBUTE_NAME + "=\"" + customAttribName + "\" />" );
        						
   	            				// Add </homeDirConfig>
   	            				tmpXml.append( "</" + HOME_DIR_CONFIG_TAG_NAME + ">" );

   	            				xml.append( tmpXml );
        					}
        					break;
        				
        				case DONT_CREATE_HOME_DIR_NET_FOLDER:
            				// Add "<homeDirConfig"
            				tmpXml.append( "<" + HOME_DIR_CONFIG_TAG_NAME + " " );

        					// Add creationOption="dontCreate"
            				tmpXml.append( CREATION_OPTION_ATTRIBUTE_NAME + "=\"" + DONT_CREATE_OPTION + "\" />" );
            				
            				xml.append( tmpXml );
        					break;

        				case USE_HOME_DIRECTORY_ATTRIBUTE:
        				case UNKNOWN:
        				default:
            				// Add "<homeDirConfig"
        					tmpXml.append( "<" + HOME_DIR_CONFIG_TAG_NAME + " " );

            				// Add creationOption="homeDirAttribute"
        					tmpXml.append( CREATION_OPTION_ATTRIBUTE_NAME + "=\"" + HOME_DIR_ATTRIBUTE_OPTION + "\" />" );
        					
        					xml.append( tmpXml );
        					break;
        				}
    				}
    			}

    			xml.append("</search>");
    		}
    		xml.append("</searches>");

            lobCreator.setClobAsString(preparedStatement,index, xml.toString()); 
        } 
    } 
 
    @Override
	public Object deepCopy(Object value) throws HibernateException{ 
        return new LinkedList<LdapConnectionConfig.SearchInfo>((List<LdapConnectionConfig.SearchInfo>) value); 
    } 
 
    @Override
	public boolean isMutable() { 
        return true; 
    } 
 
    @Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException { 
         return deepCopy(cached);
    } 

    @Override
	public Serializable disassemble(Object value) throws HibernateException { 
        return (Serializable)deepCopy(value); 
    } 
 
    @Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException { 
        return new LinkedList<LdapConnectionConfig.SearchInfo>((List<LdapConnectionConfig.SearchInfo>) original); 
    }

	/**
	 * Wrap the given text with <![CDATA[ ]]>
	 */
	private String wrapWithCDATA( String str )
	{
		StringBuffer	wrappedStr;
		
		wrappedStr = new StringBuffer( "<![CDATA[" );
		if ( str != null && str.length() > 0 )
			wrappedStr.append( str );
		
		wrappedStr.append( "]]>" );
		
		return wrappedStr.toString();
	}// end wrapWithCDATA()
}
