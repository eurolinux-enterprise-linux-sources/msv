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

package com.sun.msv.verifier.jaxp;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Tests the typical use cases of JAXP masquerading with DocumentBuilderFactory
 */
public class DOMBuilderTest extends TestCase
{
    public DOMBuilderTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(DOMBuilderTest.class);
    }
    

    
//    
//    
// use of DocumentBuilderFactory without schema
//====================================================
//
//
    public void testScenario1_1() throws Exception {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        doTest1(factory);
    }

    public void testScenario1_2() throws Exception {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        factory.setNamespaceAware(true);
        doTest1(factory);
    }
    
    private void doTest1( DocumentBuilderFactory factory ) throws Exception {
        // use it without any schema
        parse(factory.newDocumentBuilder(),true);

        // set the incorrect schema and expect the error
        try {
            factory.setAttribute( Const.SCHEMA_PROPNAME,
                new InputSource( new StringReader(TestConst.incorrectSchema) ) );
            fail("incorrect schema was accepted");
        } catch( IllegalArgumentException e ) {
            // it should throw this exception
        }

        // set the schema.
        factory.setAttribute( Const.SCHEMA_PROPNAME,
            new InputSource( new StringReader(TestConst.rngSchema) ) );
        
        // then parse again.
        parse(factory.newDocumentBuilder(),false);
        
        // set another schema.
        factory.setAttribute( Const.SCHEMA_PROPNAME,
            new InputSource( new StringReader(TestConst.xsdSchema) ) );
        
        // then parse again.
        parse(factory.newDocumentBuilder(),false);
    }

    
    public void testScenario2_1() throws Exception {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        doTest2(factory);
    }
        
    public void testScenario2_2() throws Exception {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        factory.setNamespaceAware(true);
        doTest2(factory);
    }
        
    private void doTest2( DocumentBuilderFactory factory ) throws Exception {
        DocumentBuilder builder1 = factory.newDocumentBuilder();
        
        // set the schema.
        // since builder1 is already created, it should not be affected
        // by this change.
        factory.setAttribute( Const.SCHEMA_PROPNAME,
            new InputSource( new StringReader(TestConst.xsdSchema) ) );
        DocumentBuilder builder2 = factory.newDocumentBuilder();
        
        parse(builder2,false);
        parse(builder1,true);
    }
    
    
    
    public void testScenario3_1() throws Exception {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        doTest3(factory);
    }
    public void testScenario3_2() throws Exception {
        DocumentBuilderFactory factory = new DocumentBuilderFactoryImpl();
        factory.setNamespaceAware(true);
        doTest3(factory);
    }
    
    private void doTest3( DocumentBuilderFactory factory ) throws Exception {
        // set the schema.
        factory.setAttribute( Const.SCHEMA_PROPNAME,
            new InputSource( new StringReader(TestConst.rngSchema) ) );
        
        factory.newDocumentBuilder().parse( new InputSource(
                    new StringReader("<root foo='abc'>abc</root>")) );
    }
    
    
    private void parse( DocumentBuilder builder, boolean expectationForInvalid ) throws Exception {
        // parse test. test the invalid case first to make sure that this failure
        // won't affect the rest of the story.
        
        for( int i=0; i<2; i++ ) {
            try {
                builder.parse( new InputSource(
                    new StringReader(TestConst.invalidDocument)) );
                if(expectationForInvalid==false)
                    fail("failed to reject an invalid document");
            } catch( SAXException e ) {
                if(expectationForInvalid==true)
                    fail("failed to accept a valid document");
            }
        
            builder.parse( new InputSource(
                new StringReader(TestConst.validDocument)) );
        }
    }
}
