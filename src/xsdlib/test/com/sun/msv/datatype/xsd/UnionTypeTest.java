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
 * tests UnionType.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class UnionTypeTest extends TestCase
{
    public UnionTypeTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(UnionTypeTest.class);
    }

    private UnionType createUnion( String newName,
        XSDatatype type1, XSDatatype type2, XSDatatype type3 )
            throws DatatypeException
    {
        return (UnionType)DatatypeFactory.deriveByUnion(
            "", newName, new XSDatatypeImpl[]{
                (XSDatatypeImpl)type1,
                (XSDatatypeImpl)type2,
                (XSDatatypeImpl)type3});
    }
    
    private UnionType createUnion( String newName,
        String type1, String type2, String type3 )
            throws DatatypeException
    {
        return createUnion( newName,
            DatatypeFactory.getTypeByName(type1),
            DatatypeFactory.getTypeByName(type2),
            DatatypeFactory.getTypeByName(type3) );
    }
    
    /** test get method */
    public void testIsAtomType() throws DatatypeException
    {
        // union is not an atom
        assertEquals( XSDatatype.VARIETY_UNION,
            createUnion( "test", "string", "integer", "QName" ).getVariety() );
    }
    
    /** test verify method */
    public void testVerify() throws DatatypeException
    {
        // this test is naive, and we need further systematic testing.
        // but better something than nothing.
        XSDatatype u = createUnion(null,"integer","QName","gYearMonth");
        
        assertTrue( u.isValid("1520",DummyContextProvider.theInstance) );
        assertTrue( u.isValid("foo:role",DummyContextProvider.theInstance) );
        assertTrue( u.isValid("2000-05",DummyContextProvider.theInstance) );
    }
    
    /** test convertToObject method */
    public void testConvertToObject() throws DatatypeException
    {
        XSDatatype tf = DatatypeFactory.getTypeByName("float");
        XSDatatype td = DatatypeFactory.getTypeByName("date");
        XSDatatype th = DatatypeFactory.getTypeByName("hexBinary");
        
        XSDatatype tu = createUnion("myTest", tf, td, th );
        
        assertEquals(
            tu.createValue("2.000",DummyContextProvider.theInstance),
            tf.createValue("2.000",DummyContextProvider.theInstance) );
        assertEquals(
            tu.createValue("2001-02-20",DummyContextProvider.theInstance),
            td.createValue("2001-02-20",DummyContextProvider.theInstance) );
        assertEquals(
            tu.createValue("1f5280",DummyContextProvider.theInstance),
            th.createValue("1F5280",DummyContextProvider.theInstance) );
    }
}
