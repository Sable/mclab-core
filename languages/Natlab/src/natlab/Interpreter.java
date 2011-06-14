// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

package natlab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import ast.ASTNode;
import ast.Program;
import beaver.Parser;

public class Interpreter {
	private Interpreter() {}

	public static void main(String[] args) throws IOException {
		System.out.println("Welcome to the Natlab Interpreter!");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			System.out.print("> ");
			System.out.flush();

			String line = in.readLine();
			if(line == null) {
				break;
			}

			CommentBuffer commentBuffer = new CommentBuffer();
			NatlabParser parser = new NatlabParser();
			parser.setCommentBuffer(commentBuffer);
			NatlabScanner scanner = null;
			try {
				// Temporarily changed to accept a file name - JL 2008.05.27 
				scanner = new NatlabScanner(new FileReader(line));
				scanner.setCommentBuffer(commentBuffer);
				//NatlabScanner scanner = new NatlabScanner(new StringReader(line));
				Program original = (Program) parser.parse(scanner);
				if(parser.hasError()) {
					System.out.println("**ERROR**");
					for(String error : parser.getErrors()) {
						System.out.println(error);
					}
				} else if(!original.errorCheck()) {
                    //pass true to get transformed ast
					//System.out.println(original.getStructureString( ));
					//System.out.println(original.XMLtoString(original.ASTtoXML()));
					// System.out.println(original.dumpTree());
				}
			} catch(Parser.Exception e) {
				System.out.println("**ERROR**");
				System.out.println(e.getMessage());
				for(String error : parser.getErrors()) {
					System.out.println(error);
				}
			}
		}
		System.out.println("Goodbye!");
	}
}
