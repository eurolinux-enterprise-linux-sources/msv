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

package batch;

import java.io.File;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.xml.parsers.SAXParserFactory;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver;

import batch.driver.DTDValidator;
import batch.driver.GenericValidator;
import batch.driver.IValidatorEx;
import batch.driver.IValidatorImplForRNG;
import batch.driver.IValidatorImplForXS;
import batch.model.TestReader;

/**
 * Test schemata/instances are expected to follow a naming convention.
 * 
 * <ol>
 *  <li>legal schema must have "*.rlx" or "*.trex"
 *  <li>invalid schema must have "*.e.rlx" or "*.e.trex"
 *  <li>valid test document must have "*.vNN.xml".
 *      these documents are validated against "*.rlx" or "*.trex".
 *  <li>invalid test document must have "*.nNN.xml".
 * </ol>
 * 
 * Files that follow this naming convention are all tested. If any unexpected
 * result is found, main method returns non-0 exit code.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public abstract class BatchTester {
    public SAXParserFactory factory = SAXParserFactory.newInstance();

    public CatalogResolver resolver = new CatalogResolver();
    
    /** schema file extension ".rlx", ".trex", or ".dtd" */
    public String ext;
    
    public IValidatorEx validator;
    
    private TestReader reader;
    
    /** Set to true if the strict option is specified. */
    protected boolean strict = false;
    
    
    
    
    /**
     * This method should print the usage information.
     */
    protected abstract void usage();
    
    
    /**
     * Creates the test suite from the specified file/directory.
     */
    public Test parse( String target ) throws Exception {
        return parse(target,false);
    }
    
    /**
     * Creates the test suite from the specified file/directory.
     * 
     * @param recursive
     *        if the target is directory and this parameter is true, then
     *        subdirectories are recursively parsed into a test suite.
     */
    public Test parse( String target, boolean recursive ) throws Exception {
        File src = new File(target);
        
        if(src.isDirectory())
            return reader.parseDirectory( src, ext, recursive );
        else
            return reader.parseSchema(src);
    }
    
    /**
     * Initializes various fields by setting a target schema language.
     */
    public void init( String target, boolean strict ) {
        
        if(strict)
            System.out.println("strict schema check will be done");
        
        if(strict && (
            target.equals("relax") ||
            target.equals("trex") ||
            target.equals("relax") ))
            System.out.println("*** strict option is not supported for the language "+target);
        
        if( target.equals("relax") )    setUp( ".rlx", new GenericValidator() );
        else
        if( target.equals("trex") )        setUp( ".trex", new GenericValidator() );
        else
        if( target.equals("rng") )        setUp( ".rng", new IValidatorImplForRNG(strict) );
        else
        if( target.equals("xsd") )        setUp( ".xsd", new IValidatorImplForXS(strict) );
        else
        if( target.equals("dtd") )        setUp( ".dtd", new DTDValidator() );
        else
            throw new Error("unrecognized language type: "+target );
    }
    
    /**
     * This method is called when the schema language is detected.
     */
    protected void setUp( String _ext, IValidatorEx _validator ) {
        this.ext = _ext;
        this.validator = _validator;
        this.reader = createReader();
    }
    
    /**
     * This method is called as the last step of set-up
     * to create TestReader object which will parse test files.
     */
    protected abstract TestReader createReader();
    
    
    protected void onOption( String opt ) throws Exception {
        System.out.println("unrecognized option:"+opt);
        throw new Error();
    }
    
    public void run( String[] av ) throws Exception {
        
        String target = null;
        Vector instances = new Vector();
        boolean recursive = false;
        
        for( int i=0; i<av.length; i++ ) {
            if(av[i].charAt(0)=='-') {
                if(av[i].equals("-strict")) {
                    strict = true;
                    continue;
                }
                if(av[i].equals("-recursive")) {
                    recursive = true;
                    continue;
                }
                
                onOption(av[i]);
            } else {
                if(target==null)
                    target = av[i];
                else
                    instances.add(av[i]);
            }
        }

        if( instances.size()==0 ) {
            usage();
            return;
        }
        
        init(target,strict);
        
        // collect test cases
        TestSuite s = new TestSuite();
        for( int i=0; i<instances.size(); i++ )
            s.addTest(parse((String)instances.get(i),recursive));
        
        junit.textui.TestRunner.run(s);
    }

    /*
    public static void report( ValidityViolation vv ) {
        System.out.println(
            vv.getLineNumber()+":"+vv.getColumnNumber()+
            "  " + vv.getMessage());
    }
    */
    
    
    /**
     * Creates a JUnit test suite from a set of paths that are specified
     * by a system property.
     * 
     * Mainly used to run an automated test from Ant.
     */
    public TestSuite createFromProperty( String target, String propertyName ) throws Exception {
                                                                                                   
        init(target, System.getProperty("MSV_STRICT_CHECK")!=null);
        
        String property = System.getProperty(propertyName);
        if(property==null)
            return new TestSuite();    // return an empty test suite.
        
        StringTokenizer tokens = new StringTokenizer( property, File.pathSeparator );

        // collect test cases
        TestSuite s = new TestSuite();
        while( tokens.hasMoreTokens() ) {
            String name = tokens.nextToken();
            if(name.charAt(name.length()-1)=='@')
                s.addTest(parse(name.substring(0,name.length()-1),true));
            else
                s.addTest(parse(name));
        }
        
        return s;
    }
}
