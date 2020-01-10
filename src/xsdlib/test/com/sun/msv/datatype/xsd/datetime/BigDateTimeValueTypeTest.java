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
import com.sun.msv.datatype.xsd.DateTimeType;
import com.sun.msv.datatype.xsd.DateType;
import com.sun.msv.datatype.xsd.DurationType;
import com.sun.msv.datatype.xsd.GYearMonthType;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * tests BigDateTimeValueType.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class BigDateTimeValueTypeTest extends TestCase {    
    
    public BigDateTimeValueTypeTest(String testName) {
        super(testName);
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(BigDateTimeValueTypeTest.class);
    }
    
    /** Test of getBigValue method, of class com.sun.msv.datatype.datetime.BigDateTimeValueType. */
    public void testGetBigValue()  throws Exception
    {
        BigDateTimeValueType t = parseYearMonth("2000-01");
        assertEquals( t, t.getBigValue() );
    }
    
    private BigDateTimeValueType parseYearMonth(String s) {
        return (BigDateTimeValueType)
            GYearMonthType.theInstance.createValue(s,null);
    }

    private BigDateTimeValueType parseDateTime( String s ) {
        return (BigDateTimeValueType)DateTimeType.theInstance.createValue(s,null);
    }

    private BigDateTimeValueType parseDate( String s ) {
        return (BigDateTimeValueType)DateType.theInstance.createValue(s,null);
    }
    
    private BigTimeDurationValueType parseDuration( String s ) {
        return (BigTimeDurationValueType)DurationType.theInstance.createValue(s,null);
    }
    
    /** Test of compare method, of class com.sun.msv.datatype.datetime.BigDateTimeValueType. */
    public void testCompare() throws Exception
    {
        // from examples of the spec
        int r;
        
        r = parseDateTime("2000-01-15T00:00:00").compare(
            parseDateTime("2000-02-15T00:00:00") );
        assertEquals( r, Comparator.LESS );
            
        r = parseDateTime("2000-01-15T12:00:00" ).compare(
            parseDateTime("2000-01-16T12:00:00Z") );
        assertEquals( r, Comparator.LESS );
            
        r = parseDateTime("2000-01-01T12:00:00" ).compare(
            parseDateTime("1999-12-31T23:00:00Z") );
        assertEquals( r, Comparator.UNDECIDABLE );
        
        r = parseDateTime("2000-01-16T12:00:00" ).compare(
            parseDateTime("2000-01-16T12:00:00Z") );
        assertEquals( r, Comparator.UNDECIDABLE );
            
        r = parseDateTime("2000-01-16T00:00:00" ).compare(
            parseDateTime("2000-01-16T12:00:00Z") );
        assertEquals( r, Comparator.UNDECIDABLE );
    }
    
    /** Test of normalize method, of class com.sun.msv.datatype.datetime.BigDateTimeValueType. */
    public void testNormalize() throws Exception
    {
        IDateTimeValueType v;
        
        v = parseDateTime("2000-03-04T23:00:00-03:00").normalize();
        
        // equals method compares two by calling normalize,
        // so actually this cannot be said as a testing.
        assertEquals( v, parseDateTime("2000-03-05T02:00:00Z") );
    }
    
    /** Test of add method, of class com.sun.msv.datatype.datetime.BigDateTimeValueType. */
    public void testAdd() throws Exception
    {
        BigDateTimeValueType v;
        
        // from examples of Appendix.E of the spec.
        
        v = parseDateTime("2000-01-12T12:13:14Z").add(
                parseDuration("P1Y3M5DT7H10M3.3S") ).getBigValue();
        assertEquals( v, parseDateTime("2001-04-17T19:23:17.3Z") );
        
        v = parseYearMonth("2000-01").add( parseDuration("-P3M") ).getBigValue();
        assertEquals( v, parseYearMonth("1999-10") );
        
        v = parseDate("2000-01-12-05:00").add(
                parseDuration("PT33H") ).getBigValue();
        assertEquals( v, parseDate("2000-01-13-05:00") );
    }
    
}
