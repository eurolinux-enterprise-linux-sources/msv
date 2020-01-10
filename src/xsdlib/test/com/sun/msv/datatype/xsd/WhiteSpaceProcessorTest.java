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
import org.relaxng.datatype.DatatypeException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * tests WhiteSpaceProcessor.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class WhiteSpaceProcessorTest extends TestCase
{
    public WhiteSpaceProcessorTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(WhiteSpaceProcessorTest.class);
    }
    
    /** test get method */
    public void testGet() throws DatatypeException
    {
        assertSame( "whitespace in parameter must be allowed",
            WhiteSpaceProcessor.get("preserve"),
            WhiteSpaceProcessor.get("preserve  \t \n \r ")
        );
        assertSame(
            WhiteSpaceProcessor.get("collapse"),
            WhiteSpaceProcessor.get("    \r\n  collapse \t ")
        );
        assertSame(
            WhiteSpaceProcessor.get("replace"),
            WhiteSpaceProcessor.get(" \r\n\r\nreplace") );
        
        try
        {
            WhiteSpaceProcessor.get("unknown");
            fail("should throw exception");
        }catch(DatatypeException e){;}
    }
    
    /** test behavior of preserve */
    public void testPreserve() throws DatatypeException
    {
        WhiteSpaceProcessor target = WhiteSpaceProcessor.get("preserve");
        
        String[] tests = new String[] {
            "test",
            "  a  b  c  ",
            "\r\n \ta bb \t\t c   \r\r\n\r  " };
        
        for( int i=0; i<tests.length; i++ )
            assertEquals( tests[i], target.process(tests[i]) );
    }
    
    /** tests behavior of replace */
    public void testReplace() throws DatatypeException
    {
        WhiteSpaceProcessor target = WhiteSpaceProcessor.get("replace");
        
        assertEquals( target.process(
            "test"),
            "test");
        assertEquals( target.process(
            "  a  b  c  "),
            "  a  b  c  ");
        assertEquals( target.process(
            "\r\n \ta bb \t\t c   \r\r\n\r  "),
            "    a bb    c         ");
    }

    /** tests behavior of collapse */
    public void testCollapse() throws DatatypeException
    {
        WhiteSpaceProcessor target = WhiteSpaceProcessor.get("collapse");
        
        assertEquals( target.process(
            "test"),
            "test");
        assertEquals( target.process(
            "  a  b  c  "),
            "a b c");
        assertEquals( target.process(
            "\r\n \ta bb \t\t c   \r\r\n\r  "),
            "a bb c");
        assertEquals( target.process(
            "abc  "),
            "abc");
        assertEquals( target.process(
            "abc "),
            "abc");
    }
    
    /** serializes o and then returns de-serialized object. */
    public Object freezeDry( Object o ) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        
        // serialize it
        oos.writeObject( o );
        oos.flush();
        
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        
        return ois.readObject();
    }
    
    /** test serialization. */
    public void testSerialization() throws Exception {
        
        // ensure that serialization doesn't break
        assertSame( WhiteSpaceProcessor.theCollapse,
            freezeDry(WhiteSpaceProcessor.theCollapse) );

        assertSame( WhiteSpaceProcessor.thePreserve,
            freezeDry(WhiteSpaceProcessor.thePreserve) );
        
        assertSame( WhiteSpaceProcessor.theReplace,
            freezeDry(WhiteSpaceProcessor.theReplace) );
    }
}
