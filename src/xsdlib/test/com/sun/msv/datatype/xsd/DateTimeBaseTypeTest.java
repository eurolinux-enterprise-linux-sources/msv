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

import java.util.Calendar;

/**
 * tests DateTimeBaseType.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class DateTimeBaseTypeTest extends TestCase
{
    public DateTimeBaseTypeTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(DateTimeBaseTypeTest.class);
    }
    
    public void testCreateJavaObject() throws Exception {
        Calendar o;
        
        o = (Calendar)DateTimeType.theInstance.createJavaObject(
            "2001-01-02T03:04:05.678Z", null );
        assertEquals( "2 Jan 2001 03:04:05 GMT", o.getTime().toGMTString() );
        assertEquals( 0, o.getTimeZone().getRawOffset() );
        assertEquals( 678, o.get(Calendar.MILLISECOND) );
        
        // time zone less.
        o = (Calendar)DateTimeType.theInstance.createJavaObject(
            "2001-01-02T03:04:05", null );
        // 
        assertSame( com.sun.msv.datatype.xsd.datetime.TimeZone.MISSING,
            o.getTimeZone() );
        
        // non-GMT.
        // when California (-08:00) is 3 AM, it will be 11AM in Greenwich.
        o = (Calendar)DateTimeType.theInstance.createJavaObject(
            "2001-01-02T03:04:05.678-08:00", null );
        assertEquals( "2 Jan 2001 11:04:05 GMT", o.getTime().toGMTString() );
        assertEquals( -8*60*60*1000, o.getTimeZone().getRawOffset() );
        
    // time type.
        o = (Calendar)TimeType.theInstance.createJavaObject("08:12:30Z",null);
        assertTrue( !o.isSet(Calendar.YEAR) );
        assertTrue( !o.isSet(Calendar.MONTH) );
        assertTrue( !o.isSet(Calendar.DAY_OF_MONTH) );
        assertEquals( 8, o.get(Calendar.HOUR) );
        assertEquals( 12, o.get(Calendar.MINUTE) );
        assertEquals( 30, o.get(Calendar.SECOND) );
    }
}
