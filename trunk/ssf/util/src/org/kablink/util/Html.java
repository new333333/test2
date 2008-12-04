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

/**
 * <a href="Html.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Brian Wing Shun Chan
 * @author  Clarence Shen
 * @version $Revision: 1.10 $
 *
 */
public class Html {

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

	public static String formatTo(String text) {
		if (text == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();
		char c;

		for (int i = 0; i < text.length(); i++) {
			c = text.charAt(i);

			switch (c) {
				case '&':
					sb.append("&amp;");
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

	public static String stripHtml(String text) {
		if (text == null) {
			return null;
		}

		text = stripComments(text);

		StringBuffer sb = new StringBuffer();
		int x = 0;
		int y = text.indexOf("<");

		while (y != -1) {
			sb.append(text.substring(x, y));

			x = text.indexOf(">", y) + 1;

			if (x < y) {

				// <b>Hello</b

				break;
			}

			y = text.indexOf("<", x);
		}

		if (y == -1) {
			sb.append(text.substring(x, text.length()));
		}

		return sb.toString();
	}

	//This method is similar to the stripHTML, but does not strip the img tag
	public static String restrictiveStripHtml(String text) {
		if (text == null) {
			return null;
		}

		text = stripComments(text);
		
		int intTextLength = text.length();
		String strTag = "img";

		StringBuffer sb = new StringBuffer();
		int x = 0;
		int y = text.indexOf("<");

		while (y != -1) {
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

		return sb.toString();
	}	
}