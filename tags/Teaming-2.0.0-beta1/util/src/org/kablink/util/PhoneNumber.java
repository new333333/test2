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
 * <a href="PhoneNumber.java.html"><b><i>View Source</i></b></a>
 *
 * @author  Brian Wing Shun Chan
 * @version $Revision: 1.7 $
 *
 */
public class PhoneNumber {

	public static String format(String phoneNumber) {
		if (phoneNumber == null) {
			return "";
		}

		if (phoneNumber.length() > 10) {
			return "(" + phoneNumber.substring(0,3) + ") " +
					phoneNumber.substring(3,6) + "-" +
					phoneNumber.substring(6,10) +
					" x" + phoneNumber.substring(10, phoneNumber.length());
		}
		else if (phoneNumber.length() == 10) {
			return "(" + phoneNumber.substring(0,3) + ") " +
					phoneNumber.substring(3,6) + "-" +
					phoneNumber.substring(6,10);
		}
		else if (phoneNumber.length() == 7) {
			return phoneNumber.substring(0,3) + "-" +
				   phoneNumber.substring(3,7);
		}
		else {
			return phoneNumber;
		}
	}

	public static String strip(String phoneNumber) {
		return StringUtil.extractDigits(phoneNumber);
	}

}