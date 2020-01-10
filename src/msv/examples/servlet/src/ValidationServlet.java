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

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.xml.sax.*;
import org.iso_relax.verifier.*;

public class ValidationServlet extends HttpServlet {
    
    public void doGet(HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
        // just in case the browser tries to read this servlet,
        // redirect it to the demo page.
        response.sendRedirect("index.html");
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response)
                throws IOException, ServletException {
        
        String message;
        try {
            // parse the request body as an XML document
            InputSource document = new InputSource(request.getReader());
        
            // create a verifier. Since multiple threads can concurrently call
            // the doGet method, we cannot reuse one verifier object.
            Verifier verifier = schema.newVerifier();
        
            // set an error handler that do nothing.
            // we will call the isValid method, so we don't want an error to be thrown as an exception.
            verifier.setErrorHandler( silentErrorHandler );
        
            boolean isValid = verifier.verify(document);
        
            if(isValid)        message = "valid";
            else            message = "invalid";
            
        } catch( VerifierConfigurationException e ) {
            // technically, this exception can be thrown from the newVerifier method.
            // but usually this is very unlikely (because we've already compiled schema
            // and there are not much things that can go wrong.)
            
            log("unable to create a new verifier",e);
            throw new ServletException(e);
            
        } catch( SAXException e ) {
            // verify method may throw this exception.
            // this usually happens when the input document is not wellformed.
            // so we don't send a log in this case.
              
            message = "SAXException:"+e.getMessage();
        }
        
        // send a response back to the client
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        writer.write(message);
    }
    
    /**
     * Compiled schema.
     */
    private Schema schema = null;
    
    
    public void init() throws ServletException {
        super.init();
        
        // compile a schema when the servlet is initialized
        // ------------------------------------------------
        VerifierFactory factory = new com.sun.msv.verifier.jarv.TheFactoryImpl();
        
        try {
            String schemaName = "myschema.dtd";
            
            // in this example, we load a schema from a resource file,
            // which is not visible to clients.
            // you may want to use getServletContext().getResourceAsStream()
            InputStream stream = ValidationServlet.class.getResourceAsStream(schemaName);
            if(stream==null)
                throw new ServletException("unable to locate the schema resource");
            
            InputSource is = new InputSource(stream);
            is.setSystemId(schemaName);
            
            schema = factory.compileSchema(is);
        } catch( Exception e ) {
            // VerifierConfigurationException and SAXException can be thrown,
            // but they don't have much difference in the meaning.
            
            // send the error message to a log file and abort.
            log( "unable to parse the schema file", e );
            throw new ServletException(e);
        }
    }
    
    /**
     * An error handler implementation that doesn't report any error.
     */
    private static final ErrorHandler silentErrorHandler = new ErrorHandler() {
        public void fatalError( SAXParseException e ) {}
        public void error( SAXParseException e ) {}
        public void warning( SAXParseException e ) {}
    };
}
