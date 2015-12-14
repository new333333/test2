/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * User: david
 * Date: 4/2/13
 * Time: 10:32 AM
 */
public class HtmlToTextParser {
    protected static Map<String, String> tags;
    protected static Set<String> ignoredTags;

    static {
        tags = new HashMap<String, String>();
        tags.put("div", "\n");
        tags.put("d1", "\n");
        tags.put("form", "\n");
        tags.put("h1", "\n");
        tags.put("/h1", "\n");
        tags.put("h2", "\n");
        tags.put("/h2", "\n");
        tags.put("h3", "\n");
        tags.put("/h3", "\n");
        tags.put("h4", "\n");
        tags.put("/h4", "\n");
        tags.put("h5", "\n");
        tags.put("/h5", "\n");
        tags.put("h6", "\n");
        tags.put("/h6", "\n");
        tags.put("p", "\n");
        tags.put("/p", "\n");
        tags.put("table", "\n");
        tags.put("/table", "\n");
        tags.put("u1", "\n");
        tags.put("/u1", "\n");
        tags.put("ol", "\n");
        tags.put("/ol", "\n");
        tags.put("/li", "\n");
        tags.put("br", "\n");
        tags.put("/td", "\n");
        tags.put("/tr", "\n");
        tags.put("/pre", "\n");

        ignoredTags = new HashSet<String>();
        ignoredTags.add("script");
        ignoredTags.add("noscript");
        ignoredTags.add("style");
        ignoredTags.add("object");
    }

    public static String htmlToText(String html) {
        HtmlToTextParser parser = new HtmlToTextParser();
        return parser.convert(html);
    }

    private static class TextBuilder
    {
        private StringBuilder text;
        private StringBuilder currLine;
        private int emptyLines;
        private boolean preformatted;

        // Construction
        private TextBuilder()
        {
            text = new StringBuilder();
            currLine = new StringBuilder();
            emptyLines = 0;
            preformatted = false;
        }

        public boolean isPreformatted() {
            return preformatted;
        }

        public void setPreformatted(boolean preformatted) {
            if (preformatted) {
                if (currLine.length()>0) {
                    flushCurrLine();
                }
                emptyLines = 0;
            }
            this.preformatted = preformatted;
        }

        /// <summary>
        /// Clears all current text.
        /// </summary>
        public void clear()
        {
            text.setLength(0);
            currLine.setLength(0);
            emptyLines = 0;
        }

        public void write(String s)
        {
            for (char c : s.toCharArray()) {
                write(c);
            }
        }

        public void write(char c)
        {
            if (preformatted)
            {
                // Write preformatted character
                text.append(c);
            }
            else
            {
                if (c == '\r')
                {
                    // Ignore carriage returns. We'll process
                    // '\n' if it comes next
                }
                else if (c == '\n')
                {
                    // Flush current line
                    flushCurrLine();
                }
                else if (Character.isWhitespace(c))
                {
                    // Write single space character
                    int len = currLine.length();
                    if (len == 0 || !Character.isWhitespace(currLine.charAt(len - 1))) {
                        currLine.append(' ');
                    }
                }
                else
                {
                    currLine.append(c);
                }
            }
        }

        // Appends the current line to output buffer
        protected void flushCurrLine()
        {
            // Get current line
            String line = currLine.toString().trim();

            // Determine if line contains non-space characters
            String tmp = line.replace("&nbsp;", "");
            if (tmp.length() == 0)
            {
                // An empty line
                emptyLines++;
                if (emptyLines < 2 && text.length() > 0) {
                    text.append(line);
                    text.append('\n');
                }
            }
            else
            {
                // A non-empty line
                emptyLines = 0;
                text.append(line);
                text.append('\n');
            }

            // Reset current line
            currLine.setLength(0);
        }

        @Override
        public String toString() {
            if (currLine.length() > 0) {
                flushCurrLine();
            }
            if (text.length()>0 && text.charAt(text.length()-1)=='\n') {
                return text.substring(0, text.length()-1);
            }
            return text.toString();
        }
    }

    private static class Tag {
        private String name;
        private boolean selfClosing;
    }

    private TextBuilder text;
    private String html;
    private int pos;

    public String convert(String html) {
        // Initialize state variables
        text = new TextBuilder();
        this.html = html;
        pos = 0;

        // Process input
        while (!endOfText()) {
            if (peek() == '<')
            {
                // HTML tag
                Tag tag = parseTag();

                // Handle special tag cases
                if (tag.name.equals("body"))
                {
                    // Discard content before <body>
                    text.clear();
                }
                else if (tag.name.equals("/body"))
                {
                    // Discard content after </body>
                    pos = html.length();
                }
                else if (tag.name.equals("pre"))
                {
                    // Enter preformatted mode
                    text.setPreformatted(true);
                    eatWhitespaceToNextLine();
                }
                else if (tag.name.equals("/pre"))
                {
                    // Exit preformatted mode
                    text.setPreformatted(false);
                }

                String value = tags.get(tag.name);
                if (value!=null)
                    text.write(value);

                if (ignoredTags.contains(tag.name))
                    eatInnerContent(tag.name);
            }
            else if (Character.isWhitespace(peek()))
            {
                // Whitespace (treat all as space)
                text.write(text.isPreformatted() ? peek() : ' ');
                moveAhead();
            }
            else
            {
                // Other text
                text.write(peek());
                moveAhead();
            }
        }
        // Return result
        return StringEscapeUtils.unescapeHtml(text.toString());
    }

    // Eats all characters that are part of the current tag
    // and returns information about that tag
    protected Tag parseTag()
    {
        Tag tag = new Tag();
        tag.name = "";
        tag.selfClosing = false;

        if (peek() == '<') {
            moveAhead();

            // Parse tag name
            eatWhitespace();
            int start = pos;
            if (peek() == '/') {
                moveAhead();
            }

            while (!endOfText() && !Character.isWhitespace(peek()) && peek() != '/' && peek() != '>') {
                moveAhead();
            }
            tag.name = html.substring(start, pos).toLowerCase();

            // Parse rest of tag
            while (!endOfText() && peek() != '>') {
                if (peek() == '"' || peek() == '\'') {
                    eatQuotedValue();
                } else {
                    if (peek() == '/') {
                        tag.selfClosing = true;
                    }
                    moveAhead();
                }
            }
            moveAhead();
        }
        return tag;
    }

    // Consumes inner content from the current tag
    protected void eatInnerContent(String tag)
    {
        String endTag = "/" + tag;

        while (!endOfText()) {
            if (peek() == '<') {
                // Consume a tag
                Tag t = parseTag();
                if (t.name.equals(endTag)) {
                    return;
                }
                // Use recursion to consume nested tags
                if (!t.selfClosing && !tag.startsWith("/")) {
                    eatInnerContent(tag);
                }
            }
            else moveAhead();
        }
    }

    // Returns true if the current position is at the end of
    // the string
    protected boolean endOfText()
    {
        return (pos >= html.length());
    }

    // Safely returns the character at the current position
    protected char peek()
    {
        return (pos < html.length()) ? html.charAt(pos) : (char)0;
    }

    // Safely advances to current position to the next character
    protected void moveAhead()
    {
        pos = Math.min(pos + 1, html.length());
    }

    // Moves the current position to the next non-whitespace
    // character.
    protected void eatWhitespace()
    {
        while (Character.isWhitespace(peek())) {
            moveAhead();
        }
    }

    // Moves the current position to the next non-whitespace
    // character or the start of the next line, whichever
    // comes first
    protected void eatWhitespaceToNextLine()
    {
        while (Character.isWhitespace(peek())) {
            char c = peek();
            moveAhead();
            if (c == '\n') {
                break;
            }
        }
    }

    // Moves the current position past a quoted value
    protected void eatQuotedValue() {
        char c = peek();
        if (c == '"' || c == '\'') {
            // Opening quote
            moveAhead();
            // Find end of value
            int start = pos;

            pos = indexOfAny(html, new char[] { c, '\r', '\n' }, pos);
            if (pos < 0) {
                pos = html.length();
            } else {
                moveAhead();    // Closing quote
            }
        }
    }

    protected int indexOfAny(String str, char [] chars, int fromIndex) {
        int index = -1;
        for (char ch: chars) {
            int i = str.indexOf(ch, fromIndex);
            if (index==-1 || i<index) {
                index = i;
            }
        }
        return index;
    }
}
