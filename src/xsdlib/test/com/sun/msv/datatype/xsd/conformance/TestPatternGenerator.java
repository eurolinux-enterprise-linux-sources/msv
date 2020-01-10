/*
 * Copyright (c) 2001-2013 Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sun.msv.datatype.xsd.conformance;

import org.jdom.Element;

import java.util.List;

/**
 * parses XML representation of test pattern.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
class TestPatternGenerator
{
    /**
     * parses test pattern specification and returns it.
     *
     * @param patternElement
     *        one of "combination","choice","facet".
     */
    public static TestPattern parse( Element patternElement )
        throws Exception
    {
        final String tagName = patternElement.getName();
        if( tagName.equals("combination") || tagName.equals("choice") )
        {
            // parse children
            List lst = patternElement.getChildren();
            TestPattern[] children = new TestPattern[lst.size()];
            for( int i=0; i<lst.size(); i++ )
                children[i] = parse( (Element)lst.get(i) );
            
            if( tagName.equals("combination") )
            {
                boolean mode = true;    // default is 'AND'
                if( patternElement.getAttribute("mode")!=null
                &&  patternElement.getAttributeValue("mode").equals("or") )
                    mode = false;
                
                return new FullCombinationPattern(children,mode);
            }
            else
            {
                return new ChoiceTestPattern(children);
            }
        }
        if( tagName.equals("facet") )
        {
            return new SimpleTestPattern(
                patternElement.getAttributeValue("name"),
                patternElement.getAttributeValue("value"),
                trimAnswer(patternElement.getAttributeValue("answer")) );
        }
        
        throw new Exception("unknown pattern:"+tagName);
    }
    
    public static String trimAnswer( String answer )
    {
        String r="";
        final int len = answer.length();
        for( int i=0; i<len; i++ )
        {
            final char ch = answer.charAt(i);
            if(ch=='o' || ch=='.')    r+=ch;
        }
        
        return r;
    }

    /** merges another test case into this */
    public static String merge( String a1, String a2, boolean mergeAnd )
    {
        if(a1==null)    return a2;
        if(a2==null)    return a1;
        if( a1.length()!=a2.length() )
            throw new Error("assertion: lengths of the answers are different");
        
        final int len = a1.length();
        String newAnswer ="";
        for( int i=0; i<len; i++ )
        {
            if( ( mergeAnd && (a1.charAt(i)=='o' && a2.charAt(i)=='o' ) )
            ||  (!mergeAnd && (a1.charAt(i)=='o' || a2.charAt(i)=='o' ) ) )
                newAnswer += "o";
            else
                newAnswer += ".";
        }
        
        return newAnswer;
    }
}
