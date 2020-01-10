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

package com.sun.msv.datatype.xsd.datetime;

import com.sun.msv.datatype.xsd.Comparator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * tests BigTimeDurationValueType.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class BigTimeDurationValueTypeTest extends TestCase {    
    
    public BigTimeDurationValueTypeTest(String testName) {
        super(testName);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(BigTimeDurationValueTypeTest.class);
    }
    
    private ITimeDurationValueType get( String s ) throws IllegalArgumentException {
        return new BigTimeDurationValueType(s);
    }
    
    /** Test of hashCode method, of class com.sun.msv.datatype.datetime.BigTimeDurationValueType. */
    public void testHashCode() throws Exception
    {
        assertEquals( get("P400Y").hashCode(), get("P146097D").hashCode() );
        assertEquals( get("P1D").hashCode(), get("PT24H").hashCode() );
    }
    
    /** Test of compare method, of class com.sun.msv.datatype.datetime.BigTimeDurationValueType. */
    public void testCompare() throws Exception
    {
        assertEquals( get("P1D").compare( get("PT24H") ), Comparator.EQUAL );

        assertEquals( get("P1Y").compare( get("P364D") ), Comparator.GREATER );
        assertEquals( get("P1Y").compare( get("P365D") ), Comparator.GREATER );
        assertEquals( get("P1Y").compare( get("P366D") ), Comparator.LESS );
        assertEquals( get("P1Y").compare( get("P367D") ), Comparator.LESS );

        assertEquals( get("P1M").compare( get("P27D") ), Comparator.GREATER );
        assertEquals( get("P1M").compare( get("P28D") ), Comparator.GREATER );
        assertEquals( get("P1M").compare( get("P29D") ), Comparator.UNDECIDABLE );
        assertEquals( get("P1M").compare( get("P30D") ), Comparator.UNDECIDABLE );
        assertEquals( get("P1M").compare( get("P31D") ), Comparator.LESS );
        assertEquals( get("P1M").compare( get("P32D") ), Comparator.LESS );
        
        assertEquals( get("P5M").compare( get("P149D") ), Comparator.GREATER );
        assertEquals( get("P5M").compare( get("P150D") ), Comparator.GREATER );
        assertEquals( get("P5M").compare( get("P151D") ), Comparator.UNDECIDABLE );
        assertEquals( get("P5M").compare( get("P152D") ), Comparator.UNDECIDABLE );
        assertEquals( get("P5M").compare( get("P153D") ), Comparator.LESS );
        assertEquals( get("P5M").compare( get("P154D") ), Comparator.LESS );
        
        assertEquals( get("P400Y").compare( get("P146097D") ), Comparator.EQUAL );
    }
    
    /** Test of getBigValue method, of class com.sun.msv.datatype.datetime.BigTimeDurationValueType. */
    public void testGetBigValue() throws Exception
    {
        ITimeDurationValueType td = get("P153D");
        assertSame( td.getBigValue(), td );
    }
    
//    /** Test of fromMinutes method, of class com.sun.msv.datatype.datetime.BigTimeDurationValueType. */
///    public void testFromMinutes()
}
