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

/**
 * Copyright (c) 2000-2005 Liferay, LLC. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.kablink.util;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <a href="Html.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Brian Wing Shun Chan
 * @author  Clarence Shen
 * @version $Revision: 1.10 $
 */
public class Html {
	private final static String scriptObjs = "script|embed|object|applet|html|head|body|meta|xml|blink|link|iframe|frame|frameset|ilayer|layer|base";
	private final static Pattern scriptsPattern1 = Pattern.compile("(<[\\s]*(" + scriptObjs + ")(?:[\\s]+[^>]*/>|[\\s]*/>))", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
	private final static Pattern scriptsPattern2 = 
		Pattern.compile("(<[\\s]*(" + scriptObjs + ")(?:[\\s]+[^>]*>|[\\s]+[^>]*>).*<[\\s]*/[\\s]*\\2[\\s]*[^>]*>)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	private final static String[] startMUArray = {"[[", "<", "{{"};
	private final static String[] endMUArray   = {"]]", ">", "}}"};

	public static String formatFrom(String text) {
		if (text == null) {
			return null;
		}

		// Optimize this

		text = StringUtil.replace(text, "&#42;", "*");
		text = StringUtil.replace(text, "&#47;", "/");
		text = StringUtil.replace(text, "&#58;", ":");
		text = StringUtil.replace(text, "&#63;", "?");

		return text;
	}

	private static String formatToImpl(String text, boolean textIsUrl) {
		if (text == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		char c;

		for (int i = 0; i < text.length(); i++) {
			c = text.charAt(i);

			switch (c) {
				case '&':
					if (textIsUrl)
					     sb.append("&"    );
					else sb.append("&amp;");
					break;
	
				case '<':
					sb.append("&lt;");
					break;
	
				case '>':
					sb.append("&gt;");
					break;
	
				case '\'':
					sb.append("&#39;");
					break;
	
				case '\"':
					sb.append("&quot;");
					break;
	
				default:
					if (((int)c) > 255) {
						sb.append("&#").append(((int)c)).append(";");
					}
					else {
						sb.append(c);
					}
			}
		}

		return sb.toString();
	}
	
	public static String formatTo(String text) {
		// Always use the implementation form of the method.
		return formatToImpl(text, false);	// false -> text is not a URL.
	}

	public static String formatUrl(String text) {
		// Always use the implementation form of the method.
		return formatToImpl(text, true);	// true -> text is a URL.
	}

	public static String stripComments(String text) {
		if (text == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		int x = 0;
		int y = text.indexOf("<!--");

		while ((y != -1) && (x != -1)) {
			sb.append(text.substring(x, y));
			x = text.indexOf("-->", y);
			if (x != -1)
			{
				x += 3;
				y = text.indexOf("<!--", x);
			}
		}

		if (y == -1) {
			sb.append(text.substring(x, text.length()));
		}

		return sb.toString();

		/*
		int x = text.indexOf("<!--");
		int y = text.indexOf("-->");

		if (x != -1 && y != -1) {
			return stripComments(
				text.substring(0, x) + text.substring(y + 3, text.length()));
		}
		*/

		/*
		Perl5Util util = new Perl5Util();

		text = util.substitute("s/<!--.*-->//g", text);
		*/

		//return text;
	}

	public static String stripScripts(String text) {
		//Check for <script/>, <embed/>, <iframe/>, ...
		Matcher matcher1 = scriptsPattern1.matcher(text);
		if (matcher1.find()) {
			text = matcher1.replaceAll("");
		}
		//Check for <script ...>...</script>, <embed ...>...</embed>, <iframe ...>...</iframe>, ...
		Matcher matcher2 = scriptsPattern2.matcher(text);
		if (matcher2.find()) {
			text = matcher2.replaceAll("");
		}
		return text.trim();
	}

	public static String stripHtml(String text) {
		if (text == null) {
			return null;
		}

		text = stripComments(text).trim();

		StringBuffer sb = new StringBuffer();
		int x = 0;
		int y = text.indexOf("<");

		while (y != -1) {
			sb.append(text.substring(x, y));

			x = text.indexOf(">", y) + 1;

			if (x <= 0 || x < y) {

				// <b>Hello</b

				break;
			}

			y = text.indexOf("<", x);
		}

		if (y == -1) {
			sb.append(text.substring(x, text.length()));
		}

		String reply = sb.toString();
        reply = reply.replaceAll("&nbsp;", " ");
        reply = reply.replaceAll("&#39;", "'");
        return reply;
	}

	//This method is similar to the stripHTML, but does not strip the img tag
	public static String restrictiveStripHtml(String text) {
		if (text == null) {
			return null;
		}

		text = stripScripts(text);
		text = stripComments(text);
		
		int intTextLength = text.length();
		String strTag = "img";

		StringBuffer sb = new StringBuffer();
		int x = 0;
		int y = text.indexOf("<");
		int loopDetector = 2000;

		while (y != -1 && loopDetector > 0) {
			loopDetector--;
			//Hemanth
			//We are trying to strip all the HTML tags except img. We except the HTML to be well-formed.
			
			int intStartTagCheck = y + strTag.length() + 1;
			int intEndTagCheck = y + strTag.length() + 2;

			//This code makes sure the img tag is not removed
			if ( ( (intStartTagCheck < intTextLength) && (text.substring(y, intStartTagCheck)).equalsIgnoreCase("<"+strTag) ) || 
				 ( (intEndTagCheck < intTextLength) && (text.substring(y, intEndTagCheck)).equalsIgnoreCase("</"+strTag) ) ) {
				//If we encounter a tag <img or </img, then we need to find the equivalent > tag and then append the whole
				//img tag as part of the string buffer.  
				int intEndIndex = text.indexOf(">", y);
				
				if (intEndIndex != -1 && x < intTextLength) {
					sb.append(text.substring(x, intEndIndex+1));
				}
				//we are incrementing the x value by 1 from the intEndIndex, because intEndIndex is index of > and we want
				//to continue from the next character of >
				x = intEndIndex + 1;
			} else {
				//This code removes the HTML tags apart from img tags 
				if (x < intTextLength) {
					sb.append(" ");
					sb.append(text.substring(x, y));
				}
				x = text.indexOf(">", y) + 1;
			}
			
			if (x < y || x >= intTextLength) {
				// <b>Hello</b
				break;
			}

			y = text.indexOf("<", x);
		}

		if (y == -1 && x < intTextLength) {
			sb.append(text.substring(x, intTextLength));
		}

		String reply = sb.toString();
		reply = reply.replaceAll("&nbsp;", " ");
		reply = reply.replaceAll("&#39;",  "'");
		return reply.trim();
	}	
	
	/**
	 * Given a blob of plain text, converts all white space formatting
	 * (e.g., CR's, LF's, TAB's, ...) to HTML.
	 */
	public static String plainTextToHTML(String plainText) {
		if (null == plainText) {
			plainText = "";
		}
		
		// TODO:  This needs to be implemented with a real
		//         implementation.
		return ("<pre>" + StringEscapeUtils.escapeHtml(plainText) + "</pre>");
	}

	public static String plainTextToHTML2(String plainText) {
		if (null == plainText) {
			plainText = "";
		}
        String html = StringEscapeUtils.escapeHtml(plainText);
        StringBuilder builder = new StringBuilder(html.length()*2);
        for (char ch : html.toCharArray()) {
            if (ch=='\r') {

            } else if (ch=='\n') {
                builder.append("<br>");
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
	}

	/**
	 * Given a chunk of HTML, strips it down to a maximum number of
	 * words for displaying in the Teaming feeds and activity streams.
	 * 
	 * Note:  The implementation of this method was moved from
	 * TextFormat.java into here to make it accessible to both the
	 * <ssf:textFormat> tag implementation and the
	 * GwtActivityStreamHelper implementation.
	 * 
	 * @param html
	 * @param intMaxAllowedWords
	 * 
	 * @return
	 */
	public static String wordStripHTML(String html, int intMaxAllowedWords) {
		// Do we have anything to strip?
		int htmlLen;
		if (null != html) {
			html    = html.trim();
			htmlLen = html.length();
		}
		else {
			htmlLen = 0;
		}
		if (0 == htmlLen) {
			// No!  Bail.
			return html;
		}
		
		String strStrippedHTMLContent = restrictiveStripHtml(html);		
		String summary = "";
		String [] words = strStrippedHTMLContent.split(" ");
		ArrayList<String> muArrList = new ArrayList<String>();
		
		//Looping through the word list
		//We are excepting the tags and HTML code if any to be well formed.
		for (int i = 0; i < words.length; i++) {
			String strWord = words[i];
			if (strWord.equals("")) continue;
			
			summary = summary + " " + strWord;
			String strLCWord = strWord.toLowerCase();

			//Hemanth:
			//we are trying to make sure the words do not get truncated half way between tags like
			//[[ ]] - Title Hyper Link Tag and <img /> - Image Tag
			//For this, we add the tags that we encounter in a array list and
			//when we encounter an end tag for that, we remove the tag from the arraylist
			//So when we reach the max words limit, we check to see if the arraylist size is zero,
			//if so, we assume, there is no open tag and display the summary information
			//if not, we assume, we keep going until the array list size becomes zero.
			//when the array list size is zero, we assume that there is no more open tags
			
			//Code for adding information about the open tags in the array list
			for (int j = 0; j < startMUArray.length; j++) {
				String strStartTag = startMUArray[j];
				int intStartIdx = strLCWord.indexOf(strStartTag);
				if (intStartIdx != -1) {
					muArrList.add(strStartTag);
				}
			}

			//Code for removing tags from the arraylist, when the closing tag is encountered
			for (int j = 0; j < endMUArray.length; j++) {
				String strEndTag = endMUArray[j];
				int intEndIdx = strLCWord.indexOf(strEndTag);
				if (intEndIdx != -1) {
					String strStartTag = startMUArray[j];
					for (int k = 0; k < muArrList.size(); k++) {
						String strEntry = (String) muArrList.get(k);
						if (strStartTag.equalsIgnoreCase(strEntry)) {
							muArrList.remove(k);
							break;
						}
					}
				}
			}

			//Limit the summary to intMaxAllowedWords words
			//we are checking to see if the arraylist size is zero, to ensure that there are no open tags
			if (i >= (intMaxAllowedWords - 1) && (muArrList.size() == 0) ) {
				//If the actual text length is greater than the specified textMaxWords allowed then we
				//will append the "..." to the summary displayed. If not we will not append the "...".
				if (i < words.length - 1) summary = summary + "...";
				break;
			}
		}
		
		return summary;
	}
	
	//Routine to quote all special characters in a string ("<>&)
	public static String replaceSpecialChars( String s ) {
		s = s.replaceAll( "&quot;", "\"" );
		s = s.replaceAll( "&lt;", "<" );
		s = s.replaceAll( "&gt;", ">" );
		s = s.replaceAll( "&amp;", "&" );
		
		s = s.replaceAll( "&", "&amp;" );
		s = s.replaceAll( "\"", "&quot;" );
		s = s.replaceAll( "<", "&lt;" );
		s = s.replaceAll( ">", "&gt;" );
		return s;
	}
}
