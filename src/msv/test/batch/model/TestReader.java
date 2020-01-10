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

package batch.model;

import java.io.File;
import java.io.FilenameFilter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Parses a directory into the test suite.
 * 
 * This object will enumerate test files, and TestBuilder will create
 * actual test cases.
 * 
 * @author
 *    <a href="mailto:kohsuke.kawaguchi@sun.com">Kohsuke KAWAGUCHI</a>
 */
public class TestReader
{
    public TestReader( TestBuilder _builder ) {
        this.builder = _builder;
    }

    private final TestBuilder builder;
    

    
    /**
     * Obtains a test object from a schema file (e.g., abc.rng) and
     * its instance documents.
     */
    public Test parseSchema( File schema ) throws Exception {

        String schemaName = schema.getName();
        File parent = new File(schema.getParent());
        
        final String prefix = schemaName.substring(0, schemaName.lastIndexOf('.')+1);
        final boolean isCorrect = schemaName.indexOf(".e.")==-1;
        
        if(isCorrect) {
            TestSuite suite = new TestSuite();
            
            suite.addTest( builder.createCorrectSchemaTest(schema) );
                
            // collects test instances.            
            String[] instances = parent.list( new FilenameFilter(){ 
                public boolean accept( File dir, String name ) {
                    return name.startsWith(prefix) && name.endsWith(".xml");
                }
            } );
            
            if( instances!=null ) {
                for( int i=0; i<instances.length; i++ ) {
                    boolean isValid = instances[i].indexOf(".v")!=-1;
                    File document = new File(parent,instances[i]);
                    if(isValid)
                        suite.addTest( builder.createValidDocumentTest(document) );
                    else
                        suite.addTest( builder.createInvalidDocumentTest(document) );
                }
            }
            
            return suite;
            
        } else {
            // if this schema is invalid
            return builder.createIncorrectSchemaTest(schema);
        }
    }


    /**
     * Parses a directory into a test suite .
     */
    public Test parseDirectory(
            File dir, final String ext, boolean recurseSubDirectory ) throws Exception {
        
        TestSuite suite = new TestSuite();
        
        // enumerate all schema
        String[] schemas = dir.list( new FilenameFilter(){
            public boolean accept( File dir, String name ) {
                return name.endsWith(ext);
            }
        } );
        
        for( int i=0; i<schemas.length; i++ )
            suite.addTest( parseSchema(new File(dir,schemas[i])) );
        
        if( recurseSubDirectory ) {
            // recursively process sub directories.
            String[] subdirs = dir.list( new FilenameFilter(){
                public boolean accept( File dir, String name ) {
                    return new File(dir,name).isDirectory();
                }
            });
            for( int i=0; i<subdirs.length; i++ )
                suite.addTest( parseDirectory(
                    new File(dir,subdirs[i]), ext, true ) );
        }
        
        return suite;
    }


    static final DocumentBuilderFactory domFactory;
    static {
        domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        domFactory.setValidating(false);
    }
    
    static final SAXParserFactory saxFactory;
    static {
        saxFactory = SAXParserFactory.newInstance();
        saxFactory.setNamespaceAware(true);
        saxFactory.setValidating(false);
    }
    
    
}
