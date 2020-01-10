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

import com.sun.msv.datatype.xsd.DatatypeFactory;
import com.sun.msv.datatype.xsd.StringType;
import com.sun.msv.datatype.xsd.TypeIncubator;
import com.sun.msv.datatype.xsd.XSDatatype;
import org.relaxng.datatype.DatatypeException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/**
 * command-line tester of datatype library.
 * 
 * @author <a href="mailto:kohsuke.kawaguchi@eng.sun.com">Kohsuke KAWAGUCHI</a>
 */
public class CommandLineTester
{
	public static void help()
	{
		System.out.println(
			"base <typeName>\n" +
			"  set base type name.\n" +
			"  this will reset all the facets you've added\n" +
			"add <facet name> <facet value>\n" +
			"  add facet\n" +
			"test <value>\n" +
			"  test if the value is accepted by the current base type and facets\n" +
			"quit\n"+
			"  quit this tool"
		);
	}
	public static void main( String args[] )
		throws java.io.IOException
	{
		System.out.println("XML Schema Part 2 command line tool");
		
		final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		// TypeIncubator is used to "incubate" a type by adding facets.
		// constructor accepts the base type instance.
		TypeIncubator incubator = new TypeIncubator(StringType.theInstance);
		
		while(true)
		{
			try
			{
				System.out.print("-->");
				String s = in.readLine();
				StringTokenizer tokens = new StringTokenizer(s);
			
				String cmd = tokens.nextToken();
			
				if( cmd.equals("base") )
				{
					String typeName = tokens.nextToken();
					
					// to obtain a type by name, call this method.
					XSDatatype dt = DatatypeFactory.getTypeByName(typeName);
					if(dt==null)
					{// if the name is not recognized, null is returned.
						System.out.println("no such type");
						continue;
					}
					incubator = new TypeIncubator(dt);
					continue;
				}
				if( cmd.equals("add") )
				{
					String facetName = tokens.nextToken();
					String facetValue = tokens.nextToken();
					// to add a facet, call add method.
					// you MUST supply a valid ValidationContextProvider,
					// although this example omits one.
					incubator.addFacet( facetName, facetValue, false, null );
					continue;
				}
				if( cmd.equals("test") )
				{
					String value = tokens.nextToken();
                    
					// a type can be derived by derive method.
					// the new type contains all facets that were added.
					XSDatatype dt = incubator.derive("","anonymous");
					
					// check validity.
					if( dt.isValid(value,null) )
						// verify method returns true if the value is valid.
						System.out.println("valid value");
					else
					{// it returns false otherwise,
						// call diagnose method to see what is wrong.
						try
						{
							dt.checkValid(value,null);
							System.out.println("valid");
						}
						catch( DatatypeException diag )
						{
							if( diag.getMessage()==null ) {
								// datatype object may not support diagnosis.
								// in that case, UnsupportedOperationException is thrown.
								System.out.println("invalid: no diagnosys available");
							} else {
								System.out.println("invalid: "+diag.getMessage() );
							}
						}
					}
					continue;
				}
				if( cmd.equals("quit") )
					return;
			
				help();
			}
			catch( DatatypeException bte )
			{// this exception happens in cases like:
				// 1. unapplicable facet is added ("minInclusive" for string, etc.)
				// 2. 
				System.out.println("DatatypeException: " +bte.getMessage() );
			}
			catch( java.util.NoSuchElementException nse )
			{// error in command line parsing.
				System.out.println("???");
				help();
			}
			catch( RuntimeException rte )
			{
				rte.printStackTrace();
			}
		}
	}
}
