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

package com.sun.msv.reader.trex.ng;

import javax.xml.parsers.SAXParserFactory;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import util.Checker;
import util.ResourceChecker;

import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.trex.TREXBaseReader;

public class RELAXNGReaderTest extends TestCase
{
    public RELAXNGReaderTest( String name ) { super(name); }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }
    
    public static Test suite() {
        return new TestSuite(RELAXNGReaderTest.class);
    }
    
    /** tests the existence of all messages */
    public void testMessages() throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        
        final RELAXNGReader reader = new RELAXNGReader(null,factory);
        
        Checker checker = new Checker(){
            public void check( String propertyName ) {
                // if the specified property doesn't exist, this will throw an error
                System.out.println(
                    reader.localizeMessage(propertyName,new Object[]{"@@@","@@@","@@@","@@@","@@@"}));
            }
        };
        
        String prefixes[] = new String[]{"ERR_","WRN_"};
        
        for( int i=0; i<prefixes.length; i++ ) {
            ResourceChecker.check( RELAXNGReader.class, prefixes[i], checker );
            ResourceChecker.check( TREXBaseReader.class, prefixes[i], checker );
            ResourceChecker.check( GrammarReader.class, prefixes[i], checker );
        }
    }
}
