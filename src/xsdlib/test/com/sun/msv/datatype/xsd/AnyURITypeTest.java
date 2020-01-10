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

package com.sun.msv.datatype.xsd;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * tests AnyURIType.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class AnyURITypeTest extends TestCase
{
    public AnyURITypeTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(AnyURITypeTest.class);
    }
    
    /** test escaping of ASCII characters */
    public void testAsciiEscape()
    {
        assertEquals( AnyURIType.escape(""), "" );
        assertEquals( AnyURIType.escape("ABCXYZ"), "ABCXYZ" );
        
        // those characters may not be escaped.
        assertEquals( AnyURIType.escape("-_.!~*'()[]#%"), "-_.!~*'()[]#%" );
        
        // those characters have to be escaped.
        assertEquals( AnyURIType.escape(" \""), "%20%22" );
    }
    
    /** test %HH escaping of non-ASCII characters. */
    public void testNonAsciiEscape()
    {
        assertEquals( AnyURIType.escape(
            new String( new char[]{0x125} ) ),
            "%C4%A5" );    // latin small letter h with circumflex
        // also known as Planck constant per the speed of light in Physics.
        
        assertEquals( AnyURIType.escape(
            new String( new char[]{0x937} ) ),
            "%E0%A4%B7" ); // devanagari letter SSA
        
        assertEquals( AnyURIType.escape(
            new String( new char[]{0xD8A5,0xDDC3} ) ),
            "%F0%A9%97%83" );    // #x295C3
    }
}
