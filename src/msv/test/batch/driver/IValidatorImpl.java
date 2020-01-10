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

package batch.driver;

import java.io.File;

import javax.xml.parsers.SAXParserFactory;

import org.iso_relax.verifier.Schema;
import org.iso_relax.verifier.VerifierFilter;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

import batch.model.ISchema;

import com.sun.msv.driver.textui.ReportErrorHandler;
import com.sun.msv.grammar.Grammar;
import com.sun.msv.reader.GrammarReader;
import com.sun.msv.reader.GrammarReaderController;
import com.sun.msv.reader.util.GrammarLoader;
import com.sun.msv.verifier.IVerifier;
import com.sun.msv.verifier.ValidityViolation;
import com.sun.msv.verifier.Verifier;
import com.sun.msv.verifier.regexp.REDocumentDeclaration;

/**
 * Test harness for RELAX NG conformance test suite.
 */
public abstract class IValidatorImpl extends AbstractValidatorExImpl
{
    /**
     * If true, this validator will apply extra checkes to the schema.
     */
    private final boolean strictCheck;
    
    /**
     * SAXParserFactory which should be used to parse a schema.
     */
    protected SAXParserFactory factory;
    
    public IValidatorImpl( boolean _strictCheck ) {
        strictCheck = _strictCheck;
        
        if(strictCheck) {
            // if we run a strict check, wrap it by the s4s
            factory = new com.sun.msv.verifier.jaxp.SAXParserFactoryImpl(
                getSchemaForSchema());
        } else {
            // create a plain SAXParserFactory
            factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
        }
    }
    
    /**
     * Gets the schema for schema for this language.
     */
    protected Schema getSchemaForSchema() { return null; }
    
    public ISchema parseSchema( File file ) throws Exception {
        GrammarReader reader = getReader();
        
        InputSource source = com.sun.msv.util.Util.getInputSource(
            file.getAbsolutePath());
        XMLReader parser = factory.newSAXParser().getXMLReader();
        
        if(!strictCheck) {
            parser.setContentHandler(reader);
        } else {
            Schema schema = getSchemaForSchema();
            final boolean[] error = new boolean[1];
            
            // set up a pipe line so that the file will be validated by s4s
            VerifierFilter filter = schema.newVerifier().getVerifierFilter();
            filter.setErrorHandler( new ReportErrorHandler() {
                public void error( SAXParseException e ) throws SAXException {
                    super.error(e);
                    error[0]=true;
                }
                public void fatalError( SAXParseException e ) throws SAXException {
                    super.fatalError(e);
                    error[0]=true;
                }
            });
            
            filter.setContentHandler(reader);
            parser.setContentHandler((ContentHandler)filter);
            
            if( error[0]==true )        return null;
        }

        try {
            parser.parse(source);
        } catch( ValidityViolation vv ) {
            System.out.println(vv.getMessage());
            return null;
        }
        
        Grammar grammar = getGrammarFromReader(reader,file);
        
        if( grammar==null )    return null;
        else                return new ISchemaImpl( grammar );
    }

    public Grammar parseSchema( InputSource is, GrammarReaderController controller ) throws Exception {
        return GrammarLoader.loadSchema(is,createController(),factory);
    }

    /**
     * creates a GrammarReader object to parse a grammar.
     * 
     * <p>
     * override this method to use different reader implementation.
     * RELAX NG test harness can be used to test XML Schema, TREX, etc.
     */
    protected abstract GrammarReader getReader();
    
    protected Grammar getGrammarFromReader( GrammarReader reader, File schema ) {
        return reader.getResultAsGrammar();
    }
    
    /**
     * creates a Verifier object to validate a document.
     * 
     * <p>
     * override this method to use a different verifier implementation.
     */
    protected IVerifier getVerifier( Grammar grammar ) {
        return new Verifier( new REDocumentDeclaration(grammar),
            new ReportErrorHandler() );
    }
}
