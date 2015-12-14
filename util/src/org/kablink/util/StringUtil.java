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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <a href="StringUtil.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Brian Wing Shun Chan
 * @version $Revision: 1.21 $
 *
 */
public class StringUtil {
	protected static final Log logger = LogFactory.getLog(StringUtil.class);

	/**
	 * Internal tools to pack and unpack strings.
	 */
	private static final class PackTools {
	   private static final String PACKED_HEAD     = "P:";
	   private static final String PACKED_TAIL     = "P";
	   private static final String PACKED_SPLIT    = ":";
	   private static final String PACKED_EMPTY    = "PP";
	   private static final String PACKED_ENCODING = "UTF-8";
	   private static final byte[] HEXTABLE        = {
		   ((byte) '0'),
		   ((byte) '1'),
		   ((byte) '2'),
		   ((byte) '3'),
		   ((byte) '4'),
		   ((byte) '5'),
		   ((byte) '6'),
		   ((byte) '7'),
		   ((byte) '8'),
		   ((byte) '9'),
		   ((byte) 'a'),
		   ((byte) 'b'),
		   ((byte) 'c'),
		   ((byte) 'd'),
		   ((byte) 'e'),
		   ((byte) 'f'),
	   };
	   
	   /**
	    * Encodes all characters except 0-9, A-Z, and a-z.
	    * 
	    * @param strText String to encode
	    * 
	    * @return String safe for use in StringUtil.PackTools.pack().
	    */
	   private static String packEncoder(String strText) {
	      if (null == strText) {
	         return "";
	      }

	      byte[] srcBytes;
	      try {
	         srcBytes = strText.getBytes(PACKED_ENCODING);
	      }
	      catch(UnsupportedEncodingException e) {
	    	  logger.debug("StringUtil.PackTools.packEncoder(1) using " + PACKED_ENCODING + ":  ", e);
	    	  return ("UnsupportedEncodingException using " + PACKED_ENCODING);
	      }

	      // The maximum size for the encoded string would be if every byte is
	      // converted to %dd, (three bytes)
	      int iSrcLen = srcBytes.length;
	      byte[] destBytes = new byte[iSrcLen*3];

	      int i;
	      int iDest;
	      int iSrc;
	      for (iSrc = 0, iDest = 0; iSrc < iSrcLen; iSrc += 1) {
	         byte b = srcBytes[iSrc];
	         if ((b >= 0x30 && b <=0x39)||	// 0-9
	             (b >= 0x41 && b <=0x5a)||	// A-Z
	             (b >= 0x61 && b <=0x7a)) {	// a-z
	            destBytes[iDest++] = b;
	         }
	         else if (b == 0x20) {			// space
	            destBytes[iDest++] = 0x2b;	// '+'
	         }
	         else {
	            destBytes[iDest++] = 0x25;	// '%'
	            i = ((b >> 4) & 0x000f);
	            destBytes[iDest++] = HEXTABLE[i];
	            i = (b & 0x000f);
	            destBytes[iDest++] = HEXTABLE[i];

	         }
	      }

	      try {
	         return new String(destBytes, 0, iDest, PACKED_ENCODING);
	      }
	      catch(UnsupportedEncodingException e) {
	    	  logger.debug("StringUtil.PackTools.packEncoder(2) using " + PACKED_ENCODING + ":  ", e);
	    	  return ("UnsupportedEncodingException using " + PACKED_ENCODING);
	      }
	   }
	   
	   /**
	    * Decodes strings returned from packEncoder().
	    * 
	    * @param s String to decode
	    * 
	    * @return The decoded String
	    * 
	    * @see #packEncoder(String)
	    */
	   private static String unpackDecoder(String s) {
	      byte[] byContent = s.getBytes();
	      int iContentLen = byContent.length;
	      int iOffset = 0;

	      int iSrc;
	      int iDest;
	      int iValue;
	      int iCount;

	      for (iSrc = iOffset, iDest = iOffset, iCount = 0; iCount < iContentLen; iSrc += 1, iDest += 1, iCount += 1) {
	         switch (byContent[iSrc]) {
	            // Replace the '+' with a space (' ').
	            case ((int) '+'):
	               byContent[iDest] = ((byte) ' ');
	               break;

	            // Replace the "%uXXXX" or "%XX" with the appropriate byte
	            // value(s)
	            case ((int) '%'):
	               if (byContent[iSrc+1] == 'u') {
	                  char c;
	                  int i1 = 0;
	                  int i2 = 0;

	                  i1 = (Character.digit((char) byContent[iSrc + 2], 16) * 16) + (Character.digit((char) byContent[iSrc + 3], 16));
	                  i2 = (Character.digit((char) byContent[iSrc + 4], 16) * 16) + (Character.digit((char) byContent[iSrc + 5], 16));


	                  c  = (char) ((i1&0x000000FF)<<8);
	                  c |= (char)  (i2&0x000000FF);

	                  Character ch = new Character(c);
	                  String ss = ch.toString();

	                  try {
	                     byte[] b = ss.getBytes(PACKED_ENCODING);

	                     for (int ii = 0; ii < b.length; ii += 1, iDest += 1) {
	                        byContent[iDest] = b[ii];
	                     }

	                     // Need to backup by 1 (since we increment at end
	                     // of loop.)
	                     iDest -= 1;
	                  }
	                  catch(UnsupportedEncodingException e) {
	                	  logger.debug("StringUtil.PackTools.unpackDecoder(1) using " + PACKED_ENCODING + ":  ", e);
	                	  return ("UnsupportedEncodingException using " + PACKED_ENCODING);
	                  }

	                  iSrc += 5;
	                  iCount += 5;
	               }
	               else {
	                  iValue = (Character.digit((char) byContent[iSrc + 1], 16) * 16) + (Character.digit((char) byContent[iSrc + 2], 16));
	                  iSrc += 2;
	                  iCount += 2;
	                  byContent[iDest] = ((byte) iValue);
	               }
	               break;

	            default:
	               byContent[iDest] = byContent[iSrc];
	               break;
	         }
	      }

	      String sContent = null;
	      try {
	         sContent = new String(byContent, iOffset, iDest-iOffset, PACKED_ENCODING);
	      }
	      catch(UnsupportedEncodingException e) {
	    	  logger.debug("StringUtil.PackTools.unpackDecoder(2) using " + PACKED_ENCODING + ":  ", e);
	    	  return ("UnsupportedEncodingException using " + PACKED_ENCODING);
	      }

	      return (sContent);
	   }
	   
	   /**
	    * Packs a String array into one string.  Useful for storing an
	    * arbitrary string array as a single string in the database.
	    * 
	    * @param list String array containing the values to pack.
	    *
	    * @return String containing the packed values.
	    */
	   private static String pack(String[] list) {
	      if (null == list) {
	         return PACKED_EMPTY;
	      }

	      StringBuffer sb = new StringBuffer(PackTools.PACKED_TAIL);
	      for (int i = 0; i < list.length; i += 1) {
	         sb.append(PackTools.PACKED_SPLIT).append(PackTools.packEncoder(list[i]));
	      }

	      return sb.append(PackTools.PACKED_TAIL).toString();
	   }
	   
	   /**
	    * Unpacks data packed with the pack().
	    * 
	    * @param s String containing a valid packed string.
	    *
	    * @return String array of values.
	    */
	   private static String[] unpack(String s) {
	      if (null == s) {
	         return new String[0];
	      }
	      
		  s = s.trim();
	      if (s.equals(PACKED_EMPTY)) {
	         return new String[0];
	      }
	      if (!(isPackedString(s))) {
	         return new String[]{s};
	      }

	      s = s.substring(2, s.length()-1);   // skip over first :
	      String[] list = split(s, PackTools.PACKED_SPLIT);
	      String[] ret = new String[list.length];
	      for (int i = 0; i < list.length; i += 1) {
	         ret[i] = PackTools.unpackDecoder(list[i]);
	      }

	      return ret;
	   }
	   
	   /**
	    * Checks whether s is recognized as a packed string.
	    * 
	    * @param s String to check for being packed.
	    * 
	    * @return boolean true -> s is packed.  false -> s is not packed.
	    */
	   private static boolean isPackedString(String s) {
		   return
		   		((null != s) && s.startsWith(PACKED_HEAD) && s.endsWith(PACKED_TAIL));
	   }
	}
	
	public static String add(String s, String add) {
		return add(s, add, StringPool.COMMA);
	}

	public static String add(String s, String add, String delimiter) {
		return add(s, add, delimiter, false);
	}

	public static String add(
		String s, String add, String delimiter, boolean allowDuplicates) {

		if ((add == null) || (delimiter == null)) {
			return null;
		}

		if (s == null) {
			s = StringPool.BLANK;
		}

		if (allowDuplicates || !contains(s, add, delimiter)) {
			if (Validator.isNull(s) || s.endsWith(delimiter)) {
				s += add + delimiter;
			}
			else {
				s += delimiter + add + delimiter;
			}
		}

		return s;
	}

	public static boolean contains(String s, String text) {
		return contains(s, text, StringPool.COMMA);
	}

	public static boolean contains(String s, String text, String delimiter) {
		if ((s == null) || (text == null) || (delimiter == null)) {
			return false;
		}

		if (!s.endsWith(delimiter)) {
			s += delimiter;
		}

		int pos = s.indexOf(delimiter + text + delimiter);

		if (pos == -1) {
			if (s.startsWith(text + delimiter)) {
				return true;
			}

			return false;
		}

		return true;
	}

	public static int count(String s, String text) {
		if ((s == null) || (text == null)) {
			return 0;
		}

		int count = 0;

		int pos = s.indexOf(text);

		while (pos != -1) {
			pos = s.indexOf(text, pos + text.length());
			count++;
		}

		return count;
	}

	public static boolean endsWith(String s, char end) {
		return startsWith(s, (new Character(end)).toString());
	}

	public static boolean endsWith(String s, String end) {
		if ((s == null) || (end == null)) {
			return false;
		}

		if (end.length() > s.length()) {
			return false;
		}

		String temp = s.substring(s.length() - end.length(), s.length());

		if (temp.equalsIgnoreCase(end)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static String extractChars(String s) {
		if (s == null) {
			return "";
		}

		char[] c = s.toCharArray();

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < c.length; i++) {
			if (Validator.isChar(c[i])) {
				sb.append(c[i]);
			}
		}

		return sb.toString();
	}

	public static String extractDigits(String s) {
		if (s == null) {
			return "";
		}

		char[] c = s.toCharArray();

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < c.length; i++) {
			if (Validator.isDigit(c[i])) {
				sb.append(c[i]);
			}
		}

		return sb.toString();
	}

	public static String merge(String array[]) {
		return merge(array, StringPool.COMMA);
	}

	public static String merge(String array[], String delimiter) {
		if (array == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i].trim());

			if ((i + 1) != array.length) {
				sb.append(delimiter);
			}
		}

		return sb.toString();
	}

	public static String merge(Object array[]) {
		return merge(array, StringPool.COMMA);
	}

	public static String merge(Object array[], String delimiter) {
		if (array == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < array.length; i++) {
			sb.append(array[i].toString().trim());

			if ((i + 1) != array.length) {
				sb.append(delimiter);
			}
		}

		return sb.toString();
	}

	public static String randomize(String s) {
		Randomizer r = new Randomizer();

		return r.randomize(s);
	}

	public static String remove(String s, String remove) {
		return remove(s, remove, StringPool.COMMA);
	}

	public static String remove(String s, String remove, String delimiter) {
		if ((s == null) || (remove == null) || (delimiter == null)) {
			return null;
		}

		if (Validator.isNotNull(s) && !s.endsWith(delimiter)) {
			s += delimiter;
		}

		while (contains(s, remove, delimiter)) {
			int pos = s.indexOf(delimiter + remove + delimiter);

			if (pos == -1) {
				if (s.startsWith(remove + delimiter)) {
					s = s.substring(
							remove.length() + delimiter.length(), s.length());
				}
			}
			else {
				s = s.substring(0, pos) + s.substring(pos + remove.length() +
					delimiter.length(), s.length());
			}
		}

		return s;
	}

	public static String replace(String s, char oldSub, char newSub) {
		return replace(s, oldSub, new Character(newSub).toString());
	}

	public static String replace(String s, char oldSub, String newSub) {
		if ((s == null) || (newSub == null)) {
			return null;
		}

		char[] c = s.toCharArray();

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < c.length; i++) {
			if (c[i] == oldSub) {
				sb.append(newSub);
			}
			else {
				sb.append(c[i]);
			}
		}

		return sb.toString();
	}

	public static String replace(String s, String oldSub, String newSub) {
		if ((s == null) || (oldSub == null) || (newSub == null)) {
			return null;
		}

		int y = s.indexOf(oldSub);

		if (y >= 0) {
			StringBuffer sb = new StringBuffer();
			int length = oldSub.length();
			int x = 0;

			while (x <= y) {
				sb.append(s.substring(x, y));
				sb.append(newSub);
				x = y + length;
				y = s.indexOf(oldSub, x);
			}

			sb.append(s.substring(x));

			return sb.toString();
		}
		else {
			return s;
		}
	}

	public static String replace(String s, String[] oldSubs, String[] newSubs) {
		if ((s == null) || (oldSubs == null) || (newSubs == null)) {
			return null;
		}

		if (oldSubs.length != newSubs.length) {
			return s;
		}

		for (int i = 0; i < oldSubs.length; i++) {
			s = replace(s, oldSubs[i], newSubs[i]);
		}

		return s;
	}

	public static String reverse(String s) {
		if (s == null) {
			return null;
		}

		char[] c = s.toCharArray();
		char[] reverse = new char[c.length];

		for (int i = 0; i < c.length; i++) {
			reverse[i] = c[c.length - i - 1];
		}

		return new String(reverse);
	}

	public static String shorten(String s) {
		return shorten(s, 20);
	}

	public static String shorten(String s, int length) {
		return shorten(s, length, "..");
	}

	public static String shorten(String s, String suffix) {
		return shorten(s, 20, suffix);
	}

	public static String shorten(String s, int length, String suffix) {
		if (s == null || suffix == null)  {
			return null;
		}

		if (s.length() > length) {
			s = s.substring(0, length) + suffix;
		}

		return s;
	}

	public static String[] split(String s) {
		return split(s, StringPool.COMMA);
	}

	public static String[] split(String s, String delimiter) {
		if (s == null || delimiter == null) {
			return new String[0];
		}

		s = s.trim();

		if (!s.endsWith(delimiter)) {
			s += delimiter;
		}

		if (s.equals(delimiter)) {
			return new String[0];
		}

		List nodeValues = new ArrayList();

		if (delimiter.equals("\n") || delimiter.equals("\r")) {
			try {
				BufferedReader br = new BufferedReader(new StringReader(s));

				String line = null;

				while ((line = br.readLine()) != null) {
					nodeValues.add(line);
				}

				br.close();
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		else {
			int offset = 0;
			int pos = s.indexOf(delimiter, offset);

			while (pos != -1) {
				nodeValues.add(s.substring(offset, pos));

				offset = pos + delimiter.length();
				pos = s.indexOf(delimiter, offset);
			}
		}

		return (String[])nodeValues.toArray(new String[0]);
	}

	public static boolean[] split(String s, String delimiter, boolean x) {
		String[] array = split(s, delimiter);
		boolean[] newArray = new boolean[array.length];

		for (int i = 0; i < array.length; i++) {
			boolean value = x;

			try {
				value = Boolean.valueOf(array[i]).booleanValue();
			}
			catch (Exception e) {
			}

			newArray[i] = value;
		}

		return newArray;
	}

	public static double[] split(String s, String delimiter, double x) {
		String[] array = split(s, delimiter);
		double[] newArray = new double[array.length];

		for (int i = 0; i < array.length; i++) {
			double value = x;

			try {
				value = Double.parseDouble(array[i]);
			}
			catch (Exception e) {
			}

			newArray[i] = value;
		}

		return newArray;
	}

	public static float[] split(String s, String delimiter, float x) {
		String[] array = split(s, delimiter);
		float[] newArray = new float[array.length];

		for (int i = 0; i < array.length; i++) {
			float value = x;

			try {
				value = Float.parseFloat(array[i]);
			}
			catch (Exception e) {
			}

			newArray[i] = value;
		}

		return newArray;
	}

	public static int[] split(String s, String delimiter, int x) {
		String[] array = split(s, delimiter);
		int[] newArray = new int[array.length];

		for (int i = 0; i < array.length; i++) {
			int value = x;

			try {
				value = Integer.parseInt(array[i]);
			}
			catch (Exception e) {
			}

			newArray[i] = value;
		}

		return newArray;
	}

	public static long[] split(String s, String delimiter, long x) {
		String[] array = split(s, delimiter);
		long[] newArray = new long[array.length];

		for (int i = 0; i < array.length; i++) {
			long value = x;

			try {
				value = Long.parseLong(array[i]);
			}
			catch (Exception e) {
			}

			newArray[i] = value;
		}

		return newArray;
	}

	public static short[] split(String s, String delimiter, short x) {
		String[] array = split(s, delimiter);
		short[] newArray = new short[array.length];

		for (int i = 0; i < array.length; i++) {
			short value = x;

			try {
				value = Short.parseShort(array[i]);
			}
			catch (Exception e) {
			}

			newArray[i] = value;
		}

		return newArray;
	}

    public static final String stackTrace(Throwable t) {
		String s = null;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			t.printStackTrace(new PrintWriter(baos, true));
			s = baos.toString();
		}
		catch (Exception e) {
		}

		return s;
    }

	public static boolean startsWith(String s, char begin) {
		return startsWith(s, (new Character(begin)).toString());
	}

	public static boolean startsWith(String s, String start) {
		if ((s == null) || (start == null)) {
			return false;
		}

		if (start.length() > s.length()) {
			return false;
		}

		String temp = s.substring(0, start.length());

		if (temp.equalsIgnoreCase(start)) {
			return true;
		}
		else {
			return false;
		}
	}

	public static String trimLeading(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isWhitespace(s.charAt(i))) {
				return s.substring(i, s.length());
			}
		}

		return StringPool.BLANK;
	}

	public static String trimTrailing(String s) {
		for (int i = s.length() - 1; i >= 0; i--) {
			if (!Character.isWhitespace(s.charAt(i))) {
				return s.substring(0, i + 1);
			}
		}

		return StringPool.BLANK;
	}

	public static String wrap(String text) {
		return wrap(text, 80, "\n");
	}

	public static String wrap(String text, int width, String lineSeparator) {
		if (text == null) {
			return null;
		}

		StringBuffer sb = new StringBuffer();

		try {
			BufferedReader br = new BufferedReader(new StringReader(text));

			String s = StringPool.BLANK;

			while ((s = br.readLine()) != null) {
				if (s.length() == 0) {
					sb.append(lineSeparator);
				}
				else {
					String[] tokens = s.split(StringPool.SPACE);
					boolean firstWord = true;
					int curLineLength = 0;

					for (int i = 0; i < tokens.length; i++) {
						if (!firstWord) {
							sb.append(StringPool.SPACE);
							curLineLength++;
						}

						if (firstWord) {
							sb.append(lineSeparator);
						}

						sb.append(tokens[i]);

						curLineLength += tokens[i].length();

						if (curLineLength >= width) {
							firstWord = true;
							curLineLength = 0;
						}
						else {
							firstWord = false;
						}
					}
				}
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return sb.toString();
	}
	
	public static String pack(String[] list) {
	   return PackTools.pack(list);
	}
   
	public static String[] unpack(String s) {
	   return PackTools.unpack(s);
	}
   
	public static boolean isPackedString(String s) {
		return PackTools.isPackedString(s);
	}
	
	public static String definedUnicode(String s) {
		// This will filter out all illegal unicode characters as well as valid supplemental characters.
		if(s == null || s.equals(""))
			return s;
		StringBuilder sb = new StringBuilder();
		char[] cs = s.toCharArray();
		for(char c:cs) {
			if(Character.isDefined(c))
				sb.append(c);
		}
		return sb.toString();
	}
	
    public static String toString(String[] strs) {
    	StringBuilder sb = new StringBuilder();
    	sb.append("[");
    	if(strs != null) {
	    	for(String str:strs) {
	    		if(sb.length() > 1)
	    			sb.append(",");
	    		sb.append(str);
	    	}
    	}
    	sb.append("]");
    	return sb.toString();
    }

}
