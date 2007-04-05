/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;
import java.util.ArrayList;

/**
 * Tell the difference between two text items.
 * 
 * @author Rob
 *
 */
public class StringDiffUtil
{
	private List _processResult = null;	// List of DiffSegment(s) that indicate differences between the two text items
	private static String diffBegin = "<span class='ss_diff_added'>";
	private static String sameBegin = "<span class='ss_diff_same'>";
	private static String missingBegin = "<span class='ss_diff_deleted'>";
	private static String end = "</span>";
	

	public StringDiffUtil()
	{
		_processResult = new ArrayList();
	}

	/**
	 * Uses Longest Common Subsequence algorithm (LCS) to determine what characters are in common
	 * between the two text items.
	 * 
	 * @param text1		first text to compare
	 * @param text2		second text to compare
	 * 
	 * @return			LCS string - gives common character sequence information
	 * 
	 * @throws Exception
	 */
	private String compare(String text1, String text2)
		throws Exception
	{
		String result = "";
		int i = 0,
			j = 0;
		
		/* initialize the n x m matrix B and C for dynamic programming 
	     * B[i][j] stores the directions, C[i][j] stores the length of LCS of
	     * X[0..i-1] and Y[0..j-1]
	     */ 
	    int n = text1.length();
	    int m = text2.length();
	    int[][] C = new int[n+1][m+1];
	    int[][] B = new int[n+1][m+1];
	
	    /* C[i][0] = 0 for 0<=i<=n */
	    for (i = 0; i <= n; i++) {
	        C[i][0] = 0;
	    }
	
	    /* C[0][j] = 0 for  0<=j<=m */
	    for (j = 0; j <= m; j++) {
	        C[0][j] = 0;
	    }
	    
	    /* dynamic programming */
	    for (i = 1; i <= n; i++)
	    {
	        for (j = 1; j <= m; j++)
	        {
	            if (text1.charAt(i-1) == text2.charAt(j-1))
	            {
	                C[i][j]=C[i-1][j-1]+1;
	                B[i][j]=1;  /* diagonal */
	            }
	            else if (C[i-1][j]>=C[i][j-1])
	            {
	                C[i][j]=C[i-1][j];
	                B[i][j] = 2;  /* down */
	            }
	            else 
	            {
	                C[i][j]=C[i][j-1];     
	                B[i][j]=3;   /* forword */
	            }
	        }
	    }
	    
	    /* Backtracking */
	    i=n;
	    j=m;
	    while (i!=0 && j!=0)
	    {
	        if (B[i][j] == 1)
	        {   
	        	/* diagonal */
	        	result = text1.charAt(i-1) + result;
	            i = i - 1;
	            j = j - 1;
	        }
	        if (B[i][j] == 2)
	        {
	        	/* up */
	            i = i - 1;
	        }
	        if (B[i][j] == 3)
	        {
	        	/* backword */
	            j = j - 1;
	        }
	    }
	    
	    return result;
	}
	
	/**
	 * Removes HTML tag information from text item.
	 * 
	 * @param text	Text containing HTML tags
	 * 
	 * @return	Text minus HTML tag information
	 * @throws Exception
	 */
	private String removeHtmlTags(String text)
		throws Exception
	{
		String pattern = "<(.|\n)*?>";
		
		return text.replaceAll(pattern, " ");
	}
	
	/**
	 * Recursive method that combines information from text1, text2, and LCS into a single text solution.
	 * 
	 * @param lcs		LCS result information
	 * @param text1		first text to compared 
	 * @param text2		second text to compared
	 * 
	 * @throws Exception
	 */
	private void process(String lcs, String text1, String text2)
		throws Exception
	{
		int index = 0;
		StringDiffSegment ds = null;
		String lcsFinal = "";
		
		if (lcs.length() > 0)
		{
			for (int x=0; x < lcs.length(); x++)
			{
				lcsFinal += lcs.charAt(x);
				if (text1.indexOf(lcsFinal) < 0
				|| text2.indexOf(lcsFinal) < 0)
				{
					lcsFinal = lcsFinal.substring(0, lcsFinal.length()-1);
					break;
				}
			}

			if ((index = text1.indexOf(lcsFinal)) > -1)
			{
				if (index > 0)
				{
					ds = new StringDiffSegment(StringDiffSegment.SUBTRACTION, text1.substring(0, index));
					_processResult.add(ds);
				}
				text1 = text1.substring(index+lcsFinal.length());
			}
			
			if ((index = text2.indexOf(lcsFinal)) > -1)
			{
				if (index > 0)
				{
					ds = new StringDiffSegment(StringDiffSegment.ADDITION, text2.substring(0, index));
					_processResult.add(ds);
					ds = new StringDiffSegment(StringDiffSegment.SAME, lcsFinal);
					_processResult.add(ds);
				}
				else
				{
					ds = new StringDiffSegment(StringDiffSegment.SAME, text2.substring(0, lcsFinal.length()));
					_processResult.add(ds);
				}
				text2 = text2.substring(index+lcsFinal.length());
			}
			
			process(compare(text1, text2), text1, text2);
		}
		else
		{
			ds = new StringDiffSegment(StringDiffSegment.SUBTRACTION, text1);
			_processResult.add(ds);
			ds = new StringDiffSegment(StringDiffSegment.ADDITION, text2);
			_processResult.add(ds);			
		}
		
		return;
	}
	
	/**
	 * Read a file
	 * 
	 * @param file	File location / name to read into Class
	 * 
	 * @return	File content
	 * 
	 * @throws IOException
	 */
	private static String readFile(File file)
		throws IOException
	{
		BufferedReader br = null;
		StringBuffer sb = null;
		String line = null;

		sb = new StringBuffer();
		
		try
		{
			br = new BufferedReader(new FileReader(file));
			while ((line = br.readLine()) != null)
				sb.append(line).append('\n');
		}
		finally
		{
			if (br != null)
				br.close();
		}

		return sb.toString().trim();
	}
	
	/**
	 * Run the difference functionality
	 * 
	 * @param text1 	first text to compare
	 * @param text2 	second text to compare
	 * @return
	 * 
	 * @throws Exception
	 */
	public List runDiff(String text1, String text2)
		throws Exception
	{
		text1 = removeHtmlTags(text1);
		text2 = removeHtmlTags(text2);
		
		process(compare(text1, text2), text1, text2);
		return _processResult;
	}
	
	public String getComparison(String text1, String text2) {
		StringDiffUtil d = new StringDiffUtil();
        List result = new ArrayList();
        String diffText = "";
        try {
        	result = d.runDiff(text1, text2);
        } catch(Exception e) {
        	//These strings are too different; mark them as a complete replacement
        	diffText = diffBegin + text1 + end + missingBegin + text2 + end;
        	return diffText;
        }
        for (int x=0; x < result.size(); x++)
        {
        	StringDiffSegment ds = (StringDiffSegment)result.get(x);
        	switch (ds.getType())
        	{
        		case StringDiffSegment.SAME:
        			diffText += sameBegin + ds.getContent() + end;
        		break;
        		case StringDiffSegment.ADDITION:
        			diffText += diffBegin + ds.getContent() + end;
	        	break;
        		case StringDiffSegment.SUBTRACTION:
        			diffText += missingBegin + ds.getContent() + end;
	        	break;
        		default:
        			diffText += ds.getContent();
        	}
        }
        return diffText;
	}
	
	/**
	 * Used for testing purposes.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		List result = null;
		StringDiffSegment ds = null;
		File f1 = null,
			 f2 = null;
		String file1 = "diff1.txt",
			   file2 = "Scenario1.txt",
			   text1 = "",
			   text2 = "";
		
		try
		{
			StringDiffUtil d = new StringDiffUtil();
			
			f1 = new File(file1);
			f2 = new File(file2);
			if (!f1.exists() || !f2.exists() || !f1.isFile() || !f2.isFile())
				throw new Exception("Either file: " + f1.getAbsolutePath() + " or file: " + f2.getAbsolutePath() + " does not exist or is not a file.");
			
			text1 = readFile(f1);
			text2 = readFile(f2);
			
			/* print out the result */
	        System.out.println("<p>Response:&nbsp;" + text1 + "</p>");
	        System.out.println("<p>Reply:&nbsp;" + text2 + "</p>");
	        System.out.println("<br /><br />");		
	        result = d.runDiff(text1, text2);
	        for (int x=0; x < result.size(); x++)
	        {
	        	ds = (StringDiffSegment)result.get(x);
	        	switch (ds.getType())
	        	{
	        		case StringDiffSegment.SAME:
	        			System.out.print(sameBegin + ds.getContent() + end);
	        		break;
	        		case StringDiffSegment.ADDITION:
	        			System.out.print(diffBegin + ds.getContent() + end);
		        	break;
	        		case StringDiffSegment.SUBTRACTION:
	        			System.out.print(missingBegin + ds.getContent() + end);
		        	break;
	        		default:
	        			System.out.print(ds.getContent());
	        	}
	        }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		System.exit(0);
    }
}
