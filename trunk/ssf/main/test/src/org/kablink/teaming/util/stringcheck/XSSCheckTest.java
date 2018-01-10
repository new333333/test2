/**
 * Copyright (c) 2008-2016 Novell, Inc. All Rights Reserved. THIS WORK IS AN
 * UNPUBLISHED WORK AND CONTAINS CONFIDENTIAL PROPRIETARY AND TRADE SECRET
 * INFORMATION OF NOVELL, INC. ACCESS TO THIS WORK IS RESTRICTED TO NOVELL,INC.
 * EMPLOYEES WHO HAVE A NEED TO KNOW HOW TO PERFORM TASKS WITHIN THE SCOPE
 * OF THEIR ASSIGNMENTS AND ENTITIES OTHER THAN NOVELL, INC. WHO HAVE
 * ENTERED INTO APPROPRIATE LICENSE AGREEMENTS. NO PART OF THIS WORK MAY BE
 * USED, PRACTICED, PERFORMED COPIED, DISTRIBUTED, REVISED, MODIFIED,
 * TRANSLATED, ABRIDGED, CONDENSED, EXPANDED, COLLECTED, COMPILED, LINKED,
 * RECAST, TRANSFORMED OR ADAPTED WITHOUT THE PRIOR WRITTEN CONSENT OF NOVELL,
 * INC. ANY USE OR EXPLOITATION OF THIS WORK WITHOUT AUTHORIZATION COULD SUBJECT
 * THE PERPETRATOR TO CRIMINAL AND CIVIL LIABILITY.
 */
package org.kablink.teaming.util.stringcheck;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jong
 *
 */
public class XSSCheckTest {

	private XSSCheck check;
	
    @Before
    public void setUp() throws Exception {
		check = new XSSCheck(true, "trusted.strip", "trusted.disallow");
    }

    @Test
    public void test0() {
		String in = "<input type=\"text\" onfocus=\"myFunction()\">";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		String expected = "<input type=\"text\" >";
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test1() {
		String in = "<input type=\"text\" onfocus=\"myFunction()\" >";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		String expected = "<input type=\"text\"  >";
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test2() {
    	String in = "<input type=\"text\" onfocus=alert(1)>";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");

		String expected = "<input type=\"text\" >";
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test3() {
    	String in = "<input type=text autofocus onfocus=alert(1)>";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		
		String expected = "<input type=text autofocus >";
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test4() {
    	String in = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus onfocus=alert(1)//ho8xl>";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		String expected = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus //ho8xl>";
		
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test5() {
    	String in = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus onfocus=alert(1)//ho8xl";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		String expected = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus //ho8xl";
		
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test6() {
    	String in = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus onfocus=alert(1)/ho8xl>";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		String expected = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus /ho8xl>";
		
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test7() {
    	String in = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus onfocus=alert(1)/ho8xl";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		String expected = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus onfocus=alert(1)/ho8xl";
		
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test8() {
    	String in = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus onfocus=alert(1) //ho8xl>";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		String expected = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus  //ho8xl>";
		
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test9() {
    	String in = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus onfocus=alert(1) //ho8xl";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		String expected = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus  //ho8xl";
		
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test10() {
    	String in = "<img src=x onerror=alert(1)>";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");

		String expected = "<img src=x >";
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test11() {
    	String in = "<img src=x //onerror=alert(1)>";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");

		String expected = "<img src=x //>";
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test12() {
    	String in = "<img src=x onerror=alert(1)//>";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");

		String expected = "<img src=x //>";
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test13() {
    	String in = "<img src=x onerror=alert(1)";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");

		String expected = "<img src=x onerror=alert(1)";
		Assert.assertEquals(expected, out);
    }

    @Test
    public void test14() {
    	String in = "<img src=x //onerror=alert(1)";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");

		String expected = "<img src=x //onerror=alert(1)";
		Assert.assertEquals(expected, out);
    }

    @Test
    public void test15() {
    	String in = "<img src=x onerror=alert(1)//";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");

		String expected = "<img src=x //";
		Assert.assertEquals(expected, out);
    }
    
    @Test
    public void test16() {
    	String in = "<img src=x>";
		System.out.println("INPUT:     [" + in + "]");
		String out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");

		String expected = "<img src=x>";
		Assert.assertEquals(expected, out);
    }

	public static void main(String[] args) {
		XSSCheck check = new XSSCheck(true, "trusted.strip", "trusted.disallow");
		
		String in,out;
		
		in = "<input type=\"text\" onfocus=\"myFunction()\">";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();
		
		in = "<input type=\"text\" onfocus=alert(1)>";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		in = "<input type=text autofocus onfocus=alert(1)>";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		in = "<input type=text autofocus onfocus=alert(1)";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		in = "<input type=text autofocus onfocus=alert(1)//";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		in = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus onfocus=alert(1)//ho8xl";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		in = "402883c6115753d801115777f284000bqlb8f<input type=text autofocus onfocus=alert(1)//ho8xl>";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		in = "<img src=x onerror=alert(1)>";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		in = "<img src=x //onerror=alert(1)>";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		in = "<img src=x onerror=alert(1)//>";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		// XSSCheck breaks down when the input misses closing angle bracket
		in = "<img src=x onerror=alert(1)";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		// XSSCheck breaks down when the input misses closing angle bracket
		in = "<img src=x //onerror=alert(1)";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		// XSSCheck sort of handles this case despite of missing closing angle bracket
		// due to the changes made on 12/14/2017
		in = "<img src=x onerror=alert(1)//";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		in = "<img src=x>";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		// XSSCheck breaks down when the input misses closing angle bracket
		in = "<img src=x onerror=alert(1) abc";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		// XSSCheck breaks down when the input misses closing angle bracket
		in = "<img src=x onerror=alert(1)abc";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

		in = "<img src=x onerror=alert(1)abc>";
		System.out.println("INPUT:     [" + in + "]");
		out = check.check(in, false);
		System.out.println("XSSCHECK:  [" + out + "]");
		System.out.println("SANITIZER: [" + StringCheckUtil.checkHtml(in) + "]");
		System.out.println();

	}
}
