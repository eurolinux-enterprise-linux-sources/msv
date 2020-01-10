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

import com.sun.msv.datatype.xsd.conformance.DummyContextProvider;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.relaxng.datatype.DatatypeException;

/**
 * tests ListType.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class ListTypeTest extends TestCase
{
    public ListTypeTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(ListTypeTest.class);
    }

    private ListType createList( String newName, XSDatatype itemType ) throws DatatypeException
    {
        return (ListType)DatatypeFactory.deriveByList("",newName,itemType);
    }
    
    private ListType createList( String newName, String itemType ) throws DatatypeException
    {
        return createList( newName, DatatypeFactory.getTypeByName(itemType) );
    }
    
    /** test getVariety method */
    public void testGetVariety() throws DatatypeException
    {
        // list is not an atom
        assertEquals( XSDatatype.VARIETY_LIST,
            createList( "test", "string" ).getVariety() );
    }
    
    /** test verify method */
    public void testVerify() throws DatatypeException
    {
        // this test is naive, and we need further systematic testing.
        // but better something than nothing.
        XSDatatype t = createList("test","short");
        
        assertTrue( t.isValid("  12  \t13 \r\n14\n \t   5  99  ",
            DummyContextProvider.theInstance ));
        assertTrue(!t.isValid("  51 2 6 fff  ",
            DummyContextProvider.theInstance ));
        
        assertTrue( t.isValid("",    // this should be considered as a length 0 list
            DummyContextProvider.theInstance ));
        assertTrue( t.isValid(" \t \n ",
            DummyContextProvider.theInstance ));
    }
    
    /** test convertToObject method */
    public void testConvertToObject() throws DatatypeException
    {
        XSDatatype t = createList("myTest", "string" );

        ListValueType v = (ListValueType)
            t.createValue("  a b  c",DummyContextProvider.theInstance);
        
        assertTrue(v.values.length==3);
        assertEquals(v.values[0],"a");
        assertEquals(v.values[1],"b");
        assertEquals(v.values[2],"c");
    }
}
