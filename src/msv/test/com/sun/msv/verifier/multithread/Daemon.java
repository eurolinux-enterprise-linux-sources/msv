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

package com.sun.msv.verifier.multithread;

import java.util.EmptyStackException;
import java.util.Stack;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

import com.sun.msv.driver.textui.DebugController;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.verifier.ValidityViolation;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;
import com.sun.msv.verifier.util.ErrorHandlerImpl;

/**
 * multi-thread tester.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class Daemon implements Runnable
{
    public static void main( String[] args ) throws Exception
    {
        new Daemon()._main(args);
    }
    
    Grammar grammar;
    /** file names that have to be validated. */
    private final Stack jobs = new Stack();
    
    private void _main( String[] args ) throws Exception
    {
        if( args.length!=4 )
        {
            System.out.println(
                "Usage: Daemon (relax|trex) <fileprefix> <suffix> <# of threads>\n"+
                "  <prefix>.trex or <prefix>.rlx is used as a schema\n"+
                "  <prefix>.v100<suffix> to <prefix>.v999<suffix> are used as instances");
            return;
        }
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        
        String schemaName;

        if( args[0].equals("relax") )    schemaName = args[1]+".rlx";
        else                            schemaName = args[1]+".trex";

        grammar = GrammarLoader.loadSchema(
                schemaName,
                new DebugController(false,false),
                factory );
        
        
        for( int i=100; i<=999; i++ )
            jobs.push( args[1]+".v"+i+args[2] );
        
        final long startTime = System.currentTimeMillis();
        
        final int m = Integer.parseInt( args[3] );
        System.out.println("Use " + m + " threads");
        Thread[] ts = new Thread[m];
        for( int i=0; i<m; i++ )
        {
            ts[i] = new Thread(this,"#"+i);
            ts[i].start();
        }
        
        System.out.println("launched all threads");
        
        for( int i=0; i<m; i++ )
            ts[i].join();
        
        System.out.println("Time : " + (System.currentTimeMillis()-startTime) + " ms");
    }
    
    public void run()
    {
        final String name = Thread.currentThread().getName();
        try
        {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
//            ExpressionPool localPool = new ExpressionPool(grammar.getPool());
//            ExpressionPool localPool = new ExpressionPool();
            
            while(true)
            {
                String fileName;
                try
                {
                    fileName = (String)jobs.pop();    // stack is synchronized
                }
                catch( EmptyStackException e )
                {
                    System.out.println( name + " completed" );
                    return;
                }
                
                XMLReader r = factory.newSAXParser().getXMLReader();
                Verifier v = new Verifier(
                    new REDocumentDeclaration(grammar),
//                    new REDocumentDeclaration(grammar.getTopLevel(),localPool),
                    new ErrorHandlerImpl() );
                r.setContentHandler(v);
                try
                {
                    r.parse(fileName);
                    if(!v.isValid()) throw new Error(); 
                    System.out.print('.');
                }
                catch( ValidityViolation vv )
                {
                    System.out.println( name + ':' + fileName + " invalid  " + vv.getMessage() );
                }
            }
        }
        catch( Exception e )
        {
            System.out.println( name + " aborted" );
            e.printStackTrace();
        }
    }
}
