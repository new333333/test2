package org.kablink.teaming.util;
//package com.josephoconnell.html;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * 
 * HTML filtering utility for protecting against XSS (Cross Site Scripting).
 *
 * This code is licensed under a Creative Commons Attribution-ShareAlike 2.5 License
 * http://creativecommons.org/licenses/by-sa/2.5/
 * 
 * This code is a Java port of the original work in PHP by Cal Hendersen.
 * http://code.iamcal.com/php/lib_filter/
 *
 * The trickiest part of the translation was handling the differences in regex handling
 * between PHP and Java.  These resources were helpful in the process:
 * 
 * http://java.sun.com/j2se/1.4.2/docs/api/java/util/regex/Pattern.html
 * http://us2.php.net/manual/en/reference.pcre.pattern.modifiers.php
 * http://www.regular-expressions.info/modifiers.html
 * 
 * A note on naming conventions: instance variables are prefixed with a "v"; global
 * constants are in all caps.
 * 
 * Sample use:
 * String input = ...
 * String clean = new HTMLInputFilter().filter( input );
 * 
 * If you find bugs or have suggestions on improvement (especially regarding 
 * perfomance), please contact me at the email below.  The latest version of this
 * source can be found at
 * 
 * http://josephoconnell.com/java/xss-html-filter/
 *
 * @author Joseph O'Connell <joe.oconnell at gmail dot com>
 * @version 1.0 
 * 
 * This file has been edited from its original version by Peter Hurley:
 *   1) Fixed problem in validateEntities where s was getting truncated
 *   2) Changed vAllowedEntities to vDisAllowedEntities (most entities are allowable)
 */
public class HTMLInputFilter 
{  
  /** 
   * flag determining whether to try to make tags when presented with "unbalanced"
   * angle brackets (e.g. "<b text </b>" becomes "<b> text </b>").  If set to false,
   * unbalanced angle brackets will be html escaped (unless NEVER_MAKE_TAGS is true).
   */
	  protected static final boolean ALWAYS_MAKE_TAGS = false;  //true doesn't work
	  protected static final boolean NEVER_MAKE_TAGS = true;  //true doesn't work
  
  /**
   * flag determing whether comments are allowed in input String.
   */
  protected static final boolean STRIP_COMMENTS = true;
  
  /** regex flag union representing /si modifiers in php **/
  protected static final int REGEX_FLAGS_SI = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;
  
  /** set of allowed html elements, along with allowed attributes for each element **/
  protected Map<String,List<String>> vAllowed;

  /** always allowed tags **/
  protected String vAlwaysAllowed;

  /** always disallowed tags **/
  protected String vAlwaysDisAllowed;

  /** html elements which must always be self-closing (e.g. "<img />") **/
  protected String[] vSelfClosingTags;
  
  /** html elements which must always have separate opening and closing tags (e.g. "<b></b>") **/
  protected String[] vNeedClosingTags;
  
  /** attributes which should be checked for valid protocols **/
  protected String[] vProtocolAtts;
  
  /** allowed protocols **/
  protected String[] vAllowedProtocols;
  
  /** always allowed tag attributes **/
  protected String vAlwaysAllowedAttributes;
  
  /** tags which should be removed if they contain no content (e.g. "<b></b>" or "<b />") **/
  protected String[] vRemoveBlanks;
  protected Map<String,Pattern> vRemoveBlanksPatterns1;
  protected Map<String,Pattern> vRemoveBlanksPatterns2;
  
  /** entities allowed within html markup **/
  protected String[] vDisAllowedEntities;
  
  protected boolean vDebug;
  
  //Patterns 
  private static final String PATTERN_ESCAPE_COMMENTS = "<!--(.*?)-->";
  private static final String PATTERN_CHECK_TAGS = "<(.*?)>";
  private static final String PATTERN_REGEXP_REPLACE1 = "<(.*?)>";
  private static final String PATTERN_REGEXP_REPLACE2 = "<([^>]*?)(?=<|$)";
  private static final String PATTERN_REGEXP_REPLACE3 = "(^|>)([^<]*?)(?=>)";
  private static final String PATTERN_PROCESS_TAGS1 = "^/([a-z0-9]+)";
  private static final String PATTERN_PROCESS_TAGS2 = "^([a-z0-9]+)(.*?)(/?)$";
  private static final String PATTERN_PROCESS_TAGS3 = "^!--(.*)--$";
  private static final String PATTERN_PROCESS_TAGS4 = "([a-z0-9]+)=([\"'])(.*?)\\2";
  private static final String PATTERN_PROCESS_TAGS5 = "([a-z0-9]+)(=)([^\"\\s']+)";
  private static final String PATTERN_PROCESS_PARAM_PROTOCOL = "^([^:]+):";
  private static final String PATTERN_DECODE_ENTITIES1 = "&#(\\d+);?";
  private static final String PATTERN_DECODE_ENTITIES2 = "&#x([0-9a-f]+);?";
  private static final String PATTERN_DECODE_ENTITIES3 = "%([0-9a-f]{2});?";
  private static final String PATTERN_VALIDATE_ENTITIES1 = "&([^&;]*)(?=(;|&|$))";
  private static final String PATTERN_VALIDATE_ENTITIES2 = "(>|^)([^<]+?)(<|$)";
  
  private Pattern pattern_escape_comments;
  private Pattern pattern_check_tags;
  private Pattern pattern_regexp_replace1;
  private Pattern pattern_regexp_replace2;
  private Pattern pattern_regexp_replace3;
  private Pattern pattern_process_tags1;
  private Pattern pattern_process_tags2;
  private Pattern pattern_process_tags3;
  private Pattern pattern_process_tags4;
  private Pattern pattern_process_tags5;
  private Pattern pattern_process_param_protocol;
  private Pattern pattern_decode_entities1;
  private Pattern pattern_decode_entities2;
  private Pattern pattern_decode_entities3;
  private Pattern pattern_validate_entities1;
  private Pattern pattern_validate_entities2;
  
  public HTMLInputFilter()
  {
    this(false);
  }
  
  public HTMLInputFilter( boolean debug )
  {
	pattern_escape_comments = Pattern.compile(PATTERN_ESCAPE_COMMENTS, Pattern.DOTALL);
	pattern_check_tags = Pattern.compile( PATTERN_CHECK_TAGS, Pattern.DOTALL );
	pattern_regexp_replace1 = Pattern.compile( PATTERN_REGEXP_REPLACE1 );
	pattern_regexp_replace2 = Pattern.compile( PATTERN_REGEXP_REPLACE2 );
	pattern_regexp_replace3 = Pattern.compile( PATTERN_REGEXP_REPLACE3 );
	pattern_process_tags1 = Pattern.compile( PATTERN_PROCESS_TAGS1, REGEX_FLAGS_SI );
	pattern_process_tags2 = Pattern.compile( PATTERN_PROCESS_TAGS2, REGEX_FLAGS_SI );
	pattern_process_tags3 = Pattern.compile( PATTERN_PROCESS_TAGS3, REGEX_FLAGS_SI );
	pattern_process_tags4 = Pattern.compile( PATTERN_PROCESS_TAGS4, REGEX_FLAGS_SI );
	pattern_process_tags5 = Pattern.compile( PATTERN_PROCESS_TAGS5, REGEX_FLAGS_SI );
	pattern_process_param_protocol = Pattern.compile( PATTERN_PROCESS_PARAM_PROTOCOL, REGEX_FLAGS_SI );
	pattern_decode_entities1 = Pattern.compile( PATTERN_DECODE_ENTITIES1 );
	pattern_decode_entities2 = Pattern.compile( PATTERN_DECODE_ENTITIES2 );
	pattern_decode_entities3 = Pattern.compile( PATTERN_DECODE_ENTITIES3 );
	pattern_validate_entities1 = Pattern.compile( PATTERN_VALIDATE_ENTITIES1 );
	pattern_validate_entities2 = Pattern.compile( PATTERN_VALIDATE_ENTITIES2, Pattern.DOTALL );
		
	vDebug = debug;
    
	vAllowed = new HashMap<String,List<String>>();

	vAlwaysAllowed = " * abbr acronym address b bdo big blockquote br caption center cite code col colgroup dd del dfn dir dl dt em fieldset font h1 h2 h3 h4 h5 h6 hr i ins kbd label legend li ol optgroup option p pre q s samp select small span strike strong sub sup tt u ul var";
	vAlwaysAllowedAttributes = " class style title dir lang accesskey tabindex ";

	vAlwaysDisAllowed = " script embed object applet style html head body meta xml blink link iframe frame frameset ilayer layer bgsound base ";
	
    ArrayList<String> a_atts = new ArrayList<String>();
    a_atts.add( "href" );
    a_atts.add( "target" );
    vAllowed.put( "a", a_atts );
    
    ArrayList<String> table_atts = new ArrayList<String>();
    table_atts.add( "cellspacing" );
    table_atts.add( "cellpadding" );
    table_atts.add( "border" );
    vAllowed.put( "table", table_atts );
    
    ArrayList<String> tbody_atts = new ArrayList<String>();
    vAllowed.put( "tbody", tbody_atts );
    
    ArrayList<String> tr_atts = new ArrayList<String>();
    vAllowed.put( "tr", tr_atts );
    
    ArrayList<String> td_atts = new ArrayList<String>();
    td_atts.add( "align" );
    td_atts.add( "valign" );
    vAllowed.put( "td", td_atts );
    
    ArrayList<String> th_atts = new ArrayList<String>();
    th_atts.add( "align" );
    th_atts.add( "valign" );
    vAllowed.put( "th", th_atts );
    
    ArrayList<String> div_atts = new ArrayList<String>();
    div_atts.add( "align" );
    vAllowed.put( "div", div_atts );
    
    ArrayList<String> span_atts = new ArrayList<String>();
    vAllowed.put( "span", span_atts );
    
    ArrayList<String> img_atts = new ArrayList<String>();
    img_atts.add( "src" );
    img_atts.add( "align" );
    img_atts.add( "border" );
    img_atts.add( "width" );
    img_atts.add( "height" );
    img_atts.add( "hspace" );
    img_atts.add( "vspace" );
    img_atts.add( "alt" );
    vAllowed.put( "img", img_atts );
    
    ArrayList<String> no_atts = new ArrayList<String>();
    vAllowed.put( "p", no_atts );
    vAllowed.put( "b", no_atts );
    vAllowed.put( "strong", no_atts );
    vAllowed.put( "i", no_atts );
    vAllowed.put( "em", no_atts );
    
    vSelfClosingTags = new String[] { "img", "br", "hr" };
    vNeedClosingTags = new String[] { "a", "b", "strong", "i", "em", "p", "span", "div", "table", "tr", "td", "th" };
    vAllowedProtocols = new String[] { "http", "https", "mailto" }; // no ftp.
    vProtocolAtts = new String[] { "src", "href" };
    vRemoveBlanks = new String[] { "a", "b", "strong", "i", "em", "p" };
    vRemoveBlanksPatterns1 = new HashMap<String,Pattern>();
    vRemoveBlanksPatterns2 = new HashMap<String,Pattern>();
    vDisAllowedEntities = new String[] { };
    
    for( String tag : vRemoveBlanks )
    {
      Pattern p1 = Pattern.compile("<" + tag + "(\\s[^>]*)?></" + tag + ">");
      vRemoveBlanksPatterns1.put(tag, p1);

      Pattern p2 = Pattern.compile("<" + tag + "(\\s[^>]*)?/>");
      vRemoveBlanksPatterns2.put(tag, p2);
    }
    
  }
  
  protected void debug( String msg )
  {
    if (vDebug)
      System.out.println( msg );
  }
  
  //---------------------------------------------------------------
  // my versions of some PHP library functions
  
  public static String chr( int decimal )
  {
    return String.valueOf( (char) decimal );
  }
  
  public static String htmlSpecialChars( String s )
  {
    s = s.replaceAll( "&", "&amp;" );
    s = s.replaceAll( "\"", "&quot;" );
    s = s.replaceAll( "<", "&lt;" );
    s = s.replaceAll( ">", "&gt;" );
    return s;
  }
 
  //---------------------------------------------------------------
  
  /**
   * given a user submitted input String, filter out any invalid or restricted
   * html.
   * 
   * @param input text (i.e. submitted by a user) than may contain html
   * @return "clean" version of input, with only valid, whitelisted html elements allowed
   */
  public String filter( String input )
  {
    Map<String,Integer> vTagCounts = new HashMap<String,Integer>();
    
    String s = input;
    
    debug( "************************************************" );
    debug( "              INPUT: " + input );
    
    s = escapeComments(s);
    debug( "     escapeComments: " + s );
    
    s = balanceHTML(s);
    debug( "        balanceHTML: " + s );
    
    s = checkTags(s, vTagCounts);
    debug( "          checkTags: " + s );
    
    s = processRemoveBlanks(s);
    debug( "processRemoveBlanks: " + s );
    
    s = validateEntities(s);
    debug( "    validateEntites: " + s );
    
    debug( "************************************************\n\n" );
    return s;
  }
  
  protected String escapeComments( String s )
  {
    Matcher m = pattern_escape_comments.matcher( s );
    StringBuffer buf = new StringBuffer();
    if (m.find()) {
      String match = m.group( 1 ); //(.*?)
      m.appendReplacement( buf, "<!--" + htmlSpecialChars( match ) + "-->" );
    }
    m.appendTail( buf );
    
    return buf.toString();
  }
  
  protected String balanceHTML( String s )
  {
    if (ALWAYS_MAKE_TAGS) 
    {
      //
      // try and form html
      //
      //s = regexReplace(pattern_regexp_replace1, "", s);
      s = regexReplace(pattern_regexp_replace2, "<$1>", s);
      s = regexReplace(pattern_regexp_replace3, "$1<$2", s);
      
    } 
    else if (!NEVER_MAKE_TAGS)
    {
      //
      // escape stray brackets
      //
      s = regexReplace(pattern_regexp_replace2, "&lt;$1", s);
      s = regexReplace(pattern_regexp_replace3, "$1$2&gt;<", s);
      
      //
      // the last regexp causes '<>' entities to appear
      // (we need to do a lookahead assertion so that the last bracket can
      // be used in the next pass of the regexp)
      //
      s = s.replaceAll("<>", "");
    }
    
    return s;
  }
  
  protected String checkTags( String s, Map<String,Integer> vTagCounts )
  {    
    Matcher m = pattern_check_tags.matcher( s );
    
    StringBuffer buf = new StringBuffer();
    while (m.find()) {
      String replaceStr = m.group( 1 );
      replaceStr = processTag( replaceStr, vTagCounts);
      m.appendReplacement(buf, replaceStr);
    }
    m.appendTail(buf);
    
    s = buf.toString();
    
    // these get tallied in processTag
    // (remember to reset before subsequent calls to filter method)
    for( String key : vTagCounts.keySet())
    {
      for(int ii=0; ii<vTagCounts.get(key); ii++) {
        s += "</" + key + ">";
      }
    }
    
    return s;
  }
  
  protected String processRemoveBlanks( String s )
  {
    for( String tag : vRemoveBlanks )
    {
      s = regexReplace( vRemoveBlanksPatterns1.get(tag), "", s );
      s = regexReplace( vRemoveBlanksPatterns2.get(tag), "", s );
    }
    
    return s;
  }
  
  protected String regexReplace( Pattern regex_pattern, String replacement, String s )
  {
    Matcher m = regex_pattern.matcher( s );
    return m.replaceAll( replacement );
  }
  
  protected String processTag( String s, Map<String,Integer> vTagCounts )
  {    
    // ending tags
    Matcher m = pattern_process_tags1.matcher( s );
    if (m.find()) {
      String name = m.group(1);
      if ((vAllowed.containsKey( name.toLowerCase() ) || 
    		  vAlwaysAllowed.contains(" " + name.toLowerCase() + " ") ||
    		  vAlwaysAllowed.contains(" * ")) &&
    		  !vAlwaysDisAllowed.contains(" " + name.toLowerCase() + " ")) {
        if (!inArray(name.toLowerCase(), vSelfClosingTags)) {
          if (vTagCounts.containsKey( name.toLowerCase() )) {
            vTagCounts.put(name.toLowerCase(), vTagCounts.get(name.toLowerCase())-1);
            return "</" + name + ">";
          }
        }
      }
    }
    
    // starting tags
    m = pattern_process_tags2.matcher( s );
    if (m.find()) {
      String name = m.group(1);
      String body = m.group(2);
      String ending = m.group(3);
      
      //debug( "in a starting tag, name='" + name + "'; body='" + body + "'; ending='" + ending + "'" );
      if ((vAllowed.containsKey( name.toLowerCase() ) || 
    		  vAlwaysAllowed.contains(" " + name.toLowerCase() + " ") ||
    		  vAlwaysAllowed.contains(" * ")) && 
    		  !vAlwaysDisAllowed.contains(" " + name.toLowerCase() + " ")) {
        String params = "";
        
        Matcher m2 = pattern_process_tags4.matcher( body );
        Matcher m3 = pattern_process_tags5.matcher( body );
        List<String> paramNames = new ArrayList<String>();
        List<String> paramValues = new ArrayList<String>();
        while (m2.find()) {
          paramNames.add(m2.group(1)); //([a-z0-9]+)
          paramValues.add(m2.group(3)); //(.*?)
        }
        while (m3.find()) {
          paramNames.add(m3.group(1)); //([a-z0-9]+)
          paramValues.add(m3.group(3)); //([^\"\\s']+)
        }
        
        String paramName, paramValue;
        for( int ii=0; ii<paramNames.size(); ii++ ) {
          paramName = paramNames.get(ii);
          paramValue = paramValues.get(ii);
          
          //debug( "paramName='" + paramName + "'" );
          //debug( "paramValue='" + paramValue + "'" );
          //debug( "allowed? " + vAllowed.get( name ).contains( paramName ) );
          
          if (vAllowed.get( name.toLowerCase() ).contains( paramName.toLowerCase() ) || 
        		  vAlwaysAllowedAttributes.contains(" " + paramName.toLowerCase() + " ")) {
            if (inArray( paramName.toLowerCase(), vProtocolAtts )) {
              paramValue = processParamProtocol( paramValue );
            }
            params += " " + paramName + "=\"" + paramValue + "\"";
          }
        }
        
        if (inArray( name.toLowerCase(), vSelfClosingTags )) {
          ending = " /";
        }
        
        if (inArray( name.toLowerCase(), vNeedClosingTags )) {
          ending = "";
        }
        
        if (ending == null || ending.length() < 1) {
          if (vTagCounts.containsKey( name.toLowerCase() )) {
            vTagCounts.put( name.toLowerCase(), vTagCounts.get(name.toLowerCase())+1 );
          } else {
            vTagCounts.put( name.toLowerCase(), 1 );
          }
        } else {
          ending = " /";
        }
        return "<" + name + params + ending + ">";
      } else {
        return "";
      }
    }
    
    // comments
    m = pattern_process_tags3.matcher( s );
    if (m.find()) {
      String comment = m.group();
      if (STRIP_COMMENTS) {
        return "";
      } else {
        return "<" + comment + ">"; 
      }
    }
    
    return "<" + s + ">";
  }
  
  protected String processParamProtocol( String s )
  {
    s = decodeEntities( s );
    Matcher m = pattern_process_param_protocol.matcher( s );
    if (m.find()) {
      String protocol = m.group(1);
      if (!inArray( protocol, vAllowedProtocols )) {
        // bad protocol, turn into local anchor link instead
        s = "#" + s.substring( protocol.length()+1, s.length() );
        if (s.startsWith("#//")) s = "#" + s.substring( 3, s.length() );
      }
    }
    
    return s;
  }
  
  protected String decodeEntities( String s )
  {
    StringBuffer buf = new StringBuffer();
    
    Matcher m = pattern_decode_entities1.matcher( s );
    while (m.find()) {
      String match = m.group( 1 );
      int decimal = Integer.decode( match ).intValue();
      m.appendReplacement( buf, chr( decimal ) );
    }
    m.appendTail( buf );
    s = buf.toString();
    
    buf = new StringBuffer();
    m = pattern_decode_entities2.matcher( s );
    while (m.find()) {
      String match = m.group( 1 );
      int decimal = Integer.decode( match ).intValue();
      m.appendReplacement( buf, chr( decimal ) );
    }
    m.appendTail( buf );
    s = buf.toString();
    
    buf = new StringBuffer();
    m = pattern_decode_entities3.matcher( s );
    while (m.find()) {
      String match = m.group( 1 );
      int decimal = Integer.decode( match ).intValue();
      m.appendReplacement( buf, chr( decimal ) );
    }
    m.appendTail( buf );
    s = buf.toString();
    
    s = validateEntities( s );
    return s;
  }
  
  protected String validateEntities( String s )
  {
    // validate entities throughout the string
	StringBuffer buf = new StringBuffer();
    Matcher m = pattern_validate_entities1.matcher( s );
    while (m.find()) {
      String one = m.group( 1 ); //([^&;]*) 
      String two = m.group( 2 ); //(?=(;|&|$))
      String s1 = checkEntity( one, two );
      m.appendReplacement( buf, s1);
    }
    m.appendTail( buf );
    s = buf.toString();
    
/**
 * 
    // validate quotes outside of tags
    buf = new StringBuffer();
    m = pattern_validate_entities2.matcher( s );
    while (m.find()) {
      String one = m.group( 1 ); //(>|^) 
      String two = m.group( 2 ); //([^<]+?) 
      String three = m.group( 3 ); //(<|$) 
      m.appendReplacement(buf, m.quoteReplacement(one + replaceAllQuotes(two) + three));
    }
    m.appendTail( buf );
    s = buf.toString();
*/

    return s;
  }
  
  protected String checkEntity( String preamble, String term )
  {
    if (!term.equals(";")) {
      return "&" + preamble;
    }
    
    if ( isValidEntity( preamble ) ) {
      return "&" + preamble;
    }
    
    return "&amp;" + preamble;
  }
  
  protected boolean isValidEntity( String entity )
  {
    if (inArray( entity, vDisAllowedEntities )) return false;
    return true;
  }
  
  private boolean inArray( String s, String[] array )
  {
    for (String item : array)
      if (item != null && item.equals(s))
        return true;
    
    return false;
  }
  
  protected String replaceAllQuotes(String text) {
	while (text.indexOf("\"") >= 0) {
		String one = text.substring(0, text.indexOf("\""));
		String two = text.substring(text.indexOf("\"")+1);
		text = one + "&quot;" + two;
	}
  	return text;
  }
  
  // ============================================ START-UNIT-TEST =========================================
  public static class Test extends TestCase
  {  
    protected HTMLInputFilter vFilter;
    
    protected void setUp() 
    { 
      vFilter = new HTMLInputFilter( true );
    }
    
    protected void tearDown()
    {
      vFilter = null;
    }
    
    private void t( String input, String result )
    {
      Assert.assertEquals( result, vFilter.filter(input) );
    }
    
    public void test_basics()
    {
      t( "", "" );
      t( "hello", "hello" );
    }
    
    public void test_balancing_tags()
    {
      t( "<b>hello", "<b>hello</b>" );
      t( "<b>hello", "<b>hello</b>" );
      t( "hello<b>", "hello" );
      t( "hello</b>", "hello" );
      t( "hello<b/>", "hello" );
      t( "<b><b><b>hello", "<b><b><b>hello</b></b></b>" );
      t( "</b><b>", "" );
    }
    
    public void test_end_slashes()
    {
      t("<img>","<img />");
      t("<img/>","<img />");
      t("<b/></b>","");
    }
    
    public void test_balancing_angle_brackets()
    {
      if (ALWAYS_MAKE_TAGS) {
        t("<img src=\"foo\"","<img src=\"foo\" />");
        t("i>","");
        t("<img src=\"foo\"/","<img src=\"foo\" />");
        t(">","");
        t("foo<b","foo");
        t("b>foo","<b>foo</b>");
        t("><b","");
        t("b><","");
        t("><b>","");
      } else {
        t("<img src=\"foo\"","&lt;img src=\"foo\"");
        t("b>","b&gt;");
        t("<img src=\"foo\"/","&lt;img src=\"foo\"/");
        t(">","&gt;");
        t("foo<b","foo&lt;b");
        t("b>foo","b&gt;foo");
        t("><b","&gt;&lt;b");
        t("b><","b&gt;&lt;");
        t("><b>","&gt;");
      }
    }
    
    public void test_attributes()
    {
      t("<img src=foo>","<img src=\"foo\" />"); 
      t("<img asrc=foo>","<img />");
      t("<img src=test test>","<img src=\"test\" />"); 
    }
    
    public void test_disallow_script_tags()
    {
      t("<script>","");
      if (ALWAYS_MAKE_TAGS) { t("<script","");  } else { t("<script","&lt;script"); }
      t("<script/>","");
      t("</script>","");
      t("<script woo=yay>","");
      t("<script woo=\"yay\">","");
      t("<script woo=\"yay>","");
      t("<script woo=\"yay<b>","");
      t("<script<script>>","");
      t("<<script>script<script>>","script");
      t("<<script><script>>","");
      t("<<script>script>>","");
      t("<<script<script>>","");
    }
    
    public void test_protocols()
    {
      t("<a href=\"http://foo\">bar</a>", "<a href=\"http://foo\">bar</a>");
      // we don't allow ftp. t("<a href=\"ftp://foo\">bar</a>", "<a href=\"ftp://foo\">bar</a>");
      t("<a href=\"mailto:foo\">bar</a>", "<a href=\"mailto:foo\">bar</a>");
      t("<a href=\"javascript:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"java script:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"java\tscript:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"java\nscript:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"java" + chr(1) + "script:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"jscript:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"vbscript:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
      t("<a href=\"view-source:foo\">bar</a>", "<a href=\"#foo\">bar</a>");
    }
    
    public void test_self_closing_tags()
    {
      t("<img src=\"a\">","<img src=\"a\" />");
      t("<img src=\"a\">foo</img>", "<img src=\"a\" />foo");
      t("</img>", "");
    }
    
    public void test_comments()
    {
      if (STRIP_COMMENTS) {
        t("<!-- a<b --->", "");
      } else {
        t("<!-- a<b --->", "<!-- a&lt;b --->");
      }
    }
  }
  // ============================================ END-UNIT-TEST ===========================================
}  
